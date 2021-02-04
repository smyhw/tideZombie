package online.smyhw.tideZombie.triggers;

import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;

public class IntervalTrigger implements StandardTrigger {
	IntervalTriggerTask task;
	public IntervalTrigger() {
		int interval = Tz.configer.getInt("triggers.IntervalTrigger.intervalTime",12000);
		int duration = Tz.configer.getInt("triggers.IntervalTrigger.duration",1200);
		task = new IntervalTriggerTask(interval,duration);
	}
	@Override
	public void disable() {
		task.cancel();
	}

}

class IntervalTriggerTask extends BukkitRunnable{
	int time;
	/**
	 * @param interval 间隔时间
	 * @param time 持续时间
	 */
	public IntervalTriggerTask(int interval,int time) {
		this.time = time;
		this.runTaskTimer(Tz.thisPlugin, 0, interval);
	}
	public void run() {
		if(Tz.TaskThread==null) {
			new DoMob(Tz.thisPlugin,time);
		}
	}
}
