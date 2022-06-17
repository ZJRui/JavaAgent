package agent.timer;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TimingInterceptor {


    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?>callable )throws Exception{

        long start=System.currentTimeMillis();

        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            System.out.println(method + "took" + (System.currentTimeMillis() - start) + " ms");
        }


    }
}
