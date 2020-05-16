package chat.model;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;

public class Room
{
    private String id;
    private Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

    public Room(String id)
    {
        this.id = id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void addUser(Session user)
    {
        this.users.add(user);
    }

    public Set<Session> getUsers()
    {
        return this.users;
    }

    public boolean equals(Room room)
    {
        return this.getId().equals(room.getId());
    }

    public int hashCode()
    {
        return this.id.hashCode();
    }

    public String toString()
    {
        return this.id;
    }
}
