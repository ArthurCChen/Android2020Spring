package materiallogin;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;
import cn.leancloud.im.AVIMOptions;


public class App extends Application {

    @Override
    // bmob初始化
    public void onCreate() {
        super.onCreate();
//        Bmob.initialize(this,"18656acfe1e57560c20094eaaf425968");
        AVOSCloud.initialize(this,
                "pfwsHNbdElTmkBxq8TJ3vSnQ-MdYXbMMI",
                "7W0RtWNJkful7NQiBvTpB8HP");
        AVIMOptions.getGlobalOptions().setDisableAutoLogin4Push(true);
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
//        Bmob.initialize(this, "3c7d0da1ed306e604f6be4b0958f606c");
    }
}
