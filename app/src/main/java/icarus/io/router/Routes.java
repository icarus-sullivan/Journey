package icarus.io.router;

import icarus.io.router.activities.FragmentActivity;
import icarus.io.router.activities.MainActivity;
import icarus.io.router.activities.WebActivity;
import icarus.io.router.annotation.Route;
import icarus.io.router.fragments.SimpleFragment;
import icarus.io.router.api.AppRouter;

/**
 * Created by chrissullivan on 5/10/16.
 *
 * Because this class extends AppRouter we are able to make a Proxy of it, and make calls
 * to it under the hood (invoke_special) op_code. Acting as close dynamic class as we possibly can
 * within Java.
 */
public interface Routes extends AppRouter {

    @Route( activity = FragmentActivity.class,
            fragment = SimpleFragment.class,
            extras = { "foo", "bar", "caz" } )
    void SimpleFragmentRoute();

    @Route( activity = WebActivity.class,
            url = "https://www.google.com/?gws_rd=ssl",
            title = "Google Search" )
    void GoogleWebRoute();

    @Route( activity = MainActivity.class, extras = {"tab1"})
    void TabOne();

}
