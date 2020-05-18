package chat.socket;

import chat.model.Room;
import chat.model.MysqlConnectionPool;
import chat.job.RoomDatabaseJob;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.server.ServerEndpointConfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint(value = "/socket/{token}",
    configurator = WebSocketServerConfigurator.class)
public class WebSocketEndpoint 
{
    private Session user;
    private String payload;
    private JsonParser jsonParser;
    private MysqlConnectionPool mysqlConnectionPool;
    private ServerEndpointConfig serverEndpointConfig;
    private static Set<Room> rooms = Collections.synchronizedSet(new HashSet<Room>());

    @OnOpen
    public void onOpen(EndpointConfig config, Session session)
    {
        System.out.println("New connection " + session.getId());
        this.user = session;
        this.jsonParser = new JsonParser();
        this.serverEndpointConfig = (ServerEndpointConfig) config;
        WebSocketServerConfigurator wssc = 
            (WebSocketServerConfigurator) this.serverEndpointConfig.getConfigurator();
        this.mysqlConnectionPool = wssc.getMysqlConnectionPool();
        this.mysqlConnectionPool.printDbStatus();
    }

    @OnMessage
    public void handleTextMessage(Session session, String payload) throws IOException
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

        this.payload = payload;

        RoomDatabaseJob roomDbJob = new RoomDatabaseJob(this.mysqlConnectionPool.getDbConnection(), jsonObject);
        Thread dbThread = new Thread(roomDbJob);
        dbThread.start();

        if (rooms.isEmpty()) {
            this.createRoom(roomId);
            return;
        }

        this.broadCastMessageToAllUsers(roomId);
    }

    @OnMessage(maxMessageSize = 1024000)
    public byte[] handleBinaryMessage(byte[] buffer) 
    {
        System.out.println("New Binary Message Received");
        return buffer;
    }
    
    @OnError
    public void errorHandler(Session session, Throwable th)
    {
        System.out.println("----- Socket Error -----");
        System.out.println("Session " + session.getId());
        System.out.println(th.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason)
    {
        System.out.println("-----------------------------------");
        System.out.println("Closing reason " + reason);
        System.out.println("Closing session " + session.getId());
        this.removeSessionFromRooms(session);
        System.out.println("-----------------------------------");
    }

    private void broadCastMessageToAllUsers(String roomId) throws IOException
    {
        Room room = this.getCurrentRoom(roomId);
        Set<Session> users = room.getUsers();
        boolean userNotExist = true;
        synchronized (users) 
        {
            for (Session user : users)
            {
                if (this.user.equals(user)) {
                    userNotExist = false;
                    continue;
                }

                System.out.println("Broadcasting user " + user.getId());
                System.out.println("Message sent to " + user.getId());
                user.getBasicRemote().sendText(this.payload);
            }
        }

        if (userNotExist) {
            room.addUser(this.user);
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

        return this.createRoom(roomId);
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

    private void removeSessionFromRooms(Session leavingUser)
    {
        for (Room room : rooms)
        {
            Set<Session> users = room.getUsers();

            for (Session user : users)
            {
                if (user.getId().equals(leavingUser.getId())) {
                    users.remove(leavingUser);
                    break;
                }
            }
        }
    }

}
