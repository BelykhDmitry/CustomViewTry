package dmitrybelykh.study.customviewtry;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class AnimationHelper {
    /**
     * Hides view with alpha
     * @param view View to animate
     * @param endAction Can be null
     */
    public static void hideWithAnimation(View view, Runnable endAction) {
        view.animate().cancel();
        view.animate().setDuration(250)
                .setListener(null)
                .alpha(0f)
                .withEndAction(endAction)
                .setInterpolator(new AccelerateInterpolator()).start();
    }

    /**
     * Shows view with alpha
     * @param view View to animate
     * @param endAction Can be null
     */
    public static void showWithAnimation(View view, Runnable endAction) {
        view.animate().cancel();
        view.animate().setDuration(250)
                .setListener(null)
                .alpha(1f)
                .withEndAction(endAction)
                .setInterpolator(new AccelerateInterpolator()).start();
    }
}
