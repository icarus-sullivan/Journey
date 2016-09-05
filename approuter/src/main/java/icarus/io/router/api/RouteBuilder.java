package icarus.io.router.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

import icarus.io.router.annotation.Action;
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

        if( method.isAnnotationPresent(Action.class) ) {
            handleAction( method.getAnnotation( Action.class ), args );
        }

        return null;
    }

    private void handleRoute( Route route, Object[] args ) {
        new IntentHolder<Route>( args ) {
            @Override
            void onGatherExtras(Route extras) {
                if( !extras.Fragment().isAssignableFrom( AppRouter.DummyFragment.class ) ) {
                    mFragment = extras.Fragment().getCanonicalName();
                }

                if( extras.Url() != AppRouter.EMPTY_URL ) {
                    mUrl = extras.Url();
                }

                mIntent = new Intent( appContext, extras.Activity() );
            }

            @Override
            void onLaunchIntent() {
                appContext.startActivity( mIntent );
            }
        }.launch( route, mixins );
    }

    private void handleForResult(final ForResult result, Object[] args ) {
        new IntentHolder<ForResult>( args ) {
            @Override
            void onGatherExtras(ForResult extras) {
                mIntent = new Intent(appContext, extras.Activity());

                mRequestCode = extras.RequestCode();
            }

            @Override
            void onLaunchIntent() {
                if( mCallingActivity != null && mRequestCode != AppRouter.NO_REQUEST ) {
                    mCallingActivity.startActivityForResult(mIntent, mRequestCode);
                }
            }
        }.launch( result, mixins );
    }

    private void handleAction(final Action action, Object[] args ) {
        new IntentHolder<Action>( args ) {
            @Override
            void onGatherExtras(Action extras) {
                mRequestCode = extras.RequestCode();

                if( mUri != null ) {
                    mIntent = new Intent( action.Action(), mUri );
                } else {
                    mIntent = new Intent( action.Action() );
                }
            }

            @Override
            void onLaunchIntent() {
                if( mCallingActivity != null && mRequestCode != AppRouter.NO_REQUEST ) {
                    mCallingActivity.startActivityForResult( mIntent, mRequestCode );
                } else {
                    appContext.startActivity(mIntent);
                }
            }
        }.launch( action, mixins );
    }

}
