package com.duy.fremote.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.duy.fremote.LoginActivity;
import com.duy.fremote.R;
import com.duy.fremote.client.dashboard.DashboardActivity;
import com.duy.fremote.server.services.FRemoteService;
import com.duy.fremote.server.services.IConnectListener;
import com.duy.fremote.server.services.IMessageListener;
import com.duy.fremote.utils.DLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * We need connect bluetooth here and start service
 */
public class ServerActivity extends AppCompatActivity implements IMessageListener, IConnectListener {

    private static final String TAG = "MessageActivity";
    private static final int RC_ENABLE_BLUETOOTH = 102;
    private final Handler mHandler = new Handler();
    private MenuItem mToggleConnect;
    private EditText mEditCommand;
    private RecyclerView mListMessage;
    private MessageAdapter mMessageAdapter;
    @Nullable
    private FRemoteService mFRemoteService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof FRemoteService.FRemoteServiceBinder) {
                mFRemoteService = ((FRemoteService.FRemoteServiceBinder) service).getService();
                ServerActivity.this.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.server_mode);

        startService(new Intent(this, FRemoteService.class));

        initView();
        bindFRemoteService();
    }

    private void bindFRemoteService() {
        Intent intent = new Intent(this, FRemoteService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void onServiceConnected() {
        if (mFRemoteService == null) {
            if (DLog.DEBUG) DLog.d(TAG, "populateMessageView: Service is not connected");
            return;
        }
        mFRemoteService.addMessageListener(this);
        mFRemoteService.setConnectListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mFRemoteService != null) {
            mFRemoteService.removeMessageListener(this);
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private void initView() {
        mListMessage = findViewById(R.id.message_list);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        mListMessage.setLayoutManager(layout);
        mMessageAdapter = new MessageAdapter(this);
        mListMessage.setAdapter(mMessageAdapter);

        mEditCommand = findViewById(R.id.edit_command);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFRemoteService != null) {
                    mFRemoteService.sendCommand(mEditCommand.getText().toString());
                }
            }
        });
    }

    private void connectBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not available!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RC_ENABLE_BLUETOOTH);
            return;
        }
        final int size = bluetoothAdapter.getBondedDevices().size();
        final CharSequence[] deviceNames = new CharSequence[size];
        final BluetoothDevice[] bondedDevices = bluetoothAdapter.getBondedDevices().toArray(new BluetoothDevice[size]);
        for (int i = 0; i < bondedDevices.length; i++) {
            deviceNames[i] = bondedDevices[i].getName() + " - " + bondedDevices[i].getAddress();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_device);
        builder.setSingleChoiceItems(deviceNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BluetoothDevice toBeConnect = bondedDevices[which];
                if (DLog.DEBUG) DLog.d(TAG, "toBeConnect = " + toBeConnect);

                if (mFRemoteService != null) {
                    mFRemoteService.connectBluetoothWith(toBeConnect);
                }
                //update UI
                onNewMessage(new MessageItem(MessageItem.TYPE_OUT, "Connecting " + toBeConnect.getName()));
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                connectBluetooth();
            }
        }
    }

    @WorkerThread
    @Override
    public void onNewMessage(final MessageItem message) {
        if (DLog.DEBUG) DLog.d(TAG, "onNewMessage() called with: message = [" + message + "]");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMessageAdapter != null) {
                    mMessageAdapter.add(message);
                    mListMessage.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_server, menu);
        mToggleConnect = menu.findItem(R.id.action_toggle_connect);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_connect:
                if (mFRemoteService != null) {
                    if (mFRemoteService.isBluetoothConnected()) {
                        mFRemoteService.disconnect();
                    } else {
                        connectBluetooth();
                    }
                }
                break;
            case R.id.action_sign_out:
                signOut();
                break;

            case R.id.action_dashboard:
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        unbindService(mServiceConnection);
        Intent intent = new Intent(this, FRemoteService.class);
        stopService(intent);

        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(this, googleSignInOptions)
                .signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(ServerActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onConnectStatusChange(boolean connect) {
        if (DLog.DEBUG)
            DLog.d(TAG, "onConnectStatusChange() called with: connect = [" + connect + "]");
        if (connect) {
            mToggleConnect.setTitle(R.string.disconnect);
        } else {
            mToggleConnect.setTitle(R.string.connect);
        }

        onNewMessage(new MessageItem(MessageItem.TYPE_IN, "onConnectStatusChange " + connect));
    }
}
