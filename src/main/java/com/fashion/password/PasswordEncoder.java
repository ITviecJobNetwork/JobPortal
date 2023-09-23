package com.fashion.password;

import lombok.NonNull;

public interface PasswordEncoder {
    String encode(@NonNull String raw);

    boolean matches(@NonNull String raw, @NonNull String encoded);
}
