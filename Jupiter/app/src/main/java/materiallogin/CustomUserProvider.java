package materiallogin;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

public class CustomUserProvider implements LCChatProfileProvider {

    private static CustomUserProvider sUserProvider;

    public synchronized static CustomUserProvider getInstance(){
        if(null == sUserProvider){
            sUserProvider = new CustomUserProvider();
        }
        return sUserProvider;
    }

    private CustomUserProvider() {
    }

    private static List<LCChatKitUser> partUsers = new ArrayList<LCChatKitUser>();

    static {
        partUsers.add(new LCChatKitUser("lisiyu16@mails.tsinghua.edu.cn", "楚虫", "https://lc-gluttony.s3.amazonaws.com/pfwsHNbdElTm/s30ZERQFEcJd234q63v9thn9DNdy1KFii4x72k3v.jpg"));
        partUsers.add(new LCChatKitUser("lisiyu201695@gmail.com", "楚虫", "https://lc-gluttony.s3.amazonaws.com/pfwsHNbdElTm/s30ZERQFEcJd234q63v9thn9DNdy1KFii4x72k3v.jpg"));
        partUsers.add(new LCChatKitUser("wuxs16@mails.tsinghua.edu.cn", "wxs", "https://lc-gluttony.s3.amazonaws.com/pfwsHNbdElTm/s30ZERQFEcJd234q63v9thn9DNdy1KFii4x72k3v.jpg"));
        partUsers.add(new LCChatKitUser("chen-yn17@mails.tsinghua.edu.cn", "陈语凝", "https://lc-gluttony.s3.amazonaws.com/pfwsHNbdElTm/s30ZERQFEcJd234q63v9thn9DNdy1KFii4x72k3v.jpg"));
    }

    @Override
    public void fetchProfiles(List<String> userIdList, LCChatProfilesCallBack profilesCallBack) {
        List<LCChatKitUser> userList = new ArrayList<LCChatKitUser>();
        for (String userId : userIdList) {
            for (LCChatKitUser user : partUsers) {
                if (user.getUserId().equals(userId)) {
                    userList.add(user);
                    break;
                }
            }
        }
        profilesCallBack.done(userList, null);
    }

    @Override
    public List<LCChatKitUser> getAllUsers() {
        return partUsers;
    }
}
