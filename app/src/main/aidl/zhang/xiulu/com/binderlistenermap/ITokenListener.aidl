// ITokenListener.aidl
package zhang.xiulu.com.binderlistenermap;

// Declare any non-default types here with import statements

interface ITokenListener {

    void onSuccess(String message);

    void onFailed(String message);

    int getAppId();
}
