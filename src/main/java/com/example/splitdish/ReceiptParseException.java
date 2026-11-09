package com.example.splitdish;

import java.io.IOException;

// Custom exception for receipt parsing errors
public class ReceiptParseException extends Exception {
    public ReceiptParseException(String message) {
        super(message);
    }
    public ReceiptParseException(String message, Throwable cause) {
        super(message, cause);
    }
}


