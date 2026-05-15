package com.cne_project.harnessdemo.config.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import com.cne_project.harnessdemo.dto.response.ErrorResponse;
import com.cne_project.harnessdemo.model.exception.InsufficientStockException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request
	) {
		log.warn("Resource not found: {}", ex.getMessage());
		var errorResponse = new ErrorResponse(
				Instant.now(),
				HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(),
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientStock(
			InsufficientStockException ex,
			HttpServletRequest request
	) {
		log.warn("Insufficient stock: {}", ex.getMessage());
		var errorResponse = new ErrorResponse(
				Instant.now(),
				HttpStatus.UNPROCESSABLE_ENTITY.value(),
				HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(
			MethodArgumentNotValidException ex,
			HttpServletRequest request
	) {
		var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.collect(Collectors.joining(", "));
		log.warn("Validation failed: {}", fieldErrors);
		var errorResponse = new ErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				fieldErrors,
				request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex,
			HttpServletRequest request
	) {
		log.error("Unhandled exception occurred", ex);
		var errorResponse = new ErrorResponse(
				Instant.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				"An unexpected error occurred",
				request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
