package online.smyhw.tideZombie.triggers;

import online.smyhw.tideZombie.SingleTide;
import online.smyhw.tideZombie.Tz;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 在夜晚开始时触发尸潮
 *
 * @author smyhw
 */
public class OnNightStartTrigger implements StandardTrigger {
    OnNightStartTriggerTask task;

    public OnNightStartTrigger(ConfigurationSection cfg, SingleTide singleTide) {
        task = new OnNightStartTriggerTask(cfg, singleTide);
    }

    public void disable() {
        task.cancel();
    }
}

/**
 * 每1秒检查一次时间<br>
 * 不直接算目前到晚上的时间并定时执行是因为防止服务器调整时间导致定时乱掉
 *
 * @author smyhw
 */
class OnNightStartTriggerTask extends BukkitRunnable {
    int Tinterval;//多少次夜晚过后触发一次尸潮
    int interval = 0;//目前经过了多少个夜晚
    ConfigurationSection cfg;
    SingleTide singleTide;

    public OnNightStartTriggerTask(ConfigurationSection cfg, SingleTide singleTide) {
        this.cfg = cfg;
        this.singleTide = singleTide;
        this.Tinterval = cfg.getInt("interval", 0);
        this.runTaskTimer(Tz.thisPlugin, 0, 20);
        Tz.loger.info("尸潮<" + singleTide.name + ">将在夜晚开始启动，间隔天数<" + this.Tinterval + ">");
    }

    @Override
    public void run() {
        World wd = Bukkit.getWorld(cfg.getString("on_night_world", "world"));
        if (wd == null) {
            Tz.loger.warning("用来确定时间的世界<" + cfg.getString("on_night_world", "没有找到on_night_world配置项") + ">不存在");
            this.cancel();
        }
        long time = wd.getFullTime();
        if (time > 24000) {//1.7.10以下，时间戳不会每天重置，而是一直向上叠加
            time = time % 24000;
        }
        if (time > 14000 && time <= 14020) {
            Tz.loger.info("夜晚自动开启尸潮");
            if (this.interval == this.Tinterval) {
                this.singleTide.startTide();
                this.interval = 0;
            } else {
                this.interval = this.interval + 1;
            }
        }

    }
}
