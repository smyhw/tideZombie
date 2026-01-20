package online.smyhw.tideZombie;

public class Helper {


    /**
     * 初始化所有尸潮
     */
    public static void initAllTides() {
        for (String id : ConfigTools.tidesConfigs.keySet()) {
            SingleTide singleTide = new SingleTide(ConfigTools.tidesConfigs.get(id));
            Tz.tides.put(id, singleTide);
        }

    }

    /**
     * 停止所有尸潮（及其触发器）
     */
    public static void stopAllTides() {
        Tz.loger.info("停止所有尸潮及其触发器...");
        for (String id : Tz.tides.keySet()) {
            SingleTide singleTide = Tz.tides.get(id);
            singleTide.stopTide();
            singleTide.stop();
        }
        Tz.loger.info("所有尸潮已停止...");
    }
}
