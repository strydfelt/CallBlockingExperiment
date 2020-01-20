package sg.govtech.callscreener;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {

    private MyService myService;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    private static final int RC_CALLS = 126;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            mBound = true;
            setBindStatus("Bound!");
            setServiceStatus("Available");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
            mBound = false;
            setBindStatus("Unbound!");
            setServiceStatus("Unavailable");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, MyService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

        findViewById(R.id.bind_service).setOnClickListener( (View v) -> {
            getPermissions();
        });

//        createCallServiceConnection();
        getDefaultDialer();
    }

    @AfterPermissionGranted(RC_CALLS)
    private void getPermissions(){

        String[] perms = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MANAGE_OWN_CALLS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };

        if (EasyPermissions.hasPermissions(this, perms)) {
            createCallServiceConnection();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale),
                    RC_CALLS, perms);
        }
    }

    private void createCallServiceConnection(){

        Intent mCallServiceIntent = new Intent(this, MyCallScreeningService.class);
        ServiceConnection mServiceConnection = new ServiceConnection(){

            public static final String TAG = "MServiceConn";

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // iBinder is an instance of CallScreeningService.CallScreenBinder
                // CallScreenBinder is an inner class present inside CallScreenService
                Log.i(TAG, "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i(TAG, "onServiceDisconnected");
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.i(TAG, "onBindingDied");
            }
        };
        bindService(mCallServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setBindStatus(String status){
        ((TextView)findViewById(R.id.bind_status)).setText(status);
    }

    private void setServiceStatus(String status){
        ((TextView)findViewById(R.id.service_status)).setText(status);
    }


    private void getDefaultDialer(){
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, 234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MA", " req: " + requestCode + " res: " + resultCode );

        switch(resultCode){
            case Activity.RESULT_OK:
                Log.d("MA", "OK");
                break;

            case Activity.RESULT_CANCELED:
                Log.d("MA", "Not OK");
                break;


        }
    }
}
