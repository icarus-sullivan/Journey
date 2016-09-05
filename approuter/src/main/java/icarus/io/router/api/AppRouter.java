package icarus.io.router.api;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chrissullivan on 5/10/16.
 *
 * Base AppRouter, for use in Proxy Class creation.
 *
 * It includes some common keys we will use during Route creation. It also holds default values
 * for {@link icarus.io.router.annotation.Route}, {@link icarus.io.router.annotation.ForResult}
 * and {@link icarus.io.router.annotation.Action} data, and uses this to check for nullability
 * during invoccation calls.
 */
public interface AppRouter {

    String EXTRA_URL = "_url_";

    String EXTRA_FRAGMENT = "_fragment_";

    int NO_REQUEST = -1;

    /**
     * Route defaults
     */
    @SuppressWarnings("all")
    class DummyFragment extends Fragment {}
    class DummyActivity extends AppCompatActivity {}
    String EMPTY_URL = "";

}
