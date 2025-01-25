package com.ahimmoyak.lms.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDto> handleBadRequestException(BadRequestException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        log.error("BadRequestException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionDto);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionDto> handleUnauthorizedException(UnauthorizedException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        log.error("UnauthorizedException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(exceptionDto);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionDto> handleForbiddenException(ForbiddenException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .build();

        log.error("ForbiddenException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exceptionDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        log.error("NotFoundException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exceptionDto);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ExceptionDto> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .build();

        log.error("MethodNotAllowedException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(exceptionDto);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDto> handleConflictException(ConflictException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .build();

        log.error("ConflictException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(exceptionDto);
    }

    @ExceptionHandler(DynamoDbException.class)
    public ResponseEntity<ExceptionDto> handleDynamoDbException(DynamoDbException ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String userMessage = "An unexpected error occurred.";

        if (ex instanceof ConditionalCheckFailedException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ResourceNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ProvisionedThroughputExceededException) {
            httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        }

        log.error("DynamoDB error occurred: {}", ex.getMessage(), ex);

        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(httpStatus.getReasonPhrase())
                .message(List.of(userMessage))
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .build();

        return ResponseEntity.status(httpStatus).body(exceptionDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage(), ex);

        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessages)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        log.error("ConstraintViolationException occurred: {}", ex.getMessage(), ex);

        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessages)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }

    @ExceptionHandler(SdkException.class)
    public ResponseEntity<ExceptionDto> handleSdkException(SdkException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        log.error("AWS S3 SDK operation failed: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }

}
