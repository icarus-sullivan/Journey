package icarus.io.router.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import icarus.io.router.R;
import icarus.io.router.RouterApp;

public class MainActivity extends RoutableActivity implements View.OnClickListener {

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button routeFragment = (Button) findViewById(R.id.route_fragment);
        Button routeWeb = (Button) findViewById(R.id.route_webview);
        routeFragment.setOnClickListener(this);
        routeWeb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.route_fragment:
                RouterApp.getRoutes().SimpleFragmentRoute();
                break;
            case R.id.route_webview:
                RouterApp.getRoutes().GoogleWebRoute();
                break;
        }
    }

}
