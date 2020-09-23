package io.meetings;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;

// tag::serverEndpoint-annotation[]
@ServerEndpoint(value = "/ChatEndpoint")
// end::serverEndpoint-annotation[]
public class ChatEndpoint {
    private Session currentSession = null;
    private Room room;
    private String userName = "John";

    private final String setNameCommand = "/SETNAME";
    private final String clearHistoryCommand = "/CLEARHISTORY";
    private final String setRoomCommand = "/SETROOM";

    // tag::onOpen-annotation[]
    @OnOpen
    // end::onOpen-annotation[]
    // tag::onOpen-method[]
    public void onOpen(Session session, EndpointConfig ec) {
        currentSession = session;
    }
    //end::onOpen-method[]

    // tag::onMessage-annotation[]
    @OnMessage
    // end::onMessage-annotation[]
    // tag::onMessage-method[]
    public void receiveMessage(String message) {
        if (message.equals("")) {
            return;
        // tag::setName[]    
        } else if (message.startsWith(setNameCommand)) {
            changeName(message);
            return;
        // end::setName[]
        // tag::clearHistory[]
        } else if (message.equals(clearHistoryCommand)) {
            sendMessage("Cleared server side chat history.");
            room.clearMessages();
            return;
        // end::clearHistory[]
        // tag::setRoom[] 
        } else if (message.startsWith(setRoomCommand)) {
            changeRoom(message);
            return;
        // end::setRoom[]
        }
        message = "" + userName + ": " + message;
        sendMessage(message);
    }
    // end::onMessage-method[]

    /**
     * Send a message to all clients in the same room as this client.
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (room == null) {
            return;
        }
        room.addMessage(message);
        for (Session session : currentSession.getOpenSessions()) {
            try {
                if (session.isOpen() && session.getUserProperties().get("roomName").equals(currentSession.getUserProperties().get("roomName"))) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Send all stored room history (messages) to the client.
     */
    public void loadRoomHistory() {
        //get message history for room
        ArrayList<String> messageHistory = room.getMessageHistory();

        //if there is a message history send it to the client
        if (messageHistory.size() > 0) {
            for (String line : messageHistory) {
                try {
                    currentSession.getBasicRemote().sendText(line);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    // tag::onClose-annotation[]
    @OnClose
    // end::onClose-annotation[]
    // tag::onClose-method[]
    public void onClose(Session session, CloseReason reason) {
        if (room != null) {
            sendMessage(userName + " has left the room.");
        }
        if (isRoomEmpty()) {
            RoomManager.deleteRoom(room.getName());
        }
    }
    // end::onClose-method[]

    // tag::onError-annotation[]
    @OnError
    // end::onError-annotation[]
    // tag::onError-method[]
    public void onError(Throwable t) {
        // no error processing will be done for this sample
        t.printStackTrace();
    }
    // end::onError-method[]

    /**
     * Changes the room that the user is in, sends chat history for current *room.
     */
    private void changeRoom(String message) {
        String roomName = message.replace(setRoomCommand, "").trim();
        if (room != null) {
            String oldRoomName = room.getName();
            //if old room name and new room name same, do nothing
            if (oldRoomName.equals(roomName)) {
                loadRoomHistory();//we do this as the JS has cleared message content...
                return;
            }
            sendMessage(userName + " has left the room.");
        }
        room = RoomManager.getRoomByName(roomName);
        currentSession.getUserProperties().put("roomName", room.getName());
        loadRoomHistory();
        sendMessage(userName + " has joined the room.");
    }

    /**
     * Change the display name for the user.
     *
     * @param message
     */
    private void changeName(String message) {
        String oldUserName = userName.toString();
        userName = message.replace(setNameCommand, "").trim();
        //if old and new username same, do nothing
        if (oldUserName.equals(userName)) {
            return;
        }
        sendMessage("User " + oldUserName + " changed name to " + userName);
    }

    /**
     * Looks for open sessions for the given room.
     *
     * @return boolean for if room is empty - true = empty room, false = users still connected.
     */
    private boolean isRoomEmpty() {
        for (Session session : currentSession.getOpenSessions()) {
            if (session.isOpen() && session.getUserProperties().get("roomName").equals(currentSession.getUserProperties().get("roomName"))) {
                return false;
            }
        }
        return true;
    }
}
