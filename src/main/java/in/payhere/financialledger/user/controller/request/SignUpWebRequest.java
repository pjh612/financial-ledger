package in.payhere.financialledger.user.controller.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public record SignUpWebRequest(
	@Email
	String email,

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
	String password) {
}
