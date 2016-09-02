package icarus.io.router.annotation;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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
public @interface ForResult {

    /**
     * The activity the router will try to create an Intent wit
     */
    Class<? extends AppCompatActivity> Activity() default AppRouter.DummyActivity.class;

    /**
     * A result number to use in onActivityResult
     */
    int RequestCode() default 2;

}
