package com.chatr.profile;

import com.chatr.exceptions.ErrorMessageResponse;
import com.chatr.exceptions.UserAlreadyInUseException;
import com.chatr.profile.domain.ProfileRequest;
import com.chatr.profile.domain.ProfileResponse;
import com.chatr.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Операции с профилем пользователя")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer JWT Token")
    })
    @GetMapping()
    ResponseEntity<ProfileResponse> getMe(@AuthenticationPrincipal UserDetails userDetails){
        return new ResponseEntity<>(profileService.getProfile(userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    ResponseEntity<ProfileResponse> getProfile(@PathParam("username") String username){
        return new ResponseEntity<>(profileService.getProfile(username), HttpStatus.OK);
    }


    @PutMapping()
    ResponseEntity<ProfileResponse> changeProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ProfileRequest profileRequest){
        return new ResponseEntity<>(profileService.editProfile( userDetails.getUsername() ,profileRequest), HttpStatus.OK);
    }


    @ExceptionHandler(UserAlreadyInUseException.class)
    public ResponseEntity<ErrorMessageResponse> handleAlreadyRegisteredException(UserAlreadyInUseException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.CONFLICT);
    }

}
