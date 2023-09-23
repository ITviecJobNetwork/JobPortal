package com.fashion.dto.notify;

import lombok.Data;

import java.util.Map;

@Data
public class EmailNotification extends Notification {
    private boolean isHtml;
    private boolean isTemplate;
    private Map<String, Object> data;
}
