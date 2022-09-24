package in.payhere.financialledger.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.user.dto.request.SignUpRequest;
import in.payhere.financialledger.user.dto.response.SignUpResponse;
import in.payhere.financialledger.user.dto.response.UserResponse;
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
		assertThat(response.id()).isNotNull();
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
		assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
	}

	@Test
	@DisplayName("이메일로 유저를 조회할 수 있다.")
	void findByEmailSuccess() {
		//given
		String email = "test123@gmail.com";
		User dummyUser = createDummyUser(email);
		em.persist(dummyUser);

		//when
		UserResponse foundUser = userService.findByEmail(email);

		//then
		assertThat(foundUser.id()).isEqualTo(dummyUser.getId());
		assertThat(foundUser.email()).isEqualTo(email);
	}

	@Test
	@DisplayName("존재하지 않는 이메일로 유저 조회는 실패한다.")
	void findByNotExistsEmailFail() {
		//given
		String notExistsEmail = "NOTEXISTSEMAIL@NOTEXISTS.NET";

		//when, then
		assertThatThrownBy(() -> userService.findByEmail(notExistsEmail)).isInstanceOf(EntityNotFoundException.class);
	}

	private User createDummyUser(String email) {
		String encodedPassword = passwordEncoder.encode("test12345");
		return new User(email, encodedPassword);
	}

}