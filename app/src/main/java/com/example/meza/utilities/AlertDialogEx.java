package com.example.meza.utilities;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.meza.R;

public class AlertDialogEx extends AppCompatDialogFragment {

    private EditText editTextOldPassword;
    private EditText editTextnewPassword;
    private EditText editTextConfirmPassword;

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_change_password, null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldpassword = editTextOldPassword.getText().toString();
                        String newpassword = editTextnewPassword.getText().toString();
                        String confirmpassword = editTextConfirmPassword.getText().toString();

                        listener.applyTexts(oldpassword, newpassword, confirmpassword);
                    }
                });

        editTextOldPassword = view.findViewById(R.id.oldpassword);
        editTextnewPassword = view.findViewById(R.id.newPassword1);
        editTextConfirmPassword = view.findViewById(R.id.newPassword2);

        AlertDialog alert = builder.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        });
        alert.show();

        return alert;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement");
        }

    }

    public interface DialogListener{
        void applyTexts(String oldpw, String newpw, String confirmpw);
    }
}
