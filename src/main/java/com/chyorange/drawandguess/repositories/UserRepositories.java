package com.chyorange.drawandguess.repositories;

import com.chyorange.drawandguess.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepositories  extends CrudRepository<User,Long> {

    @Query("from User user where user.phoneNumber = ?1 or user.nickName = ?2")
    User query_getUserByPhoneName(String phoneNumber, String nickName);
    @Query("from User user where user.id = ?1")
    User query_getUserByUserId(Long userId);
    @Query("from User user where user.phoneNumber = ?1")
    User query_getUserIdByPhone(String phoneNumber);
}
