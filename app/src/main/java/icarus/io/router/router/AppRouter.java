package icarus.io.router.router;

import android.support.v4.app.Fragment;

/**
 * Created by chrissullivan on 5/10/16.
 *
 * Base AppRouter, for use in Proxy Class creation.
 *
 * It includes some common keys we will use during Route creation. It also holds default values
 * for {@link icarus.io.router.annotation.Route} data, and uses this to check for nullability
 * during invoccation calls.
 */
public interface AppRouter {

    String MAIN_ACTIVITY = "android.intent.action.MAIN";
    String FRAGMENT_ACTIVITY = "com.smashingideas.FRAGMENT_VIEW";
    String WEB_ACTIVITY = "com.smashingideas.WEB_VIEW";

    String TITLE = "_title";
    String EXTRAS = "_extras";
    String META_ROUTE = "_meta";


    /**
     *
     * Route defaults
     */
    @SuppressWarnings("all")
    class DummyFragment extends Fragment {}
    String EMPTY_URL = "";
    String DEFAULT_TITLE = "App";       // original.. right?

}
