package com.duy.fremote.client.dashboard.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.duy.fremote.R;
import com.duy.fremote.client.database.DatabaseConstants;
import com.duy.fremote.models.ResultCallback;
import com.duy.fremote.models.scenes.IScene;
import com.duy.fremote.models.scenes.Scene;


public class AddSceneDialog extends AppCompatDialog {
    private EditText editDescription;
    private EditText editName;
    private Spinner iconList;

    private ResultCallback<IScene> onCompleteListener;

    public AddSceneDialog(Context context, ResultCallback<IScene> onCompleteListener) {
        super(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_scene);
        editName = findViewById(R.id.edit_name);
        editDescription = findViewById(R.id.edit_description);
        iconList = findViewById(R.id.spinner_icons);
        iconList.setAdapter(new IconAdapter(getContext(), -1, DatabaseConstants.SCENE_ICON_IDS));

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
                        Scene scene = new Scene();
                        scene.setName(editName.getText().toString());
                        scene.setDescription(editDescription.getText().toString());
                        if (onCompleteListener != null) {
                            onCompleteListener.onSuccess(scene);
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
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError(getContext().getString(R.string.enter_name));
            return false;
        }
        return true;
    }
}
