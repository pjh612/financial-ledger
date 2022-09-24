package in.payhere.financialledger.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorModel {
	//COMMON
	RUNTIME_EXCEPTION("C001", "Runtime error", HttpStatus.BAD_REQUEST),

	//VALIDATION
	METHOD_ARGUMENT_NOT_VALID("V001", "Validation error", HttpStatus.BAD_REQUEST),
	CONSTRAINT_VIOLATION("V002", "Validation error", HttpStatus.BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V003", "Data integrity violation", HttpStatus.BAD_REQUEST),

	//USER
	NOT_FOUND_USER("U001", "Not found data", HttpStatus.NOT_FOUND),

	//AUTHENTICATION
	AUTHENTICATION_FAIL("A001", "Authentication failed", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String toString() {
		return "ErrorCode[" +
			"type='" + name() + '\'' +
			",code='" + code + '\'' +
			", message='" + message + '\'' +
			']';
	}
}
