package online.smyhw.tideZombie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DoMob extends BukkitRunnable
{
	private final JavaPlugin plugin;
	List<World> WorldList = new ArrayList<World>();
	
	public DoMob(JavaPlugin plugin)
	{
		this.plugin = plugin;
		
    	List<String> WorldNameStringList = mc.configer.getStringList("enable_worlds");
    	for(String name:WorldNameStringList)
    	{
    		World tmp1= plugin.getServer().getWorld(name);
    		if(tmp1==null) {mc.loger.warning("指定的世界<"+name+">不存在(如果你使用多世界插件,请确保在第一个玩家进入服务器前所有世界已经加载完成)");continue;}
    		WorldList.add(tmp1);
    	}
		
		this.runTaskTimer(this.plugin, 0,mc.configer.getInt("spawn_ticks", 10));
	}

    @Override
    public void run() 
    {
    	for(World wd:WorldList)
    	{
    		for(Player p : wd.getPlayers())
    		{
    			if(p.getLocation().getNearbyEntitiesByType(Monster.class, mc.configer.getInt("max_radius", 64), mc.configer.getInt("max_Y_radius", 5)).size() >= mc.configer.getInt("max_mob_per_player", 15))
    			{continue;}//达到了最大刷怪量，不继续刷怪
    			Location spawnLoc = Helper.getSpwanLoc(p.getLocation());
    			if(spawnLoc==null) {continue;}
    			Entity e = p.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
    			if(e==null) {continue;}
    			((Creature) e).setTarget(p);
    		}
    		
    	}
    }
}
