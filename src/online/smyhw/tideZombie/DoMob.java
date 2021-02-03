package online.smyhw.tideZombie;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 代表一个正在运行的尸潮
 * @author smyhw
 *
 */
public class DoMob extends BukkitRunnable {

	private final JavaPlugin plugin;
	List < World > WorldList = new ArrayList < World > ();
	private stop_this_thread cancelThread;//关闭这个尸潮的计时task(如果有)

	public DoMob(JavaPlugin plugin) {
		//先检查有没有已经在运行的尸潮
		if(Tz.TaskThread  != null) {
			Tz.loger.info("§b[§ctideZombie§b]§r:尸潮已经处于启动状态，将在停止后启动新尸潮...");
			Tz.TaskThread.tzCancel();
		}
		this.plugin = plugin;
		List < String > WorldNameStringList = Tz.configer.getStringList("enable_worlds");
		for(String name: WorldNameStringList) {
			World tmp1 = plugin.getServer().getWorld(name);
			if(tmp1 == null) {
				Tz.loger.warning("指定的世界<" + name + ">不存在");
				continue;
			}
			WorldList.add(tmp1);
		}
		this.runTaskTimer(this.plugin, 0, Tz.configer.getInt("spawn_ticks", 10));
		Bukkit.broadcastMessage("§b[§ctideZombie§b]§r:尸潮已经开始...");
	}
	public DoMob(JavaPlugin plugin, int time) {
		this(plugin);
		new stop_this_thread(plugin,this,time);
	}

	@Override
	public void run() {
		for(World wd: WorldList) {
			for(Player p: wd.getPlayers()) {
				//达到了最大刷怪量，不继续刷怪
				try {
					if(Tz.configer.getBoolean("limit_mobs", false) && p.getLocation().getNearbyEntitiesByType(Monster.class, Tz.configer.getInt("max_radius", 64), Tz.configer.getInt("max_Y_radius", 5)).size() >= Tz.configer.getInt("max_mob_per_player", 15)) {
						continue;
					}
				}catch(Exception e) {
					Tz.loger.warning("1.7.10请不要开启限制怪物数量功能！");
					this.tzCancel();
				}
				Location spawnLoc = Helper.getSpwanLoc(p.getLocation());
				//如果没有找到合适的刷怪位置，不刷怪
				if(spawnLoc == null) {
					continue;
				}
				//亮度过高，不刷怪
				if(spawnLoc.getBlock().getLightLevel() > Tz.configer.getInt("max_light", 999))
				{
					continue;
				}
				Helper.spawnMob(spawnLoc,p);
			}
		}
	}

	/**
	 * 取消这个尸潮
	 */
	public void tzCancel() {
		if(this.cancelThread != null) { //如果有定时取消，那么先取消这个定时取消
			this.cancelThread.cancel();
		}
		this.cancel();
		Tz.TaskThread = null;
		Bukkit.broadcastMessage("§b[§ctideZombie§b]§r:尸潮已经停止...");
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