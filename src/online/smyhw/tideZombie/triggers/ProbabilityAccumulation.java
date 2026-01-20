package online.smyhw.tideZombie.triggers;

import online.smyhw.tideZombie.SingleTide;
import online.smyhw.tideZombie.Tz;
import online.smyhw.tideZombie.exceptions.Tide_Init_Exception;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ProbabilityAccumulation implements StandardTrigger {
    Task task;

    public ProbabilityAccumulation(ConfigurationSection cfg, SingleTide singleTide) {
        try {
            task = new Task(cfg, singleTide);
        } catch (Tide_Init_Exception e) {
            Tz.loger.warning("尸潮<" + singleTide.name + ">概率累积触发器初始化失败! -> " + e.getMessage());
        }
    }

    @Override
    public void disable() {
        if (task != null) {
            task.cancel();
        }
    }

}

class Task extends BukkitRunnable {
    Random rNum = new Random();
    World wd;
    String name;
    SingleTide singleTide;
    ConfigurationSection cfg;
    int chance = 0;//现在的几率
    int act_num; //判定参数
    int act_type; //判定类型
    int act_tmp_day = 1;//如果是对应天数傍晚判定，这是已经度过了多少天

    public Task(ConfigurationSection cfg, SingleTide singleTide) throws Tide_Init_Exception {
        this.name = singleTide.name;
        this.cfg = cfg;
        this.singleTide = singleTide;
        this.wd = Bukkit.getWorld(cfg.getString("on_night_world", "world"));
        if (wd == null) {
            Tz.loger.warning("用来确定时间的世界<" + cfg.getString("on_night_world", "没有找到on_night_world配置项") + ">不存在");
            return;
        }
        //判断判定时机
        String tmp1 = cfg.getString("probability", "1dn");
        if (tmp1.endsWith("dn")) {
            //对应天数傍晚判定
            try {
                act_num = Integer.parseInt(tmp1.replaceAll("dn", ""));
            } catch (NumberFormatException e) {
                throw new Tide_Init_Exception("给定的间隔字符串<" + tmp1 + ">无法解析数字");
            }
            this.runTaskTimer(Tz.thisPlugin, 0, 20);
            act_type = 1;
        } else if (tmp1.endsWith("tk")) {
            //间隔判定
            try {
                act_num = Integer.parseInt(tmp1.replaceAll("tk", ""));
            } catch (NumberFormatException e) {
                throw new Tide_Init_Exception("给定的间隔字符串<" + tmp1 + ">无法解析数字");
            }
            this.runTaskTimer(Tz.thisPlugin, 0, act_num);
            act_type = 2;
        } else {
            throw new Tide_Init_Exception("给定的间隔字符串<" + tmp1 + ">无法解析");
        }
        Tz.loger.info("尸潮<" + this.name + ">概率累积触发器启动，初始概率 " + chance + "%，判定方式: " + tmp1);
    }

    @Override
    public void run() {
        if (act_type == 1) {
            long time = wd.getFullTime();
            if (time > 24000) {//1.7.10以下，时间戳不会每天重置，而是一直向上叠加
                time = time % 24000;
            }
            if (time > 14000 && time <= 14020) {
                if (act_tmp_day >= act_num) {
                    act_tmp_day = 1;
                } else {
                    act_tmp_day++;
                    return;
                }
            } else {
                return;
            }
        }
        int tmp = rNum.nextInt(101);
        if (tmp <= chance) {
            Tz.loger.info("尸潮<" + this.name + ">触发!概率:" + chance + "%");
            chance = 0;
            this.singleTide.startTide();
        } else {
            Tz.loger.info("尸潮没有触发!概率:" + chance + "%(尸潮:" + this.name + ")");
            chance = chance + cfg.getInt("accumulation", 10);
        }
    }
}
