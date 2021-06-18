package online.smyhw.tideZombie.triggers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;

public class ProbabilityAccumulation implements StandardTrigger {
	static Task  task;
	public ProbabilityAccumulation(String triggerID) {
		task = new Task(triggerID);
	}

	@Override
	public void disable() {
		task.cancel();
	}

}

class Task extends BukkitRunnable {
	Random rNum = new Random();
	World wd;
	int chance = 0;//现在的几率
	int act_num; //判定参数
	int act_type; //判定类型
	int act_tmp_day = 1;//如果是对应天数傍晚判定，这是已经度过了多少天
	String triggerID;
	public Task(String triggerID ) {
		this.triggerID = triggerID;
		this.wd = Bukkit.getWorld(Tz.configer.getString("triggers."+triggerID+".on_night_world", "world"));
		if(wd==null) {
			Tz.loger.warning("用来确定时间的世界<"+Tz.configer.getString("triggers."+triggerID+".on_night_world", "没有找到on_night_world配置项")+">不存在(触发器ID="+this.triggerID+")");
			return;
		}
		//判断判定时机
		String tmp1 = Tz.configer.getString("triggers."+triggerID+".probability", "1dn");
		if(tmp1.endsWith("dn")) {
			//对应天数傍晚判定
			try {act_num = Integer.parseInt(tmp1.replaceAll("dn", ""));}catch(NumberFormatException e) {Tz.loger.warning("给定的间隔字符串<"+tmp1+">无法解析数字(触发器ID="+this.triggerID+")");return;}
			this.runTaskTimer(Tz.thisPlugin, 0, 20);
			act_type = 1;
		}else if(tmp1.endsWith("tk")) {
			//间隔判定
			try {act_num = Integer.parseInt(tmp1.replaceAll("tk", ""));}catch(NumberFormatException e) {Tz.loger.warning("给定的间隔字符串<"+tmp1+">无法解析数字(触发器ID="+this.triggerID+")");return;}
			this.runTaskTimer(Tz.thisPlugin, 0, act_num);
			act_type = 2;
		}else {
			Tz.loger.warning("给定的间隔字符串<"+tmp1+">无法解析(触发器ID="+this.triggerID+")");
			this.cancel();
		}
	}

	@Override
	public void run() {
		if(act_type==1) {
			long time = wd.getFullTime();
			if(time>24000) {//1.7.10以下，时间戳不会每天重置，而是一直向上叠加
				time = time%24000;
			}
			if(time>14000 && time<= 14020){
				if(act_tmp_day>=act_num) {
					act_tmp_day = 1;
				}else {
					act_tmp_day++;
					return;
				}
			}else {
				return;
			}
		}
		int tmp = rNum.nextInt(101);
		if(tmp<=chance) {
			Tz.loger.info("尸潮触发!概率:"+chance+"%(触发器:"+this.triggerID+")");
			chance=0;
			new DoMob(Tz.thisPlugin,Tz.configer.getString("triggers."+triggerID+".targetTide"),"触发器<"+triggerID+">");
		}else {
			Tz.loger.info("尸潮没有触发!概率:"+chance+"%(触发器:"+this.triggerID+")");
			chance = chance+Tz.configer.getInt("triggers."+triggerID+".accumulation", 10);
		}
	}
}
