package online.smyhw.tideZombie.triggers;

import online.smyhw.tideZombie.SingleTide;
import online.smyhw.tideZombie.Tz;
import online.smyhw.tideZombie.Utils;
import org.bukkit.configuration.ConfigurationSection;

public class TriggerManager {

    public static StandardTrigger getTrigger(ConfigurationSection cfg, SingleTide singleTide) {
        String triggerType = cfg.getString("type");
        //反射实例化触发器
        Utils.debug("反射触发器类型 -> " + triggerType);
        try {
            Class<?> triggerClass = Class.forName("online.smyhw.tideZombie.triggers." + triggerType);
            StandardTrigger finClass = (StandardTrigger) triggerClass.getDeclaredConstructor(ConfigurationSection.class, SingleTide.class).newInstance(cfg, singleTide);
            return finClass;
        } catch (ClassNotFoundException e) {
            Tz.loger.warning("触发器名称<" + triggerType + ">未知，检查一下有没有拼错!");
            return null;
        } catch (Exception e) {//理论上，其他异常不应该被触发
            e.printStackTrace();
            return null;
        }
    }
}


