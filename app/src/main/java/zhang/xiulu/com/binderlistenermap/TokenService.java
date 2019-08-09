package zhang.xiulu.com.binderlistenermap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService extends Service {
    private static final String TAG = "TokenService";

    private ConcurrentHashMap<Integer, HashMap<IBinder, TokenWatcher>> mTokenListeners;


    @Override
    public void onCreate() {
        super.onCreate();
        mTokenListeners = new ConcurrentHashMap<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TokenStub();
    }


    private final class TokenWatcher implements IBinder.DeathRecipient {
        private int appId;
        private ITokenListener listener;

        public TokenWatcher(ITokenListener listener) {
            this.listener = listener;
            try {
                this.appId = listener.getAppId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onSuccess(String message) {
            if (listener != null) {
                try {
                    listener.onSuccess(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onFailed(String message) {
            if (listener != null) {
                try {
                    listener.onFailed(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void binderDied() {
            mTokenListeners.remove(appId);
        }
    }


    public class TokenStub extends ITokenOperator.Stub {

        @Override
        public void addTokenListener(ITokenListener listener) throws RemoteException {
            if (listener == null) return;
            final long origId = Binder.clearCallingIdentity();
            Integer appId = listener.getAppId();
            if (!mTokenListeners.containsKey(appId)) {
                mTokenListeners.put(appId, new HashMap<IBinder, TokenWatcher>());
            }
            TokenWatcher tokenWatcher = new TokenWatcher(listener);
            IBinder binder = listener.asBinder();
            binder.linkToDeath(tokenWatcher, 0);
            mTokenListeners.get(appId).put(binder, tokenWatcher);
            Binder.restoreCallingIdentity(origId);

            HashMap<IBinder, TokenWatcher> listeners = mTokenListeners.get(appId);
            if (listeners != null) {
                for (IBinder binderClient:listeners.keySet()) {
                    listeners.get(binderClient).onSuccess("hahhahah");
                    listeners.get(binderClient).onFailed("It's a pity! I am failed!");
                }
            }
            Log.d(TAG, "add length:" + mTokenListeners.get(appId).size());
        }


        @Override
        public void removeTokenListener(ITokenListener listener) throws RemoteException {
            if (listener == null) return;
            final long origId = Binder.clearCallingIdentity();
            Integer appId = listener.getAppId();
            if (mTokenListeners.containsKey(appId)) {
                IBinder binder = listener.asBinder();
                TokenWatcher tokenWatcher = mTokenListeners.get(appId).get(binder);
                if (tokenWatcher != null) {
                    binder.unlinkToDeath(tokenWatcher, 0);
                    mTokenListeners.get(appId).remove(binder);
                }
            }
            Binder.restoreCallingIdentity(origId);
            Log.d(TAG, "remove length:" + mTokenListeners.get(appId).size());

        }
    }


}
