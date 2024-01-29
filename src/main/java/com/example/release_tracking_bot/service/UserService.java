package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.model.User;
import com.example.release_tracking_bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(String userName, String userChatId) {
        User existedUser = userRepository.findByChatId(userChatId);

        if (existedUser != null) {
            return existedUser;
        }

        User user = new User();
        user.setName(userName);
        user.setChatId(userChatId);
        user.setArtists(new ArrayList<>());
        return userRepository.save(user);
    }
}
