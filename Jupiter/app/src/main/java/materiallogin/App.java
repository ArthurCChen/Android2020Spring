package materiallogin;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import cn.bmob.v3.Bmob;
import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.im.AVIMOptions;

import cn.leancloud.push.PushService;
import materiallogin.CustomUserProvider;


public class App extends Application {

    @Override
    // bmob初始化
    public void onCreate() {
        Context sContext;

        super.onCreate();
//        Bmob.initialize(this,"18656acfe1e57560c20094eaaf425968");
        AVOSCloud.initialize(this,
                "pfwsHNbdElTmkBxq8TJ3vSnQ-MdYXbMMI",
                "7W0RtWNJkful7NQiBvTpB8HP",
                "https://console.leancloud.app");
//        AVIMOptions.getGlobalOptions().setDisableAutoLogin4Push(true);
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
//        Bmob.initialize(this, "3c7d0da1ed306e604f6be4b0958f606c");

        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        LCChatKit.getInstance().init(getApplicationContext(),
                "pfwsHNbdElTmkBxq8TJ3vSnQ-MdYXbMMI",
                "7W0RtWNJkful7NQiBvTpB8HP",

                "");                  //国际版不用域名，空着即可，用控制台的链接也不会出bug

                "https://console.leancloud.app");//国际版不用域名，我查到论坛有人用这个访问控制台
        PushService.setDefaultPushCallback(this, MainActivity.class);
        PushService.setAutoWakeUp(true);

        sContext = getApplicationContext();

        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
