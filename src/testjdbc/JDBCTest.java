package testjdbc;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class JDBCTest {


    @Test
    public void testresultsetmetadata() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT flowid flow_Id, type, idcard id_Card, "
                    + "examcard exam_Card, studentname student_Name, "
                    + "location, grade " + "FROM students WHERE flowid = ?";

            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 5);

            resultSet = preparedStatement.executeQuery();

            Map<String, Object> values =
                    new HashMap<String, Object>();

            //1. 得到 ResultSetMetaData 对象
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while(resultSet.next()){
                //2. 打印每一列的列名
                for(int i = 0; i < rsmd.getColumnCount(); i++){
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object columnValue = resultSet.getObject(columnLabel);

                    values.put(columnLabel, columnValue);
                    System.out.println();
                }
            }

//			System.out.println(values);

            Class clazz = Student.class;

            Object object = clazz.newInstance();
            for(Map.Entry<String, Object> entry: values.entrySet()){
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

//				System.out.println(fieldName + ": " + fieldValue);

                ReflectionUtils.setFieldValue(object, fieldName, fieldValue);
            }

            System.out.println(object);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet, preparedStatement, connection);
        }
    }
    @Test
    public void testResultmetaData(){
        //是描述ResultSet的元数据对象 ，即从中可以获取结果集的列数列名，
        //调用ResultSet的getMetaData()方法
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            con = JDBCTools.getConnection();
            String sql = "select flowid ,type,idcard ,examcard,studentname,location,grade from students where flowid = ?";
            preparedStatement = con.prepareStatement(sql);

            preparedStatement.setInt(1,5);
            resultSet = preparedStatement.executeQuery();
            //1.得到ResultSetMetaData对象
            ResultSetMetaData rsmd = resultSet.getMetaData();
            //2.创建一个Map<String, Object> 对象，键：Sql查询的列的别名 值：列的值
            Map<String,Object> values = new HashMap<>();

            //3.处理结果集。利用反射ResultSetMetaData 填充2对应的Map对象
            while (resultSet.next()){

                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object columnValue = resultSet.getObject(columnLabel);
//                    int q;
                    values.put(columnLabel,columnValue);
                }
            }
            System.out.println(values);
            //4.若map不为空，利用反射创建clazz对应的对象
            Class clazz = Student.class;
            Object object = clazz.newInstance();
            //5.遍历Map对象
            for (Map.Entry<String,Object> entry: values.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
//                System.out.println(fieldName + ":" + fieldValue);

                ReflectionUtils.setFieldValue(object,fieldName,fieldValue);

            }
            System.out.println(object);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet,preparedStatement,con);
        }
    }


    @Test
    public void testGet(){
        String sql = "SELECT id, name, email from customers birth where id = ? ";
        Customer customer = get(Customer.class , sql,5);
        System.out.println(customer);
        sql = "select flowid,type,idcard,examcard,studentname,location,grade from students where flowid = ? ";
        Student student = get(Student.class,sql,5);
        System.out.println(student);
    }

    public <T> T get(Class clazz,String sql,Object ...args){
        T entity = null;

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            con = JDBCTools.getConnection();
            preparedStatement = con.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            Map<String,Object> values = new HashMap<>();

            if (resultSet.next()){
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String col_label = rsmd.getColumnLabel(i+1);
                    Object col_value = resultSet.getObject(i+1);
                    values.put(col_label,col_value);
                }
            }
            if (values.size() >0){
                entity = (T) clazz.newInstance();
                for (Map.Entry<String,Object> entry:values.entrySet()
                     ) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    ReflectionUtils.setFieldValue(entry,fieldName,fieldValue);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet,preparedStatement,con);
        }
        return entity;
    }


    public Student getStudents(String sql, Object ... args){
        Student s = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            con = JDBCTools.getConnection();
            preparedStatement = con.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }
            resultSet = preparedStatement.executeQuery(sql);
            if (resultSet.next()){
                s = new Student();
                s.setFlowId(resultSet.getInt(1));
                s.setType(resultSet.getInt(2));
                s.setIdCard(resultSet.getString(3));
                s.setExamCard(resultSet.getString(4));
                s.setStudentName(resultSet.getString(5));
                s.setLocation(resultSet.getString(6));
                s.setGrade(resultSet.getInt(7));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet,preparedStatement,con);
        }
        return s;
    }

    @Test
    public void testDriver() throws SQLException {
        //1.创建一个Driver实现类的对象
        Driver driver = new com.mysql.cj.jdbc.Driver();
        //2.准备连接数据库的基本信息：url，user，password
        String url = "jdbc:mysql://localhost:3306/mysql? "
                + "useUnicode=true & "
                + "characterEncoding=UTF-8 & "
                + "useJDBCCompliantTimezoneShift=true & "
                + "useLegacyDatetimeCode=false&serverTimezone=UTC &&"
                + "useSSL=false";
        Properties info = new Properties();
        info.put("user","root");
        info.put("password","salis");
        //3.调用Driver接口的connect(url，info)获取数据库的连接
        Connection con = driver.connect(url,info);

        System.out.println(con);
    }
    /**
     * 编写一个通用的方法，在不修改源程序的情况下，可以获取任何数据库的连接
     * 解决方案：把数据库的驱动Driver实现类的全类名，url，user，password放入一个配置文件中，通过修改配置文件的方式实现和具体的数据库解耦。
     *
     */


    public Connection getConnection() throws Exception {
        String dirverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        //读取类路径下的配置文件
        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(in);

        dirverClass= properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");


        Driver driver =(Driver)Class.forName(dirverClass).newInstance();
        Properties info = new Properties();
        info.put("user",user);
        info.put("password",password);

        Connection connection = driver.connect(jdbcUrl,info);
        return connection;
    }

    @Test
    public void testGetConnection() throws Exception {
        System.out.println(getConnection());
    }

    @Test
    public void testDriverManager() throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        //1.准备连接数据库的4个字符串
        //驱动的全类名
        String dirverClass = null;
        //jdbcUrl
        String jdbcUrl = null;
        //user
        String user = null;
        //password
        String password = null;

        //读取类路径下的配置文件
        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(in);

        dirverClass= properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        //2.加载数据库驱动程序（注册驱动）
