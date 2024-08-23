package org.silluck.user.domain.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.silluck.user.domain.common.TransactionType;
import org.silluck.user.domain.entity.CustomerBalanceHistory;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class CustomerBalanceHistoryDTO {
    private Long id;
    private Long customerId;
    private Integer changeMoney;
    private Integer currentMoney;
    private String fromMessage;
    private String description;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String transactionId;

    public static CustomerBalanceHistoryDTO from(
            CustomerBalanceHistory balanceHistory) {
        return CustomerBalanceHistoryDTO.builder()
                .id(balanceHistory.getId())
                .customerId(builder().customerId)
                .changeMoney(balanceHistory.getChangeMoney())
                .currentMoney(balanceHistory.getCurrentMoney())
                .fromMessage(balanceHistory.getFromMessage())
                .description(balanceHistory.getDescription())
                .transactionType(balanceHistory.getTransactionType())
                .transactionId(balanceHistory.getTransactionId())
                .build();
    }
}
