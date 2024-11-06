package org.example.demofunkos.notifications.config;

import java.io.IOException;

public interface WebSocketSender {
    void sendMessage(String message) throws IOException;
}
