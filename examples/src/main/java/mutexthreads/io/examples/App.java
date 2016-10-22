package mutexthreads.io.examples;

import android.app.Application;

/**
 * Created by chris on 10/22/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Router.create( getApplicationContext() );
    }
}
