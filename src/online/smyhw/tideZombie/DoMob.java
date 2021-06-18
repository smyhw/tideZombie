package online.smyhw.tideZombie;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import online.smyhw.tideZombie.location_generators.StandardGenerator;
import online.smyhw.tideZombie.summoners.StandardSummoner;
import online.smyhw.tideZombie.triggers.StandardTrigger;
import online.smyhw.tideZombie.exceptions.*;

/**
 * 代表一个正在运行的尸潮<br>
 * 
 * 由一个坐标生成器generator和一个实体生成器summoner组成<br>
 * 每ticks向坐标生成器索要一次坐标列表，并把该列表发送到实体生成器
 * 
 * @author smyhw
 *
 */
public class DoMob extends BukkitRunnable {

	public final JavaPlugin plugin;//插件实例
	public final String tideID;//这个尸潮的ID
	public ConfigurationSection configer;//这个尸潮对应的配置文件
	public String reason;//这个尸潮启动的原因
	public stop_this_thread cancelThread;//关闭这个尸潮的计时task(如果有)
	public StandardGenerator generator;//本尸潮的坐标生成器
	public StandardSummoner summoner;//本尸潮的实体生成器

	public DoMob(JavaPlugin plugin,String tideID,String reason) {
		Tz.loger.info("尸潮启动触发...类型<"+tideID+">,触发原因<"+reason+">");
		this.tideID = tideID;
		this.plugin = plugin;
		this.configer = Helper.getCfgById(tideID);
		this.reason = reason;
		
		//反射生成坐标生成器
		String generator_type = configer.getString("generate_loc_type","around_player");
		Tz.loger.info("初始化坐标生成器<"+generator_type+">...");
		try {
			Class<?> generator_class = Class.forName("online.smyhw.tideZombie.location_generators."+generator_type);
			generator = (StandardGenerator) generator_class.getDeclaredConstructor().newInstance();
			generator.init(this, configer);
		}catch(ClassNotFoundException e) {
			Tz.loger.warning("坐标生成器<"+generator_type+">未知,检查<generate_loc_type>配置项目有没有拼写错误!");
			return;
		} catch (Exception e) {//理论上，其他异常不应该被触发
			Tz.loger.warning("初始化坐标生成器<"+generator_type+">出现意料外的错误！(麻烦报告给作者!)");
			e.printStackTrace();
			return;
		}

		//反射生成实体生成器
		String summoner_type = configer.getString("summon_mob_type","def");
		Tz.loger.info("初始化实体坐标生成器<"+summoner_type+">...");
		try {
			Class<?> summoner_class = Class.forName("online.smyhw.tideZombie.summoners."+summoner_type);
			summoner = (StandardSummoner) summoner_class.getDeclaredConstructor().newInstance();
			summoner.init(this, configer);
		}catch(ClassNotFoundException e) {
			Tz.loger.warning("实体生成器<"+summoner_type+">未知,检查<summon_mob_type>配置项目有没有拼写错误!");
			return;
		}catch(Summoner_Init_Exception e) {
			Tz.loger.warning("初始化实体生成器<"+summoner_type+">失败！");
			Tz.loger.warning(e.getMessage());
			return;
		}catch (Exception e) {//理论上，其他异常不应该被触发
			Tz.loger.warning("初始化实体生成器<"+summoner_type+">出现意料外的错误！(麻烦报告给作者!)");
			e.printStackTrace();
			return;
		}

		//如果有持续时间设置,生成一个关闭计时器以便在持续时间结束后自动关闭本尸潮
		if(configer.getInt("duration") != -1) {
			Tz.loger.info("创建定时器...");
			cancelThread = new stop_this_thread(Tz.thisPlugin,this,configer.getInt("duration"));
		}

		//启动自己
		this.runTaskTimer(this.plugin, 0, 1);
		Tz.TaskThreads.add(this);
		Tz.loger.info("尸潮启动流程完毕...尸潮id<"+Tz.TaskThreads.indexOf(this)+">");
	}

	@Override
	public void run() {
		List<Location> loc = generator.get_loc();
		if(loc.isEmpty()) {return;}
//		System.out.println(loc.size());
		summoner.do_summon(loc);
	}

	/**
	 * 取消这个尸潮
	 */
	public void tzCancel() {
		//如果有定时取消线程，那么先取消这个定时取消
		if(this.cancelThread != null) { 
			this.cancelThread.cancel();
		}
		generator.stop();
		summoner.stop();
		//关闭自己的定时循环
		this.cancel();
		Tz.TaskThreads.remove(this);
		Tz.loger.info("尸潮结束...");
	}
}

/**
 * 定时结束指定task
 * @author smyhw
 */
class stop_this_thread extends BukkitRunnable {
	DoMob target;
	public stop_this_thread(JavaPlugin plugin, DoMob tar, int time) {
		this.target = tar;
		this.runTaskLater(plugin, time);
	}
	@Override
	public void run() {
		this.target.tzCancel();
	}
}