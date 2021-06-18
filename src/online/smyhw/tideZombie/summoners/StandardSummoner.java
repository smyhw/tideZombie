package online.smyhw.tideZombie.summoners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;

public interface StandardSummoner {
	public boolean init(DoMob tide,ConfigurationSection configer) throws Summoner_Init_Exception;
	public boolean stop();
	public boolean do_summon(List<Location> loc_list);
}