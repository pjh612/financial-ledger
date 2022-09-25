package in.payhere.financialledger.user.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.user.controller.request.SignUpWebRequest;
import in.payhere.financialledger.user.service.UserService;
import in.payhere.financialledger.user.service.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpWebRequest request) {
		return new ApiResponse<>(userService.signUp(request.email(), request.password()));
	}
}
