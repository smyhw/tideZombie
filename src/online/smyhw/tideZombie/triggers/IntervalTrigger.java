package online.smyhw.tideZombie.triggers;

import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;

public class IntervalTrigger implements StandardTrigger {
	IntervalTriggerTask task;
	public final String ID;
	public IntervalTrigger(String triggerID) {
		this.ID = triggerID;
		//持续时间
		int interval = Tz.configer.getInt("triggers."+ID+".intervalTime",12000);
		task = new IntervalTriggerTask(interval,ID);
	}
	@Override
	public void disable() {
		task.cancel();
	}

}

class IntervalTriggerTask extends BukkitRunnable{
	String ID;
	/**
	 * @param interval 间隔时间
	 * @param time 持续时间
	 */
	public IntervalTriggerTask(int interval,String ID) {
		this.ID = ID;
		this.runTaskTimer(Tz.thisPlugin, 0, interval);
	}
	public void run() {
		new DoMob(Tz.thisPlugin,Tz.configer.getString("triggers."+ID+".targetTide"),"触发器<"+ID+">");
	}
}
