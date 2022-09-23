package in.payhere.financialledger.common.exception;

public class BusinessException extends RuntimeException {
	private final ErrorModel errorModel;

	public BusinessException(ErrorModel errorModel) {
		this.errorModel = errorModel;
	}

	public BusinessException(ErrorModel errorModel, String message) {
		super(message);
		this.errorModel = errorModel;
	}

	public ErrorModel errorModel() {
		return errorModel;
	}

	@Override
	public String toString() {
		return "BusinessException - " + errorModel;
	}
}
