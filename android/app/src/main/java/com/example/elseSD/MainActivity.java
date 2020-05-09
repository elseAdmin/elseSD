package com.example.elseSD;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterFragmentActivity;

public class MainActivity extends FlutterFragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlutterActivityBeacon flutterActivityBeacon = new FlutterActivityBeacon();
        flutterActivityBeacon.onCreate(savedInstanceState);
    }
     */

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Else SD Project";
    private static final int PERMISSIONS_REQUEST_CODE = 1111;
    private MessageListener mMessageListener;
    private Message mMessage;
    private OnFailureListener onFailureListener;
    private Message mActiveMessage;

    private static final int TTL_IN_SECONDS = 1;
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(TTL_IN_SECONDS).build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!havePermissions()) {
            Log.i(TAG, "Requesting permissions needed for this app.");
            requestPermissions();
        }

        onFailureListener = e -> Log.i(TAG,"failed");

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.i("nameSpace : ",message.getNamespace());
                Log.i(TAG, "Found message: " + new String(message.getContent()));
                Log.i(TAG, "Namespace : " + new String(message.getNamespace()));
                Log.i(TAG, "Content : " + new String(message.getContent()));
                Log.i(TAG, "Type : " + new String(message.getType()));
                Log.i(TAG, "Timestamp : " + new String(String.valueOf(message.hashCode())));
            }

            @Override
            public void onLost(Message message) {
                System.out.print(message.getNamespace());
                Log.i(TAG, "Lost sight of message: " + new String(message.getContent()));
            }

            /**
             * Called when the Bluetooth Low Energy (BLE) signal associated with a message changes.
             *
             * This is currently only called for BLE beacon messages.
             *
             * For example, this is called when we see the first BLE advertisement
             * frame associated with a message; or when we see subsequent frames with
             * significantly different received signal strength indicator (RSSI)
             * readings.
             *
             * For more information, see the MessageListener Javadocs.
             */
            @Override
            public void onBleSignalChanged(Message message, BleSignal bleSignal) {
                super.onBleSignalChanged(message, bleSignal);

                Log.i(TAG, "Signal Changed : " + new String(message.getContent()));
                Log.i(TAG, "ble Data : " + String.valueOf(bleSignal.getRssi()));

            }

            /**
             * Called when Nearby's estimate of the distance to a message changes.
             *
             * This is currently only called for BLE beacon messages.
             *
             * For more information, see the MessageListener Javadocs.
             */
          /*  @Override
            public void onDistanceChanged(final Message message, final Distance distance) {
                Log.i(TAG, "Message " + new String(message.getContent()) + " Distance changed, new distance: " + distance);
            }*/
        };

        mMessage = new Message("Hello World".getBytes());
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.DEFAULT)
                .build();

        Nearby.getMessagesClient(this).subscribe(mMessageListener).addOnFailureListener(onFailureListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    protected void onStop() {
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (havePermissions()) {
            buildGoogleApiClient();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // There are states to watch when a user denies permission when presented with
                // the Nearby permission dialog: 1) When the user pressed "Deny", but does not
                // check the "Never ask again" option. In this case, we display a Snackbar which
                // lets the user kick off the permissions flow again. 2) When the user pressed
                // "Deny" and also checked the "Never ask again" option. In this case, the
                // permission dialog will no longer be presented to the user. The user may still
                // want to authorize location and use the app, and we present a Snackbar that
                // directs them to go to Settings where they can grant the location permission.
                if (shouldShowRequestPermissionRationale(permission)) {
                    Log.i(TAG, "Permission denied without 'NEVER ASK AGAIN': " + permission);
                } else {
                    Log.i(TAG, "Permission denied with 'NEVER ASK AGAIN': " + permission);
                }
            } else {
                Log.i(TAG, "Permission granted, building GoogleApiClient");
                buildGoogleApiClient();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended. Error code: " + i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        // Nearby.Messages.subscribe(...) requires a connected GoogleApiClient. For that reason,
        // we subscribe only once we have confirmation that GoogleApiClient is connected.
        subscribe();
        publish("Main aa gya");
    }

    private synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .build();
        }
    }

    private boolean havePermissions() {
        return ContextCompat.checkSelfPermission(this,     Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }
}
