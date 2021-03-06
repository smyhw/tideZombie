package online.smyhw.tideZombie.triggers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;


public class OnNightStartTrigger implements StandardTrigger{
	static OnNightStartTriggerTask  task;
	public OnNightStartTrigger(String triggerID) {
		task = new OnNightStartTriggerTask(triggerID);
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
	String triggerID;
	public OnNightStartTriggerTask(String triggerID) {
		this.triggerID = triggerID;
		this.runTaskTimer(Tz.thisPlugin, 0, 20);
	}

	@Override
	public void run() {
		World wd = Bukkit.getWorld(Tz.configer.getString("triggers.OnNightStartTrigger.on_night_world", "world"));
		if(wd==null) {
			Tz.loger.warning("用来确定时间的世界<"+Tz.configer.getString("triggers.OnNightStartTrigger.on_night_world", "没有找到on_night_world配置项")+">不存在");
			this.cancel();
		}
		long time = wd.getFullTime();
		if(time>24000) {//1.7.10以下，时间戳不会每天重置，而是一直向上叠加
			time = time%24000;
		}
		if(time>14000 && time< 14030){
			Tz.loger.info("夜晚自动开启尸潮");
			new DoMob(Tz.thisPlugin,Tz.configer.getString("triggers."+triggerID+".targetTide"),"触发器<"+triggerID+">");
		}
		
	}
}
