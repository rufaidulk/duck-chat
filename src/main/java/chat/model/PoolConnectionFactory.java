package chat.model;

import java.sql.DriverManager;
import java.sql.SQLException;
 
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;

public class PoolConnectionFactory
{
    static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    static final String DRIVER_URL = "jdbc:apache:commons:dbcp:";
    private static final String DBCP_DRIVER = "org.apache.commons.dbcp2.PoolingDriver";

    private static PoolingDriver driver;

    public static void registerJdbcDriver(String driver)
    {
        try {
            Class driverClass = Class.forName(driver);
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static ConnectionFactory getConnectionFactory(String connUrl, String user, String passwd)
    {
        // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection 
        // Object!
        ConnectionFactory connFact = new DriverManagerConnectionFactory(connUrl, user, passwd);

        return connFact;
    }

    public static PoolingDriver getDbcpDriver()
    {
        try 
        {
            Class.forName(DBCP_DRIVER);
            driver = (PoolingDriver) DriverManager.getDriver(DRIVER_URL);
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return driver;
    }

    public static void registerPool(String poolName, ObjectPool pool)
    {
        driver.registerPool(poolName, pool);
    }
}
