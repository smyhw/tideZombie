package online.smyhw.tideZombie.summoners;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Tz;
import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;
import online.smyhw.tideZombie.Helper;

public class cmd implements StandardSummoner {

	List<String> cmdList ;
	
	@Override
	public boolean init(DoMob tide, ConfigurationSection configer) throws Summoner_Init_Exception {
		this.cmdList = configer.getStringList("spawn_cmds");
		if(cmdList.size()==0) {//异常反馈
			Tz.loger.warning("你选择了使用指令生成怪物(summon_mob_type=\"def\"),但是配置文件中没有找到指令列表(spawn_cmds),请检查配置文件！(尸潮ID<"+tide.tideID+">)");
			throw new Summoner_Init_Exception("你选择了使用指令生成怪物(summon_mob_type=\"def\"),但是配置文件中没有找到指令列表(spawn_cmds),请检查配置文件！(尸潮ID<"+tide.tideID+">)");
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public boolean do_summon(List<Location> loc_list) {
		Random rNum = new Random();
		for(Location loc : loc_list) {
			int list_rNum = rNum.nextInt(cmdList.size());
			String cmd = cmdList.get(list_rNum);
			//替换变量
			cmd = cmd.replaceAll("%x%", loc.getBlockX()+"");
			cmd = cmd.replaceAll("%y%", loc.getBlockY()+"");
			cmd = cmd.replaceAll("%z%", loc.getBlockZ()+"");
			cmd = cmd.replaceAll("%world%", loc.getWorld().getName());
			if(cmd.startsWith("player:")) {
				Player player = loc.getWorld().getPlayers().get(0);
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
		}
		return true;
	}

}
