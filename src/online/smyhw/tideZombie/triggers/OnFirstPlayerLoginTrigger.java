package online.smyhw.tideZombie.triggers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;

public class OnFirstPlayerLoginTrigger implements StandardTrigger {
	static OnFirstPlayerLoginListerner listener;
	public OnFirstPlayerLoginTrigger() {
		listener = new OnFirstPlayerLoginListerner();
		listener.enable = true;
		listener.firstPlayer = true;
		Bukkit.getPluginManager().registerEvents(listener, Tz.thisPlugin);
	}
	
	@Override
	public void disable() {
		listener.enable = false;
	}

}

class OnFirstPlayerLoginListerner implements Listener{
	//这里的enable设置是为了热重载时可以关掉这个功能，因为Bukkit监听器貌似无法取消监听
	public boolean enable = false;
	boolean firstPlayer = true;
	@EventHandler
	public void OnPlayerJoinEvent(PlayerJoinEvent e) {
		//是否在第一个玩家进服时启动
		if(Tz.TaskThread ==null && firstPlayer && enable) {
			Tz.TaskThread = new DoMob(Tz.thisPlugin);
			firstPlayer = false;
		}
	}
}
