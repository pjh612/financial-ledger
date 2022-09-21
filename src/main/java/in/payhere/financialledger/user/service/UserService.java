package in.payhere.financialledger.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.user.dto.response.SignUpResponse;
import in.payhere.financialledger.user.entity.User;
import in.payhere.financialledger.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignUpResponse signUp(String email, String password) {
		password = passwordEncoder.encode(password);
		User newUser = userRepository.save(new User(email, password));

		return new SignUpResponse(newUser.getId());
	}
}
