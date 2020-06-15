package materiallogin;

import cn.bmob.v3.BmobObject;

// 无用，忽略
public class BmobShangjia extends BmobObject {

    private String username;
    private String password;
    private String sjmc;
    private String sjdh;
    private String sjfr;
    private String qybh;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSjmc() {
        return sjmc;
    }

    public void setSjmc(String sjmc) {
        this.sjmc = sjmc;
    }

    public String getSjdh() {
        return sjdh;
    }

    public void setSjdh(String sjdh) {
        this.sjdh = sjdh;
    }

    public String getSjfr() {
        return sjfr;
    }

    public void setSjfr(String sjfr) {
        this.sjfr = sjfr;
    }

    public String getQybh() {
        return qybh;
    }

    public void setQybh(String qybh) {
        this.qybh = qybh;
    }
}
