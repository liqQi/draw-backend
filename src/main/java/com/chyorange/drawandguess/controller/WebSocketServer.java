package com.chyorange.drawandguess.controller;

import com.chyorange.drawandguess.models.*;
import com.chyorange.drawandguess.services.UserService;
import com.chyorange.drawandguess.services.WordService;
import com.chyorange.drawandguess.utils.GsonUtils;
import com.chyorange.drawandguess.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.chyorange.drawandguess.controller.RoomController.roomMap;

@Component
@ServerEndpoint("/ws/{userId}/{roomId}")
public class WebSocketServer {


    private UserService userService;

    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    static Log log = LogFactory.getLog(WebSocketServer.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * key userId
     * value webSocketServer
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * key sessionId
     * value userId
     */
    private static ConcurrentHashMap<String, String> sessionUserIdMap = new ConcurrentHashMap<>();
    /**
     * key userId
     * value roomId
     */
    private static ConcurrentHashMap<String, String> userRoomMap = new ConcurrentHashMap<>();

    /**
     * 接收userId
     */
    private String userId = "";

    private Session session;

    private boolean userReady = false;


    public static int roomCursor = 600001;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("roomId") String roomId) {
        userService = applicationContext.getBean(UserService.class);
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            sessionUserIdMap.put(session.getId(), userId);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            sessionUserIdMap.put(session.getId(), userId);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        Room room = roomMap.get(roomId);
        List<String> roomUsers = room.getRoomUsers();
        if (!roomUsers.contains(userId)) {
            roomUsers.add(userId);
        }
        userRoomMap.put(userId, roomId);
        notifyUserJoin(roomUsers, userId);

        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        //可以群发消息
        //消息保存到数据库、redis
        if (message != null && !message.isEmpty()) {
            try {
                WsMessage wsMessage = GsonUtils.fromJson(message, WsMessage.class);
                switch (wsMessage.getAction()) {
                    case WsMessage.USER_READY:
                        userReady = true;
                        notifyRoomUserStatusChange();
                        break;
                    case WsMessage.USER_CANCEL_READY:
                        userReady = false;
                        notifyRoomUserStatusChange();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyRoomUserStatusChange() {
        if (userRoomMap.containsKey(userId)) {
            String roomId = userRoomMap.get(userId);
            if (roomMap.containsKey(roomId)) {
                Room room = roomMap.get(roomId);
                List<String> roomUsers = room.getRoomUsers();
                for (String roomUser : roomUsers) {
                    User user = userService.getUser(roomUser);
                    String sendToUserId = user.getId().toString();
                    if (webSocketMap.containsKey(sendToUserId)) {
                        WebSocketServer webSocketServer = webSocketMap.get(sendToUserId);
                        WsMessage wsMessage = new WsMessage();
                        wsMessage.setAction(WsMessage.SERVER_NOTIFY);
                        wsMessage.setFromUserId("-1");
                        wsMessage.setMsg("用户 " + user.getNickName() + (userReady ? "准备好了" : "取消了准备"));
                        try {
                            webSocketServer.sendMessage(wsMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                checkRoomStatus(room);
            }


        }
    }

    private void checkRoomStatus(Room room) {
        boolean roomReady = true;
        if (room != null) {
            List<String> roomUsers = room.getRoomUsers();
            if (roomUsers != null && !roomUsers.isEmpty()) {
                for (String roomUser : roomUsers) {
                    if (webSocketMap.containsKey(roomUser)) {
                        WebSocketServer webSocketServer = webSocketMap.get(roomUser);
                        if (!webSocketServer.userReady) {
                            roomReady = false;
                            break;
                        }
                    }
                }
                if (roomReady) {
                    notifyRoomReady(room, roomUsers);
                }
            }
        }
    }

    private void notifyRoomReady(Room room, List<String> roomUsers) {
        for (String roomUser : roomUsers) {
            if (webSocketMap.containsKey(roomUser)) {
                WebSocketServer webSocketServer = webSocketMap.get(roomUser);
                WsMessage wsMessage = new WsMessage();
                wsMessage.setAction(WsMessage.SERVER_NOTIFY);
                wsMessage.setFromUserId("-1");
                wsMessage.setMsg("所有人都准备好了！游戏开始！");
                try {
                    webSocketServer.sendMessage(wsMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int cursor;
                if (roomDrawerCursor.containsKey(room.getId())) {
                    cursor = roomDrawerCursor.get(room.getId());
                    cursor++;
                    if (cursor == roomUsers.size()) {
                        cursor = 0;
                    }
                } else {
                    cursor = 0;
                }
                String currentUserId = roomUsers.get(cursor);
                User user = userService.getUser(currentUserId);
                WsMessage wsMessageRoomReady = new WsMessage();
                wsMessage.setAction(WsMessage.ROOM_READY);
                Question question = WordService.getInstance().getQuestion();
                DrawingUser drawingUser = new DrawingUser();
                drawingUser.setDrawingUserId(currentUserId);
                drawingUser.setDrawingUserNickName(user.getNickName());
                drawingUser.setQuestion(question);
                roomDrawerCursor.put(room.getId(), cursor);
                wsMessage.setMsg(GsonUtils.toJson(drawingUser));
                wsMessage.setFromUserId("-1");
                try {
                    webSocketServer.sendMessage(wsMessageRoomReady);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static HashMap<String, Integer> roomDrawerCursor = new HashMap<>();


    private static void notifyUserJoin(List<String> roomUsers, String userId) {
        for (String roomUser : roomUsers) {
            if (webSocketMap.containsKey(roomUser)) {
                WebSocketServer webSocketServer = webSocketMap.get(roomUser);
                WsMessage wsMessage = new WsMessage();
                wsMessage.setAction(WsMessage.SERVER_NOTIFY);
                wsMessage.setFromUserId("-1");
                User user = webSocketServer.userService.getUser(userId);
                wsMessage.setMsg("用户 " + user.getNickName() + " 加入了房间，欢迎！");
                try {
                    webSocketServer.sendMessage(wsMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message, String sessionId) throws IOException {
        WsMessage wsMessage = new WsMessage();
        wsMessage.setMsg(message);
        wsMessage.setAction(WsMessage.SERVER_NOTIFY);
        wsMessage.setFromUserId("-1");
        this.session.getBasicRemote().sendText(GsonUtils.toJson(wsMessage));
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(WsMessage message) throws IOException {
        this.session.getBasicRemote().sendText(GsonUtils.toJson(message));
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message, webSocketMap.get(userId).session.getId());
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
