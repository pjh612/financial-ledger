package in.payhere.financialledger.user.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserResponse(
	Long id,
	String email,
	@JsonIgnore
	String password) {
}
