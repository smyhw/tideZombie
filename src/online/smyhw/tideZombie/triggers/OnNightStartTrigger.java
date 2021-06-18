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
	int Tinterval;//多少次夜晚过后触发一次尸潮
	int interval = 0;//目前经过了多少个夜晚
	String triggerID;
	public OnNightStartTriggerTask(String triggerID) {
		this.triggerID = triggerID;
		this.Tinterval = Tz.configer.getInt("triggers."+triggerID+".interval",0);
		this.runTaskTimer(Tz.thisPlugin, 0, 20);
	}

	@Override
	public void run() {
		World wd = Bukkit.getWorld(Tz.configer.getString("triggers."+triggerID+".on_night_world", "world"));
		if(wd==null) {
			Tz.loger.warning("用来确定时间的世界<"+Tz.configer.getString("triggers."+triggerID+".on_night_world", "没有找到on_night_world配置项")+">不存在");
			this.cancel();
		}
		long time = wd.getFullTime();
		if(time>24000) {//1.7.10以下，时间戳不会每天重置，而是一直向上叠加
			time = time%24000;
		}
		if(time>14000 && time<= 14020){
			Tz.loger.info("夜晚自动开启尸潮");
			if(this.interval==this.Tinterval) {
				new DoMob(Tz.thisPlugin,Tz.configer.getString("triggers."+triggerID+".targetTide"),"触发器<"+triggerID+">");
				this.interval =  0;
			}else {
				this.interval = this.interval+1;	
			}
		}
		
	}
}
