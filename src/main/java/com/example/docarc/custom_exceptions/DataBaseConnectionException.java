package com.example.docarc.custom_exceptions;

public class DataBaseConnectionException extends Exception {
    public DataBaseConnectionException(String message) {
        super(message);
    }
}
