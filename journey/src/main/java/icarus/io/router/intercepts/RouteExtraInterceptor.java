package icarus.io.router.intercepts;

import android.content.Intent;

import icarus.io.router.api.Journey;

/**
 * Created by chris on 10/21/16.
 */
public abstract class RouteExtraInterceptor implements RouteInterceptor {

    public abstract boolean onRouteExtras( Intent intent, String[] extras );

    @Override
    public boolean onRoute(Intent intent) {
        String[] extras = intent.getStringArrayExtra(Journey.EXTRA_LIST);
        if( extras == null ) {
            return true;
        }
        else {
            return onRouteExtras(intent, extras);
        }
    }
}
