package materiallogin;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

// 代取实体类
public class BmobExpress extends BmobObject {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String fbgs;
    private String kddh;
    private String kdgs;
    private String qqdz;
    private String qwdz;
    private String stu;
    private String type;
    private String status; // 待接单 正在进行 已完成
    private String cmt;
    private String xj;

    private String xdsj;
    private String jdsj;
    private String wcsj;

    public String getXdsj() {
        return xdsj;
    }

    public void setXdsj(String xdsj) {
        this.xdsj = xdsj;
    }

    public String getJdsj() {
        return jdsj;
    }

    public void setJdsj(String jdsj) {
        this.jdsj = jdsj;
    }

    public String getWcsj() {
        return wcsj;
    }

    public void setWcsj(String wcsj) {
        this.wcsj = wcsj;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getXj() {
        return xj;
    }

    public void setXj(String xj) {
        this.xj = xj;
    }

    public String getT() {
        return "代取" + type + "：";
    }

    @Override
    public String toString() {
        BmobExpress express = this;

        StringBuilder sb = new StringBuilder();
        if (type.equals("快递")) {
            sb.append("\n快递单号：" + express.getKddh());
            sb.append("\n快递公司：" + express.getKdgs());
        }
        if (type.equals("外卖")) {
            sb.append("\n外卖名称：" + express.getKddh());
            sb.append("\n配送公司：" + express.getKdgs());
        }
        if (type.equals("商品")) {
            sb.append("\n商品名称：" + express.getKddh());
            sb.append("\n商品备注：" + express.getKdgs());
        }
        sb.append("\n取物地址：" + express.getQqdz());
        sb.append("\n送物地址：" + express.getQwdz());
        sb.append("\n代取用户：" + (TextUtils.isEmpty(express.getStu()) ? "无（待接单）" : express.getStu()));
        sb.append("\n支付费用：" + express.getPrice() + "元");

        if (!TextUtils.isEmpty(xdsj)) {
            sb.append("\n\n发布时间：" + xdsj);
        }
        if (!TextUtils.isEmpty(jdsj)) {
            sb.append("\n接单时间：" + jdsj);
        }
        if (!TextUtils.isEmpty(wcsj)) {
            sb.append("\n完成时间：" + wcsj);
        }

        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQwdz() {
        return qwdz;
    }

    public void setQwdz(String qwdz) {
        this.qwdz = qwdz;
    }

    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFbgs() {
        return fbgs;
    }

    public void setFbgs(String fbgs) {
        this.fbgs = fbgs;
    }

    public String getKddh() {
        return kddh;
    }

    public void setKddh(String kddh) {
        this.kddh = kddh;
    }

    public String getKdgs() {
        return kdgs;
    }

    public void setKdgs(String kdgs) {
        this.kdgs = kdgs;
    }

    public String getQqdz() {
        return qqdz;
    }

    public void setQqdz(String qqdz) {
        this.qqdz = qqdz;
    }

    public String getStu() {
        return stu;
    }

    public void setStu(String stu) {
        this.stu = stu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (status.equalsIgnoreCase("待接单")) {
            xdsj = SIMPLE_DATE_FORMAT.format(new Date());
        }
        if (status.equalsIgnoreCase("正在进行")) {
            jdsj = SIMPLE_DATE_FORMAT.format(new Date());
        }
        if (status.equalsIgnoreCase("已完成")) {
            wcsj = SIMPLE_DATE_FORMAT.format(new Date());
        }
    }

    public String gettp() {
        if (TextUtils.isEmpty(xj)) {
            return "";
        }
        return "\n" + xj + "：" + cmt;
    }
}
