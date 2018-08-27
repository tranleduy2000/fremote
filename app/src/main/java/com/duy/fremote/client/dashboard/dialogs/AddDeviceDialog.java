package com.duy.fremote.client.dashboard.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.EditText;

import com.duy.fremote.R;
import com.duy.fremote.models.ResultCallback;
import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.models.devices.IArduinoDevice;


public class AddDeviceDialog extends AppCompatDialog {
    private EditText editPin;
    private EditText editDeviceName;
    private ResultCallback<IArduinoDevice> onCompleteListener;

    public AddDeviceDialog(Context context, ResultCallback<IArduinoDevice> onCompleteListener) {
        super(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_device);
        editDeviceName = findViewById(R.id.edit_device_name);
        editPin = findViewById(R.id.edit_device_pin);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidData()) {
                    try {
                        int pin = Integer.parseInt(editPin.getText().toString());
                        String name = editDeviceName.getText().toString();
                        DigitalDevice digitalDevice = new DigitalDevice(name, pin);
                        if (onCompleteListener != null) {
                            onCompleteListener.onSuccess(digitalDevice);
                        }
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private boolean isValidData() {
        if (editPin.getText().toString().trim().isEmpty()) {
            editPin.setError(getContext().getString(R.string.enter_device_pin));
            return false;
        }
        return true;
    }

    public interface OnCompleteListener {
        void onComplete(IArduinoDevice device);
    }
}
