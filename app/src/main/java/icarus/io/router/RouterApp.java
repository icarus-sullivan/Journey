package icarus.io.router;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import icarus.io.router.api.RouteBuilder;
import icarus.io.router.mixin.RouteMixin;

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

        // Keep router alive during app lifecycle
        router = (Routes) new RouteBuilder( getApplicationContext() )
                .registerMixin(new RouteMixin() {
                    @Override
                    public void onNewIntent(Intent intent) {
                        Log.d("====>", "intent: " + intent.getComponent());
                    }
                })
                .build(Routes.class);
    }

    public static Routes getRoutes() {
        return router;
    }

}
