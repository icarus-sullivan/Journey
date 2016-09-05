package icarus.io.router.annotation;

import android.content.Intent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import icarus.io.router.api.AppRouter;


/**
 * Created by chris on 9/1/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {

    /**
     * The action to be triggered with the intent
     * @return
     */
    String Action() default Intent.ACTION_VIEW;

    /**
     * A result number to use in onActivityResult, if this is for result
     */
    int RequestCode() default AppRouter.NO_REQUEST;

}
