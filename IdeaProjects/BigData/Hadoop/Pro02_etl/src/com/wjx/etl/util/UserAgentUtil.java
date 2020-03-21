package com.wjx.etl.util;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;

import java.io.IOException;

/**
 * 解析浏览器的user agent的工具类
 * 调用的lib下的uasparser jar文件
 *
 * @Auther:wjx
 * @Date:2019/7/8
 * @Description:com.wjx.etl.util
 * @version:1.0
 */
public class UserAgentUtil {
    static UASparser uaSparser = null;

    // static 代码块，初始化uaSparser对象
    static {
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部解析后的浏览器信息model对象
     */
    public static class UserAgentInfo{

    }
}
