package com.fashion.password;

import com.fashion.constant.PWEncoder;
import lombok.NonNull;

import java.util.Map;
import java.util.Objects;

public class DelegatePasswordEncoder implements PasswordEncoder {

    private PWEncoder passwordEncoderActive;
    private PasswordEncoder encoder;

    private PasswordEncoder defaultPasswordEncoder = new DefaultPasswordEncoder();

    private static final Map<PWEncoder, PasswordEncoder> passwordEncoderMap = Map.of(
            PWEncoder.BASE64, new Base64PasswordEncoder(),
            PWEncoder.NOOP, new NoopPasswordEncoder()
    );

    public DelegatePasswordEncoder() {
        this(PWEncoder.BASE64);
    }

    public DelegatePasswordEncoder(PWEncoder passwordEncoderActive) {
        this.passwordEncoderActive = passwordEncoderActive;
        this.encoder = passwordEncoderMap.getOrDefault(passwordEncoderActive, defaultPasswordEncoder);
        if (Objects.isNull(this.encoder)) {
            throw new RuntimeException(passwordEncoderActive + " not found");
        }
    }

    @Override
    public String encode(@NonNull String raw) {
        return this.passwordEncoderActive.getType() + this.encoder.encode(raw);
    }

    @Override
    public boolean matches(@NonNull String raw, @NonNull String encoded) {
        PWEncoder key = this.getKey(encoded);
        PasswordEncoder passwordEncoder = passwordEncoderMap.getOrDefault(key, defaultPasswordEncoder);
        encoded = encoded.replace(key.getType(), "");
        return passwordEncoder.matches(raw, encoded);
    }

    private PWEncoder getKey(String encoded) {
        String key = encoded.substring(0, encoded.lastIndexOf("}}") + 2);
        return PWEncoder.fromType(key);
    }
}
