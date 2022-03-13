package ru.oceancraft.chess.net;

import ru.oceancraft.chess.model.ActionTransmitter;

public interface NetworkActionTransmitter extends ActionTransmitter {
    void setRoomFullListener(RoomFullListener roomFullListener);

    void removeRoomFullListener();

    void setMessageListener(MessageListener messageListener);

    void setOnStatusCheckListener(StatusCheckListener statusCheckListener);

    void connect(Listener success, Listener error);

    void join(String key, Listener success, Listener error);

    void createRoom(OnRoomCreated listener, Listener error);

    void sendMessage(String text);

    void unbind();

    void close();
}
