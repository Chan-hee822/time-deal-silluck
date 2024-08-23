package org.silluck.domain.order.client;

import org.silluck.domain.order.client.user.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-api", path = "/customer")
public interface UserClient {

    @GetMapping("/getInfo")
    ResponseEntity<CustomerDTO> getCustomerInfo(
            @RequestHeader(name = "X-USER_ID") String token);

//    @PostMapping("/balance")
//    ResponseEntity<Integer> changeBalance(
//            @RequestHeader(name = "X-USER-ID") String token,
//            @RequestBody ChangeBalanceForm form);

}