package in.payhere.financialledger.user.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.user.controller.request.SignUpWebRequest;
import in.payhere.financialledger.user.service.MailService;
import in.payhere.financialledger.user.service.UserService;
import in.payhere.financialledger.user.service.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserRestController {
	private final UserService userService;
	private final MailService mailService;

	@PostMapping("/signup")
	public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpWebRequest request) {
		return new ApiResponse<>(userService.signUp(request.email(), request.password()));
	}

	@PatchMapping("/emails/verification")
	public ResponseEntity<Void> emailConfirm(@RequestBody ConfirmEmailWebRequest request) {
		mailService.verify(request.email(), request.token());

		return ResponseEntity.ok().build();
	}

	@PostMapping("/emails/verification/token")
	public ResponseEntity<Void> emailCheck(@RequestBody EmailCheckWebRequest request) throws Exception {
		mailService.sendVerificationEmail(request.email());

		return ResponseEntity.ok().build();
	}

}
