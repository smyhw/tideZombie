package online.smyhw.tideZombie.summoners;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface StandardSummoner {
    boolean init(DoMob tide, ConfigurationSection configer) throws Summoner_Init_Exception;

    boolean stop();

    boolean do_summon(List<Location> loc_list);
}