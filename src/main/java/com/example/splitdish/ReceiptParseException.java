package com.example.splitdish;

public class ReceiptParseException extends Exception {
    public ReceiptParseException(String message) {
        super(message);
    }
    public ReceiptParseException(String message, Throwable cause) {
        super(message, cause);
    }
}


