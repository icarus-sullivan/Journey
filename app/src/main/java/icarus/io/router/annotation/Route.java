package icarus.io.router.annotation;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import icarus.io.router.api.AppRouter;

/**
 * Created by chrissullivan on 5/10/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {

    /**
     * Optional title
     */
    String title() default AppRouter.DEFAULT_TITLE;

    /**
     * The activity the router will try to create an Intent with, default will go to
     * the main app screen
     */
    Class<? extends AppCompatActivity> activity() default AppRouter.DummyActivity.class;

    /**
     * Fragment must extend support.v4.Fragment
     */
    Class<? extends Fragment> fragment() default AppRouter.DummyFragment.class;

    /**
     * A web url for use in a WebView
     */
    String url() default AppRouter.EMPTY_URL;

    /**
     * Annotations only handle String arrays and not Object arrays, if you want to add any
     * other primitive types, you will need to call Primitive.valueOf( val );
     */
    String[] extras() default {};

}
