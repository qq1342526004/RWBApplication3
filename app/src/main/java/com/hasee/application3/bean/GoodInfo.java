package com.hasee.application3.bean;

import java.io.Serializable;

/**
 * Created by fangju on 2018/11/22
 * Email:1342526004@qq.com
 */
public class GoodInfo implements Serializable {
    private String vsnCode;//vsnCode
    private String spsRoute;//配送路线
    private String spsPSJID;//配送架ID
    private String dcpsPlanID;//单车配送ID
    private boolean flag = false;//默认不选中

    public GoodInfo(){

    }

    public String getVsnCode() {
        return vsnCode;
    }

    public void setVsnCode(String vsnCode) {
        this.vsnCode = vsnCode;
    }

    public String getSpsRoute() {
        return spsRoute;
    }

    public void setSpsRoute(String spsRoute) {
        this.spsRoute = spsRoute;
    }

    public String getSpsPSJID() {
        return spsPSJID;
    }

    public void setSpsPSJID(String spsPSJID) {
        this.spsPSJID = spsPSJID;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getDcpsPlanID() {
        return dcpsPlanID;
    }

    public void setDcpsPlanID(String dcpsPlanID) {
        this.dcpsPlanID = dcpsPlanID;
    }
}
