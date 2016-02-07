package org.jfantasy.websocket;

public class WebSocketServerTest {

    public static void main(String[] args) throws Exception {
        WebSocketServer server = new WebSocketServer(8090);
        server.setListenerClass(TestWebSocketListener.class);
        server.start();

        while (true){
            Thread.sleep(1000);
        }

    }
}