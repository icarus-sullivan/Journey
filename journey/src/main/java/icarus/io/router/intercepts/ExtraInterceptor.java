package icarus.io.router.intercepts;

import android.content.Intent;

import java.util.HashMap;

/**
 * Created by chris on 10/5/16.
 */
public class ExtraInterceptor implements RouteIntercepter {

    private HashMap<String, Object> extras = new HashMap<>();

    public ExtraInterceptor( Object[] args ) {
        if( args != null ) {
            for( Object o : args ) {

            }
        }
    }

    @Override
    public boolean onRoute(Intent intent) {

        return true;
    }
}
