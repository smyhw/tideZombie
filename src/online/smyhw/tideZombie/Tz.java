package online.smyhw.tideZombie;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Tz extends JavaPlugin implements Listener {

	public static JavaPlugin thisPlugin;
	public static Logger loger;
	public static FileConfiguration configer;
	//存储尸潮线程
	public static DoMob TaskThread;
	//存储夜晚定时线程
	public static OnNightStart OnNightTask;

	@Override
	public void onEnable() {
		getLogger().info("tideZombie加载中...");
		firstPlayer = true;
		getLogger().info("正在加载环境...");
		loger = getLogger();
		configer = getConfig();
		thisPlugin = this;
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this, this);
		//其他可选加载
		if(configer.getBoolean("on_night", false)) {
			getLogger().info("正在创建夜晚检测TASK...");
			OnNightTask = new OnNightStart();
		}
		getLogger().info("tideZombie加载完成");
	}

	@Override
	public void onDisable() {
		getLogger().info("tideZombie卸载");
		//关闭尸潮线程
		if(TaskThread != null)
			TaskThread.tzCancel();
		//关闭夜晚定时线程
		if(OnNightTask != null)
			OnNightTask.cancel();
	}
	
	static boolean firstPlayer = true;
	//在这里初始化实例，因为在这里，所有世界都应该加载完毕
	@EventHandler
	public void CreatureSpawnEvent(PlayerJoinEvent e) {
		//是否在第一个玩家进服时启动
		if(TaskThread !=null && configer.getBoolean("on_enable", false) && firstPlayer) {
			TaskThread = new DoMob(thisPlugin);
			firstPlayer = false;
		}
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
					TaskThread = new DoMob(thisPlugin);
					sender.sendMessage("§b[§ctideZombie§b]§r:启动持续性尸潮...");
				} else {
					int time;
					try {
						time = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) {
						sender.sendMessage("§b[§ctideZombie§b]§r:无法解析你输入的持续时间");
						return true;
					}
					TaskThread = new DoMob(thisPlugin, time);
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
				return true;
			case "help":
			default:
				sender.sendMessage("§b[§ctideZombie§b]§r:命令帮助\n" + "/tz start §7§o#开始尸潮(持续)\n" + "/tz start 10000 §7§o#开始一个持续10000ticks的尸潮\n" + "/tz stop §7§o#结束当前尸潮\n" + "/tz reload §7§o#重载配置文件");
				return true;
		}
	}
}