package com.duy.fremote.client.dashboard.devices;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.duy.fremote.R;
import com.duy.fremote.client.database.IDatabaseManager;
import com.duy.fremote.models.devices.DigitalDevice;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    private final Context activity;
    private final IDatabaseManager mDeviceManager;
    private List<DigitalDevice> mDevices = new ArrayList<>();

    public DevicesAdapter(Context activity, IDatabaseManager deviceManager) {
        this.activity = activity;
        this.mDeviceManager = deviceManager;
    }

    @NonNull
    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_deivce_digital, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DevicesAdapter.ViewHolder holder, int position) {
        final DigitalDevice device = mDevices.get(position);
        holder.txtName.setText(device.getName());
        holder.statusView.setOn(device.getValue() != 0);
        final OnToggledListener onToggledListener = new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                device.setValue(isOn ? 1 : 0);
                mDeviceManager.updateDevice(device);
            }
        };
        holder.statusView.setOnToggledListener(onToggledListener);
        holder.openMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMenuFor(v, device);
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.statusView.setOn(!holder.statusView.isOn());
                onToggledListener.onSwitched(holder.statusView, holder.statusView.isOn());
            }
        });
    }

    private void displayMenuFor(View view, final DigitalDevice device) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_device_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_device:
                        break;
                    case R.id.action_remove_device:
                        mDeviceManager.removeDevice(device);
                        int index = mDevices.indexOf(device);
                        mDevices.remove(index);
                        notifyItemRemoved(index);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void add(DigitalDevice digitalDevice) {
        mDevices.add(digitalDevice);
        notifyItemInserted(mDevices.size() - 1);
    }

    public void clearAll() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    public List<DigitalDevice> getDevices() {
        return mDevices;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private View openMenuView;
        private LabeledSwitch statusView;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_device_name);
            openMenuView = itemView.findViewById(R.id.btn_more_action);
            statusView = itemView.findViewById(R.id.status_view);
            rootView = itemView.findViewById(R.id.root_view);
        }
    }
}
