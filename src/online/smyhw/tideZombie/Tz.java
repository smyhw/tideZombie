package online.smyhw.tideZombie;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import online.smyhw.tideZombie.triggers.TriggerManager;

import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Tz extends JavaPlugin implements Listener {

	public static JavaPlugin thisPlugin;
	public static Logger loger;
	public static FileConfiguration configer;
	//存储尸潮线程
	public static DoMob TaskThread;

	@Override
	public void onEnable() {
		getLogger().info("tideZombie加载中...");
		getLogger().info("正在加载环境...");
		loger = getLogger();
		configer = getConfig();
		thisPlugin = this;
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
		if(TaskThread != null)
			TaskThread.tzCancel();
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
		switch(args[0]) {
			case "start":
				if(args.length < 2) { //参数不满2个
					new DoMob(thisPlugin);
					sender.sendMessage("§b[§ctideZombie§b]§r:启动持续性尸潮...");
				} else {
					int time;
					try {
						time = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) {
						sender.sendMessage("§b[§ctideZombie§b]§r:无法解析你输入的持续时间");
						return true;
					}
					new DoMob(thisPlugin, time);
					sender.sendMessage("§b[§ctideZombie§b]§r:启动定时尸潮,持续" + time + "ticks...");
				}
				return true;
			case "stop":
				if(TaskThread == null) {
					sender.sendMessage("§b[§ctideZombie§b]§r:当前没有尸潮在运行...");
					return true;
				}
				sender.sendMessage("§b[§ctideZombie§b]§r:停止当前尸潮...");
				TaskThread.tzCancel();
				return true;
			case "reload":
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
				sender.sendMessage("§b[§ctideZombie§b]§r:命令帮助\n" + "/tz start §7§o#开始尸潮(持续)\n" + "/tz start 10000 §7§o#开始一个持续10000ticks的尸潮\n" + "/tz stop §7§o#结束当前尸潮\n" + "/tz reload §7§o#重载配置文件");
				return true;
		}
	}
}