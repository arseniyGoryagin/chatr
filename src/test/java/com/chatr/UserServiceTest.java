package com.chatr;

import com.chatr.exceptions.UserAlreadyInUseException;
import com.chatr.user.UserRepository;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void loadMocks(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void createNewUserTest_if_does_not_exist(){

        String email = "example@email.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userService.createNewUser(user);

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);

    }

    @Test
    public void createNewUserTest_if_does_exist(){

        String email = "example@email.com";;

        User user = new User();
        user.setEmail(email);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Assertions.assertThrows(UserAlreadyInUseException.class, () -> userService.createNewUser(user));

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);

    }




}
