package in.payhere.financialledger.common.exception;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.warn("Method argument not valid exception occurred : {}", e.toString(), e);

		return newResponse(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	public ResponseEntity<ErrorResponse<ErrorModel>> handleConstraintViolation(ConstraintViolationException e) {
		log.warn("Constraint violation exception occurred: {}", e.toString(), e);

		return newResponse(ErrorCode.CONSTRAINT_VIOLATION);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleDataIntegrityViolationException(
		DataIntegrityViolationException e) {
		log.warn("DataIntegrityViolation exception occurred : {}", e.toString(), e);

		return newResponse(ErrorCode.DATA_INTEGRITY_VIOLATION);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleBusinessException(BusinessException e) {
		log.error("Business exception occurred : {}", e.toString(), e);

		return newResponse(e.errorModel());
	}

	@ExceptionHandler({RuntimeException.class, Exception.class})
	public ResponseEntity<ErrorResponse<ErrorModel>> handleRuntimeException(Throwable e) {
		log.error("Unexpected exception occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.RUNTIME_EXCEPTION);
	}

	private ResponseEntity<ErrorResponse<ErrorModel>> newResponse(ErrorModel errorCode) {
		ErrorResponse<ErrorModel> errorResponse = new ErrorResponse<>(errorCode);
		return new ResponseEntity<>(errorResponse, errorCode.getStatus());
	}
}
