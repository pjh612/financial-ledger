package in.payhere.financialledger.user.service;

import static java.text.MessageFormat.format;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.user.entity.EmailToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {
	private final JavaMailSender mailSender;
	private final EmailTokenRepository emailTokenRepository;
	private final TemplateEngine templateEngine;

	@Transactional
	public void sendVerificationEmail(String to) throws Exception {
		String token = createToken();
		Map<String, Object> variables = Map.of("token", token);
		emailTokenRepository.findByEmail(to)
			.ifPresentOrElse(emailToken -> emailToken.updateToken(token),
				() -> emailTokenRepository.save(EmailToken.createEmailToken(to, token)));

		MimeMessage message = createMessage(to, "FINANCIAL-LEDGER 회원가입 이메일 인증", "token-mail", variables);
		try {
			mailSender.send(message);
		} catch (MailException es) {
			throw new BusinessException(es, ErrorCode.MAIL_SEND_FAIL);
		}
	}

	private String createToken() {
		StringBuilder token = new StringBuilder();
		Random random = new Random();

		IntStream.range(0, 8)
			.map(i -> random.nextInt(3))
			.map(caseNumber ->
				switch (caseNumber) {
					case 0 -> random.nextInt(26) + 97;
					case 1 -> (char)(random.nextInt(26) + 65);
					case 2 -> random.nextInt(10);
					default -> throw new IllegalStateException("Unexpected value: " + caseNumber);
				}
			).forEach(token::append);

		return token.toString();
	}

	private MimeMessage createMessage(String to, String subject, String templateName, Map<String, Object> variables)
		throws MessagingException, UnsupportedEncodingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		mimeMessage.addRecipients(MimeMessage.RecipientType.TO, to);
		mimeMessage.setSubject(subject);
		mimeMessage.setText(setContext(templateName, variables), "utf-8", "html");
		mimeMessage.setFrom(new InternetAddress("gksfk612@naver.com", "ledger-admin"));

		return mimeMessage;
	}

	private String setContext(String templateName, Map<String, Object> variables) {
		Context context = new Context();
		context.setVariables(variables);

		return templateEngine.process(templateName, context);
	}

	@Transactional
	public void verify(String email, String token) {
		emailTokenRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_VERIFICATION_TOKEN,
				format("Not found verification token of email {}", email)))
			.verify(token);
	}
}
