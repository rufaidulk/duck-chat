package chat.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class MysqlConnectionPool
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/redmine";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String POOL_NAME = "dbcp-chat";

    private static ObjectPool objectPool = null;

    public MysqlConnectionPool()
    {
        this.setupPool();
        this.printDbStatus();
    }

    private void setupPool()
    {
        PoolConnectionFactory.registerJdbcDriver(PoolConnectionFactory.MYSQL_DRIVER);
        ConnectionFactory connFact = PoolConnectionFactory.getConnectionFactory(DB_URL, DB_USER, DB_PASS);
        
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the 
        // CreatesonnectionFactory to Add Object Pooling Functionality!
        PoolableConnectionFactory poolableConnFact = new PoolableConnectionFactory(connFact, null);

        // Creates an Instance of GenericObjectPool That Holds Pool of Connections Object!
        objectPool = new GenericObjectPool(poolableConnFact);

        // Set the objectPool to enforces the association (prevent bugs)
        poolableConnFact.setPool(objectPool);

        // Get the driver of the pool and register them.
        PoolingDriver dbcpDriver = PoolConnectionFactory.getDbcpDriver();
        dbcpDriver.registerPool(POOL_NAME, objectPool);
    }

    public void printDbStatus()
    {
        System.out.println("-------------------- MySql Db Status ------------------");
        System.out.println("Active connections: " + getObjectPool().getNumActive());
        System.out.println("Idle connections: " + getObjectPool().getNumIdle());
        System.out.println("------------------ Status end--------------------------");
    }

    public ObjectPool getObjectPool()
    {
        return objectPool;
    }

    public Connection getDbConnection()
    {
        try {
            return DriverManager.getConnection(PoolConnectionFactory.DRIVER_URL + POOL_NAME);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
