package in.payhere.financialledger.auth.controller.request;

import javax.validation.constraints.Email;

public record SignInWebRequest(
	@Email
	String email,

	String password) {
}
