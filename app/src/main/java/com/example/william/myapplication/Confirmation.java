package com.example.william.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Confirmation {
    private Dialog dialog;
    private Activity activity;

    private TextView confirmationText;
    private Button okButton;
    private Button cancelbutton;

    public interface ConfirmationListener {
        void statusSelected(boolean status); // available listener method
    }

    public Confirmation(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);

        View v = activity.getLayoutInflater().inflate(R.layout.confirmation, null);
        confirmationText = (TextView)v.findViewById(R.id.confirmationText);
        okButton = (Button)v.findViewById(R.id.okButton);
        cancelbutton = (Button)v.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.statusSelected(true); // execute listener method on event
                dialog.dismiss();
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.statusSelected(false);
                dialog.dismiss();
            }
        });

        dialog.setContentView(v);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public Confirmation setStatusListener(ConfirmationListener statusListener) {
        this.statusListener = statusListener;
        return this;
    }

    private ConfirmationListener statusListener;

    // execute last
    public void showDialog() {
        dialog.show();
    }

    // set content text
    public void setConfirmationText(String[] values){
        String _confirmationText = activity.getText(R.string.confirm).toString(); // 0
        String _yesText = activity.getText(R.string.yes).toString(); // 1
        String _noText = activity.getText(R.string.no).toString(); // 2

        if( values != null ){
            if( values[0] != null ){
                _confirmationText = values[0];
            }

            if( values[1] != null ){
                _yesText = values[1];
            }

            if( values[2] != null ){
                _noText = values[2];
            }
        }

        confirmationText.setText( _confirmationText );
        okButton.setText(_yesText);
        cancelbutton.setText(_noText);
    }

}
