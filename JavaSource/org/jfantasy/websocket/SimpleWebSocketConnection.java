package org.jfantasy.websocket;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.io.Buffer;
import org.jfantasy.util.LinkedBlockingQueue;
import org.jfantasy.util.MessageDigestUtil;
import org.jfantasy.util.WebUtil;
import org.jfantasy.websocket.data.Frame;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleWebSocketConnection implements WebSocketConnection {

	private final static Log logger = LogFactory.getLog(WebSocketConnection.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private boolean opean = true;
    private LinkedBlockingQueue<Frame> queue = new LinkedBlockingQueue<Frame>();
	private String id;

	@Override
	public String getId() {
		return this.id;
	}

	public void fillBuffersFrom(Buffer paramBuffer) {
	}

	@SuppressWarnings("unchecked")
	public void handshake(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("Upgrade", request.getHeader("Upgrade"));
		headers.put("Connection", "Upgrade");
		headers.put("Sec-WebSocket-Location", "ws://" + request.getHeader("Host"));
		headers.put("Sec-WebSocket-Origin", request.getHeader("Origin"));

		String key = request.getHeader("Sec-WebSocket-Key");
		if (key != null) {
			key += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			MessageDigestUtil messageDigestUtils = MessageDigestUtil.getInstance("SHA-1");
            try {
                String newkey = new String(Base64.encodeBase64(messageDigestUtils.get(key.getBytes("iso-8859-1"))));
                headers.put("Sec-WebSocket-Accept", newkey);
            }catch (IOException e){
                logger.error(e.getMessage(),e);
            }
		}

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			response.addHeader(entry.getKey(), entry.getValue());
		}

		if (logger.isDebugEnabled()) {
			StringBuilder buffer = new StringBuilder("\r\n握手信息如下:");
			buffer.append("\r\n request headers \r\n----------------------------------------------");
			Enumeration<String> enumHeaderName = request.getHeaderNames();
			while (enumHeaderName.hasMoreElements()) {
				String name = enumHeaderName.nextElement();
				String value = request.getHeader(name);
				buffer.append("\r\n").append(name).append(":").append(value);
			}
			buffer.append("\r\n\r\n response headers \r\n----------------------------------------------");
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				buffer.append("\r\n").append(entry.getKey()).append(":").append(entry.getValue());
			}
			logger.debug(buffer.toString());
		}

        if (!"13".equals(request.getHeader("Sec-WebSocket-Version")))
            throw new RuntimeException("暂时只支持 version 13");

		this.id = UUID.randomUUID().toString().toUpperCase();
	}

	public void disconnect() {
        this.sendMessage(Frame.CLOSE, "server close!");
	}

	public boolean isOpen() {
		return this.opean;
	}

	/**
	 * 发送字节码到客户端
	 * 
	 * @param paramBoolean
	 * @param paramByte
	 * @param paramArrayOfByte
	 * @param paramInt1
	 * @param paramInt2
	 */
	public void sendFragment(boolean paramBoolean, byte paramByte, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
	}

	/**
	 * 发送字符串到客户端
	 * 
	 * @param msg 发送文本消息
	 */
	public void sendMessage(String msg) {
		this.sendMessage(Frame.TXT, msg);
	}

	public void sendMessage(byte opcode, String msg) {
        WebUtil.Browser browser = WebUtil.browser(request);
        try {
            queue.put(Frame.newFrame(opcode, msg, browser == WebUtil.Browser.mozilla));
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
	}

	public void sendMessage(byte paramByte, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
	}

	public void closed() {
		this.opean = false;
	}

    public Frame take() throws InterruptedException {
        return queue.take();
    }

}
