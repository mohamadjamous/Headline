package com.news.headline.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.news.headline.R;


public class CustomProgressDialog {

    public static AlertDialog showCustomDialog(Context context, String message, int backgroundColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        // Customize background color
//        dialogView.setBackgroundColor(backgroundColor);

        // Customize progress bar color
//        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
//        progressBar.getIndeterminateDrawable().setColorFilter(progressColor, android.graphics.PorterDuff.Mode.MULTIPLY);

        // Customize message text
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        return dialog;
    }
}
