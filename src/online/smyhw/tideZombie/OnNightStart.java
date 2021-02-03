package online.smyhw.tideZombie;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 每1秒检查一次时间<br>
 * 不直接算目前到晚上的时间并定时执行是因为防止服务器调整时间导致定时乱掉
 * @author smyhw
 */
public class OnNightStart extends BukkitRunnable {
	public OnNightStart() {
		this.runTaskTimer(Tz.thisPlugin, 0, 20);
	}

	@Override
	public void run() {
		World wd = Bukkit.getWorld(Tz.configer.getString("on_night_world", "world"));
		if(wd==null) {
			Tz.loger.warning("用来确定时间的世界<"+Tz.configer.getString("on_night_world", "没有找到on_night_world配置项")+">不存在");
			this.cancel();
		}
		if(wd.getFullTime()>14000 && wd.getFullTime()< 14030 && Tz.TaskThread==null){
			Tz.loger.info("夜晚自动开启尸潮");
			Tz.TaskThread = new DoMob(Tz.thisPlugin,10000);
		}
		
	}
}
