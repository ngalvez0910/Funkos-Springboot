package org.example.demofunkos.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    @Override
    public List<String> getSubProtocols() {
        return List.of();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        log.info("Nueva conexión: " + entity);
        TextMessage message = new TextMessage("Se ha conectado " + entity);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException{
        sessions.remove(session);
        log.info("Conexión cerrada: " + entity);
        TextMessage message = new TextMessage("Se ha desconectado " + entity);
        session.sendMessage(message);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        for (WebSocketSession session : sessions){
            if(session.isOpen()){
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
