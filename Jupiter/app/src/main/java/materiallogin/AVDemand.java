package materiallogin;

import android.content.Intent;

import java.util.Date;

import cn.leancloud.AVObject;

public class AVDemand extends AVObject {

    private String title;
    private String content;
    private String username;
    private String demand_state;
    private int wanted_number;
    private Date end_time;
    private Float reward;
    private String type;

    public static String tablename = "demand";

    public AVDemand() {
        super(tablename);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.put("title", title);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.put("content", content);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.put("username", username);
    }

    public String getDemand_state() {
        return demand_state;
    }

    public void setDemand_state(String demand_state) {
        this.demand_state = demand_state;
        this.put("demand_state", demand_state);
    }

    public int getWanted_number() {
        return wanted_number;
    }

    public void setWanted_number(int wanted_number) {
        this.wanted_number = wanted_number;
        this.put("wanted_number", wanted_number);
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
        this.put("end_time", end_time);
    }

    public Float getReward() {
        return reward;
    }

    public void setReward(Float reward) {
        this.reward = reward;
        this.put("reward", reward);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.put("type", type);
    }


}
