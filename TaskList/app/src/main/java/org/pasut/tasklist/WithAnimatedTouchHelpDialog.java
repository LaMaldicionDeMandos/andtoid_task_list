package org.pasut.tasklist;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by marcelo on 25/03/14.
 */
public class WithAnimatedTouchHelpDialog extends HelpDialog {
    public WithAnimatedTouchHelpDialog(Context context, int layout, OnDismissListener onDissmiss, int touchId, int animation, final boolean repeat) {
        super(context, layout, onDissmiss);
        final View touch = findViewById(touchId);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), animation);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (repeat) {
                    animation.setStartOffset(0);
                    touch.startAnimation(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        touch.startAnimation(anim);
    }
}
