package icarus.io.router.annotation;

import android.support.v4.app.Fragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import icarus.io.router.router.AppRouter;

/**
 * Created by chrissullivan on 5/10/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {

    /**
     * The action the router will try to create an Intent with, default will go to
     * the main app screen
     */
    String action() default AppRouter.MAIN_ACTIVITY;

    /**
     * Optional title
     */
    String title() default AppRouter.DEFAULT_TITLE;

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
