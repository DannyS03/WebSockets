package io.meetings;

import java.util.ArrayList;

// tag:: room[]
public class Room {
    // tag::name[]
    private String name = "";
    // end::name[]
    // tag::messageHistory[]
    private ArrayList<String> messageHistory = new ArrayList<>();
    // end::messageHistory[]

    public Room(String roomName) {
        name = roomName;
    }

    /**
     * Sets the name for the room.
     *
     * @param newName new name for the room.
     */
    // tag::setName[]
    public void setName(String newName) {
        name = newName;
    }
    // end::setName[]

    /**
     * Returns the name of the room.
     *
     * @return String name of the room.
     */
    // tag::getName[]
    public String getName() {
        return name;
    }
    // end::getName[]

    /**
     * Removes all message history from this room.
     */
    public void clearMessages() {
        messageHistory.clear();
    }

    /**
     * Adds a message to the room message history.
     *
     * @param message to add to history.
     */
    // tag::addMessage[]
    public void addMessage(String message) {
        messageHistory.add(message);
    }
    // end::addMessage[]

    /**
     * Gets all messages from this room's history.
     *
     * @return ArrayList<String> of all messages for this room.
     */
    // tag::getAllMessages[]
    public ArrayList<String> getMessageHistory() {
        return messageHistory;
    }
    // end::getAllMessages[]
}
// end::room[]