//        DriverManager.registerDriver((Driver) Class.forName(dirverClass).newInstance());
        Class.forName(dirverClass);
        //3.通过DriverManager的getConnection(jdbcUrl，user，password)方法获取
        Connection connection = DriverManager.getConnection(jdbcUrl,user,password);
        System.out.println(connection);
    }
    /**
     * 通过jdbc向指定的数据表中插入一条记录
     */

    public void update(String sql){
        Connection conn = null;
        Statement statement = null;
        try {
            conn = JDBCTools.getConnection();
            statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCTools.release(statement,conn);
        }
    }


    @Test
    public void testStatement() throws Exception {
        //1.获取数据库连接
        Connection con = null;
        Statement statement = null;
        try {
            con = getConnection();
            //2.准备插入的sql语句
            String sql= "INSERT INTO customers(`name`,Location,Email,CardID)" +
                        " VALUES ('The Shy','IG','ig666','1003')";
            String sqlDel = "delete from customers where id = 1";
            String sqlUpdate = "update customers set Name = 'ming' where ID = 2";

            //3.执行插入
            //1）获取操作SQL语句的Statement对象:调用Connection的createStatement方法
            statement = con.createStatement();

            //2）调用Statement对象的executeUpdate(sql) 执行SQL语句进行插入
            statement.executeUpdate(sqlUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.关闭Statement对象
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {

                //5.关闭连接
                if (con != null) {
                    con.close();
                }
            }
        }

    }

    /**
     * ResultSet:结果集，封装了使用JDBC进行查询的结果
     * 1.调用Statement对象的executeQuery(sql)方法可以得到结果集
     * 2.ResultSet返回的实际上就是一张数据表，有一个指针指向数据表的第一样的前面，可以调用next()方法检测下一行是否有效，相当于hasnext()和nexy()方法的结合体
     * 3.当指针对位到一行时，可以通过调用getXxx(index)或getXxx(column)
     */
    @Test
    public void test(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            con = getConnection();
            statement = con.createStatement();
            String sql = "select * from customers";
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                String name = resultSet.getString(2);
                System.out.println(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testResultSet(){
        //获取id=5 的记录并打印
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //1.获取Connection
            con = JDBCTools.getConnection();
            //2.获取Statement
            statement = con.createStatement();

            //3.准备Sql
            String search = "sun";
            String sql = "select ID,name,location,email,CardID,Birth from customers  ";
            String sql1 = "select * from stringchar where  stringChar like '% sun %'";

//            String sql1 = "select * from stringchar where  stringChar like '%'|| search ||'%'";
            //4.执行查询
            resultSet = statement.executeQuery(sql1);
            //5.处理ResultSet
            while (resultSet.next()){
//                int id = resultSet.getInt(1);
//                String name = resultSet.getString("name");
//                String location = resultSet.getString("location");
//                String emil = resultSet.getString(4);
//                int CardId = resultSet.getInt(5);
//                Date date = resultSet.getDate(6);
                String result = resultSet.getString(1);
//                System.out.print(id);
//                System.out.print(name);
//                System.out.print(location);
//                System.out.print(emil);
//                System.out.print(CardId);
//                System.out.println(date);
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.关闭数据库资源
            JDBCTools.release(resultSet,statement,con);
        }


    }

//    public static void addNewStudents2(Student student){
//        String sql = "insert into students (flowid,type,idcard,examcard,studentname,location,grade)values(?,?,?,?,?,?,?)";
//        JDBCTools.update(sql,student.getFlowId(),student.getType(),student.getIdCard(),student.getExamCard(),
//                student.getStudentName(),student.getLocation(),student.getGrade());
//    }

//    public void addNewStudent(Student student){
//        String sql = "insert into students values("+student.getType()+",'"
//                +student.getIdCard()+"','"
//                +student.getExamCard()+"','"
//                +student.getStudentName()+"','"
//                +student.getLocation()+"',"
//                +student.getGrade()+") ";
//        System.out.println(sql);
//        JDBCTools.update(sql);
//    }
//
//    public static void main(String[] args) {
//        Student student = (Student) getStudentFromConsole();
//        addNewStudents2(student);
//    }


    /**
     * 从控制台输入学生的信息
     * @return
     */
    public static Student getStudentFromConsole() {

        Scanner in = new Scanner(System.in);
        Student student = new Student();
        System.out.print("请输入flowID：");
        student.setFlowId(in.nextInt());
        System.out.print("请输入考试类型（4/6）：");
        student.setType(in.nextInt());
        System.out.print("请输入身份证号码：");
        student.setIdCard(in.next());
        System.out.print("请输入准考证号：");
        student.setExamCard(in.next());
        System.out.print("请输入考生姓名：");
        student.setStudentName(in.next());
        System.out.print("请输入地址：");
        student.setLocation(in.next());
        System.out.print("请输入分数：");
        student.setGrade(in.nextInt());

        return student;
    }


    @Test
    public void testGetStudent(){
        //1.得到查询的类型
        int searchType = getSearchTypeFromConsole();

        Student student = searchStudent(searchType);

        printStudents(student);

    }

    /**
     * 打印学生的信息
     * @param student
     */
    private void printStudents(Student student) {
        if (student != null){
            System.out.println(student);
        }else {
            System.out.println("查无此人！");
        }
    }

    /**
     * 具体的查询信息
     * @param searchType 1或者2
     * @return
     */
    private Student searchStudent(int searchType) {
        Scanner scanner =  new Scanner(System.in);
        String sql = "select * from students where ";
        //1.根据输入的提示用户输入信息
            //1.1若searchType为1则输入身份证号码，若为2，提示输入准考证号
        //2.根据searchType确定sql
        if (searchType ==1){
            System.out.print("请输入准考证号：");
            String examCard = scanner.next();
            sql = sql + "examcard = '"+examCard+"'";
        }else {
            System.out.print("请输入身份证号：");
            String IDCard = scanner.next();
            sql = sql + "idcard = '"+ IDCard +"'";
        }

        //3.执行查询
        Student student = getStudent(sql);

        //4.若存在查询结果，把查询结果封装为一个student对象
        return null;
    }

    private Student getStudent(String sql) {
        Student s = null;
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            con = JDBCTools.getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()){
                s = new Student(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getInt(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(resultSet,statement,con);
        }
        return s;
    }

    /**
     * 1：身份证，2：准考证号
     * @return
     */
    private int getSearchTypeFromConsole() {
        System.out.print("请输入查询类型:1.按身份证号码查询  2：按准考证号查询");
        Scanner in = new Scanner(System.in);
        int type = in.nextInt();
        if (!(type == 1 || type ==2)){
            System.out.println("输入有误，请重新输入");
            throw new RuntimeException();
        }
        return type;
    }


    @Test
    public void testPreparedStatement(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {

             connection = JDBCTools.getConnection();
             String sql = "insert into customers(name,email,birth) value(?,?,?)";
             preparedStatement = connection.prepareStatement(sql);
             preparedStatement.setNString(1,"wss");   //setNString 和 setString 不知道有什么区别
             preparedStatement.setString(2,"sakhdakh@163.com");
             preparedStatement.setDate(3,new Date(new java.util.Date().getTime()));
             preparedStatement.executeLargeUpdate();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(null,preparedStatement,connection);
        }


    }
    @Test
    public void testSQLInjection1(){

        String username = "a' or password = ";
        String password = " or '1' = '1";
//        String sql = "select * from usertest where username= 'a' or password = ' and password = ' or '1' = '1';
        String sql= "SELECT * from usertest  where username = ? and password = ? ";

//        String sql = "SELECT * FROM users WHERE username = '" + username
//                + "' AND " + "password = '" + password + "'";
        System.out.println(sql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败");
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(resultSet,preparedStatement, connection);
        }
    }

    @Test
    public void testSQLInjection(){
        String username = "a' or password = ";
        String password = " or '1' = '1";
//        String sql = "select * from usertest where username= '"+ username +
//                "' and" + "passwrod = '"+ password+"'";
        String sql= "SELECT * from usertest  where username = '"+username+"' and password = '"+password+"'";

//        String sql = "SELECT * FROM users WHERE username = '" + username
//                + "' AND " + "password = '" + password + "'";
        System.out.println(sql);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCTools.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败");
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(resultSet,statement, connection);
        }
    }
}
