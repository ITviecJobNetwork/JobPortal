package vn.hcmute.springboot.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.response.ErrorResponse;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
@RestController
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(
          ConflictException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRoleNotFoundException(
          NotFoundException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
          UsernameNotFoundException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> BadRequestException(
          BadRequestException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> MultipartException(
          UnauthorizedException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<ErrorResponse> TokenExpiredException(
          TokenExpiredException ex) {
    var errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errorCode(ex.getMessage())
            .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

}
