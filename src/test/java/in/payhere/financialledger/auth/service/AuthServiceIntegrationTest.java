package in.payhere.financialledger.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.auth.service.dto.response.SignInResponse;
import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.security.Role;
import in.payhere.financialledger.user.entity.User;

@Transactional
@SpringBootTest
class AuthServiceIntegrationTest {

	@Autowired
	EntityManager em;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthService authService;

	@Test
	@DisplayName("로그인 성공 테스트")
	void signInSuccess() {
		//given
		String email = "test1234@gmail.com";
		User dummyUser = createDummyUser(email);
		em.persist(dummyUser);

		//when
		SignInResponse response = authService.signIn(email, "test12345");

		//then
		assertThat(response.id()).isEqualTo(dummyUser.getId());
		assertThat(response.email()).isEqualTo(dummyUser.getEmail());
		assertThat(response.accessToken()).isNotNull();
		assertThat(response.refreshToken()).isNotNull();
		assertThat(response.role()).isEqualTo(Role.USER);
		assertThat(response.jwtAuthenticationToken()).isNotNull();
	}

	@Test
	@DisplayName("비밀번호가 맞지않으면 로그인에 실패한다.")
	void signInFailWithNotMatchPassword() {
		//given
		String email = "test1234@gmail.com";
		User dummyUser = createDummyUser(email);
		em.persist(dummyUser);

		//when, then
		assertThatThrownBy(() -> authService.signIn(email, "NOTMATCHPASSWORD")).isInstanceOf(BusinessException.class)
			.hasMessageContaining("not matched");
	}

	@Test
	@DisplayName("존재하지 않는 이메일로 로그인은 실패한다.")
	void signInFailWithNotExistsEmail() {
		//given
		String notExistsEmail = "NOTEXISTSEMAIL@gmail.com";

		//when, then
		assertThatThrownBy(() -> authService.signIn(notExistsEmail, "NOTMATCHPASSWORD")).isInstanceOf(
				BusinessException.class)
			.hasMessageContaining("not found");
	}

	private User createDummyUser(String email) {
		String encodedPassword = passwordEncoder.encode("test12345");
		return new User(email, encodedPassword);
	}
}