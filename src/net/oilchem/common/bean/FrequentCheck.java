package net.oilchem.common.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-3-15
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FrequentCheck {
    boolean value() default true;
}
