package icarus.io.router.router;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import icarus.io.router.annotation.Route;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class RouteBuilder implements InvocationHandler {

    private Class<? extends AppRouter> clz;
    private Context appContext;

    public RouteBuilder( Context context, Class<? extends AppRouter> clz ) {
        this.appContext = context;
        this.clz = clz;
    }

    public AppRouter build() {
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
        Intent intent = new Intent( AppRouter.MAIN_ACTIVITY );

        Route route = method.getAnnotation( Route.class );
        if( route != null ) {
            intent = new Intent( route.action() );

            if( !route.url().equals( AppRouter.EMPTY_URL ) ) {
                intent.putExtra( AppRouter.META_ROUTE, route.url() );
            }

            if( !route.fragment().isAssignableFrom( AppRouter.DummyFragment.class ) ) {
                intent.putExtra( AppRouter.META_ROUTE, route.fragment().getCanonicalName() );
            }

            if( route.title() != null ) {
                intent.putExtra( AppRouter.TITLE, route.title() );
            }

            String[] extras = route.extras();
            if( extras.length > 0 ) {
                intent.putExtra( AppRouter.EXTRAS, extras);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
        return null;
    }
}
