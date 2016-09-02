package icarus.io.router.api;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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

    String META_ROUTE = "_meta";

    /**
     *
     * Route defaults
     */
    @SuppressWarnings("all")
    class DummyFragment extends Fragment {}
    class DummyActivity extends AppCompatActivity {}
    String EMPTY_URL = "";

}
