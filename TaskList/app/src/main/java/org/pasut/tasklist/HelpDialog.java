package org.pasut.tasklist;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by marcelo on 22/03/14.
 */
public class HelpDialog extends Dialog {
    public static class Builder {
        private final Context context;
        private final int layout;
        private OnDismissListener onDismiss;
        private final List<AnimationPack> animations = Lists.newArrayList();

        public Builder(Context context, int layout) {
            this.context = context;
            this.layout = layout;
        }

        public Builder withDismissListener(OnDismissListener onDismiss) {
            this.onDismiss = onDismiss;
            return this;
        }

        public Builder addAnimation(AnimationPack pack) {
            animations.add(pack);
            return this;
        }

        public Builder addAnimation(int target, int animation, boolean repeat) {
            return addAnimation(new AnimationPack(target, animation, repeat));
        }

        public Builder addAnimation(int target, int animation) {
            return addAnimation(target, animation, false);
        }

        public HelpDialog build() {
            return new HelpDialog(context, layout, onDismiss, animations);
        }

    }

    public static class AnimationPack {
        private final int target;
        private final int animation;
        private final boolean repeat;

        public AnimationPack(int target, int animation, boolean repeat) {
            this.target = target;
            this.animation = animation;
            this.repeat = repeat;
        }

        public AnimationPack(int target, int animation) {
            this(target, animation, false);
        }

        public int getAnimation() {
            return animation;
        }

        public int getTarget() {
            return target;
        }

        public boolean isRepeat() {
            return repeat;
        }
    }
    private HelpDialog(Context context, int layout, OnDismissListener onDissmiss, List<AnimationPack> animations) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.setOnDismissListener(onDissmiss);
        this.setContentView(layout);
        View okButton = checkNotNull(findViewById(R.id.help_ok), "The layout must has a view with name \"R.id.help_ok\"");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        for (AnimationPack pack : animations) {
            addAnimation(pack);
        }
    }

    private void addAnimation(final AnimationPack pack) {
        final Animation anim = AnimationUtils.loadAnimation(getContext(), pack.getAnimation());
        final View target = findViewById(pack.getTarget());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (pack.isRepeat()) {
                    animation.setStartOffset(0);
                    target.startAnimation(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        target.startAnimation(anim);
    }

}
