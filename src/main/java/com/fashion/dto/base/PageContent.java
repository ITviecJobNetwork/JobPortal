package com.fashion.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageContent {
    private String title;
    private String url;
    private List<String> css;
    private List<String> js;
    private boolean isRedirect;
}
