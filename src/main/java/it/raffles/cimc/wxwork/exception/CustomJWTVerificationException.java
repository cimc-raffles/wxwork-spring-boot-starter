package it.raffles.cimc.wxwork.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.auth0.jwt.exceptions.JWTVerificationException;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class CustomJWTVerificationException extends RuntimeException {

	public CustomJWTVerificationException(JWTVerificationException exception) {
		super(exception.getCause());
	}

	public CustomJWTVerificationException(String message) {
		super(message, null);
	}

	public CustomJWTVerificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
