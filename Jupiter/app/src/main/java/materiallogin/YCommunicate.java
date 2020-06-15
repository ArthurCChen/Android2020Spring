package materiallogin;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class YCommunicate extends BmobObject implements Serializable {

    public static final long serialVersionUID = 2L;

    public static final int READ_FOR_R = 0x0001;
    public static final int READ_FOR_U = 0x0002;
    public static final int READ_FOR_ALL = 0x0003;

    private long id;
    private String un1;
    private String un2;
    private String n1;
    private String n2;
    private String msg; // 聊天内容json

    public String getN1() {
        return n1;
    }

    public void setN1(String n1) {
        this.n1 = n1;
    }

    public String getN2() {
        return n2;
    }

    public void setN2(String n2) {
        this.n2 = n2;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUn1() {
        return un1;
    }

    public void setUn1(String un1) {
        this.un1 = un1;
    }

    public String getUn2() {
        return un2;
    }

    public void setUn2(String un2) {
        this.un2 = un2;
    }

    public static int getReadForR() {
        return READ_FOR_R;
    }

    public static int getReadForU() {
        return READ_FOR_U;
    }

    public static int getReadForAll() {
        return READ_FOR_ALL;
    }

    public long getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ChatMsg> convertToMsg() {
        if (TextUtils.isEmpty(msg)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(msg, ChatMsg.class);
    }

    public String getOth() {
        if (un1.equals(SPUtils.getString("username"))) {
            return un2;
        }
        return un1;
    }

    public String getOthn() {
        if (un1.equals(SPUtils.getString("username"))) {
            return n2;
        }
        return n1;
    }

    @Override
    public String toString() {
        return "与 " + getOthn() + " 的聊天";
    }
}