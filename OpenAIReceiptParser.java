
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

public class OpenAIReceiptParser implements ReceiptParser {

    private final String apiKey;

    private static final String MODEL = "gpt-4.1-mini";

    private static final String API_URL =
        "https://api.openai.com/v1/responses";


    public OpenAIReceiptParser(String apiKey) {

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

            // Validate image
            if (image == null || !image.exists() || !image.isFile() || !image.canRead()) {

                throw new ReceiptParseException(
                    "Receipt image does not exist or cannot be read."
                );
            }


            // Determine image format/MIME type
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

            // OpenAI expects a Base64 data URL because they hate me
            String imageDataURL =
                "data:" + mimeType + ";base64," + base64Image;


            // Build OpenAI request
            JsonObject requestBody = new JsonObject();

            requestBody.addProperty("model", MODEL);

            // Build input array
            JsonArray input = new JsonArray();
            JsonObject message = new JsonObject();

            message.addProperty("role", "user");


            // Add text instructions
            JsonArray content = new JsonArray();
            JsonObject textPart = new JsonObject();

            textPart.addProperty("type", "input_text");
            textPart.addProperty(
                "text",
                "Extract the restaurant name, purchased items, " +
                "quantities, line totals, subtotal, tax, and total " +
                "from this receipt. Copy monetary values directly " +
                "from the receipt. Do not calculate unit prices, " +
                "divide line totals, or invent missing values. " +
                "Return the receipt information as structured JSON."
            );

            content.add(textPart);


            // Attach receipt image
            JsonObject imagePart = new JsonObject();

            imagePart.addProperty("type", "input_image");
            imagePart.addProperty("image_url", imageDataURL);
            imagePart.addProperty("detail", "high");

            content.add(imagePart);

            message.add("content", content);

            input.add(message);

            requestBody.add("input", input);


            // Request structured JSON output
            JsonObject textConfig = new JsonObject();

            JsonObject format = new JsonObject();

            format.addProperty("type", "json_schema");
            format.addProperty("name", "receipt");
            format.addProperty("strict", true);

            format.add("schema", createReceiptSchema());

            textConfig.add("format", format);

            requestBody.add("text", textConfig);


            // Send HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
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
                    "OpenAI API error (" + response.statusCode()
                    + "): " + response.body()
                );
            }


            // Extract JSON text from OpenAI response
            JsonObject responseJson = JsonParser
                .parseString(response.body())
                .getAsJsonObject();

            // Check response status
            if (responseJson.has("status")
                    && !"completed".equals(
                        responseJson.get("status").getAsString()
                    )) {

                throw new ReceiptParseException(
                    "OpenAI response was not completed."
                );
            }


            JsonArray output = responseJson.getAsJsonArray("output");

            if (output == null) {

                throw new ReceiptParseException(
                    "OpenAI did not return receipt data."
                );
            }


            StringBuilder extractedText = new StringBuilder();


            // THE FOR LOOP!!!! THE FOR LOOP IS REAL!!!!
            for (JsonElement outputElement : output) {

                JsonObject outputObject =
                    outputElement.getAsJsonObject();

                if (!outputObject.has("type")
                        || !"message".equals(
                            outputObject.get("type").getAsString()
                        )) {

                    continue;
                }

                if (!outputObject.has("content")) {
                    continue;
                }

                JsonArray outputContent =
                    outputObject.getAsJsonArray("content");

                for (JsonElement contentElement : outputContent) {

                    JsonObject contentObject =
                        contentElement.getAsJsonObject();

                    if (contentObject.has("type")
                            && "output_text".equals(
                                contentObject.get("type").getAsString()
                            )) {

                        extractedText.append(
                            contentObject.get("text").getAsString()
                        );
                    }

                    // Handle model refusals
                    if (contentObject.has("type")
                            && "refusal".equals(
                                contentObject.get("type").getAsString()
                            )) {

                        throw new ReceiptParseException(
                            "OpenAI refused to process the receipt."
                        );
                    }
                }
            }


            if (extractedText.length() == 0) {

                throw new ReceiptParseException(
                    "OpenAI returned no readable receipt data."
                );
            }


            // Parse the extracted receiptJSON
            // from here it's all pretty similar to the Gemini parser
            JsonObject receiptData = JsonParser
                .parseString(extractedText.toString())
                .getAsJsonObject();


            // Convert JSON into Receipt object
            return convertToReceipt(receiptData);


        } catch (ReceiptParseException e) {

            throw e;

        } catch (IOException | InterruptedException e) {

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new ReceiptParseException(
                "Unable to communicate with OpenAI or read receipt image.",
                e
            );

        } catch (Exception e) {

            throw new ReceiptParseException(
                "Unable to parse OpenAI receipt information: "
                + e.getMessage(),
                e
            );
        }
    }


    // Creates the structured JSON schema OpenAI must follow
    private JsonObject createReceiptSchema() {

        JsonObject schema = new JsonObject();

        schema.addProperty("type", "object");

        schema.addProperty("additionalProperties", false);

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


    // Converts OpenAI's JSON into SplitDish Receipt objects
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


                // Calculate individual price the real way
                double unitPrice = lineTotal / quantity;


                // Create separate ReceiptItem for each unit
                //THE FOR LOOP!!!! THE FOR LOOP IS REAL!!!! AGAIN!!!
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
                "Unable to convert OpenAI response into a Receipt.",
                e
            );
        }
    }

}
