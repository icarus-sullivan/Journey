package icarus.io.router.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import icarus.io.router.R;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class FragmentActivity extends RoutableActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        Fragment fragment = (Fragment) getMetaRoute( Meta.FRAGMENT );
        getSupportFragmentManager()
                .beginTransaction()
                .add( R.id.fragment_container, fragment )
                .commit();
    }
}
