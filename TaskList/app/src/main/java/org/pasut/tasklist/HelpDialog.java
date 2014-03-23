package org.pasut.tasklist;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by marcelo on 22/03/14.
 */
public class HelpDialog extends Dialog {

    public HelpDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.setContentView(R.layout.help);
        final View touch = findViewById(R.id.slider_touch);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.help_slide);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setStartOffset(0);
                touch.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        touch.startAnimation(anim);
    }
}
