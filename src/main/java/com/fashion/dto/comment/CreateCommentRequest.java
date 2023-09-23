package com.fashion.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateCommentRequest {

    @NotBlank
    @Size(max = 200)
    private String content;

    @NotNull
    private Long productId;

    @JsonIgnore
    private String productCode;
}
