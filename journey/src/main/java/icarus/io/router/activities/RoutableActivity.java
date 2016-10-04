package icarus.io.router.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import icarus.io.router.api.Journey;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class RoutableActivity extends AppCompatActivity {

    /**
     * Convenience for getting a url from bundle extras
     * @return a url or null
     */
    public String getUrl() {
        return getExtras().getString(Journey.EXTRA_URL);
    }

    /**
     * Convenience for getting a fragment that was passed in, if one was provided
     * @return a fragment instance or null
     */
    public Fragment getFragment() {
        String fragmentClass = getExtras().getString(Journey.EXTRA_FRAGMENT);

        // null if no fragment found
        if( fragmentClass == null ) return null;
        return Fragment.instantiate(this, fragmentClass);
    }

    /**
     * A convenience to get Bundle extras quickly
     * @return
     */
    public Bundle getExtras() {
        return getIntent().getExtras();
    }

}
