package chat.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestMysql
{
    public void handle() 
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String dbUrl = "jdbc:mysql://localhost:3306/redmine?useUnicode=true&characterEncoding=UTF-8&user=root&password=root";
            Connection conn = DriverManager.getConnection(dbUrl);
            ResultSet rs = conn.prepareStatement("show tables").executeQuery();
     
            while(rs.next()){
                String s = rs.getString(1);
                System.out.println(s);
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
