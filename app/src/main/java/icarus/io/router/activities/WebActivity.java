package icarus.io.router.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import icarus.io.router.R;

/**
 * Created by chrissullivan on 5/10/16.
 */
public class WebActivity extends RoutableActivity {

    private WebView web;
    private File webCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // allow the webview to click 'home' for navigation
        if( getSupportActionBar() != null ) {
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initWebView();
    }

    public boolean canCache() {
        webCache = new File( getCacheDir(), ".web" );

        boolean canCache = false;
        if( !webCache.exists() ) {
            canCache = webCache.mkdirs();
        } else {
            canCache = true;
        }
        return canCache;
    }

    @SuppressWarnings("all")
    private void initWebView() {
        boolean canCache = canCache();

        web = (WebView) findViewById(R.id.webHost);
        web.setWebViewClient( new WebViewClient() );

        WebSettings settings = web.getSettings();
        if( settings != null ) {
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setAllowContentAccess(true);
            settings.setAppCacheEnabled(true);

            if (canCache) {
                settings.setAppCachePath(webCache.getAbsolutePath());
            }
        }

        String metaRoute = (String) getRouteMeta( Meta.WEB );
        web.loadUrl( metaRoute );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case android.R.id.home:
                onBackPressed();            // farm navigation to onBackPressed
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if( web.canGoBack() ) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
