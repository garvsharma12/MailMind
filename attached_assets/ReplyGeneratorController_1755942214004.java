package com.draig.mailmind_sb;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//api to call :- http://localhost:8080/api/email/generate

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class ReplyGeneratorController {

    private final ReplyGeneratorService replyGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateReply(@RequestBody EmailRequest emailRequest){
        String reply = replyGeneratorService.generateReply(emailRequest);
        return ResponseEntity.ok(reply);
    }
}
