package com.chatr.profile;
import com.chatr.exceptions.UserAlreadyInUseException;
import com.chatr.profile.domain.ProfileRequest;
import com.chatr.profile.domain.ProfileResponse;
import com.chatr.user.UserRepository;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserService userService;
    private final UserRepository userRepository;


    ProfileResponse editProfile(String currentUserUsername, ProfileRequest profileRequest){


        User user = userService.getUserByEmail(currentUserUsername).orElseThrow(() -> new NoSuchElementException("No such user"));

        if(userRepository.existsByUsername(profileRequest.getUsername()))
            throw new UserAlreadyInUseException("Username already in use exception");

        user.setDescription(profileRequest.getDescription());
        user.setUsername(profileRequest.getUsername());

        return userToProfileResponse( userService.saveUser(user));

    }

    ProfileResponse getProfile(String username){
        return userToProfileResponse(userService.getUserByUsername(username).orElseThrow(() -> new NoSuchElementException("No such user")));
    }

    private ProfileResponse userToProfileResponse(User user){
        return ProfileResponse.builder()
                .username(user.getUsername())
                .description(user.getEmail())
                .build();
    }



}
