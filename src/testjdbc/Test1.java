package testjdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

public class Test1 {


    @Test
    public void test() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            connection = JDBCTools.getConnection();
            statement = connection.createStatement();
            String sql = "select * from customers ";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String localtion = resultSet.getString("location");
                Date date = resultSet.getDate("birth");

                System.out.print("id" + id);
                System.out.print(name);
                System.out.print(email);
                System.out.print(localtion);
                System.out.print(date);
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet, statement, connection);
        }
    }


}
