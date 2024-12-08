package com.example.projectmatrix.connection.websocket;

import static com.example.projectmatrix.dto.SmartwatchDataMapper.map;

import android.util.Log;

import com.example.projectmatrix.MainActivity;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebsocketConnectionService extends WebSocketServer {

    private final MainActivity context;

    public WebsocketConnectionService(String address, int port, MainActivity context) {
        super(new InetSocketAddress(address, port));
        this.context = context;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d("Websocket", "Closing connection...");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("Websocket", "Received message: " + message);
        context.saveWatchData(map(message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d("Websocket", "Failed to connect :( " + ex.getMessage());
    }

    @Override
    public void onStart() {
        Log.d("Websocket","Server started successfully!");
    }
}
