package org.silluck.domain.order.client;

import org.silluck.domain.order.client.user.ChangeBalanceForm;
import org.silluck.domain.order.client.user.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-api", path = "/customer")
public interface UserClient {

    @GetMapping("/getInfo")
    ResponseEntity<CustomerDTO> getCustomerInfo(
            @RequestHeader(name = "X-AUTH-TOKEN") String token);

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody ChangeBalanceForm form);

}