package testjdbc;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class BeanUtilsTest {
    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object object = new Student();
        System.out.println(object);
        BeanUtils.setProperty(object,"idCard","12313123123");
        System.out.println(object);
        Object val = BeanUtils.getProperty(object,"idCard");
        System.out.println(val);
    }
}
