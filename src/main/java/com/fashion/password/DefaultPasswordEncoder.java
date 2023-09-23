package com.fashion.password;

import lombok.NonNull;

public class DefaultPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(@NonNull String raw) {
        return null;
    }

    @Override
    public boolean matches(@NonNull String raw, @NonNull String encoded) {
        return false;
    }
}
