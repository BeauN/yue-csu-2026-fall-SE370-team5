import java.io.IOException;
package Services;

// Custom exception for receipt parsing errors
public class ReceiptParseException extends Exception {
    public ReceiptParseException(String message) {
        super(message);
    }
    public ReceiptParseException(String message, Throwable cause) {
        super(message, cause);
    }
}


