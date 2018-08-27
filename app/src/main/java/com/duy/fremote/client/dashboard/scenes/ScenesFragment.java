package com.duy.fremote.client.dashboard.scenes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duy.fremote.R;
import com.duy.fremote.client.dashboard.devices.DevicesFragment;
import com.duy.fremote.client.database.DatabaseConstants;
import com.duy.fremote.client.database.DatabaseManager;
import com.duy.fremote.models.ResultCallback;
import com.duy.fremote.models.scenes.IScene;
import com.duy.fremote.models.scenes.Scene;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScenesFragment extends Fragment {
    public static final String PACKAGE_NAME = "com.duy.fremote.client.dashboard.scenes";
    public static final String ACTION_UPDATE_SCENES = PACKAGE_NAME + ".ACTION_UPDATE_SCENES";

    private static final int RC_REQUEST_PERMISSION = 123;
    private TextView mTxtTemperature;
    private TextView mTxtHumidity;
    private TextView mTxtLocation;

    private DatabaseReference mScenesDatabase;
    private DatabaseReference mDatabase;
    private DatabaseManager mDatabaseManager;

    private ValueEventListener mTemperatureValueWatcher;
    private ValueEventListener mHumidityValueWatcher;
    private ScenesAdapter mScenesAdapter;
    private BroadcastReceiver mUpdateScenesListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fetchScenes();
        }
    };

    public static ScenesFragment newInstance() {
        Bundle args = new Bundle();
        ScenesFragment fragment = new ScenesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseManager = new DatabaseManager(firebaseUser);
        mDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(firebaseUser.getUid());
        mScenesDatabase = mDatabase.child(DatabaseConstants.KEY_SCENES);

        //scenes database listener
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_SCENES);
        getContext().registerReceiver(mUpdateScenesListener, filter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scenes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTxtHumidity = view.findViewById(R.id.txt_humidity);
        mTxtTemperature = view.findViewById(R.id.txt_temperature);
        mTxtLocation = view.findViewById(R.id.txt_location);

        final RecyclerView scenesView = view.findViewById(R.id.scenes_list);
        scenesView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScenesAdapter = new ScenesAdapter(getContext(), mDatabaseManager);
        mScenesAdapter.setOnSceneClickListener(new ResultCallback<IScene>() {
            @Override
            public void onSuccess(IScene result) {
                showDialogConfirmSelectScene(result);
            }

            @Override
            public void onFailure(@Nullable Exception e) {

            }
        });
        scenesView.setAdapter(mScenesAdapter);

        fetchTemperatureAndHumidity();
        fetchLocation();
        fetchScenes();
    }

    private void showDialogConfirmSelectScene(final IScene result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.apply_scene);
        builder.setMessage(result.getName());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //auto update value at server mode
                mDatabaseManager.applyScene(result);
                notifyUpdateDevicesStatus();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void notifyUpdateDevicesStatus() {
        Intent intent = new Intent();
        intent.setAction(DevicesFragment.ACTION_UPDATE_STATUS);
        getContext().sendBroadcast(intent);
    }

    private void fetchScenes() {
        if (getContext() == null || !isAdded()) {
            return;
        }
        mScenesAdapter.clearAll();
        mScenesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Scene scene = snapshot.getValue(Scene.class);
                        if (mScenesAdapter != null) {
                            mScenesAdapter.add(scene);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, RC_REQUEST_PERMISSION);
            return;
        }

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.getResult() != null) {
                            new FetchAddressTask(getContext(), task.getResult(), mTxtLocation).execute();
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_REQUEST_PERMISSION) {
            fetchLocation();
        }
    }

    private void fetchTemperatureAndHumidity() {
        mTemperatureValueWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mTxtTemperature != null) {
                    try {
                        Float value = dataSnapshot.getValue(Float.class);
                        if (value == null) {
                            mTxtTemperature.setText(R.string.not_available);
                        } else {
                            mTxtTemperature.setText(String.valueOf(value));
                            mTxtTemperature.append("°C");
                        }
                    } catch (Exception e) {
                        mTxtTemperature.setText(R.string.not_available);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mHumidityValueWatcher = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mTxtHumidity != null) {
                    try {
                        Float value = dataSnapshot.getValue(Float.class);
                        if (value == null) {
                            mTxtHumidity.setText(R.string.not_available);
                        } else {
                            mTxtHumidity.setText(String.valueOf(value));
                            mTxtHumidity.append("%");
                        }
                    } catch (Exception e) {
                        mTxtHumidity.setText(R.string.not_available);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child(DatabaseConstants.KEY_HUMIDITY).addValueEventListener(mHumidityValueWatcher);
        mDatabase.child(DatabaseConstants.KEY_TEMPERATURE).addValueEventListener(mTemperatureValueWatcher);
    }

    @Override
    public void onDestroyView() {
        getContext().unregisterReceiver(mUpdateScenesListener);
        mDatabase.removeEventListener(mHumidityValueWatcher);
        mDatabase.removeEventListener(mTemperatureValueWatcher);
        super.onDestroyView();

    }

    @SuppressLint("StaticFieldLeak")
    private static class FetchAddressTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final Location location;
        private TextView txtAddress;

        FetchAddressTask(Context context, Location location, TextView txtAddress) {
            this.context = context;
            this.location = location;
            this.txtAddress = txtAddress;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses == null || addresses.isEmpty()) {
                    return null;
                }
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                return TextUtils.join("\n", addressFragments);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!isCancelled() && s != null) {
                txtAddress.setText(s);
            }
        }
    }

}
