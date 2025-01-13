package com.ahimmoyak.lms.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDto> handleBadRequestException(BadRequestException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionDto);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionDto> handleUnauthorizedException(UnauthorizedException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(exceptionDto);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionDto> handleForbiddenException(ForbiddenException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exceptionDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exceptionDto);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ExceptionDto> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(exceptionDto);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDto> handleConflictException(ConflictException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(exceptionDto);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionDto> handleInternalServerErrorException(InternalServerErrorException ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(List.of(ex.getMessage()))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exceptionDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGeneralException(Exception ex) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(List.of("An unexpected error occurred."))  // List<String>으로 처리
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exceptionDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessages)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ExceptionDto exceptionDto = ExceptionDto.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessages)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionDto);
    }

}
