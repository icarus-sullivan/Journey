package icarus.io.router.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

import icarus.io.router.annotation.Extra;
import icarus.io.router.annotation.Route;
import icarus.io.router.intercepts.RouteIntercepter;

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

    private Journey( Context context, Vector<RouteIntercepter> interceptors ) {
        this.appContext = context;
        this.interceptors.addAll( interceptors );
    }

    private void handleRoute(final Method method, final Object[] args) {
        Route route = method.getAnnotation(Route.class);
        IntentCreator creator = new IntentCreator( appContext, args )
                .addInterceptors( interceptors )
                .addInterceptor(new RouteIntercepter() {
                    @Override
                    public boolean onRoute(Intent intent) {
                        if( args != null ) {
                            Annotation[][] annotations = method.getParameterAnnotations();
                            for( int i = 0; i < args.length; i++ ) {
                                handleAnnotations(intent, annotations[i], args[i]);
                            }
                        }
                        return true;
                    }
                })
                .addRoute(route);
        creator.create();
    }

    /**
     * Check annotations for any Extra's given so we can add them to the intent right
     * away
     *
     * @param intent
     * @param annotations
     * @param arg
     */
    private void handleAnnotations(Intent intent, Annotation[] annotations, Object arg ) {
        Class<?> clz = arg.getClass();

        for( Annotation annotation : annotations ) {
            if( annotation instanceof Extra ) {
                Extra extra = (Extra) annotation;
                String name = extra.value();
                if( clz.isAssignableFrom(String.class)) {
                    intent.putExtra(name, (String)arg);
                }
                else if( clz.isAssignableFrom(String[].class)) {
                    intent.putExtra(name, (String[])arg);
                }
                else if( clz.isAssignableFrom(boolean.class)) {
                    intent.putExtra(name, (boolean)arg);
                }
                else if( clz.isAssignableFrom(boolean[].class)) {
                    intent.putExtra(name, (boolean[])arg);
                }
                else if( clz.isAssignableFrom(byte.class)) {
                    intent.putExtra(name, (byte)arg);
                }
                else if( clz.isAssignableFrom(byte[].class)) {
                    intent.putExtra(name, (byte[])arg);
                }
                else if( clz.isAssignableFrom(char.class)) {
                    intent.putExtra(name, (char)arg);
                }
                else if( clz.isAssignableFrom(char[].class)) {
                    intent.putExtra(name, (char[])arg);
                }
                else if( clz.isAssignableFrom(CharSequence[].class)) {
                    intent.putExtra(name, (CharSequence[])arg);
                }
                else if( clz.isAssignableFrom(CharSequence.class)) {
                    intent.putExtra(name, (CharSequence)arg);
                }
                else if( clz.isAssignableFrom(double.class)) {
                    intent.putExtra(name, (double)arg);
                }
                else if( clz.isAssignableFrom(double[].class)) {
                    intent.putExtra(name, (double[])arg);
                }
                else if( clz.isAssignableFrom(float.class)) {
                    intent.putExtra(name, (float)arg);
                }
                else if( clz.isAssignableFrom(float[].class)) {
                    intent.putExtra(name, (float[])arg);
                }
                else if( clz.isAssignableFrom(int.class)) {
                    intent.putExtra(name, (int)arg);
                }
                else if( clz.isAssignableFrom(int[].class)) {
                    intent.putExtra(name, (int[])arg);
                }
                else if( clz.isAssignableFrom(long.class)) {
                    intent.putExtra(name, (long)arg);
                }
                else if( clz.isAssignableFrom(long[].class)) {
                    intent.putExtra(name, (long[])arg);
                }
                else if( clz.isAssignableFrom(Parcelable.class)) {
                    intent.putExtra(name, (Parcelable)arg);
                }
                else if( clz.isAssignableFrom(Parcelable[].class)) {
                    intent.putExtra(name, (Parcelable[])arg);
                }
                else if( clz.isAssignableFrom(Serializable.class)) {
                    intent.putExtra(name, (Serializable)arg);
                }
                else if( clz.isAssignableFrom(short.class)) {
                    intent.putExtra(name, (short)arg);
                }
                else if( clz.isAssignableFrom(short[].class)) {
                    intent.putExtra(name, (short[])arg);
                }
                else if( clz.isAssignableFrom(Bundle.class)) {
                    intent.putExtra(name, (Bundle)arg);
                }
            }
        }
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
            handleRoute( method, args );
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
