package io.meetings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// tag::RoomManager[]
public class RoomManager {
    // tag::Room[]
    private static List<Room> rooms = Collections.synchronizedList(new ArrayList<>());
    // end::Room[]

    /**
     * Finds and returns the room with the given name. If no room exists with that name creates and returns room.
     *
     * @param name of room requested
     * @return Room with given name (either an existing room or a new room if it did not exist before request).
     */
    // tag::roomName[]
    public static Room getRoomByName(String name) {
        synchronized (rooms) {
            for (Room room : rooms) {
                if (room.getName().equalsIgnoreCase(name)) {
                    return room;
                }
            }
    // end::roomName[]        
            //if we get here there was no pre-existing room with given name. Create room and return that.
            // tag::createNewRoom[]
            Room newRoom = createRoom(name);
            rooms.add(newRoom);
            return newRoom;
            // end::createNewRoom[]
        }
    }

    /**
     * Creates a Room with the provided name.
     *
     * @param name of room to be created.
     * @return Room with requested name.
     */
    // tag::createRoom[]
    public static Room createRoom(String name) {
        return new Room(name);
    }
    // end::createRoom[]

    /**
     * Removes the room with the given name.
     *
     * @param name of room to be deleted.
     */
    // tag::deleteRoom[]
    public static void deleteRoom(String name) {
        synchronized (rooms) {
            for (Room room : rooms) {
                if (room.getName().equalsIgnoreCase(name)) {
                    rooms.remove(room);
                    return;
                }
            }
        }
    }
    // end::deleteRoom[]
}
// end::RoomManager[]
