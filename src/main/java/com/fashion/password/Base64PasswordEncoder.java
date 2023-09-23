package com.fashion.password;

import lombok.NonNull;

import java.util.Base64;

public class Base64PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(@NonNull String raw) {
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    @Override
    public boolean matches(@NonNull String raw,@NonNull String encoded) {
        byte[] decoded = Base64.getDecoder().decode(encoded);
        String decodedString = new String(decoded);
        return raw.equals(decodedString);
    }
}
