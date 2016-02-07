package org.jfantasy.websocket;

import org.jfantasy.websocket.exception.WebSocketException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestWebSocketListener implements WebSocketListener {

    private final static UserStore store = new UserStore();

    private User user;

    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println("连接关闭:" + reason + " > " + user.getNickname());
        store.remove(user.getConnection().getId());
        if(user.getNickname() != null) {
            for (User user : store.getUsers(this.user.getConnection().getId())) {
                user.getConnection().sendMessage(this.user.getNickname() + " 已离开 ~~~");
            }
        }
    }

    public void onWebSocketConnect(WebSocketConnection connection) {
        System.out.println("建立新连接:" + connection);
        store.addUser(this.user = new User(connection));
    }

    public void onWebSocketException(WebSocketException error) {
        store.remove(user.getConnection().getId());
    }

    public void onWebSocketText(String message) {
        if (this.user.getNickname() == null) {
            this.user.setNickname(message);
            user.getConnection().sendMessage("欢迎你:" + user.getNickname());
            for (User user : store.getUsers(this.user.getConnection().getId())) {
                user.getConnection().sendMessage(this.user.getNickname() + " 上线了~~~~");
            }
        } else {
            for (User user : store.getUsers(this.user.getConnection().getId())) {
                user.getConnection().sendMessage(this.user.getNickname() + " 说 :" + message);
            }
        }
    }

    public void onWebSocketBinary(byte[] payload, int offset, int len) {
    }

    public static class UserStore {
        private ConcurrentMap<String, User> onlineUsers = new ConcurrentHashMap<String, User>();

        public User remove(String id) {
            return onlineUsers.remove(id);
        }

        public void addUser(User user) {
            this.onlineUsers.put(user.getConnection().getId(), user);
        }

        public List<User> getUsers(String... ignores) {
            List<User> users = new ArrayList<User>();
            for (Map.Entry<String, User> entry : onlineUsers.entrySet()) {
                if (!Arrays.asList(ignores).contains(entry.getKey())) {
                    users.add(entry.getValue());
                }
            }
            return users;
        }

    }

    public static class User {

        private WebSocketConnection connection;
        private String nickname;

        public User(WebSocketConnection connection) {
            this.connection = connection;
        }

        public WebSocketConnection getConnection() {
            return connection;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }


    }

}
