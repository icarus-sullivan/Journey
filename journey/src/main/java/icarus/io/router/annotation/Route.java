package icarus.io.router.annotation;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import icarus.io.router.api.Journey;

/**
 * Created by chrissullivan on 5/10/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {

    /**
     * The activity the router will try to create an Intent with, default will go to
     * the main app screen
     */
    Class<? extends AppCompatActivity> Activity() default Journey.DummyActivity.class;

    /**
     * Fragment must extend support.v4.Fragment
     */
    Class<? extends Fragment> Fragment() default Journey.DummyFragment.class;

    /**
     * The action to be triggered with the intent
     * @return
     */
    String Action() default Journey.NO_ACTION;

    /**
     * A web url for use in a WebView
     */
    String Url() default Journey.EMPTY_URL;

    /**
     * Used for request activities
     */
    int RequestCode() default Journey.NO_REQUEST;

}
