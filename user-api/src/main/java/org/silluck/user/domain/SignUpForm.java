package org.silluck.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// test code 작성 시 필드를 mocking 해서 사용하는 것이 정석이지만, 빠른 테스트 코드 작성 위해 아래 어노테이션 추가
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
    private String email;
    private String nickname;
    private String password;
    private String phone;
    private String address;
}
