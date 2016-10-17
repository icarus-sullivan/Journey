package icarus.io.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chris on 10/17/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Extras {

    /**
     * Can be used to pass in a list of int constants
     *
     * Usage would be.
     *
     * @Extras( { State.AUTH_REQUIRED.oridinal(), State.CLOSE_ON_FAILURE.ordinal() } )
     * void myMethod();
     *
     * and can be retrieved with getExtras().getIntArray( Journey.EXTRA_LIST ) from and activity
     *
     * -- or --
     * and can be retrieved with intent.getIntArrayExtra(Journey.EXTRA_LIST) from an interceptor
     */
    int[] value() default {};

}
