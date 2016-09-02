package icarus.io.router.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

import icarus.io.router.annotation.ForResult;
import icarus.io.router.annotation.Route;
import icarus.io.router.mixin.RouteMixin;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class RouteBuilder implements InvocationHandler {

    private Context appContext;

    // vectors should prevent against ConcurrentModificationExceptions
    private Vector<RouteMixin> mixins = new Vector<>();

    public RouteBuilder( Context context ) {
        this.appContext = context;
    }

    public RouteBuilder( Context context, RouteMixin mixin ) {
        this.appContext = context;
        if( !mixins.contains( mixin ) ) {
            mixins.add( mixin );
        }
    }

    public RouteBuilder registerMixins( RouteMixin ... register ) {
        for( RouteMixin mixin : register ) {
            if( !mixins.contains( mixin ) ) {
                mixins.add( mixin );
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends AppRouter> T build( Class<T> clz ) {
        if( clz == null ) return null;

        return (T) Proxy.newProxyInstance( clz.getClassLoader(),
                new Class[]{ clz }, this );
    }

    /**
     * Ignore regular invoccation, the invoke method is simply to extract route data
     * and allow us to generate the Routes pseudo-dynamically.
     *
     * Future updates should/might have the ability to add Mixins to this method in order for
     * more 'risky' devs to modify generated intents before they are started.
     *
     * @return null -- always
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if( method.isAnnotationPresent( Route.class ) ) {
            handleRoute( method.getAnnotation( Route.class ), args );
        }

        if( method.isAnnotationPresent(ForResult.class) ) {
            handleForResult( method.getAnnotation( ForResult.class ), args );
        }

        return null;
    }

    private void handleRoute( Route route, Object[] args ) {
        // check for valid activity class
        if( route.Activity().isAssignableFrom( AppRouter.DummyActivity.class ) ) {
            return;
        }

        Intent intent = new Intent( appContext, route.Activity() );
        // check if this is a URL based route
        if( !route.Url().equals( AppRouter.EMPTY_URL ) ) {
            intent.putExtra( AppRouter.META_ROUTE, route.Url() );
        }

        // check if this is a fragment based route
        if( !route.Fragment().isAssignableFrom( AppRouter.DummyFragment.class ) ) {
            intent.putExtra( AppRouter.META_ROUTE, route.Fragment().getCanonicalName() );
        }

        // pass intent to mix-ins for extra data modification
        for (RouteMixin mixin : mixins) {
            mixin.onNewIntent( intent );
        }

        // check for RouteMixin parameter
        if( args != null && args.length > 0 ) {
            RouteMixin rm = null;
            for( Object o : args ) {
                if( o instanceof RouteMixin ) {
                    rm = (RouteMixin) o;
                }

                if( o instanceof Bundle ) {
                    intent.putExtras( (Bundle)o );
                }
            }

            if( rm != null ) {
                rm.onNewIntent( intent );
            }
        }

        // start our new activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        appContext.startActivity(intent);
    }

    private void handleForResult( ForResult result, Object[] args ) {
        AppCompatActivity callingActivity = null;
        RouteMixin mixin = null;
        Bundle bundle = null;

        for( Object o : args ) {
            if( o instanceof AppCompatActivity ) {
                callingActivity = (AppCompatActivity) o;
            }

            if( o instanceof RouteMixin ) {
                mixin = (RouteMixin) o;
            }

            if( o instanceof Bundle )  {
                bundle = (Bundle) o;
            }
        }

        // no calling activity, can't actually call this
        if( callingActivity == null ) return;

        // create intent to pass arguments to it
        Intent intent = new Intent( callingActivity, result.Activity() );

        // invoke mixin
        if( mixin != null ) {
            mixin.onNewIntent( intent );
        }

        // add extras
        if( bundle != null ) {
            intent.putExtras( bundle );
        }

        // start er' up
        callingActivity.startActivityForResult( intent, result.RequestCode() );
    }

}
