package mutexthreads.io.examples.activities;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.net.MalformedURLException;
import java.net.URL;

import icarus.io.router.activities.RoutableActivity;
import mutexthreads.io.examples.R;
import mutexthreads.io.examples.Router;

/**
 * Created by chris on 6/4/16.
 */
public class WebActivity extends RoutableActivity {

    private WebView web;
    private LinearLayout loadingOverlay;

    private final int MAX_PROGRESS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // best to configure actionbar before contentView
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // pass progress bar to web configuration for page loading UX
        loadingOverlay = (LinearLayout) findViewById(R.id.loading_overlay);
        configureWebView();


        // get our url meta-route and load our page
        web.loadUrl( getUrl() );
    }

    private void configureWebView() {

        // assign our webview client, and configure our settings
        web = (WebView) findViewById(R.id.web);
        if( web != null ) {
            web.setWebViewClient( new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl( url );
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    // truncate the url for the title -- only show the host
                    try {
                        setTitle( new URL(url).getHost() );
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    ObjectAnimator.ofFloat(loadingOverlay, "translationY", -loadingOverlay.getHeight(), 0)
                            .setDuration(300L)
                            .start();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    ObjectAnimator.ofFloat(loadingOverlay, "translationY", 0, -loadingOverlay.getHeight())
                            .setDuration(300L)
                            .start();
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }
            });

            WebSettings settings = web.getSettings();
            if( settings != null ) {
                settings.setJavaScriptEnabled(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setAllowContentAccess(true);
                settings.setAppCacheEnabled(true);
            }
        }
    }

    /**
     * Make sure if we can traverse back in web page to do so, otherwise notify
     * incoming request to handle navigation natively
     *
     * @return false if webpage navigation occured, else true
     */
    private boolean navigatedInWebPage() {
        if( web != null && web.canGoBack() ) {
            web.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case android.R.id.home:
                if( !navigatedInWebPage() ) {
                    Router.navigateTo().MainActivityWithExtras( new int[]{1, 2, 3} );
                    finish();
                }
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if( !navigatedInWebPage() ) {
            super.onBackPressed();
        }
    }

}
