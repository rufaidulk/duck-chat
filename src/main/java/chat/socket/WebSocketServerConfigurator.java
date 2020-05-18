package chat.socket;

import chat.model.MysqlConnectionPool;

import javax.websocket.server.ServerEndpointConfig;

public class WebSocketServerConfigurator extends ServerEndpointConfig.Configurator
{
    private MysqlConnectionPool mysqlConnectionPool;

    public WebSocketServerConfigurator()
    {
        this.mysqlConnectionPool = new MysqlConnectionPool();
    }

    public MysqlConnectionPool getMysqlConnectionPool()
    {
        return this.mysqlConnectionPool;
    }
}
