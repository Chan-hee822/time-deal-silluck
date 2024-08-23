package org.silluck.user.service.customer;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.dto.request.ChangeBalanceForm;
import org.silluck.user.domain.entity.CustomerBalanceHistory;
import org.silluck.user.exception.CustomException;
import org.silluck.user.repository.CustomerBalanceHistoryRepository;
import org.silluck.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.silluck.user.domain.common.TransactionType.DEPOSIT;
import static org.silluck.user.domain.common.TransactionType.WITHDRAWAL;
import static org.silluck.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.silluck.user.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})
    // 해당 exception이 나와 있을 때 noRollback
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomException {   // 잔액 부족 같은 예외

        CustomerBalanceHistory customerBalanceHistory =
                customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                        .orElse(CustomerBalanceHistory.builder()
                                .changeMoney(0)
                                .currentMoney(0)
                                .customer(customerRepository.findById(customerId)
                                        .orElseThrow(() -> new CustomException(NOT_FOUND_USER)))
                                .build());

        if (customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0) {
            throw new CustomException(NOT_ENOUGH_BALANCE);
        }

        customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(form.getMoney())
                .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())
                .description(form.getMessage())
                .fromMessage(form.getFrom())
                .customer(customerBalanceHistory.getCustomer())
                .transactionType(form.getMoney() > 0 ? DEPOSIT : WITHDRAWAL)
                .transactionId(UUID.randomUUID().toString().replace("-", ""))
                .build();

        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

        return customerBalanceHistoryRepository.save(customerBalanceHistory);
    }
}