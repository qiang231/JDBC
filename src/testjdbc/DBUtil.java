package testjdbc;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.sql.Connection;

public class DBUtil {

    @Test
    public void testUpdate(){
        Connection connection = null;
        QueryRunner queryRunner = null;
        try {
            connection = JDBCTools.getConnection();
            String sql = "UPDATE customers SET name = ? " +
                    "WHERE id = ?";
            queryRunner.update(connection,sql,"asa",6);
            queryRunner.update(connection, sql, "MIKE", 7);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            JDBCTools.release(null, null, connection);
        }
    }
}
