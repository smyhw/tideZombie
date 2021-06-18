package online.smyhw.tideZombie;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Helper {
	/**
	 * 根据玩家坐标随机一个x,z坐标
	 * @param iloc 中心坐标/玩家坐标
	 * @return 返随机坐标
	 */
	public static Location getRandomLoc(Location iloc,ConfigurationSection configer){
		Location loc = iloc.clone();
		int mr = configer.getInt("max_radius", 32);
		int ir = configer.getInt("min_radius", 9);
		int ramNumX = (int) ((mr-ir)*Math.random());
		int ramNumZ = (int) ((mr-ir)*Math.random());
		ramNumX = ramNumX+ir;
		ramNumZ = ramNumZ+ir;
		//随机正负
		if(Math.random()>0.5) 
		{loc.setX(loc.getX()-ramNumX);}
		else
		{loc.setX(loc.getX()+ramNumX);}
		
		if(Math.random()>0.5) 
		{loc.setZ(loc.getZ()-ramNumZ);}
		else
		{loc.setZ(loc.getZ()+ramNumZ);}
		return loc;
	}
	

	
	/**
	 * 在给定的坐标上进行怪物生成操作
	 * @param loc 坐标
	 * @return 如果是 原生 生成，则返回生成的怪物实例，如果是指令生成或者生成失败，则返回null
	 */
	public static Creature spawnMob(Location loc,Player player,String tideID){
		ConfigurationSection configer = Helper.getCfgById(tideID);
		Random rNum = new Random();
		if(configer.getBoolean("useCommands",false)){//使用指令生成怪物
			List<String> cmdList = configer.getStringList("spawn_cmds");
			if(cmdList.size()==0) {//异常反馈
				Tz.loger.warning("你选择了使用指令生成怪物(useCommands=true),但是配置文件中没有找到指令列表(spawn_cmds),请检查配置文件！(尸潮ID<"+tideID+">)");
				return null;
			}
			int list_rNum = rNum.nextInt(cmdList.size());
			String cmd = cmdList.get(list_rNum);
			//替换变量
			cmd = cmd.replaceAll("%x%", loc.getBlockX()+"");
			cmd = cmd.replaceAll("%y%", loc.getBlockY()+"");
			cmd = cmd.replaceAll("%z%", loc.getBlockZ()+"");
			cmd = cmd.replaceAll("%world%", loc.getWorld().getName());
			if(cmd.startsWith("player:")) {
				cmd = cmd.substring(7);//取消前缀
				if(player.isOp()) {
					player.performCommand(cmd);
				}else {
				player.setOp(true);
				player.performCommand(cmd);
				player.setOp(false);
				}
			}
			try {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
			}catch(CommandException  e) {
				Tz.loger.warning("执行刷怪指令<"+cmd+">时出现错误");
			}
		}else{
			List<String> MobList = configer.getStringList("enable_mob");
			if(MobList.size()==0) {//异常反馈
				Tz.loger.warning("配置文件中没有找到怪物列表(enable_mob),请检查配置文件！(尸潮ID<"+tideID+">)");
				return null;
			}
			int list_rNum = rNum.nextInt(MobList.size());
			EntityType et;
			try {
				et = EntityType.valueOf(MobList.get(list_rNum));
			}catch(java.lang.IllegalArgumentException e){
				Tz.loger.warning("怪物类型<"+MobList.get(list_rNum)+">无法识别！");
				return null;
			}
			Entity e = loc.getWorld().spawnEntity(loc, et);
			((Creature) e).setTarget(player);
			return (Creature) e;
		}
		return null;
	}
	
	/**
	 * 向给定世界中的所有玩家发送一条信息
	 * @param word_list 给定的世界列表
	 * @param msg 要发送的信息
	 */
	public static void broadcastMessageToWorld(List<World> word_list,String msg) {
		for(World world : word_list) {
			for(Player p : world.getPlayers()) {
				p.sendMessage(msg);
			}
		}
	}
	
	public static ConfigurationSection getCfgById(String tideID) {
		return Tz.configer.getConfigurationSection("tides."+tideID);
	}
	
}
