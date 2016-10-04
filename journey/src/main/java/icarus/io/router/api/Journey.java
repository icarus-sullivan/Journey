package icarus.io.router.api;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

import icarus.io.router.annotation.Route;
import icarus.io.router.mixin.RouteIntercepter;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class Journey implements InvocationHandler {

    public static final String EXTRA_URL = "_url_";

    public static final String EXTRA_FRAGMENT = "_fragment_";

    public static final String NO_ACTION = "";

    public static final int NO_REQUEST = -1;

    /**
     * Route defaults
     */
    @SuppressWarnings("all")
    public static final class DummyFragment extends Fragment {}
    public static final class DummyActivity extends AppCompatActivity {}

    public static final String EMPTY_URL = "";

    private Context appContext;
    private Vector<RouteIntercepter> interceptors = new Vector<>();

    private Journey(Context context, Vector<RouteIntercepter> interceptors ) {
        this.appContext = context;
        this.interceptors.addAll( interceptors );
    }

    private void handleRoute(Route route, Object[] args ) {
        IntentCreator creator = new IntentCreator( appContext, args )
                .addInterceptors( interceptors )
                .addRoute(route);
        creator.create();
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

        return null;
    }


    /**
     * A builder for the Journey.Builder innvocation handler
     */
    public static class Builder {

        private Context context;
        private Vector<RouteIntercepter> interceptors = new Vector<>();

        public Builder(Context app) {
            this.context = app;
        }

        public Builder addInterceptors( RouteIntercepter... register ) {
            for( RouteIntercepter mixin : register ) {
                if( !interceptors.contains( mixin ) ) {
                    interceptors.add( mixin );
                }
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T create(Class<T> clz ) {
            if( clz == null ) return null;

            return (T) Proxy.newProxyInstance( clz.getClassLoader(),
                    new Class[]{ clz }, new Journey(context, interceptors) );
        }

    }

}
