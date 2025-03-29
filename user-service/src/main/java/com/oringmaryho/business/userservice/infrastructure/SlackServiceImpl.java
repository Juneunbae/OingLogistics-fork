package com.oringmaryho.business.userservice.infrastructure;

import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackServiceImpl implements DirectMessageAuthService {

  public String makeDirectMessage(String code) {
    String message = String.format(
        "안녕하세요, 스파르타 물류입니다.\n" +
            "슬랙 ID 인증을 위해 아래 인증 코드를 사용해 주세요.\n\n" +
            "*인증 코드*: `%s`\n\n" +
            "이 코드는 5분간 유효합니다. 인증이 완료되면 물류 시스템에 접근할 수 있습니다.\n" +
            "문의 사항은 OingMaryho@sparta-logistics.com으로 연락 부탁드립니다.",
        code
    );
    return message;
  }

  public String generateCode() {
    SecureRandom random = new SecureRandom();
    int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
    return String.valueOf(code);
  }
}
