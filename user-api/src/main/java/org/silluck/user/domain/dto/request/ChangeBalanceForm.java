package org.silluck.user.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBalanceForm {    // 출금과 입금을 위한 폼
    private String from;
    private String message;
    private Integer money;
}