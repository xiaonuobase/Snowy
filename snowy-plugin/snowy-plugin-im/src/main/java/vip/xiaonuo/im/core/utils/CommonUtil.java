package vip.xiaonuo.im.core.utils;

/**
 * @author chengchuanyao
 * @date 2025/5/12 15:32
 */
public class CommonUtil {

    public static String durationFormat(long duration) {
        duration = Math.max(0, duration);

        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        // 格式化数字为两位数
        String formatNumber = String.format("%02d", seconds);

        // 如果小于60秒，返回 00:xx 格式
        if (duration < 60) {
            return "00:" + formatNumber;
        }

        // 如果小于一小时，返回 00:xx:xx 格式
        if (duration < 3600) {
            return "00:" + String.format("%02d", minutes) + ":" + formatNumber;
        }

        // 大于等于一小时，返回 xx:xx:xx 格式
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
