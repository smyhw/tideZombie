package online.smyhw.tideZombie;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import online.smyhw.tideZombie.triggers.TriggerManager;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Tz extends JavaPlugin implements Listener {

	public static JavaPlugin thisPlugin;
	public static Logger loger;
	public static FileConfiguration configer;
	//存储尸潮线程
	public static CopyOnWriteArrayList<DoMob> TaskThreads;

	@Override
	public void onEnable() {
		getLogger().info("tideZombie加载中...");
		getLogger().info("正在加载环境...");
		loger = getLogger();
		configer = getConfig();
		thisPlugin = this;
		TaskThreads = new CopyOnWriteArrayList<DoMob>();
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		getLogger().info("加载触发器");
		TriggerManager.enable();
		getLogger().info("tideZombie加载完成");
	}

	@Override
	public void onDisable() {
		getLogger().info("tideZombie卸载");
		//关闭尸潮线程
		for (DoMob TaskThread : TaskThreads) {
			TaskThread.tzCancel();
		}
		//关闭触发器
		TriggerManager.disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equals("tz")) {
			return false;
		}
		if(!sender.hasPermission("tideZombie.admin")) {
			sender.sendMessage("§b[§ctideZombie§b]§r:权限不足");
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage("§b[§ctideZombie§b]§r:缺少参数，帮助: tz help");
			return true;
		}
		switch(args[0]) {
			case "start":
				if(args.length != 2) { //参数不对
					sender.sendMessage("§b[§ctideZombie§b]§r:参数错误，用法:tz start <尸潮ID> (这里指配置文件中的尸潮ID)");
				} else {
					if(!Tz.configer.contains("tides."+args[1])) {
						sender.sendMessage("§b[§ctideZombie§b]§r:尸潮ID<"+args[1]+">不存在");
						return true;
					}
					new DoMob(thisPlugin,args[1],"指令");
				}
				return true;
			case "stop":
				if(args.length != 2) { //参数不对
					sender.sendMessage("§b[§ctideZombie§b]§r:参数错误，用法:tz stop <尸潮ID> (这里指tz list中的尸潮ID)");
					return true;
				}
				int id;
				try {
					id = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					sender.sendMessage("§b[§ctideZombie§b]§r:无法解析你输入的尸潮ID");
					return true;
				}
				if(TaskThreads.size()<id) {
					sender.sendMessage("§b[§ctideZombie§b]§r:你输入的尸潮ID不存在...");
					return true;
				}
				sender.sendMessage("§b[§ctideZombie§b]§r:停止尸潮<"+id+">...");
				TaskThreads.get(id).tzCancel();
				return true;
			case "list":
				sender.sendMessage("§b[§ctideZombie§b]§r:目前有<"+TaskThreads.size()+">个尸潮正在运行");
				for(int num = 0 ; num < TaskThreads.size() ; num++) {
//					String world_list = "";
//					for (World world:TaskThreads.get(num).WorldList) {
//						world_list = world_list+world.getName()+",";
//					}
					sender.sendMessage("§b[§ctideZombie§b]§r:["+num+"]尸潮类型<"+TaskThreads.get(num).tideID+"> | 触发来源<"+TaskThreads.get(num).reason+"> | 持续时间<"+TaskThreads.get(num).configer.getInt("duration",-1)+"> ");
				}
				return true;
			case "reload":
				sender.sendMessage("§b[§ctideZombie§b]§r:结束所有尸潮...");
				for (DoMob TaskThread : TaskThreads) {
					TaskThread.tzCancel();
				}
				sender.sendMessage("§b[§ctideZombie§b]§r:重载配置文件...");
				this.reloadConfig();
				configer = getConfig();
				sender.sendMessage("§b[§ctideZombie§b]§r:重载触发器...");
				TriggerManager.disable();
				TriggerManager.enable();
				sender.sendMessage("§b[§ctideZombie§b]§r:重载完成...");
				return true;
			case "help":
			default:
				sender.sendMessage("§b[§ctideZombie§b]§r:命令帮助§r\n" + "/tz start <ID>(这里指配置文件中的尸潮ID) §7§o#开始尸潮§r\n" + "/tz list §7§o#列出当前正在运行的所有尸潮§r\n" + "/tz stop <尸潮ID>(这里指tz list中的尸潮ID) §7§o#结束指定当前尸潮§r\n" + "/tz reload §7§o#重载配置文件§r");
				return true;
		}
	}
}