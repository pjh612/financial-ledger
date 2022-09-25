package in.payhere.financialledger.auth.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import in.payhere.financialledger.common.security.Role;
import in.payhere.financialledger.common.security.jwt.JwtAuthenticationToken;

public record SignInResponse(
	Long id,
	String email,
	Role role,

	@JsonIgnore
	JwtToken accessToken,

	@JsonIgnore
	JwtToken refreshToken,

	@JsonIgnore
	JwtAuthenticationToken jwtAuthenticationToken
) {

}
