package com.duy.fremote.client.dashboard.devices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duy.fremote.R;
import com.duy.fremote.client.dashboard.scenes.ScenesFragment;
import com.duy.fremote.client.database.DatabaseManager;
import com.duy.fremote.client.database.IDatabaseManager;
import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.utils.DLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DevicesFragment extends Fragment {
    public static final String PACKAGE_NAME = "com.duy.fremote.client.dashboard.devices";
    public static final String ACTION_UPDATE_STATUS = PACKAGE_NAME + ".ACTION_UPDATE_STATUS";

    private static final String TAG = "DeviceFragment";

    private FirebaseUser mFirebaseUser;
    private RecyclerView mDeviceListView;
    private DevicesAdapter mDevicesAdapter;
    private IDatabaseManager mDatabaseManager;
    private DatabaseReference mDevicesDatabase;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ValueEventListener mValueEventListener;

    private BroadcastReceiver mUpdateDevicesListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fetchDevicesList();
        }
    };

    public static DevicesFragment newInstance() {

        Bundle args = new Bundle();

        DevicesFragment fragment = new DevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseManager = new DatabaseManager(mFirebaseUser);
        getContext().registerReceiver(mUpdateDevicesListener, new IntentFilter(ACTION_UPDATE_STATUS));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_devices_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_device:
                showDialogAddDevice();
                break;
            case R.id.action_create_scene:
                showDialogCreateNewScene();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceListView = view.findViewById(R.id.device_list_view);
        mDeviceListView.setHasFixedSize(false);
        mDeviceListView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        mDevicesAdapter = new DevicesAdapter(getContext(), mDatabaseManager);
        mDeviceListView.setAdapter(mDevicesAdapter);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        fetchDevicesList();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDevicesList();
            }
        });
    }


    /**
     * Get data from database
     */
    private void fetchDevicesList() {
        //need clear all data before update
        mDevicesAdapter.clearAll();

        mSwipeRefreshLayout.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mDevicesDatabase = databaseReference.child(mFirebaseUser.getUid()).child("devices");
        if (mValueEventListener != null) {
            mDevicesDatabase.removeEventListener(mValueEventListener);
        }
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (DLog.DEBUG)
                    DLog.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                List<DigitalDevice> devicesList = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    try {

                        DigitalDevice device = item.getValue(DigitalDevice.class);
                        devicesList.add(device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                displayData(devicesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDevicesDatabase.addListenerForSingleValueEvent(mValueEventListener);
    }

    private void displayData(@NonNull List<DigitalDevice> devicesList) {
        mDevicesAdapter.clearAll();
        for (DigitalDevice iArduinoDevice : devicesList) {
            mDevicesAdapter.add(iArduinoDevice);
        }

        //hide progress
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDevicesDatabase.removeEventListener(mValueEventListener);
        getContext().unregisterReceiver(mUpdateDevicesListener);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    private void showDialogCreateNewScene() {
//        AddSceneDialog dialog = new AddSceneDialog(getContext(),
//                new ResultCallback<IScene>() {
//                    @Override
//                    public void onSuccess(IScene scene) {
//                        scene.setDevicesStatus(mDevicesAdapter.getDevices());
//                        mDatabaseManager.addScene(scene, new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                notifyUpdateScenes();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(@Nullable Exception e) {
//
//                    }
//                });
//        dialog.show();
    }

    private void notifyUpdateScenes() {
        if (getContext() == null || !isAdded()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(ScenesFragment.ACTION_UPDATE_SCENES);
        getContext().sendBroadcast(intent);
    }

    private void showDialogAddDevice() {
//        AddDeviceDialog addDeviceDialog = new AddDeviceDialog(getContext(),
//                new ResultCallback<IArduinoDevice>() {
//                    @Override
//                    public void onSuccess(IArduinoDevice result) {
//                        mDatabaseManager.addDevice(result);
//                        fetchDevicesList();
//                    }
//
//                    @Override
//                    public void onFailure(@Nullable Exception e) {
//
//                    }
//                });
//        addDeviceDialog.show();
    }
}
