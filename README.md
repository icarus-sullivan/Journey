[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/approuter/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/approuter/_latestVersion)

# approuter
An Android app router, used to consolidate all app routes into one class definition. 


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:approuter:1.0.1'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>approuter</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```


## How do I use it?

### Creating a router
Create a new interface class that extends AppRouter.
* Add some method declarations that describe the app route.
* Add Route annotations to describe the contents of the Route.

```java
public interface Router extends AppRouter {
    
    // A generic activity route
    @Route( activty = MainActivity.class, title = "Welcome" )
    void MainPage();

    // an activity hosting a fragment
    @Route( activity = FragActivity.class, fragment = ExampleFragment.class, title = "foo")
    void FragmentRoute();

    // an activity hosting a webview
    @Route( activity = WebActivity.class, url = "https://github.com/icarus-sullivan/approuter", title ="Source Code")
    void VisitWebPage();

}
```

#### The full list of Route options:
* title - an optional title ( will default to activity classname )
* activity - a mandatory class that must extend from AppCompatActivity
* fragment - an optional fragment class extending support.v4.Fragment
* url - an optional URL that can be passed to the route activity
* extras - an optional String array of data, passed into the activity intent


## RouteBuilder 
#### Without Mixins
The Router interface we made must usually be implemented by some other class. However, this is where the RouteBuilder comes into play.

* Create a subclass of Application
* Add a static instance of the Router interface we made earlier
* Override the method onCreate() and create the Router with RouterBuilder
* Create a getter method to allow app-wide classes access to our Router.
```
public class App extends Application {

    private static Router router;

    @Override
    public void onCreate() {
        super.onCreate();

        router = (Router) new RouteBuilder(getApplicationContext() ).build(Router.class);
    }

    public static Router getRouter() {
        return router;
    }

}
```

#### With Mixins
In case developers want to hook into the Intent creation process before the created routes are started, we can construct the Router above with an optional parameter in the RouteBuilder called a RouteMixin. Additionally we can add multiple mixins as long as the RouteBuilder class has not been built into our Router class.

```
// an example of a mixin --
//              allowing modification of routes via Intent access
router = (Router) new RouteBuilder(getApplicationContext(), 
        new RouteMixin() {
            @Override
            public void onNewIntent(Intent intent) {
                Log.d("====>", "handleMixinData");
            }
        })
        .build(Router.class);
```

## RoutableActivity
If you want to receive data from the Routes easily you can choose to extend your AppCompatActivities with RoutableActivity instead. The only difference between the two is that RoutableActivity will generate the optional title for you, and adds a couple convenience methods for data retrieval.

Here is an example WebActivity using one of the convenience methods.

```
public class WebActivity extends RoutableActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView web = findViewById(R.id.webHost);
        ... 
        // get route meta data -- in this case WEB
        String metaRoute = (String) getRouteMeta( Meta.WEB );
        web.loadUrl( metaRoute );
    }
 
}
```

Here is an example FragmentActivity.

```
public class FragmentActivity extends RoutableActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        Fragment fragment = (Fragment) getRouteMeta( Meta.FRAGMENT );
        getSupportFragmentManager()
                .beginTransaction()
                .add( R.id.fragment_container, fragment )
                .commit();

		// extra string data from Route annotation
		String[] extraData = getExtras();
    }
}
```