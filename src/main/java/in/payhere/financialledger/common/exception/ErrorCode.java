package in.payhere.financialledger.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorModel {
	//COMMON
	RUNTIME_EXCEPTION("C001", "Runtime error", HttpStatus.BAD_REQUEST),
	FORBIDDEN("C002", "You don't have authorities.", HttpStatus.FORBIDDEN),

	//VALIDATION
	METHOD_ARGUMENT_NOT_VALID("V001", "Validation error", HttpStatus.BAD_REQUEST),
	CONSTRAINT_VIOLATION("V002", "Validation error", HttpStatus.BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V003", "Data integrity violation", HttpStatus.BAD_REQUEST),

	//USER
	NOT_FOUND_USER("U001", "Not found data", HttpStatus.NOT_FOUND),

	//AUTHENTICATION
	AUTHENTICATION_FAIL("A001", "Authentication failed", HttpStatus.BAD_REQUEST),

	//LEDGER
	NOT_FOUND_LEDGER("L001", "Not found data", HttpStatus.NOT_FOUND),
	NOT_FOUND_LEDGER_RECORD("L002", "Not found data", HttpStatus.NOT_FOUND),

	//EMAIL_TOKEN
	NOT_VERIFIED_EMAIL("E001", "Not verified email", HttpStatus.BAD_REQUEST),
	NOT_FOUND_VERIFICATION_TOKEN("E002", "Not found data", HttpStatus.BAD_REQUEST),
	UPDATE_VERIFICATION_TOKEN_EXCEPTION("E003", "Cannot update verification token", HttpStatus.BAD_REQUEST),
	NOT_VALID_VERIFICATION_TOKEN("E004", "Not valid verification token", HttpStatus.BAD_REQUEST),

	//MAIL
	MAIL_SEND_FAIL("M001", "Mail send failed", HttpStatus.INTERNAL_SERVER_ERROR);

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
