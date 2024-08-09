package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.service.test.EmailSendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final EmailSendService emailSendService;

    @GetMapping
    public String sendTestEmail() {
        return emailSendService.sendEmail();
    }
}