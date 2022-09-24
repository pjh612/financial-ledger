package in.payhere.financialledger.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorModel {
	String getCode();

	String getMessage();

	HttpStatus getStatus();
}
