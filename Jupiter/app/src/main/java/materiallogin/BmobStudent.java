package materiallogin;

import cn.bmob.v3.BmobObject;

// 账户信息
public class BmobStudent extends BmobObject{

    private String username;
    private String password;
    private String studentId;
    private String name;
    private String className;
    private String susheName;

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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSusheName() {
        return susheName;
    }

    public void setSusheName(String susheName) {
        this.susheName = susheName;
    }
}
