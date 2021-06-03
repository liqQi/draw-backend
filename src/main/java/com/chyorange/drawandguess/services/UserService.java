package com.chyorange.drawandguess.services;

import com.chyorange.drawandguess.models.User;
import com.chyorange.drawandguess.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepositories userRepositories;

    public boolean isRepeatNickName(String phoneNumber, String nickName) {
        User user = userRepositories.query_getUserByPhoneName(phoneNumber, nickName);
        return user != null;
    }

    public void register(String phoneNumber, String nickName, String password) {
        User user = new User();
        user.setNickName(nickName);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        userRepositories.save(user);
    }

    public User getUser(String userId){
        return userRepositories.query_getUserByUserId(Long.parseLong(userId));
    }

    public Long getUserId(String phoneNumber){
        User user = userRepositories.query_getUserIdByPhone(phoneNumber);
        return user.getId();
    }
}
