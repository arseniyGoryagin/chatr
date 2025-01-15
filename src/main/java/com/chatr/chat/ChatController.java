package com.chatr.chat;
import com.chatr.chat.domain.Chat;
import com.chatr.chat.domain.NewChatRequest;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping()
    ResponseEntity<Page<Chat>> getUserChats(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size){
        return new ResponseEntity<>(chatService.getUserChats((User) userDetails, page, size), HttpStatus.OK);
    }


    @PostMapping()
    ResponseEntity<Chat> newChat(@RequestBody NewChatRequest newChatRequest){
        return new ResponseEntity<>(chatService.makeNewChat(newChatRequest.getWith()), HttpStatus.OK);
    }


}
