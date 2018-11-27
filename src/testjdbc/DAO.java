package testjdbc;

import org.apache.commons.beanutils.BeanUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAO {

    //insert,update,delete包含在其中

    public void update(String sql, Object ... args){
        Connection conne = null;
        PreparedStatement preparedStatement = null;
        try {
            conne = JDBCTools.getConnection();
            preparedStatement = conne.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);

            }
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(null,preparedStatement,conne);
        }

    }


    //查询一条记录，返回对应的对象
    public <T> T get(Class<T> clazz, String sql, Object ... args){

        T entry = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            //1.获取connection
            connection = JDBCTools.getConnection();
            //2.获取preparedStatement
            preparedStatement = connection.prepareStatement(sql);

            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }
            //4.进行查询，获得ResultSet
            resultSet = preparedStatement.executeQuery();
            //5.若ResultSet中有记录，准备一个Map<String，Object>
            if (resultSet.next()){
                Map<String,Object> values = new HashMap<>();

                //6.得到ResultSetMetaData
                //7.处理ResultSet，把指针移动到下一位
                ResultSetMetaData rsmd = resultSet.getMetaData();

                //8.由rsmd对象得到结果中有多少列
                int columnCount = rsmd.getColumnCount();

                //9.由rsmd得到每一列的别名，得到具体每一列的值
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    Object columnValue = resultSet.getObject(i+1);
                    //10.填充map对象
                    values.put(columnLabel,columnValue);
                }
                //11.用反射创建Class对应的对象
                Object object = clazz.newInstance();
                //12.遍历map对象，用反射填充对象的属性值：属性名为map中的key，值为map中的value
                for (Map.Entry<String,Object> entity:values.entrySet()
                ) {
                    String propetyName = entity.getKey();
                    Object value = entity.getValue();

//                    ReflectionUtils.setFieldValue(object,propetyName,value);
                    BeanUtils.setProperty(entity,propetyName,value);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(resultSet,preparedStatement,connection);
        }

        return entry;
    }

    //查询多条记录，返回对应的对象集合
    public  <T> List <T> getForList(Class<T> clazz, String sql, Object ... args){

        List <T> list = new ArrayList<>();

        Connection connection =null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            resultSet = preparedStatement.executeQuery();

            List<Map<String,Object>>  values = new ArrayList<>();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            Map<String,Object> map = null;

            while (resultSet.next()){
                map = new HashMap<>();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    Object value = resultSet.getObject(i+1);
                    map.put(columnLabel,value);
                }

                values.add(map);
            }
            Object bean = null;
            if (values.size() > 0){
                for (Map<String,Object> map1:values
                     ) {
                    for (Map.Entry<String,Object> entry:map1.entrySet()){
                        String propertyName = entry.getKey();
                        Object value = entry.getValue();

                        bean = clazz.newInstance();
                        BeanUtils.setProperty(bean,propertyName,value);
                    }
                    values.add(map);
                }
            }
            


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCTools.release(resultSet,preparedStatement,connection);
        }


        return list;
    }

    //返回某条记录的某一个字段的值或一个统计的值（一共有多少条记录）
    public <E> E getForValue(String sql, Object ... args){

        return null;
    }


}
