package in.payhere.financialledger.user.service;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.user.dto.response.SignUpResponse;
import in.payhere.financialledger.user.dto.response.UserResponse;
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

	public UserResponse findByEmail(String email) {
		User foundUser = userRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_USER,
				MessageFormat.format("email : {0} not found", email)));

		return new UserResponse(foundUser.getId(), foundUser.getEmail(), foundUser.getPassword());
	}
}
