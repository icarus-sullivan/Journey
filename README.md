

[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/journey/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/journey/_latestVersion)

# Journey
An Android app router, used to consolidate all app routes into one class definition. It's invention was sparked by an app I worked on with Josh Shepard where he implemented a Router for mutliple navigations.


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:journey:1.1.1'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>journey</artifactId>
  <version>1.1.1</version>
  <type>pom</type>
</dependency>
```


## Getting Started with Annotations

### Annotation @Extra
In case you want to add some extras to your intent on-the-fly you can parameterize the arguments of your
router to accept extras. The value given to @Extra will be used the key-value to be retrieved in your intent.

_example_
```java
@Route( Activity = WebActivity.class )
void WebActivity( @Extra("FALLBACK_URL") String fallback, @Extra("IDS") int[] vals, RouteIntercepter intercept );
```

Here is a list of accepted Intent arguments.

```java
 String.class
 String[].class
 boolean.class
 boolean[].class
 byte.class
 byte[].class
 char.class
 char[].class
 CharSequence[].class
 CharSequence.class
 double.class
 double[].class
 float.class
 float[].class
 int.class
 int[].class
 long.class
 long[].class
 Parcelable.class
 Parcelable[].class
 Serializable.class
 short.class
 short[].class
 Bundle.class

```

#### Additional supported parameter types
* RouteInterceptor - an intent interceptor, returning false in onNewIntent will cut the intent short, this is good for auth failure or conditional routes
* Bundle - your standard bundle of arguments to pass in
* (Action) Uri - the uri to include in an action, only used for Action based intents

### Annotation @Route
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

### Annotation @Extras
Extras is an annotation can that be used to pass in extras constants into your navigation
* An integer array

_example_
```java
@Extras({ State.NEEDS_AUTH.ordinal(), State.CLOSE_ON_FAILURE.ordinal()})
@Route( Activity = BouncyCastleActivity.class )
void BouncyCastle( RouteInterceptor intercept );

...

App.getRouter().BouncyCastle( new RouteIntercepter() {
	   @Override
	    public boolean onRoute(Intent intent) {
	        int[] list = intent.getIntArrayExtra(Journey.EXTRA_LIST);
	        for( int item : list ) {
				if( item == State.NEEDS_AUTH.ordinal() &&
							!User.isAuthenticated() ) {
					return false;
				}
			}
		    return true;
	    }
	});
```

### Activities for Result
If you are calling an activity for a result, you must provide a calling activity and a requestCode.

_example_
```java

int REQUEST_CODE = 0x0003;

// Not forResult
@Route( Action = Intent.ACTION_VIEW )
void GoToDeviceBrowser( Uri uri );

// For Result
@Route( Action = Intent.ACTION_GET_CONTENT, RequestCode = REQUEST_CODE)
void GetAPicture( AppCompatActivity callingActivity );
```


## Creating a router
Create a new interface class and declare your navigation methods. Any methods not decorated with annotations will be ignored.

```java
public interface Router {

    int REQUEST_CODE = 0x0003;

	@Route( Activity = SplashActivity.class )
	void GoToSplash( @Extra("FIRST_TIME") boolean firstLaunch );

    @Route( Action = Intent.ACTION_VIEW )
    void ViewInBrowser( Uri uri );

    // an activity hosting a webview
    @Route( Activity = WebActivity.class, Url = "https://github.com/icarus-sullivan/approuter")
    void VisitWebPage( RouteInterceptor intercept, Bundle extras );

    @Route( Activity = About.class, Fragment = AboutFragment.class )
    void AboutUs();

    // must include an AppCompatActivity as a parameter
    @Route( Activity = CameraActivity.class, RequestCode = REQUEST_CODE )
    void GetImageFromCamera( AppCompatActivity callingApp );


	class Instance {

		static Router inst;

		public static void create( Context app ) {
			// can provide chained RouteInterceptors for all Routes
			inst = new Journey.Builder(app)
                .addInterceptors(new RouteIntercepter() {
                    @Override
                    public boolean onRoute(Intent intent) {
                        // check auth for user
                        return User.isAuthenticated();
                    }
                })
                .create(Router.class);
		}

		public static Router get() {
			return inst;
		}

	}
}
```

### Instantiating your Router
To instantiate your app, and make your routes global we need to create a class that extends Application.

_example_
```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // create our router here, must use application context!!!
		Router.Instance.create( getApplicationContext() );
    }
}
```

## Usage
If all goes well you should be able to call your Router anywhere in the app.

_example_
```java
// example route
Router.Instance.get().VisitWebPage(new RouteInterceptor() {
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