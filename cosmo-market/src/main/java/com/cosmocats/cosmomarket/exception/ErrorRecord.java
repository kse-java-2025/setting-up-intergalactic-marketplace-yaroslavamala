package com.cosmocats.cosmomarket.exception;

public record ErrorRecord(
    int status,
    String error,
    String message,
    String path
) {}

