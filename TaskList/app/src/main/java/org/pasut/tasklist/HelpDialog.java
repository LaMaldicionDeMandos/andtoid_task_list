package org.pasut.tasklist;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by marcelo on 22/03/14.
 */
public class HelpDialog extends Dialog {

    public HelpDialog(Context context, int layout, OnDismissListener onDissmiss) {
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
    }
}
