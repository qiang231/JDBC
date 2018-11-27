package testjdbc;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作JDBC的工具类，其中封装了一些工具方法
 */
public class JDBCTools {

    /**
     * 关闭Statement和connection
     *
     * @param statement
     * @param connection
     */

    public static void release(ResultSet resultset, Statement statement, Connection connection) {
        if (resultset != null) {
            try {
                resultset.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void release(Statement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 1.获取连接的方法
     * 通过读取配置文件从数据库服务器中获取一个连接
     *
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        String dirverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        //读取类路径下的配置文件
        InputStream in = JDBCTools.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(in);

        dirverClass = properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");


        Driver driver = (Driver) Class.forName(dirverClass).newInstance();
        Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);

        Connection connection = driver.connect(jdbcUrl, info);
        return connection;
    }
}
