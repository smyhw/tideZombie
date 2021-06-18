package online.smyhw.tideZombie.location_generators;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.exceptions.Generators_Init_Exception;
import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;

public interface StandardGenerator {
	public boolean init(DoMob tide,ConfigurationSection configer) throws Generators_Init_Exception;
	public boolean stop();
	public List<Location> get_loc();
}
