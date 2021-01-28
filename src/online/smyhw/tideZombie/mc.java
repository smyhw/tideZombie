package online.smyhw.tideZombie;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.*;


public class mc extends JavaPlugin implements Listener 
{
	public static JavaPlugin thisPlugin;
	public static Logger loger;
	public static FileConfiguration configer;
	public static List<DoMob> mobList = new ArrayList<DoMob>();
	public static DoMob TaskThread;
	@Override
    public void onEnable() 
	{
		getLogger().info("tideZombie加载中...");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		thisPlugin=this;
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("tideZombie加载完成");
	}

	@Override
	public void onDisable() 
	{
		getLogger().info("tideZombie卸载");
    }
	
	//在这里初始化实例，因为在这里，所有世界都应该加载完毕
	@EventHandler
	public void CreatureSpawnEvent(PlayerJoinEvent e)
	{
		TaskThread = new DoMob(thisPlugin);
	}
}