package online.smyhw.tideZombie.summoners;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;
import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;

public class def implements StandardSummoner {
	List<String> MobList;
	@Override
	public boolean do_summon(List<Location> loc_list) {
		Random rNum = new Random();
		for(Location loc : loc_list) {
			int list_rNum = rNum.nextInt(MobList.size());
			EntityType et;
			try {
				et = EntityType.valueOf(MobList.get(list_rNum));
			}catch(java.lang.IllegalArgumentException e){
				Tz.loger.warning("怪物类型<"+MobList.get(list_rNum)+">无法识别！");
				return false;
			}
			//生成怪物
			Entity e = loc.getWorld().spawnEntity(loc, et);
			//TODO 将怪物的目标锁定为最近的玩家
			return true;
		}
		return true;
	}

	@Override
	public boolean init(DoMob tide,ConfigurationSection configer)  throws Summoner_Init_Exception{
		MobList = configer.getStringList("enable_mob");
		if(MobList.size()==0) {//异常反馈
			Tz.loger.warning("配置文件中没有找到怪物列表(enable_mob),请检查配置文件！(尸潮ID<"+tide.tideID+">)");
			throw new Summoner_Init_Exception("配置文件中没有找到怪物列表(enable_mob),请检查配置文件！(尸潮ID<"+tide.tideID+">)");
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO 自动生成的方法存根
		return true;
	}

}
