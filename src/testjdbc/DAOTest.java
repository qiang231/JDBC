package testjdbc;

import org.junit.Test;

import java.sql.Date;
import java.util.List;

public class DAOTest {

    DAO dao = new DAO();

    @Test
    public void test(){
        String sql = "insert into customers(Name,email,birth) values(?,?,?)";

        dao.update(sql,"clear","clearEDG@qq.com",new Date(new java.util.Date().getTime()));
//        JDBCTools.update(sql,"xm","xm@qq.com",new Date(new java.util.Date().getTime()));
    }

    @Test
    public void testGet(){
        String sql = "select Name,email,birth " +
                "from customers where Name = ?";
        Customer customer = dao.get(Customer.class,sql,"clear");
        System.out.println(customer);
    }
    @Test
    public void test1(){
        String sql = "select Name,email from customers";
        List<Customer> customer = dao.getForList(Customer.class,sql);
        System.out.println(customer);
    }
}
