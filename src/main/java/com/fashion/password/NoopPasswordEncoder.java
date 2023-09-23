package com.fashion.password;

import lombok.NonNull;

public class NoopPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(@NonNull String raw) {
        return raw;
    }

    @Override
    public boolean matches(@NonNull String raw, @NonNull String encoded) {
        return raw.equals(encoded);
    }
}
