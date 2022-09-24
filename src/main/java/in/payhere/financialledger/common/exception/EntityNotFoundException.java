package in.payhere.financialledger.common.exception;

public class EntityNotFoundException extends BusinessException{
	public EntityNotFoundException(ErrorModel errorModel) {
		super(errorModel);
	}

	public EntityNotFoundException(ErrorModel errorModel, String message) {
		super(errorModel, message);
	}
}
