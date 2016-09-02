package icarus.io.router.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import icarus.io.router.api.AppRouter;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class RoutableActivity extends AppCompatActivity {

    public enum Meta {
        FRAGMENT,
        WEB
    }

    public Bundle getExtras() {
        return getIntent().getExtras();
    }

    public Object getRouteMeta( Meta meta ) {
        String metaRoute = getIntent().getStringExtra(AppRouter.META_ROUTE);
        switch( meta ) {
            case FRAGMENT:
                if( metaRoute != null ) {
                    try {
                        Class<?> c = Class.forName( metaRoute );
                        Fragment extraFragment = (Fragment) c.newInstance();
                        extraFragment.setArguments( getExtras()  );
                        return extraFragment;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case WEB:
                return metaRoute;
        }
        return null;
    }

}
