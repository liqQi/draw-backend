package com.chyorange.drawandguess.controller;

import com.chyorange.drawandguess.models.BaseResponse;
import com.chyorange.drawandguess.models.User;
import com.chyorange.drawandguess.services.UserService;
import com.chyorange.drawandguess.utils.GsonUtils;
import com.chyorange.drawandguess.utils.PhoneCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestParam String phoneNumber, @RequestParam String nickName, @RequestParam String password) {
        BaseResponse<User> userBaseResponse = new BaseResponse<>();
        if (phoneNumber.length() != 11 || !PhoneCheckUtils.isChinaPhoneLegal(phoneNumber)) {
            userBaseResponse.setCode(BaseResponse.BAD_PARAMS);
            userBaseResponse.setMessage("电话号码格式不对");
            return GsonUtils.toJson(userBaseResponse);
        }

        if (nickName.isEmpty() || nickName.length() > 8) {
            userBaseResponse.setCode(BaseResponse.BAD_PARAMS);
            userBaseResponse.setMessage("昵称长度不对");
            return GsonUtils.toJson(userBaseResponse);
        }

        if (password.isEmpty()) {
            userBaseResponse.setCode(BaseResponse.BAD_PARAMS);
            userBaseResponse.setMessage("请输入密码");
            return GsonUtils.toJson(userBaseResponse);
        }

        boolean repeat = userService.isRepeatNickName(phoneNumber, nickName);
        if (repeat) {
            userBaseResponse.setCode(BaseResponse.ERROR);
            userBaseResponse.setMessage("用户名或手机号已存在");
            return GsonUtils.toJson(userBaseResponse);
        } else {
            userService.register(phoneNumber, nickName, password);
            userBaseResponse.setCode(BaseResponse.SUCCESS);
            userBaseResponse.setMessage("成功");
            User t = new User();
            t.setPassword(password);
            t.setNickName(nickName);
            t.setPhoneNumber(phoneNumber);
            t.setId(userService.getUserId(phoneNumber));
            userBaseResponse.setT(t);
            userBaseResponse.setCode(BaseResponse.SUCCESS);
            return GsonUtils.toJson(userBaseResponse);
        }

    }

}
