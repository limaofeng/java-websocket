package org.jfantasy.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.util.LinkedBlockingQueue;
import org.jfantasy.util.StringUtil;
import org.jfantasy.websocket.data.Frame;
import org.jfantasy.websocket.http.WebSocketRequest;
import org.jfantasy.websocket.http.WebSocketResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class WebSocketClient {

    protected final static Log logger = LogFactory.getLog(WebSocketClient.class);

    // 握手信息-请求信息;
    private HttpServletRequest request;

    private HttpServletResponse response;

    private WebSocketListener listener;

    private SimpleWebSocketConnection connection;

    private Socket socket;
    /**
     * 线程池
     */
    private Executor executor;
    /**
     * 定时任务
     */
    private ScheduledExecutorService scheduler;

    public WebSocketClient(Socket socket, WebSocketListener listener) throws TimeoutException {
        try {
            this.socket = socket;
            this.listener = listener;
            WebSocketRequest request = new WebSocketRequest(socket);
            this.response = new WebSocketResponse(socket.getOutputStream(), request, socket);
            // 构建connection
            this.connection = new SimpleWebSocketConnection();
            // WebUtil.getRealIpAddress(request);
            // load 握手数据
            request.loadData();
            // 握手
            this.connection.handshake(request, response);
            // 发送握手信息
            response.sendError(101, "Switching Protocols");
            response.flushBuffer();

            this.request = request;

            this.listener.onWebSocketConnect(connection);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void service() throws IOException {
        // 监听推送消息队列
        InputStream in = request.getInputStream();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (isOpen()) {
                    Frame data;
                    try {
                        data = connection.take();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                        return;
                    }
                    try {
                        if (data.getHandler().getOpcode() == Frame.CLOSE) {
                            connection.closed();
                        }
                        OutputStream out = response.getOutputStream();
                        out.write(Frame.toBytes(data));
                        out.flush();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        // 消息队列
        final LinkedBlockingQueue<Frame> queue = new LinkedBlockingQueue<Frame>();
        // 为websocket创建上下文访问的
        executor.execute(new Runnable() {// 监听器线程
            public void run() {
                while (connection.isOpen()) {
                    Frame data;
                    try {
                        data = queue.take();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                        return;
                    }
                    byte opcode = data.getHandler().getOpcode();
                    if (Frame.TXT == opcode) {
                        listener.onWebSocketText(data.getMessage());
                    } else if (Frame.BINARY == opcode) {
                        listener.onWebSocketBinary(data.getBody(), 0, (int) data.getHandler().getDataLength());
                    } else if (Frame.CLOSE == opcode) {
                        listener.onWebSocketClose(opcode, StringUtil.defaultValue(data.getMessage(), "client close!"));
                        connection.closed();
                    } else if (Frame.PING == opcode) {
                        connection.sendMessage(Frame.PONG, "");
                    } else if (Frame.PONG == opcode) {
                        logger.debug("PONG:" + opcode + "\t" + data.getMessage());
                    } else {
                        logger.debug("opcode:" + opcode);
                    }
                }
            }
        });
        Frame data = new Frame();
        int intByte = in.read();
        boolean closed = false;
        while (intByte > -1) {
            data.put((byte) intByte);
            if (data.finish()) {
                try {
                    queue.put(data);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                closed = Frame.CLOSE == data.getHandler().getOpcode();
                if (!closed) {
                    data = new Frame();
                }
            }
            if (closed) {
                intByte = -1;
            } else {
                intByte = in.read();
            }
        }
    }


    public boolean isOpen() {
        return connection.isOpen();
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

}
