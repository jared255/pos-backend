package com.fastfoodpos.domain.port.out;

public interface PasswordHasherPort {
    String hash(String rawPassword);
}
