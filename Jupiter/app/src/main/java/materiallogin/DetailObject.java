package materiallogin;

import com.alibaba.fastjson.annotation.JSONField;

public class DetailObject {
    @JSONField(name = "alert")
    public String alert;

    @JSONField(name = "objectId")
    public String objectId;

    @JSONField(name = "state")
    public String state;

    public DetailObject(){
        alert = "";
        objectId = "";
        state = "";
    }

    public DetailObject(String a, String b, String c){
        alert = a;
        objectId = b;
        state = c;
    }
}
