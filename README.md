[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/approuter/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/approuter/_latestVersion)

# AppRouter
An Android app router, used to consolidate all app routes into one class definition. It's invention was sparked by an app I worked on with Josh Shepard where he implemented a Router for mutliple navigations.


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:approuter:1.0.4'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>approuter</artifactId>
  <version>1.0.4</version>
  <type>pom</type>
</dependency>
```


## How do I use it?

### Creating a router
Create a new interface class that extends AppRouter.
* Add some method declarations that describe the app route.
* Add Route annotations to describe the contents of the Route.
* Add ForResult annotations for startActivityForResult cases

```java
public interface Router extends AppRouter {

    int REQUEST_CODE = 0x0003;

    // A generic activity route
    @Route( Activity = MainActivity.class )
    void MainPage();

    // an activity hosting a fragment
    @Route( Activity = FragActivity.class, Fragment = ExampleFragment.class )
    void FragmentRoute( @Nullable Bundle extras );

    // an activity hosting a webview
    @Route( Activity = WebActivity.class, Url = "https://github.com/icarus-sullivan/approuter")
    void VisitWebPage();

    // must include an AppCompatActivity as a parameter, without this the activity for result
    // does not know where to return the result to
    @ForResult( Activity = CameraActivity.class, RequestCode = REQUEST_CODE )
    void GetImageFromCamera( AppCompatActivity callingApp );

}
```

#### Route Options + Optional Params
* Activity - a mandatory class that must extend from AppCompatActivity
* Fragment - an optional fragment class extending support.v4.Fragment
* Url - an optional URL that can be passed to the route activity
* @param Bundle - a bundle of extra data to pass to the intent
* @param RouteMixin - a mixin that can allow modification of intents before triggered

### ForResult + Optional Params
* Activity - the activity requesting a result
* RequestCode - the normal request code
* @param Bundle
* @param RouteMixin


## RouteBuilder Usage
RouteBuilder is a class that constructs our Router interface. You must use it as the Router constructor in order for it to work. RouteBuilder comes with two constructors, one with a mixin option, and one without.

#### Without Mixins
* Create a subclass of Application
* Add a static instance of the Router interface we made earlier
* Override the method onCreate() and construct the Router with RouterBuilder
* Create a getter method to allow app-wide classes access to our Router.
```java
public class App extends Application {

    private static Router router;

    @Override
    public void onCreate() {
        super.onCreate();

        router = new RouteBuilder(getApplicationContext()).build(Router.class);
    }

    public static Router getRouter() {
        return router;
    }

}
```

#### With Mixins
In case developers want to hook into the Intent creation process before the created routes are started, we can construct the Router above with an optional parameter in the RouteBuilder called a RouteMixin. Additionally we can add multiple mixins as long as the RouteBuilder class has not been built into our Router class.

```java
// modify our Router interface
public interface Router extends AppRouter {
    ...
    String PROFILE_KEY = "profile_key";
}

// an example of a mixin --
//              allowing modification of routes via Intent access
router = new RouteBuilder(getApplicationContext(),
        new RouteMixin() {
            @Override
            public void onNewIntent(Intent intent) {
                // pass our user profile id to another activity/fragment
                intent.putExtra( Router.PROFILE_KEY, "fae532g12d");
            }
        })
        .build(Router.class);
```
An example of a RouteBuidler with multiple mixins

```java
RouteBuilder rb = new RouteBuilder( getApplicationContext());
rb.registerMixins(
    new RouteMixin() {
        @Override
        public void onNewIntent(Intent intent) {
            // mixin 1
        }
    },
    new RouteMixin() {
        @Override
        public void onNewIntent(Intent intent) {
            // mixin 2
        }
});

router = rb.build(Router.class);
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
	    Bundle extraData = getExtras();
    }
}
```

## Regular Activities
If you don't extend RoutableActivity you can still retrieve our route information like you would any other data passed into an Intent.

```java
public class RegularActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // meta route will contain url or a fragment class canonical name
        String url = getIntent().getStringExtra(AppRouter.META_ROUTE);

        // if its a fragment the canonical name will be given
        String fragmentClass = getIntent().getStringExtra(AppRouter.META_ROUTE);

        // you can get it construct the fragment like this
        Fragment fragment = Fragment.instantiate( getBaseContext(), fragmentClass );

        // if they were passed as a parameter
        Bundle extras = getIntent().getExtras();

    }
}
```

## Navigation to Routes

Assuming Router was built statically within the App class like the above example. We can now navigate to any activity in the app
from wherever we are in the app.

```
// getRouter statically, and navigate to MainPage
App.getRouter().MainPage();

// or launch the Source Code page
App.getRouter().VisitWebPage();
```
You can now set a RouteMixin as a parameter in your route files and provide a mixin when calling a route. This will pass you
a reference of the created intent before it is launched but at a navigation-invoccation level.

```
// Router modifications
public class Router {

    @Route( activity = MyActivity.class )
    void MainPage( RouteMixin routeMixing );

}

// class level invoccation
App.getRouter().MainPage( new RouteMixin() {
    @Override
    public void onNewIntent(Intent route ) {
        // add data to created Intent before its called
    }
});


```

## Want a more granular mixin?



## Class Specific Routes
You can create routes any way you wish, making routes specific to activities, fragments, or any POJO. Here is an example using an Activity.
```java
public class RegularActivity extends AppCompatActivity {

    private Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        router = (Router) new RouteBuilder( getBaseContext(), new RouteMixin() {
            @Override
            public void onNewIntent(Intent intent) {
                // mixin data if desired
            }
        }).build( Router.class );
    }
}
```

### Final Thoughts
Have Suggestions or extra features you want in AppRouter? Let me know chrissullivan.dev@gmail.com. Or log an issues on github to let me know.