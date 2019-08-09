// ITokenOperator.aidl
package zhang.xiulu.com.binderlistenermap;

// Declare any non-default types here with import statements
import zhang.xiulu.com.binderlistenermap.ITokenListener;

interface ITokenOperator {

    void addTokenListener(ITokenListener listener);

    void removeTokenListener(ITokenListener listener);
}
