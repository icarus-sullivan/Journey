package icarus.io.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chris on 10/5/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Extra {

    /**
     * Can be used to pass extra's the intents
     *
     * Usage would be.
     *
     * void myMethod( @Extra( "KEY_FOR_EXTRA" ) String value );
     */
    String value() default "EXTRA";

}
