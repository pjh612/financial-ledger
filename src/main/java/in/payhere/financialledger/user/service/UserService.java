package in.payhere.financialledger.user.service;

import static java.text.MessageFormat.format;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.user.converter.UserConverter;
import in.payhere.financialledger.user.entity.EmailToken;
import in.payhere.financialledger.user.entity.User;
import in.payhere.financialledger.user.repository.UserRepository;
import in.payhere.financialledger.user.service.dto.response.SignUpResponse;
import in.payhere.financialledger.user.service.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final EmailTokenRepository emailTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserConverter userConverter;

	@Transactional
	public SignUpResponse signUp(String email, String password) {
		EmailToken emailToken = emailTokenRepository.findByEmail(email).orElseThrow(RuntimeException::new);
		if (!emailToken.isVerified()) {
			throw new BusinessException(ErrorCode.NOT_VERIFIED_EMAIL, format("email : {0} is not verified.", email));
		}

		password = passwordEncoder.encode(password);
		User newUser = userRepository.save(new User(email, password));

		return new SignUpResponse(newUser.getId());
	}

	public UserResponse findByEmail(String email) {
		User foundUser = userRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_USER,
				format("email : {0} not found", email)));

		return userConverter.toUserResponse(foundUser);
	}

	public UserResponse findById(Long userId) {
		User foundUser = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_USER,
				format("userId : {0} not found", userId)));

		return userConverter.toUserResponse(foundUser);
	}
}
