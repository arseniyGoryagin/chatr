package com.chatr.messages;


import com.chatr.messages.domain.Message;
import com.chatr.messages.domain.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("{chatId}")
    ResponseEntity<Page<MessageResponse>> getMessages(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long chatId, @RequestParam int page, @RequestParam int size){
        return new ResponseEntity<>(messageService.getMessages( userDetails,chatId, page, size), HttpStatus.OK);
    }



}
