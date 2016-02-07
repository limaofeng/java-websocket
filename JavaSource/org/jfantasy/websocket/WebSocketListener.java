package org.jfantasy.websocket;

import org.jfantasy.websocket.exception.WebSocketException;

public interface WebSocketListener {

    /**
     * 接收字节
     *
     * @param payload 字节
     * @param offset  开始位置
     * @param len     长度
     */
    void onWebSocketBinary(byte[] payload, int offset, int len);

    /**
     * @param statusCode 状态码
     * @param reason     关闭原因
     */
    void onWebSocketClose(int statusCode, String reason);

    /**
     * 握手成功后的回调
     *
     * @param connection 连接对象
     */
    void onWebSocketConnect(WebSocketConnection connection);

    /**
     * @param error 异常对象
     */
    void onWebSocketException(WebSocketException error);

    /**
     * 接收文本消息
     *
     * @param message 文本消息
     */
    void onWebSocketText(String message);

}
