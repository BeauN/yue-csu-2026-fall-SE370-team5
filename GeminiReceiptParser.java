
package Services;

import Model.Receipt;
import Model.ReceiptItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Locale;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class GeminiReceiptParser implements ReceiptParser {

    private final String apiKey;

    private static final String MODEL = "gemini-2.5-flash";

    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/"
        + MODEL + ":generateContent";


    public GeminiReceiptParser(String apiKey) {

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "API key cannot be empty."
            );
        }

        this.apiKey = apiKey;
    }


    @Override
    public Receipt parseReceipt(File image)
            throws ReceiptParseException {

        try {

            // Validate the image
            if (image == null || !image.exists()
                    || !image.isFile() || !image.canRead()) {

                throw new ReceiptParseException(
                    "Receipt image does not exist or cannot be read."
                );
            }


            // Determine image format / MIME type
            String fileName = image.getName().toLowerCase(Locale.ROOT);
            String mimeType;

            if (fileName.endsWith(".png")) {

                mimeType = "image/png";

            } else if (fileName.endsWith(".jpg")
                    || fileName.endsWith(".jpeg")) {

                mimeType = "image/jpeg";

            } else {

                throw new ReceiptParseException(
                    "Unsupported image format. Use JPG, JPEG, or PNG."
                );
            }


            // Convert image to Base64
            byte[] imageBytes = Files.readAllBytes(image.toPath());

            String base64Image =
                Base64.getEncoder().encodeToString(imageBytes);


            // Build the Gemini request
            JsonObject requestBody = new JsonObject();

            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();

            JsonArray parts = new JsonArray();


            // Instructions for Gemini
            JsonObject textPart = new JsonObject();

            textPart.addProperty(
                "text",
                "Extract the restaurant name, purchased items, " +
                "quantities, line totals, subtotal, tax, and total " +
                "from this receipt. Copy monetary values directly " +
                "from the receipt. Do not calculate unit prices, " +
                "divide line totals, or invent missing values. " +
                "Return only the requested structured JSON."
            );

            parts.add(textPart);


            // Attach receipt image
            JsonObject imageData = new JsonObject();

            imageData.addProperty("mime_type", mimeType);
            imageData.addProperty("data", base64Image);

            JsonObject imagePart = new JsonObject();

            imagePart.add("inline_data", imageData);

            parts.add(imagePart);


            content.add("parts", parts);
            contents.add(content);

            requestBody.add("contents", contents);


            // Request structured JSON output
            JsonObject generationConfig = new JsonObject();

            generationConfig.addProperty(
                "responseMimeType",
                "application/json"
            );

            generationConfig.add(
                "responseSchema",
                createReceiptSchema()
            );

            requestBody.add("generationConfig", generationConfig);


            // Send HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(
                    requestBody.toString()
                ))
                .build();


            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );


            // Check API response
            if (response.statusCode() != 200) {

                throw new ReceiptParseException(
                    "Gemini API error (" + response.statusCode()
                    + "): " + response.body()
                );
            }


            // Extract JSON text from Gemini response
            JsonObject responseJson = JsonParser
                .parseString(response.body())
                .getAsJsonObject();

            JsonArray candidates =
                responseJson.getAsJsonArray("candidates");

            if (candidates == null || candidates.size() == 0) {

                throw new ReceiptParseException(
                    "Gemini did not return a receipt response."
                );
            }


            JsonObject responseContent = candidates
                .get(0).getAsJsonObject()
                .getAsJsonObject("content");

            if (responseContent == null
                    || !responseContent.has("parts")) {

                throw new ReceiptParseException(
                    "Gemini returned an incomplete response."
                );
            }


            JsonArray responseParts =
                responseContent.getAsJsonArray("parts");

            StringBuilder extractedText = new StringBuilder();

            for (JsonElement part : responseParts) {

                JsonObject partObject = part.getAsJsonObject();

                if (partObject.has("text")) {

                    extractedText.append(
                        partObject.get("text").getAsString()
                    );
                }
            }


            if (extractedText.length() == 0) {

                throw new ReceiptParseException(
                    "Gemini returned no readable receipt data."
                );
            }


            // Parse the extracted receipt JSON
            JsonObject receiptData = JsonParser
                .parseString(extractedText.toString())
                .getAsJsonObject();


            // Convert JSON into a Receipt object
            return convertToReceipt(receiptData);


        } catch (ReceiptParseException e) {

            throw e;

        } catch (IOException | InterruptedException e) {

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new ReceiptParseException(
                "Unable to communicate with Gemini or read receipt image.",
                e
            );

        } catch (Exception e) {

            throw new ReceiptParseException(
                "Unable to parse receipt information: " + e.getMessage(),
                e
            );
        }
    }


    // Creates the structured JSON schema Gemini must follow
    private JsonObject createReceiptSchema() {

        JsonObject schema = new JsonObject();

        schema.addProperty("type", "OBJECT");

        JsonObject properties = new JsonObject();


        // Restaurant name
        properties.add(
            "restaurantName",
            createStringSchema()
        );


        // Individual receipt items
        JsonObject itemsSchema = new JsonObject();

        itemsSchema.addProperty("type", "ARRAY");

        JsonObject itemSchema = new JsonObject();

        itemSchema.addProperty("type", "OBJECT");

        JsonObject itemProperties = new JsonObject();


        itemProperties.add(
            "name",
            createStringSchema()
        );


        JsonObject quantitySchema = new JsonObject();

        quantitySchema.addProperty("type", "INTEGER");

        itemProperties.add("quantity", quantitySchema);


        JsonObject lineTotalSchema = new JsonObject();

        lineTotalSchema.addProperty("type", "NUMBER");

        itemProperties.add("lineTotal", lineTotalSchema);


        itemSchema.add("properties", itemProperties);


        JsonArray itemRequired = new JsonArray();

        itemRequired.add("name");
        itemRequired.add("quantity");
        itemRequired.add("lineTotal");

        itemSchema.add("required", itemRequired);


        itemsSchema.add("items", itemSchema);

        properties.add("items", itemsSchema);


        // Receipt totals
        JsonObject numberSchema = new JsonObject();

        numberSchema.addProperty("type", "NUMBER");

        properties.add("subtotal", numberSchema);
        properties.add("tax", numberSchema);
        properties.add("total", numberSchema);


        schema.add("properties", properties);


        JsonArray required = new JsonArray();

        required.add("restaurantName");
        required.add("items");
        required.add("subtotal");
        required.add("tax");
        required.add("total");

        schema.add("required", required);


        return schema;
    }


    private JsonObject createStringSchema() {

        JsonObject schema = new JsonObject();

        schema.addProperty("type", "STRING");

        return schema;
    }


    // Converts Gemini's JSON into Receipt objects
    private Receipt convertToReceipt(JsonObject data)
            throws ReceiptParseException {

        try {

            // Extract restaurant name
            if (!data.has("restaurantName")
                    || data.get("restaurantName").isJsonNull()) {

                throw new ReceiptParseException(
                    "Receipt is missing its restaurant name."
                );
            }

            String restaurantName =
                data.get("restaurantName").getAsString();

            if (restaurantName.trim().isEmpty()) {

                throw new ReceiptParseException(
                    "Receipt restaurant name is empty."
                );
            }


            // Check required receipt totals
            if (!data.has("subtotal")
                    || !data.has("tax")
                    || !data.has("total")
                    || data.get("subtotal").isJsonNull()
                    || data.get("tax").isJsonNull()
                    || data.get("total").isJsonNull()) {

                throw new ReceiptParseException(
                    "Receipt is missing subtotal, tax, or total."
                );
            }


            double reportedSubtotal =
                data.get("subtotal").getAsDouble();

            double tax =
                data.get("tax").getAsDouble();

            double total =
                data.get("total").getAsDouble();


            if (!Double.isFinite(reportedSubtotal)
                    || !Double.isFinite(tax)
                    || !Double.isFinite(total)
                    || reportedSubtotal < 0
                    || tax < 0
                    || total < 0) {

                throw new ReceiptParseException(
                    "Receipt contains invalid monetary values."
                );
            }


            // Create Receipt
            Receipt receipt = new Receipt(restaurantName);


            // Extract items
            JsonArray items = data.getAsJsonArray("items");

            if (items == null) {

                throw new ReceiptParseException(
                    "Receipt does not contain an items list."
                );
            }


            // Process each item
            for (JsonElement element : items) {

                JsonObject itemData = element.getAsJsonObject();


                if (!itemData.has("name")
                        || !itemData.has("quantity")
                        || !itemData.has("lineTotal")
                        || itemData.get("name").isJsonNull()
                        || itemData.get("quantity").isJsonNull()
                        || itemData.get("lineTotal").isJsonNull()) {

                    throw new ReceiptParseException(
                        "Receipt contains an incomplete item."
                    );
                }


                String name =
                    itemData.get("name").getAsString();

                int quantity =
                    itemData.get("quantity").getAsInt();

                double lineTotal =
                    itemData.get("lineTotal").getAsDouble();


                // Validate item values
                if (name.trim().isEmpty()
                        || quantity <= 0
                        || !Double.isFinite(lineTotal)
                        || lineTotal < 0) {

                    throw new ReceiptParseException(
                        "Invalid item quantity or price: " + name
                    );
                }


                // Calculate individual price in Java
                double unitPrice = lineTotal / quantity;


                // Create a separate ReceiptItem for each unit
                for (int i = 0; i < quantity; i++) {

                    ReceiptItem item =
                        new ReceiptItem(name, unitPrice);

                    receipt.addItem(item);
                }
            }


            // Set receipt tax and total
            receipt.setTax(tax);

            receipt.setTotal(total);


            // Return completed Receipt
            return receipt;


        } catch (ReceiptParseException e) {

            throw e;

        } catch (Exception e) {

            throw new ReceiptParseException(
                "Unable to convert Gemini response into a Receipt.",
                e
            );
        }
    }

}
