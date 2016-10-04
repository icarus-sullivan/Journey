[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/Journey/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/Journey/_latestVersion)

# Journey
An Android app router, used to consolidate all app routes into one class definition. It's invention was sparked by an app I worked on with Josh Shepard where he implemented a Router for mutliple navigations.


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:approuter:1.0.7'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>approuter</artifactId>
  <version>1.0.7</version>
  <type>pom</type>
</dependency>
```


## How do I use it?

### Supported parameter types
* RouteInterceptor - an intent interceptor, returning false in onNewIntent will cut the intent short, this is good for auth failure or conditional routes
* Bundle - your standard bundle of arguments to pass in
* (Action) Uri - the uri to include in an action, only used for Action based intents

### @Route
Route is an annotation that supports multiple values
* A class extending android.support.v4.app.Fragment
* A class extending android.support.v7.app.AppCompatActivity
* A url {String}
* An Intent Action {String}
* A Request Code

_example_
```java
@Route( Activity = WebViewActivity.class, Fragment = WebViewFragment.class, Url = "http://domain.com" )
void GoToDomain( RouteInterceptor intercept, Bundle extras );
```

### Activities for Result
If you are calling an activity for a result, you must provide a calling activity and a requestCode.

_example_
```java
// Not forResult
@Action( Action = Intent.ACTION_VIEW )
void GoToDeviceBrowser( Uri uri );

@Action( Action = Intent.ACTION_GET_CONTENT, RequestCode = REQUEST_CODE)
void GetAPicture( AppCompatActivity callingAct, RouteIntercepter intercept );  // mixin can be used to setType
```


## Creating a router
Create a new interface class and declare your navigation methods. Any methods not decorated with annotations will be ignored.

```java
public interface Router {

    int REQUEST_CODE = 0x0003;

    @Action( Action = Intent.ACTION_VIEW )
    void ViewInBrowser( Uri uri );

    // an activity hosting a webview
    @Route( Activity = WebActivity.class, Url = "https://github.com/icarus-sullivan/approuter")
    void VisitWebPage( RouteInterceptor intercept, Bundle extras );

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

		// can provide chained RouteInterceptors for all Routes
        route = new Journey.Builder(getApplicationContext())
                .addInterceptors(new RouteIntercepter() {
                    @Override
                    public boolean onRoute(Intent intent) {
                        // check auth for user
                        return User.isAuthenticated();
                    }
                })
                .create(Router.class);
    }

	// Make our Router easier to obtain
    public static Router getRouter() { return route; }
}
```

## Usage
If all goes well you should be able to call your Router anywhere in the app.

_example_
```java
// example route
App.getRouter().VisitWebPage(new RouteInterceptor() {
        @Override
        public boolean onRoute(Intent intent) {
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