package chat.socket;

import chat.model.Room;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

@ServerEndpoint(value = "/socket/{token}")
public class WebSocketEndpoint 
{
    private Session user;
    private String payload;
    private JsonParser jsonParser = new JsonParser();
    private static Set<Room> rooms = Collections.synchronizedSet(new HashSet<Room>());

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token)
    {
        System.out.println("New connection " + session.getId());
        // System.out.println(token);
    }

    @OnMessage
    public String handleTextMessage(Session session, String payload) throws IOException
    {
        System.out.println("-------------------------------------");
        System.out.println("New Text payload Received");
        System.out.println("message " + payload);
        JsonObject jsonObject = this.jsonParser.parse(payload).getAsJsonObject();

        String roomId = jsonObject.get("roomId").getAsString();
        String message = jsonObject.get("message").getAsString();

        System.out.println("message " + message);
        System.out.println("room " + roomId);
        System.out.println(session.getId());

        this.user = session;
        this.payload = payload;

        if (rooms.isEmpty()) {
            this.createRoom(roomId);
            return payload;
        }

        this.broadCastMessageToAllUsers(roomId);


        // System.out.println("New Text payload Received");
        // System.out.println("message " + payload);
        // JsonObject jsonObject = this.jsonParser.parse(payload).getAsJsonObject();

        // String roomId = jsonObject.get("roomId").getAsString();
        // String message = jsonObject.get("message").getAsString();



        // System.out.println("New Text Message Received");
        // System.out.println("message " + message);
        // System.out.println("room " + roomId);
        // System.out.println(session.getId());

        return message;
    }

    @OnMessage(maxMessageSize = 1024000)
    public byte[] handleBinaryMessage(byte[] buffer) 
    {
        System.out.println("New Binary Message Received");
        return buffer;
    }

    private void broadCastMessageToAllUsers(String roomId) throws IOException
    {
        Room room = this.getCurrentRoom(roomId);
        Set<Session> users = room.getUsers();

        synchronized (users) 
        {
            for (Session user : users)
            {
                System.out.println("Broadcasting user " + user.getId());
                if (!this.user.equals(user)) {
                    System.out.println("Message sent to " + user.getId());
                    user.getBasicRemote().sendText(this.payload);
                }
            }
        }

        System.out.println("Message broad casted");
    }

    private Room getCurrentRoom(String roomId)
    {
        System.out.println("Current rooms");
        System.out.println(rooms);
        for (Room room : rooms)
        {
            if (room.getId().equals(roomId)) {
                return room;
            }
        }

        return null;
    }

    private Room createRoom(String roomId)
    {
        Room room = new Room(roomId);
        room.addUser(this.user);

        rooms.add(room);
        System.out.println("new room created");
        System.out.println("Room Id " + roomId);
        System.out.println(rooms);
        return room;
    }

}
