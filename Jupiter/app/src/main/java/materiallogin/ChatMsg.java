package materiallogin;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMsg implements Parcelable {

    private String time;
    private String name;
    private String from;
    private String content;

    public ChatMsg() {
    }

    public ChatMsg(String time, String name, String from, String content) {
        this.time = time;
        this.name = name;
        this.from = from;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public static Creator<ChatMsg> getCREATOR() {
        return CREATOR;
    }

    protected ChatMsg(Parcel in) {
        time = in.readString();
        name = in.readString();
        from = in.readString();
        content = in.readString();
    }

    public static final Creator<ChatMsg> CREATOR = new Creator<ChatMsg>() {
        @Override
        public ChatMsg createFromParcel(Parcel in) {
            return new ChatMsg(in);
        }

        @Override
        public ChatMsg[] newArray(int size) {
            return new ChatMsg[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(name);
        dest.writeString(from);
        dest.writeString(content);
    }
}
