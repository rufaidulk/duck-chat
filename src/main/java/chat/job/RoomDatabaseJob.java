package chat.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.google.gson.JsonObject;
import org.apache.commons.dbcp2.Utils;

public class RoomDatabaseJob implements Runnable
{
    private Connection conn;
    private JsonObject payload;

    public RoomDatabaseJob(Connection conn, JsonObject payload)
    {
        this.conn = conn;
        this.payload = payload;
    }

    public void run()
    {
        try
        {
            System.out.println("---------- Room database job starts ---------");
        
            this.insertPayloadToChatTable();
            Utils.closeQuietly(this.conn);

            System.out.println("---------- Room database job ends ----------");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertPayloadToChatTable() throws SQLException
    {
        String query = "INSERT INTO chats (room_id, sender_id, message, media_type) VALUES (?, ?, ?, ?)";
        PreparedStatement prepStmt = this.conn.prepareStatement(query);

        prepStmt.setString(1, this.payload.get("room_id").getAsString());
        prepStmt.setString(2, this.payload.get("sender_id").getAsString());
        prepStmt.setString(3, this.payload.get("message").getAsString());
        prepStmt.setString(4, this.payload.get("media_type").getAsString());

        prepStmt.executeUpdate();
    }
}
