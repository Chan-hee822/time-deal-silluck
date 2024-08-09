package org.silluck.user.client;

import org.silluck.user.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun") // 여러개의 페인 클라이어느 사용할 것이기 때문에 퀄리파이어 사용해서 이름 지정
public interface MailgunClient {

    @PostMapping("sandbox0244bd4a0e8240278b31853225e24514.mailgun.org/messages")
    ResponseEntity<String> sendEmail(@SpringQueryMap SendMailForm form);
}
