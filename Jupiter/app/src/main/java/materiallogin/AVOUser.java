package materiallogin;

import cn.leancloud.AVObject;
import io.reactivex.Observable;

public class AVOUser extends AVObject {
    private String username;
    private String password;
    private String email;
    private String studentId;
    private String name;
    private String domain;
    private String dormitory;

    public static String tablename = "Student";

    public AVOUser(String username, String password, String email, String studentId, String name, String domain, String dormitory){
        super(tablename);
        this.username = username;
        this.password = password;
        this.email = email;
        this.studentId = studentId;
        this.name = name;
        this.domain = domain;
        this.dormitory = dormitory;
    }

    @Override
    public Observable<? extends AVObject> saveInBackground() {
        this.put("email", email);
        this.put("password", password);
        this.put("studentId", studentId);
        this.put("username", username);
        this.put("domain", domain);
        this.put("dormitory", dormitory);
        return super.saveInBackground();
    }

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
        return domain;
    }

    public void setClassName(String className) {
        this.domain = className;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }


}
