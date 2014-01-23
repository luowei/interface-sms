package net.oilchem.common.bean;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-25
 * Time: 上午10:15
 * To change this template use File | Settings | File Templates.
 */
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedLogin {
    boolean value() default true;
}
