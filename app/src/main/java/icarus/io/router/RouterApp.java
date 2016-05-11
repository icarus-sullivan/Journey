package icarus.io.router;

import android.app.Application;

import icarus.io.router.router.RouteBuilder;

/**
 * Created by chrissullivan on 5/11/16.
 *
 * We need the Routes to stay in memory during the entire lifecycle of the app without being
 * gc'd. For this reason, we will make the Routes static and add convenience methods to retrieve
 * the Routes from anywhere in the app.
 *
 * Additionaly, because we constructed the Routes with
 * the application Context, when we invoke startActivity via Proxy we should always have a valid
 * context.
 */
public class RouterApp extends Application {

    private static Routes router;

    @Override
    public void onCreate() {
        super.onCreate();

        // Keep router alive during app life
        router = (Routes) new RouteBuilder( getApplicationContext(), Routes.class ).build();
    }

    public static Routes getRoutes() {
        return router;
    }

}
