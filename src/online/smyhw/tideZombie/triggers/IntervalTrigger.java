package online.smyhw.tideZombie.triggers;

import online.smyhw.tideZombie.SingleTide;
import online.smyhw.tideZombie.Tz;
import online.smyhw.tideZombie.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class IntervalTrigger implements StandardTrigger {
    IntervalTriggerTask task;
    SingleTide ts;
    ConfigurationSection cfg;

    public IntervalTrigger(ConfigurationSection cfg, SingleTide singleTide) {
        this.ts = singleTide;
        this.cfg = cfg;
        //持续时间
        int interval = cfg.getInt("intervalTime", 12000);
        task = new IntervalTriggerTask(singleTide, interval);
    }

    @Override
    public void disable() {
        Utils.debug("定时器关闭...");
        task.cancel();
    }

}

class IntervalTriggerTask extends BukkitRunnable {

    SingleTide singleTide;

    public IntervalTriggerTask(SingleTide singleTide, int interval) {
        this.singleTide = singleTide;
        Tz.loger.info("尸潮<" + singleTide.name + ">定时触发器启动，间隔 " + interval + " 刻");
        this.runTaskTimer(Tz.thisPlugin, 1, interval);
    }

    public void run() {
        Utils.debug("尝试启动尸潮...");
        this.singleTide.startTide();
    }
}
