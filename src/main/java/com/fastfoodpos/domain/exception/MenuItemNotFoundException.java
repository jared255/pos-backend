package com.fastfoodpos.domain.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(Integer id) {
        super("No existe item de menu activo con id " + id);
    }
}
