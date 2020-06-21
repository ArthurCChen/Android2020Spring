package materiallogin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thu.qinghuaquan.R;

import java.util.List;

import androidx.core.app.NotificationCompat;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMConversationEventHandler;
import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.AVIMMessageHandler;
import cn.leancloud.im.v2.messages.AVIMTextMessage;

public class MyPush {
    // Java/Android SDK 通过定制自己的对话事件 Handler 处理服务端下发的对话事件通知
    public static class CustomConversationEventHandler extends AVIMConversationEventHandler {
        @Override
        public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {

        }

        @Override
        public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {

        }

        @Override
        public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {

        }

        /**
         * 实现本方法来处理当前用户被邀请到某个聊天对话事件
         *
         * @param client
         * @param conversation 被邀请的聊天对话
         * @since 3.0
         */
        @Override
        public void onInvited(AVIMClient client, AVIMConversation conversation, String invitedBy) {
            // 当前 clientId（Jerry）被邀请到对话，执行此处逻辑
        }
    }
// 设置全局的对话事件处理 handler

    // Java/Android SDK 通过定制自己的消息事件 Handler 处理服务端下发的消息通知
    public static class CustomMessageHandler extends AVIMMessageHandler {
        /**
         * 重载此方法来处理接收消息
         *
         * @param message
         * @param conversation
         * @param client
         */
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){

            if(message instanceof AVIMTextMessage){
                Context context = App.getContext();
                Intent intent = new Intent(context, DemandDetailActivity.class);
                String demandId = (((AVIMTextMessage)message).getText());
                intent.putExtra("objectId", demandId);
                PendingIntent pi = PendingIntent.getActivities(context, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, demandId);
                Notification notification = notificationBuilder.setOngoing(false)
                        .setSmallIcon(R.mipmap.xiaoxi)
                        .setContentTitle("您有交易更新")
                        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                        .setCategory(Notification.CATEGORY_RECOMMENDATION)
                        .setContentIntent(pi)
                        .build();
            }
        }
    }

}
