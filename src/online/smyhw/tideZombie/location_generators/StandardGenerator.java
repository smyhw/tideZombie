package online.smyhw.tideZombie.location_generators;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.exceptions.Generators_Init_Exception;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface StandardGenerator {
    boolean init(DoMob tide, ConfigurationSection configer) throws Generators_Init_Exception;

    boolean stop();

    List<Location> get_loc();
}
