package icarus.io.router.mixin;

import android.content.Intent;

/**
 * Created by chris on 5/13/16.
 */
public interface RouteMixin {

    /**
     * Return whether or not you want the intent to launch
     * @param intent the current intent about to be handled
     * @return true if you want the intent to launch, false if not
     */
    boolean onNewIntent( Intent intent );

}
