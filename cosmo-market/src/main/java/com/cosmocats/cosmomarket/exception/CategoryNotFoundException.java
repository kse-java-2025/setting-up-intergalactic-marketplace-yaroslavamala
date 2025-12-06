package com.cosmocats.cosmomarket.exception;

public class CategoryNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Category not found: %s";

    public CategoryNotFoundException(long categoryId) {
        super(String.format(MESSAGE_TEMPLATE, categoryId));
    }
}
