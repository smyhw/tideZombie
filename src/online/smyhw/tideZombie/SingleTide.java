package online.smyhw.tideZombie;

import online.smyhw.tideZombie.exceptions.Tide_Init_Exception;
import online.smyhw.tideZombie.triggers.StandardTrigger;
import online.smyhw.tideZombie.triggers.TriggerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class SingleTide {

    // ID
    public String name;
    // 运行中的尸潮实例
    DoMob Ins;
    // 关闭计时器
    TideTimer timer;
    // 触发器
    StandardTrigger trigger;
    // 配置
    ConfigurationSection cfg;

    public SingleTide(ConfigurationSection cfg) {
        this.cfg = cfg;
        this.name = cfg.getString("name");
        // 初始化触发器
        String triggerType = cfg.getString("trigger.type", "");
        if (triggerType.equalsIgnoreCase("")) {
            Tz.loger.warning("尸潮<" + this.name + ">未指定触发器类型，跳过加载！");
            return;
        }
        this.trigger = TriggerManager.getTrigger(cfg.getConfigurationSection("trigger"), this);
        if (this.trigger == null) {
            Tz.loger.warning("尸潮<" + this.name + ">触发器加载失败，将不会自动触发！");
        }

    }

    public boolean stopTide() {
        if (this.Ins != null) {
            this.Ins.tzCancel();
            this.Ins = null;
        } else {
            Tz.loger.info("尸潮<" + this.name + ">未在运行中");
        }
        Tz.loger.info("尸潮<" + this.name + ">已停止！");
        return true;
    }

    public boolean startTide() {
        if (this.Ins != null) {
            Tz.loger.info("尸潮<" + this.name + ">已经在运行中，跳过启动！");
            return false;
        }
        try {
            this.Ins = new DoMob(this.cfg);
        } catch (Tide_Init_Exception e) {
            Tz.loger.warning("尸潮<" + this.name + ">启动失败！ -> " + e.getMessage());
            return false;
        }
        // 初始化关闭计时器
        int tideTime = cfg.getInt("duration", -1);
        if (tideTime > 0) {
            this.timer = new TideTimer(this, tideTime);
            Tz.loger.info("尸潮<" + this.name + ">启动，持续时间：" + tideTime + "ticks");
        }
        return true;
    }

    public boolean stop() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.trigger != null) {
            this.trigger.disable();
            Tz.loger.info("尸潮<" + this.name + ">触发器已停止！");
            return true;
        } else {
            Tz.loger.info("尸潮<" + this.name + ">未启用触发器，无需停止！");
            return false;
        }
    }
}

/**
 * 尸潮关闭计时器
 *
 * @author smyhw
 */
class TideTimer extends BukkitRunnable {
    SingleTide target;

    public TideTimer(SingleTide tar, int time) {
        this.target = tar;
        this.runTaskLater(Tz.thisPlugin, time);
    }

    @Override
    public void run() {
        target.stopTide();
    }
}