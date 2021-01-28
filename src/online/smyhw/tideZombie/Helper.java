package online.smyhw.tideZombie;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class Helper 
{
	public static Location getRandomLoc(Location iloc)
	{
		Location loc = iloc.clone();
		int mr = mc.configer.getInt("max_radius", 32);
		int ir = mc.configer.getInt("min_radius", 9);
		int ramNumX = (int) ((mr-ir)*Math.random());
		int ramNumZ = (int) ((mr-ir)*Math.random());
		ramNumX = ramNumX+ir;
		ramNumZ = ramNumZ+ir;
		//随机正负
		if(Math.random()>0.5) 
		{loc.setX(loc.getX()-ramNumX);}
		else
		{loc.setX(loc.getX()+ramNumX);}
		
		if(Math.random()>0.5) 
		{loc.setZ(loc.getZ()-ramNumZ);}
		else
		{loc.setZ(loc.getZ()+ramNumZ);}
		return loc;
	}
	
	public static Location getSpwanLoc(Location loc)
	{
		for(int num = 0; num <= mc.configer.getInt("max_try_times", 15);num++)
		{
			Location rloc = getRandomLoc(loc);
			int maxY = rloc.getBlockY()+mc.configer.getInt("max_Y_radius", 5);
			for(int y = maxY-(mc.configer.getInt("max_Y_radius", 5)*2)+2;y<=maxY;y++)
			{
//				System.out.println("world="+rloc.getWorld().getName()+"x="+rloc.getBlockX()+"y="+y+"z="+rloc.getBlockZ());
				Location tmp1 = new Location(rloc.getWorld(),rloc.getBlockX(),y,rloc.getBlockZ());
				if(tmp1.getBlock().getType()!=Material.AIR) {continue;}
				tmp1.setY(y-1);
				if(tmp1.getBlock().getType()!=Material.AIR) {continue;}
				tmp1.setY(y-2);
				if(tmp1.getBlock().getType()==Material.AIR) {continue;}
				tmp1.setY(y-1);
				return tmp1;
			}
		}
		return null;
	}
}
