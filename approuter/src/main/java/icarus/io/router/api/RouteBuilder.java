package icarus.io.router.api;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

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

    public AppRouter build( Class<? extends AppRouter> clz ) {
        if( clz == null ) return null;

        return (AppRouter) Proxy.newProxyInstance( clz.getClassLoader(),
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
        Route route = method.getAnnotation( Route.class );
        if( route != null ) {
            // check for valid activity class
            if( route.activity().isAssignableFrom( AppRouter.DummyActivity.class ) ) {
                return null;
            }

            Intent intent = new Intent( appContext, route.activity() );
            // check if this is a URL based route
            if( !route.url().equals( AppRouter.EMPTY_URL ) ) {
                intent.putExtra( AppRouter.META_ROUTE, route.url() );
            }

            // check if this is a fragment based route
            if( !route.fragment().isAssignableFrom( AppRouter.DummyFragment.class ) ) {
                intent.putExtra( AppRouter.META_ROUTE, route.fragment().getCanonicalName() );
            }

            // check for title
            if( route.title() != null ) {
                intent.putExtra( AppRouter.TITLE, route.title() );
            }

            // add extras if any
            String[] extras = route.extras();
            if( extras.length > 0 ) {
                intent.putExtra( AppRouter.EXTRAS, extras);
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
                }
                
                if( rm != null ) {
                    rm.onNewIntent( intent );
                }
            }

            // start our new activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            appContext.startActivity(intent);
        }
        return null;
    }
}
