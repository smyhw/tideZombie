package online.smyhw.tideZombie.triggers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;


public class OnNightStartTrigger implements StandardTrigger{
	static OnNightStartTriggerTask  task;
	public OnNightStartTrigger() {
		task = new OnNightStartTriggerTask();
	}
	
	public void disable() {
		task.cancel();
	}
}

/**
 * 每1秒检查一次时间<br>
 * 不直接算目前到晚上的时间并定时执行是因为防止服务器调整时间导致定时乱掉
 * @author smyhw
 */
class OnNightStartTriggerTask extends BukkitRunnable {
	public OnNightStartTriggerTask() {
		this.runTaskTimer(Tz.thisPlugin, 0, 20);
	}

	@Override
	public void run() {
		World wd = Bukkit.getWorld(Tz.configer.getString("on_night_world", "world"));
		if(wd==null) {
			Tz.loger.warning("用来确定时间的世界<"+Tz.configer.getString("on_night_world", "没有找到on_night_world配置项")+">不存在");
			this.cancel();
		}
		if(wd.getFullTime()>14000 && wd.getFullTime()< 14030){
			//如果没有开启强制覆盖，并且已经有尸潮在运行，则不创建新的尸潮
			if(Tz.configer.getBoolean("triggers.OnNightStartTrigger.force", false) &&  Tz.TaskThread!=null ) {return;}
			Tz.loger.info("夜晚自动开启尸潮");
			Tz.TaskThread = new DoMob(Tz.thisPlugin,10000);
		}
		
	}
}
