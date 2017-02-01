

[ ![Download](https://api.bintray.com/packages/icarus-sullivan/maven/journey/images/download.svg) ](https://bintray.com/icarus-sullivan/maven/journey/_latestVersion)

# Journey
An Android app router, used to consolidate all app routes into one class definition. It's invention was sparked by an app I worked on with Josh Shepard where he implemented a Router for mutliple navigations.


## How to get it?

### Android Studio
If your using Android Studio, add this to your apps build.gradle

```javascript
dependencies {
    compile 'com.github.icarus-sullivan:journey:1.1.3'
}
```

### Maven
If your using maven you can add this to your project.

```xml
<dependency>
  <groupId>com.github.icarus-sullivan</groupId>
  <artifactId>journey</artifactId>
  <version>1.1.3</version>
  <type>pom</type>
</dependency>
```


## Getting Started with Annotations

### Annotation @Route
Route is an annotation that supports multiple values
* A class extending android.support.v4.app.Fragment
* A class extending android.support.v7.app.AppCompatActivity
* A url {String}
* An Intent Action {String}
* A Request Code

_example_
```java
public static final String githubPage = "https://github.com/icarus-sullivan/Journey";

...

@Route( Activity = WebActivity.class, Url = githubPage )
void GoToGitPage( RouteExtraInterceptor interceptor );
```

### Annotation @Extra
In case you want to add some extras to your intent on-the-fly you can parameterize the arguments of your
router to accept extras. The value given to @Extra will be used the key-value to be retrieved in your intent.

_example_
```java

// intent extra key
public static final String IDS = "ids";

...

@Route(Activity = MainActivity.class)
void MainActivityWithExtras(@Extra(IDS) int[] ids );
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


### Annotation @Conditions
Conditions is an annotation can that be used to pass an array of Strings during an intent and can be viewed with the interceptor RouteConditionInterceptor to check for extra string conditions.

_example_
```java

public static final String AUTH = "NeedsAuth";

@Conditions({ AUTH })
@Route( Activity = WebActivity.class, Url = githubPage )
void GoToGitPage( RouteConditionInterceptor interceptor );
...

Router.navigateTo().GoToGitPage(new RouteConditionInterceptor() {
	@Override
	 public boolean onRouteExtras(Intent intent, String[] conditions) {
	     // example of RouteExtras
	     for( String condition : conditions ) {
	         if( AUTH.equalsIgnoreCase( condition ) ) {
				// check auth and return false if no auth exists
	         }
	     }

	     return true;
	 }
	});
```

### Activities for Result
If you are calling an activity for a result, you must provide a calling activity, uri, and a requestCode.

_example_
```java
public static final int REQUEST_VIEW = 0x0002;

...

@Route( Action = Intent.ACTION_GET_CONTENT, RequestCode = REQUEST_VIEW )
void GetContent(AppCompatActivity callingAct, Uri uri );
```


## Creating a router
Create a new class with and inner interface class and declare your navigation methods within the interface. Any methods not decorated with annotations will be ignored.

```java
public class Router {

	// our router instance
    public static Instance navigateTo;

	...

	// convenience method for creating the router
    public static void create( Context app ) {
        navigateTo = new Journey.Builder( app )
                .create( Instance.class );
    }

	// Our actual router interface here
    public interface Instance {

        @Route( Action = Intent.ACTION_VIEW )
        void ViewInBrowser(Uri uri );

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
		Router.create( getApplicationContext() );
    }
}
```

## Usage
If all goes well you should be able to call your Router anywhere in the app.

_example_
```java
// example route
Router.navigateTo.VisitWebPage(new RouteInterceptor() {
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

// view about us ativity -- can pass in null values for expected parameters
Router.navigateTo.MainActivity(null);
```

### Final Thoughts
Have Suggestions or extra features you want in AppRouter? Let me know chrissullivan.dev@gmail.com. Or log an issues on github to let me know.