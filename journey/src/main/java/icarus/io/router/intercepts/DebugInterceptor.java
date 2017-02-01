package icarus.io.router.intercepts;

import icarus.io.router.api.IntentCreator;

/**
 * Created by chris on 10/23/16.
 */
public interface DebugInterceptor extends RouteInterceptor {

    void onIntentCreated(IntentCreator creator);

}