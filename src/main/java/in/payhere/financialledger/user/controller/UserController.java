package in.payhere.financialledger.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {

	@GetMapping("/signup")
	public String signUp() {
		return "signup";
	}
}
