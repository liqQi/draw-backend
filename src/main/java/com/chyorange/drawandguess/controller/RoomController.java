package com.chyorange.drawandguess.controller;

import com.chyorange.drawandguess.models.BaseResponse;
import com.chyorange.drawandguess.models.Room;
import com.chyorange.drawandguess.services.UserService;
import com.chyorange.drawandguess.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@RestController
@RequestMapping("/room")
public class RoomController {

    /**
     * KEY:roomId
     * VALUE:room
     */
    public static ConcurrentHashMap<String, Room> roomMap = new ConcurrentHashMap<>();
    public static ConcurrentLinkedDeque<String> uselessRoomId = new ConcurrentLinkedDeque<>();

    @Autowired
    UserService userService;

    private Random random = new Random();

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam String userId) {

        BaseResponse<Room> userBaseResponse = new BaseResponse<>();

        if (userId == null || userId.isEmpty()) {
            userBaseResponse.setCode(BaseResponse.BAD_PARAMS);
            userBaseResponse.setMessage("用户未找到");
            return GsonUtils.toJson(userBaseResponse);
        }
        String roomId = generateRoomId();
        List<String> users = new ArrayList<>();
        users.add(userId);
        Room room = new Room();
        room.setId(roomId);
        room.setCreatorId(userId);
        room.setRoomUsers(users);
        roomMap.put(roomId, room);
        userBaseResponse.setT(room);
        userBaseResponse.setCode(BaseResponse.SUCCESS);
        return GsonUtils.toJson(userBaseResponse);
    }

    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public String join(@RequestParam String roomId, @RequestParam String userId) {
        BaseResponse<Room> roomResponse = new BaseResponse<>();

        if (userId == null || userId.isEmpty()) {
            roomResponse.setCode(BaseResponse.BAD_PARAMS);
            roomResponse.setMessage("用户未找到");
            return GsonUtils.toJson(roomResponse);
        }

        if (roomId == null || roomId.isEmpty() || !roomMap.containsKey(roomId)) {
            roomResponse.setCode(BaseResponse.BAD_PARAMS);
            roomResponse.setMessage("房间号错误");
            return GsonUtils.toJson(roomResponse);
        }
        Room room = roomMap.get(roomId);
        room.getRoomUsers().add(userId);
        roomResponse.setT(room);
        roomResponse.setCode(BaseResponse.SUCCESS);
        return GsonUtils.toJson(roomResponse);
    }


    private String generateRoomId() {
        if (!uselessRoomId.isEmpty()) {
            return uselessRoomId.pollFirst();
        }
        return String.valueOf(++WebSocketServer.roomCursor);
    }
}
