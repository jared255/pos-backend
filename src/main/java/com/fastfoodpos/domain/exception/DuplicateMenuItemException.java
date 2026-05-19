package com.fastfoodpos.domain.exception;

public class DuplicateMenuItemException extends RuntimeException {
    public DuplicateMenuItemException(String name) {
        super("Ya existe un item de menu con nombre: " + name);
    }
}
