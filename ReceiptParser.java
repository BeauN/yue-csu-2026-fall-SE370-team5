package Services;

import java.io.File;
import Model.Receipt;
//its just an interface for parsing receipts ion got much commentary
public interface ReceiptParser {

    Receipt parseReceipt(File image) throws ReceiptParseException;

}