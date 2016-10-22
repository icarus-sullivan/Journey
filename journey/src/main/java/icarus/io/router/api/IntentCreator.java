package icarus.io.router.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Vector;

import icarus.io.router.annotation.Route;
import icarus.io.router.intercepts.RouteInterceptor;

/**
 * Created by chris on 10/3/16.
 */
public class IntentCreator {

    /**
     * Interceptor for routes, can kill an intent prematurely
     */
    protected Vector<RouteInterceptor> intercepts = new Vector<>();

    /**
     * For action based intents
     */
    protected Uri uri = Uri.EMPTY;

    /**
     * An intent to launch
     */
    protected Intent intent = null;

    /**
     * Extra arguments passed via a bundle
     */
    protected Bundle bundle = null;

    /**
     * For activityForResult
     */
    protected AppCompatActivity callingFrom = null;

    /**
     * For Result requests
     */
    protected int requestCode = Journey.NO_REQUEST;

    /**
     * App Context, used for nomral activity navigation
     */
    private Context app;

    public IntentCreator(Context context, Object ... args ) {
        app = context;

        if( args == null ) return;

        for( Object o : args ) {
            if( o == null ) continue;       // ignore null params

            if( o instanceof Uri ) {
                uri = (Uri) o;
            } else

            if( o instanceof AppCompatActivity ) {
                callingFrom = (AppCompatActivity) o;
            }

            if( o instanceof RouteInterceptor) {
                intercepts.add((RouteInterceptor)o);
            }

            if( o instanceof Bundle )  {
                bundle = (Bundle) o;
            }
        }
    }

    public IntentCreator addRoute(Route route) {
        if( !route.Action().equals(Journey.NO_ACTION ) ) {
            intent = new Intent(route.Action());
        }

        if( !route.Activity().isAssignableFrom(Journey.DummyActivity.class) ) {
            intent = new Intent(app, route.Activity());
        }

        if( route.RequestCode() != Journey.NO_REQUEST ) {
            requestCode = route.RequestCode();
        }

        if( bundle != null ) {
            intent.putExtras(bundle);
        }

        if( uri != null ) {
            intent.setData(uri);
        }

        if( !route.Fragment().isAssignableFrom(Journey.DummyFragment.class) ) {
            intent.putExtra(Journey.EXTRA_FRAGMENT, route.Fragment());
        }

        if( !route.Url().equals(Journey.EMPTY_URL) ) {
            intent.putExtra(Journey.EXTRA_URL, route.Url());
        }

        return this;
    }

    public IntentCreator addInterceptor( RouteInterceptor inter ) {
        if( !intercepts.contains( inter ) ) {
            intercepts.add( 0, inter );
        }
        return this;
    }

    public IntentCreator addInterceptors( Vector<RouteInterceptor> interOther ) {
        intercepts.addAll( 0, interOther );
        return this;
    }

    public void create() {
        if( intent == null ) return;

        // make sure we clear top and set new task
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // check if intercepts want to continue
        for( RouteInterceptor intercept : intercepts ) {
            if( !intercept.onRoute( intent ) ) {
               return;
            }
        }

        // for result
        if( requestCode != Journey.NO_REQUEST && callingFrom != null ) {
            callingFrom.startActivityForResult(intent, requestCode);
        }

        // regular
        else {
            app.startActivity( intent );
        }
    }

}
