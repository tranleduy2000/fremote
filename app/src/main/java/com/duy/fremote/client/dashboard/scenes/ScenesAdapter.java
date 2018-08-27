package com.duy.fremote.client.dashboard.scenes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.duy.fremote.R;
import com.duy.fremote.client.database.DatabaseManager;
import com.duy.fremote.models.ResultCallback;
import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.models.devices.IArduinoDevice;
import com.duy.fremote.models.scenes.IScene;

import java.util.ArrayList;
import java.util.List;

public class ScenesAdapter extends RecyclerView.Adapter<ScenesAdapter.ViewHolder> {
    private ArrayList<IScene> mScenes = new ArrayList<IScene>();
    private Context mContext;
    private DatabaseManager mDatabaseManager;
    @Nullable
    private ResultCallback<IScene> mOnSceneClickListener;

    public ScenesAdapter(Context context, DatabaseManager databaseManager) {
        mContext = context;
        mDatabaseManager = databaseManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_scene, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final IScene scene = mScenes.get(position);
        holder.txtName.setText(scene.getName());
        String description = scene.getDescription();
        if (description == null || description.isEmpty()) {
            description = getGenerateDescriptionFor(scene);
        }
        holder.txtDescription.setText(description);
        holder.btnMoreAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMenuFor(v, scene);
            }
        });

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSceneClickListener != null) {
                    mOnSceneClickListener.onSuccess(scene);
                }
            }
        });
    }


    private void displayMenuFor(View view, final IScene scene) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_device_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_device:
                        // TODO: 24-Aug-18
                        break;
                    case R.id.action_remove_device:
                        mDatabaseManager.removeScene(scene);
                        int index = mScenes.indexOf(scene);
                        mScenes.remove(index);
                        notifyItemRemoved(index);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private String getGenerateDescriptionFor(IScene scene) {
        List<DigitalDevice> devices = scene.getDevicesStatus();
        ArrayList<String> info = new ArrayList<>();
        for (IArduinoDevice device : devices) {
            if (device instanceof DigitalDevice) {
                String status = device.getValue() != 0 ? mContext.getString(R.string.turn_on)
                        : mContext.getString(R.string.turn_off);
                info.add(status + " " + device.getName());
            }
        }
        return TextUtils.join(", ", info);
    }

    public void add(IScene scene) {
        mScenes.add(scene);
        notifyItemInserted(mScenes.size() - 1);
    }

    public void clearAll() {
        mScenes.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mScenes.size();
    }

    public void setOnSceneClickListener(@Nullable ResultCallback<IScene> mOnSceneClickListener) {
        this.mOnSceneClickListener = mOnSceneClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtDescription;
        private ImageView iconView;
        private TextView txtTime;
        private View btnMoreAction;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDescription = itemView.findViewById(R.id.txt_description);
            iconView = itemView.findViewById(R.id.icon);
            txtTime = itemView.findViewById(R.id.txt_time);
            btnMoreAction = itemView.findViewById(R.id.btn_more_action);
            rootView = itemView.findViewById(R.id.root_view);
        }
    }
}