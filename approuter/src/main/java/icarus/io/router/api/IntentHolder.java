package icarus.io.router.api;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Collection;

import icarus.io.router.mixin.RouteMixin;

/**
 * Created by chris on 9/4/16.
 */
public abstract class IntentHolder<T> {

    /**
     * An intent to launch
     */
    protected Intent mIntent = null;

    /**
     * Interceptor for routes, can kill an intent prematurely
     */
    protected RouteMixin mMixin = null;

    /**
     * Extra arguments passed via a bundle
     */
    protected Bundle mArgs = null;

    /**
     * For normal routes, these can be considered meta routes
     */
    protected String mUrl = null;
    protected String mFragment = null;

    /**
     * For action based intents
     */
    protected Uri mUri = Uri.EMPTY;

    /**
     * For activityForResult
     */
    protected AppCompatActivity mCallingActivity = null;
    protected int mRequestCode = AppRouter.NO_REQUEST;

    public IntentHolder( Object[] args ) {
        getArgs( args );
    }

    public void getArgs( Object[] args ) {
        // ignore null arguments
        if( args == null ) return;

        for( Object o : args ) {
            if( o instanceof Uri ) {
                mUri = (Uri) o;
            }

            if( o instanceof AppCompatActivity ) {
                mCallingActivity = (AppCompatActivity) o;
            }

            if( o instanceof RouteMixin ) {
                mMixin = (RouteMixin) o;
            }

            if( o instanceof Bundle )  {
                mArgs = (Bundle) o;
            }
        }
    }

    abstract void onGatherExtras( T extras );

    abstract void onLaunchIntent();

    /**
     * Calling this will actually launch the intent, after calling onGatherExtras,
     * and onLaunchIntent
     * @param someT the annotation of the route
     * @param mixins an optional collection of mixins from the Router
     */
    public void launch(T someT, Collection<RouteMixin> mixins ) {
        onGatherExtras( someT );

        if( mIntent != null ) {
            if( mFragment != null ) {
                mIntent.putExtra( AppRouter.EXTRA_FRAGMENT, mFragment );
            }

            if( mUrl != null ) {
                mIntent.putExtra( AppRouter.EXTRA_URL, mUrl );
            }

            if( mArgs != null ) {
                mIntent.putExtras( mArgs );
            }

            for( RouteMixin mixin : mixins ) {
                if( !mixin.onNewIntent( mIntent ) ) {
                    return;
                }
            }

            // possibly intercept intent and kill it off if some conditions aren't met
            if( mMixin != null && !mMixin.onNewIntent( mIntent ) ) {
                return;
            }

            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            onLaunchIntent();
        }
    }

}