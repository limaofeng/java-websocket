package org.jfantasy.websocket;

import org.junit.Test;

import java.io.IOException;

public class WebSocketServerTest {

    @Test
    public void runServer() throws IOException, InterruptedException {
        WebSocketServer server = new WebSocketServer(8090);
        server.setListenerClass(TestWebSocketListener.class);
        server.start();
        while (true){
            Thread.sleep(1000);
        }
    }
}