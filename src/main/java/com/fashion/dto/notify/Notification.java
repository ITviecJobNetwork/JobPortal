package com.fashion.dto.notify;

import lombok.Data;

import java.util.Map;

@Data
public class Notification {
    private String receiver;
    private String title;
    private String body;
    private Map<String, Object> data;
}
