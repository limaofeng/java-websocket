package org.jfantasy.websocket;

import org.jfantasy.io.Buffer;
import org.jfantasy.websocket.exception.WebSocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebSocketConnection extends WebSocketOutbound {

	String getId();

	void fillBuffersFrom(Buffer paramBuffer);

	void handshake(HttpServletRequest request, HttpServletResponse response) throws WebSocketException;

}
