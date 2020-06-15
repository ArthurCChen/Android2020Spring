package materiallogin;

import android.app.Application;

import cn.bmob.v3.Bmob;


public class App extends Application {

    @Override
    // bmob初始化
    public void onCreate() {
        super.onCreate();
//        Bmob.initialize(this,"18656acfe1e57560c20094eaaf425968");
        Bmob.initialize(this, "3c7d0da1ed306e604f6be4b0958f606c");
    }
}
