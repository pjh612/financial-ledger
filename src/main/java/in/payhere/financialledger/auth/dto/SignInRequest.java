package in.payhere.financialledger.auth.dto;

import javax.validation.constraints.Email;

public record SignInRequest(
	@Email
	String email,

	String password) {
}
