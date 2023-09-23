package com.fashion.service;

import com.fashion.annotation.Order;
import com.fashion.dao.CommentDao;
import com.fashion.dao.ProductDao;
import com.fashion.dao.UserDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.comment.CommentDTO;
import com.fashion.dto.comment.CreateCommentRequest;
import com.fashion.dto.user.UserResponse;
import com.fashion.entity.Comment;
import com.fashion.entity.Product;
import com.fashion.entity.User;
import com.fashion.utils.ObjectUtil;
import lombok.Setter;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Order(Integer.MAX_VALUE)
@Setter
public class CommentService extends BaseService {

    private CommentDao commentDao;
    private ProductDao productDao;
    private UserDao userDao;

    public List<CommentDTO> findByProductId(Long productId, Session session) {
        List<Comment> comments = this.commentDao.findByProductId(productId, session);
        List<String> emails = comments.stream()
                .map(Comment::getCreatedBy)
                .collect(Collectors.toList());
        Map<String, User> userMap = this.userDao.findByEmails(emails, session)
                .stream()
                .collect(Collectors.toMap(User::getEmail, u -> u));

        List<CommentDTO> commentDTOS = comments.stream()
                .map(comment -> {
                    User user = userMap.get(comment.getCreatedBy());
                    return this.convertCommentToDto(comment, user);
                })
                .collect(Collectors.toList());
        Map<Long, List<CommentDTO>> commentMap = commentDTOS.stream()
                .filter(c -> Objects.nonNull(c.getParentId()))
                .collect(Collectors.groupingBy(CommentDTO::getParentId));

        return commentDTOS
                .stream()
                .map(commentDTO -> {
                    Long id = commentDTO.getId();
                    List<CommentDTO> child = commentMap.get(id);
                    commentDTO.setChild(child);
                    return commentDTO;
                })
                .filter(commentDTO -> Objects.isNull(commentDTO.getParentId()))
                .collect(Collectors.toList());
    }

    private CommentDTO convertCommentToDto(Comment comment, User user) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedDate(comment.getCreatedDate());
        commentDTO.setFullName(user.getFullName());
        commentDTO.setEmail(user.getEmail());
        commentDTO.setRole(user.getRole());
        commentDTO.setParentId(comment.getParentId());
        return commentDTO;
    }

    public Result<CreateCommentRequest> addComment(CreateCommentRequest createCommentRequest, UserResponse userResponse) {
        return this.tryCatchWithTransaction(session -> {
            Product product = this.productDao.findById(createCommentRequest.getProductId(), session)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
            Comment comment = new Comment();
            comment.setCreatedBy(userResponse.getEmail());
            comment.setProductId(product.getId());
            comment.setContent(createCommentRequest.getContent());
            comment.setHidden(false);
            this.commentDao.save(comment, session);
            createCommentRequest.setProductCode(product.getCode());
            return Result.<CreateCommentRequest>builder()
                    .isSuccess(true)
                    .message("Bình luận thành công")
                    .data(createCommentRequest)
                    .build();
        }, createCommentRequest);
    }
}
