package com.tools.speedlib.utils;

/**
 * 测速转换类
 * Created by wong on 17-3-28.
 */
public class ConverUtil {
    private static final long ONE_128KB = 128 * 1024L; //128kb
    private static final long TWO_256KB = 256 * 1024L; //256kb
    private static final long THREE_512KB = 512 * 1024L; //512kb
    private static final long FOUR_1MB = 1024 * 1024L; //1MB
    private static final long FIVE_2MB = 2 * 1024 * 1024L; //2MB
    private static final long SIX_5MB = 5 * 1024 * 1024L; //5MB
    private static final long SEVEN_10MB = 10 * 1024 * 1024L; //10MB
    private static final long EIGHT_20MB = 20 * 1024 * 1024L; //20MB
    private static final long NINE_50MB = 50 * 1024 * 1024L; //50MB
    private static final long TEN_100MB = 100 * 1024 * 1024L; //100MB

    /**
     * 转化成百分比
     *
     * @param speed
     * @return
     */
    public static int getSpeedPercent(long speed) {
        if (speed >= 0 && speed < ONE_128KB) {
            return conver(speed * 10 / ONE_128KB);
        } else if (speed >= ONE_128KB && speed < TWO_256KB) {
            return conver(speed * 10 / TWO_256KB) + 10;
        } else if (speed >= TWO_256KB && speed < THREE_512KB) {
            return conver(speed * 10 / THREE_512KB) + 20;
        } else if (speed >= THREE_512KB && speed < FOUR_1MB) {
            return conver(speed * 10 / FOUR_1MB) + 30;
        } else if (speed >= FOUR_1MB && speed < FIVE_2MB) {
            return conver(speed * 10 / FIVE_2MB) + 40;
        } else if (speed >= FIVE_2MB && speed < SIX_5MB) {
            return conver(speed * 10 / SIX_5MB) + 50;
        } else if (speed >= SIX_5MB && speed < SEVEN_10MB) {
            return conver(speed * 10 / SEVEN_10MB) + 60;
        } else if (speed >= SEVEN_10MB && speed < EIGHT_20MB) {
            return conver(speed * 10 / EIGHT_20MB) + 70;
        } else if (speed >= EIGHT_20MB && speed < NINE_50MB) {
            return conver(speed * 10 / NINE_50MB) + 80;
        } else {
            return conver(speed * 10 / TEN_100MB) + 90;
        }
    }

    /**
     * double转int
     *
     * @param originData
     * @return
     */
    private static int conver(long originData) {
        return Integer.parseInt(new java.text.DecimalFormat("0").format(originData));
    }

    /**
     * 格式化速度
     *
     * @param speed
     * @return
     */
    public static String[] fomartSpeed(long speed) {
        final long UNIT_KB = 1024;
        final long UNIT_MB = UNIT_KB * 1024;
        final long UNIT_GB = UNIT_MB * 1024;
        int unit = 0;
        long temp = speed; //unit B
        while (temp / 1024 > 0) {
            temp = temp / 1024;
            unit++;
        }
        String floatPart = null;
        switch (unit) {
            case 0: //unit B
                return new String[]{temp + "", "B/S"};
            case 1: //unit KB
                floatPart = speed % UNIT_KB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "KB/S"};
            case 2: //unit MB
                floatPart = speed % UNIT_MB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "MB/S"};
            case 3: //unit GB
                floatPart = speed % UNIT_GB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "GB/S"};
            default:
                return new String[]{"0", "B/S"};
        }
    }
}
