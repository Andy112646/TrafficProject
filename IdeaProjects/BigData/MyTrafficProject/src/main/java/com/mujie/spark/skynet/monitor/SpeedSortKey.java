package com.mujie.spark.skynet.monitor;

import java.io.Serializable;

/**
 * @Auther:wjx
 * @Date:2019/8/1
 * @Description:com.mujie.spark.skynet.monitor
 * @version:1.0
 */
public class SpeedSortKey implements Comparable<SpeedSortKey>, Serializable {
    private static final long serialVersionUID = 1L;
    private long lowSpeed;
    private long normalSpeed;
    private long mediumSpeed;
    private long highSpeed;

    public SpeedSortKey() {
        super();
    }

    public SpeedSortKey(long lowSpeed, long normalSpeed, long mediumSpeed, long highSpeed) {
        super();
        this.lowSpeed = lowSpeed;
        this.normalSpeed = normalSpeed;
        this.mediumSpeed = mediumSpeed;
        this.highSpeed = highSpeed;
    }

    public long getLowSpeed() {
        return lowSpeed;
    }

    public void setLowSpeed(long lowSpeed) {
        this.lowSpeed = lowSpeed;
    }

    public long getNormalSpeed() {
        return normalSpeed;
    }

    public void setNormalSpeed(long normalSpeed) {
        this.normalSpeed = normalSpeed;
    }

    public long getMediumSpeed() {
        return mediumSpeed;
    }

    public void setMediumSpeed(long mediumSpeed) {
        this.mediumSpeed = mediumSpeed;
    }

    public long getHighSpeed() {
        return highSpeed;
    }

    public void setHighSpeed(long highSpeed) {
        this.highSpeed = highSpeed;
    }

    @Override
    public String toString() {
        return "SpeedSortKey [lowSpeed=" + lowSpeed + ", normalSpeed=" + normalSpeed + ", mediumSpeed=" + mediumSpeed + ", highSpeed=" + highSpeed + "]";
    }

    @Override
    public int compareTo(SpeedSortKey o) {
        if (highSpeed - o.getHighSpeed() != 0) {
            return (int) (highSpeed - o.getHighSpeed());
        } else if (mediumSpeed - o.getMediumSpeed() != 0) {
            return (int) (mediumSpeed - o.getMediumSpeed());
        } else if (normalSpeed - o.getNormalSpeed() != 0) {
            return (int) (normalSpeed - o.getNormalSpeed());
        } else if (lowSpeed - o.getLowSpeed() != 0) {
            return (int) (lowSpeed - o.getLowSpeed());
        }
        return 0;
    }
}
