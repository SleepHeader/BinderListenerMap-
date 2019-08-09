package zhang.xiulu.com.binderlistenermap;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ITokenOperator mService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ITokenOperator.Stub.asInterface(service);
            addTokenListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, TokenService.class);
        boolean result = bindService(intent, connection, Service.BIND_AUTO_CREATE);
        Log.d(TAG, "service:" + result);

    }

    private ITokenListener listener = new ITokenListener.Stub() {
        @Override
        public void onSuccess(String message) throws RemoteException {
            Log.d(TAG, "onSuccess:" + message);
        }

        @Override
        public void onFailed(String message) throws RemoteException {
            Log.d(TAG, "onFailed:" + message);
        }

        @Override
        public int getAppId() throws RemoteException {
            return 1;
        }

    };

    private void addTokenListener() {

        try {
            if (mService != null){
                mService.addTokenListener(listener);
                mService.removeTokenListener(listener);
                mService.addTokenListener(new ITokenListener.Stub() {
                    @Override
                    public void onSuccess(String message) throws RemoteException {
                        Log.d(TAG, "onSuccess:" + message);
                        Toast.makeText(MainActivity.this, "addTokenSuccess:" + message, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed(String message) throws RemoteException {
                        Log.d(TAG, "onFailed:" + message);
                    }

                    @Override
                    public int getAppId() throws RemoteException {
                        return 0;
                    }
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
