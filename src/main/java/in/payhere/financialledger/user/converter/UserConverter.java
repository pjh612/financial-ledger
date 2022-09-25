package in.payhere.financialledger.user.converter;

import org.springframework.stereotype.Component;

import in.payhere.financialledger.user.service.dto.response.UserResponse;
import in.payhere.financialledger.user.entity.User;

@Component
public class UserConverter {

	public User toUser(UserResponse userResponse) {
		return new User(userResponse.id(), userResponse.email(), userResponse.password());
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getPassword());
	}
}
