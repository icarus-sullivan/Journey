package icarus.io.router.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import icarus.io.router.api.AppRouter;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class RoutableActivity extends AppCompatActivity {

    public String getUrl() {
        return getExtras().getString(AppRouter.EMPTY_URL);
    }

    public Fragment getFragment() {
        String fragmentClass = getExtras().getString(AppRouter.EXTRA_FRAGMENT);
        return Fragment.instantiate(this, fragmentClass);
    }

    public Bundle getExtras() {
        return getIntent().getExtras();
    }

}
