package com.flyn.mydb.Util;

import android.app.ActivityManager;
import android.os.StatFs;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BaseApi {
    public static final int TERA = 0;
    public static final int GIGA = 1;
    public static final int MEGA = 2;
    public static final int KILO = 3;
    public static final int BYTE = 4;
    public static double freeRam(ActivityManager manager, ActivityManager.MemoryInfo info) {
         manager.getMemoryInfo(info);
         return info.availMem;
    }
    public static long totalRam(ActivityManager manager, ActivityManager.MemoryInfo info){
        manager.getMemoryInfo(info);
        return info.totalMem;
    }
    public static double round(double value, int places) {
        if (places < 0) places = 0;

        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e) {
            return value;
        }
    }
    public static double formatSize(long bytes, int unit) {
        double convert = 1048576.0;
        double smallConvert = 1024.0;

        double result;

        switch (unit) {
            case TERA:
                result = (bytes / convert) / convert;
                break;
            case GIGA:
                result = (bytes / convert) / smallConvert;
                break;
            case MEGA:
                result = bytes / convert;
                break;
            case KILO:
                result = bytes / smallConvert;
                break;
            case BYTE:
                result = bytes;
                break;
            default: return -1;
        }

        return round(result, 2);
    }
    public static double getAvailableSpace(File dir, int unit) {
        if(dir == null) return -1;

        StatFs statFs = new StatFs(dir.getAbsolutePath());
        long blocks = statFs.getAvailableBlocks();
        return formatSize(blocks * statFs.getBlockSize(), unit);
    }

    public static double getTotaleSpace(File dir, int unit) {
        if(dir == null) return -1;

        StatFs statFs = new StatFs(dir.getAbsolutePath());
        long blocks = statFs.getBlockCount();
        return formatSize(blocks * statFs.getBlockSize(), unit);
    }
}
