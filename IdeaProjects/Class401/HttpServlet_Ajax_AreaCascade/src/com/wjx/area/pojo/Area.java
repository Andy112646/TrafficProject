package com.wjx.area.pojo;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.pojo
 * @version:1.0
 */
public class Area {
    private int areaid;
    private String areaname;
    private int parentid;
    private int arealevel;
    private int status;

    public Area() {
    }

    public Area(int areaid, String areaname, int parentid, int arealevel, int status) {
        this.areaid = areaid;
        this.areaname = areaname;
        this.parentid = parentid;
        this.arealevel = arealevel;
        this.status = status;
    }

    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getArealevel() {
        return arealevel;
    }

    public void setArealevel(int arealevel) {
        this.arealevel = arealevel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Area{" +
                "areaid=" + areaid +
                ", areaname='" + areaname + '\'' +
                ", parentid=" + parentid +
                ", arealevel=" + arealevel +
                ", status=" + status +
                '}';
    }
}
