package in.payhere.financialledger.user.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.user.dto.request.SignUpRequest;
import in.payhere.financialledger.user.dto.response.SignUpResponse;
import in.payhere.financialledger.user.entity.User;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserService userService;

	@Test
	@DisplayName("회원 가입 성공하면 ID값을 반환한다")
	void signUpSuccess() {
		//given
		SignUpRequest request = new SignUpRequest("testuser12@gmail.com", "test12345");

		//when
		SignUpResponse response = userService.signUp(request.email(), request.password());

		//then
		Assertions.assertThat(response.id()).isNotNull();
	}

	@Test
	@DisplayName("중복된 이메일로는 가입할 수 없다.")
	void signUpFailWithDuplicatedEmail() {
		//given
		String duplicatedEmail = "testuser12@gmail.com";
		User userA = createDummyUser(duplicatedEmail);
		em.persist(userA);
		em.flush();

		SignUpRequest request = new SignUpRequest(duplicatedEmail, "test12345");

		//when
		userService.signUp(request.email(), request.password());

		//then
		Assertions.assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
	}

	private User createDummyUser(String email) {
		String encodedPassword = passwordEncoder.encode("test12345");
		return new User(email, encodedPassword);
	}
}