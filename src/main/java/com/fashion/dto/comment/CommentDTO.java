package com.fashion.dto.comment;

import com.fashion.constant.RoleConstant;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private String fullName;
    private String email;
    private Date createdDate;
    private String content;
    private RoleConstant role;
    private Long parentId;
    private List<CommentDTO> child;
}
