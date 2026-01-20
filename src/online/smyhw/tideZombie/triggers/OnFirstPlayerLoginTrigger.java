package online.smyhw.tideZombie.triggers;

import online.smyhw.tideZombie.SingleTide;
import online.smyhw.tideZombie.Tz;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 在第一个玩家登录时触发尸潮
 *
 * @author smyhw
 */
public class OnFirstPlayerLoginTrigger implements StandardTrigger {
    public final String ID;
    OnFirstPlayerLoginListerner listener;

    public OnFirstPlayerLoginTrigger(ConfigurationSection cfg, SingleTide singleTide) {
        this.ID = singleTide.name;
        listener = new OnFirstPlayerLoginListerner();
        listener.enable = true;
        listener.firstPlayer = true;
        listener.singleTide = singleTide;
        Bukkit.getPluginManager().registerEvents(listener, Tz.thisPlugin);
        Tz.loger.info("尸潮<" + singleTide.name + ">将在第一个玩家登录时启动");
    }

    @Override
    public void disable() {
        listener.enable = false;
    }

}

class OnFirstPlayerLoginListerner implements Listener {
    //这里的enable设置是为了热重载时可以关掉这个功能，因为Bukkit监听器貌似无法取消监听
    public boolean enable = false;
    boolean firstPlayer = true;
    SingleTide singleTide;

    @EventHandler
    public void OnPlayerJoinEvent(PlayerJoinEvent e) {
        //是否在第一个玩家进服时启动
        if (firstPlayer && enable) {
            singleTide.startTide();
            firstPlayer = false;
        }
    }
}
