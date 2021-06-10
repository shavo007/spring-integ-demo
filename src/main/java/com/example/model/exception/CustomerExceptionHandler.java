package com.example.model.exception;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ControllerAdvice
public class CustomerExceptionHandler {
	@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResult errorResult = new ErrorResult();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			errorResult.getFieldErrors()
					.add(new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return errorResult;
	}

	@Getter
	@NoArgsConstructor
	public
	static class ErrorResult {
		private final List<FieldValidationError> fieldErrors = new ArrayList<>();

		public ErrorResult(String field, String message) {
			this.fieldErrors.add(new FieldValidationError(field, message));
		}
	}

	@Getter
	@AllArgsConstructor
	static class FieldValidationError {
		private String field;
		private String message;
	}

}
