package com.example.splitdish;
import java.io.File;

public interface ReceiptParser {

    Receipt parseReceipt(File image) throws ReceiptParseException;

}