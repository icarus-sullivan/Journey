package mutexthreads.io.examples.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import icarus.io.router.intercepts.RouteExtraInterceptor;
import icarus.io.router.intercepts.RouteInterceptor;
import mutexthreads.io.examples.R;
import mutexthreads.io.examples.Router;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerClicks(
                R.id.githubpage,
                R.id.failure,
                R.id.forresult,
                R.id.intercept,
                R.id.action
        );
    }

    private void registerClicks( int ... args ) {
        for( int arg : args ) {
            findViewById(arg).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.githubpage:
                Router.navigateTo.GoToGitPage(new RouteExtraInterceptor() {
                    @Override
                    public boolean onRouteExtras(Intent intent, String[] extras) {
                        // example of RouteExtras
                        String checkAuth = "NeedsAuth";
                        for( String extra : extras ) {
                            if( extra.equalsIgnoreCase( checkAuth ) ) {
                                Toast.makeText(getBaseContext(), extra, Toast.LENGTH_SHORT).show();
                            }
                        }

                        return true;
                    }
                });
                break;
            case R.id.failure:
                Router.navigateTo.MainActivity(new RouteInterceptor() {
                    @Override
                    public boolean onRoute(Intent intent) {
                        Toast.makeText( getBaseContext(), "intercept caused failure", Toast.LENGTH_SHORT).show();
                        return false;           // stop navigation
                    }
                });
                break;
            case R.id.forresult:
                Router.navigateTo.GetContent( this, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                break;
            case R.id.intercept:
                Router.navigateTo.MainActivity(new RouteInterceptor() {
                    @Override
                    public boolean onRoute(Intent intent) {
                        Toast.makeText(getBaseContext(), "intercept was handled", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                break;
            case R.id.action:
                Router.navigateTo.ViewInBrowser(Uri.parse(Router.githubPage));
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int[] ids = intent.getIntArrayExtra(Router.IDS);
        if( ids != null ) {
            Toast.makeText( getBaseContext(), "onNewIntent w/" + Arrays.toString( ids ), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getBaseContext(), "onNewIntent", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Router.REQUEST_VIEW && resultCode == Activity.RESULT_OK ) {
            Toast.makeText( getBaseContext(), "result code: " + data.getData().toString(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
