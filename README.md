[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/approuter/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/approuter/_latestVersion)

# AppRouter
An Android app router, used to consolidate all app routes into one class definition. It's invention was sparked by an app I worked on with Josh Shepard where he implemented a Router for mutliple navigations.


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:approuter:1.0.6'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>approuter</artifactId>
  <version>1.0.6</version>
  <type>pom</type>
</dependency>
```


## How do I use it?

### Supported parameter types
* RouteMixin - an intent interceptor, returning false in onNewIntent will cut the intent short, this is good for auth failure or conditional routes
* Bundle - your standard bundle of arguments to pass in
* (Action) Uri - the uri to include in an action, only used for Action based intents

### @Route
Route is an annotation that supports three values
* A class extending android.support.v4.app.Fragment
* A class extending android.support.v7.app.AppCompatActivity
* A url

_example_
```java
@Route( Activity = WebViewActivity.class, Fragment = WebViewFragment.class, Url = "http://domain.com" )
void GoToDomain( RouteMixin mixin, Bundle extras );
```

### @ForResult
ForResult calls activity for a result, and needs a callingActivity as a parameter to its methods
* A class extending android.support.v7.app.AppCompatActivity
* RequestCode - the request code to be sent to the activityForResult

_example_
```java
int REQUEST_CODE = 0x0003;

@ForResult( Activity = CameraActivity.class, RequestCode = REQUEST_CODE )
void GetImageFromCamera( AppCompatActivity callingActivity );
```

### @Action
An action based intent, can have optional parameter Uri
* Action some String defining an action
* (Optional) RequestCode, in case your action is also returning a result - (if so a callingActivity must be provided)

_example_
```java
@Action( Action = Intent.ACTION_VIEW )
void GoToDeviceBrowser( Uri uri );

@Action( Action = Intent.ACTION_GET_CONTENT, RequestCode = REQUEST_CODE )
void GetAPicture( RouteMixin mixin );  // mixin can be used to setType
```


## Creating a router
Create a new interface class that extends AppRouter and declare your navigation methods. Any methods not decorated with annotations will be ignored.

```java
public interface Router extends AppRouter {

    int REQUEST_CODE = 0x0003;

    @Action( Action = Intent.ACTION_VIEW )
    void ViewInBrowser( Uri uri );

    // an activity hosting a webview
    @Route( Activity = WebActivity.class, Url = "https://github.com/icarus-sullivan/approuter")
    void VisitWebPage( RouteMixin mixin, Bundle extras );

    @Route( Activity = About.class, Fragment = AboutFragment.class )
    void AboutUs();

    // must include an AppCompatActivity as a parameter
    @ForResult( Activity = CameraActivity.class, RequestCode = REQUEST_CODE )
    void GetImageFromCamera( AppCompatActivity callingApp );

}
```

### Instantiating your Router
To instantiate your app, and make your routes global we need to create a class that extends Application.

_example_
```java
public class App extends Application {

    private static Router route;

    @Override
    public void onCreate() {
        super.onCreate();

        // create our router here, must use application context!!!
        route = new RouteBuilder(getApplicationContext()).build(Router.class);
    }

    public static Router getRouter() { return route; }
}
```

## Usage
If all goes well you should be able to call your Router anywhere in the app.

_example_
```java
// example route
App.getRouter().VisitWebPage(new RouteMixin() {
        @Override
        public boolean onNewIntent(Intent intent) {
            // modify our intent as we pass through so we can re-use the webActivity
            // adding an extra title, and overriding url
            intent.putExtra(Router.TITLE_EXTRA, Router.WIKIPEDIA_LINK );
            intent.putExtra(Router.EXTRA_URL, Router.WIKIPEDIA_LINK );
            return true;
        }
    }, null );


...

// view about us
App.getRouter().AboutUs();
```

### Final Thoughts
Have Suggestions or extra features you want in AppRouter? Let me know chrissullivan.dev@gmail.com. Or log an issues on github to let me know.