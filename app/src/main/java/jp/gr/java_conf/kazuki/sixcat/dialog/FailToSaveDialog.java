package jp.gr.java_conf.kazuki.sixcat.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import jp.gr.java_conf.kazuki.sixcat.ProfileListActivity;
import jp.gr.java_conf.kazuki.sixcat.R;

/**
 * Created by kazuki on 2015/01/30.
 */
public class FailToSaveDialog extends DialogFragment {

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.delete_confirm_dialog_title));
        builder.setMessage(message);

        builder.setNegativeButton("OK", null);
        return builder.create();
    }
}