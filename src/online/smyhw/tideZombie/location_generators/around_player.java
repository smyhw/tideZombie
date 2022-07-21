package online.smyhw.tideZombie.location_generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.Line;

import online.smyhw.tideZombie.DoMob;
import online.smyhw.tideZombie.Helper;
import online.smyhw.tideZombie.Tz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class around_player implements StandardGenerator {
	public around_player() {}
	ConfigurationSection configer;
	List < World > WorldList = new ArrayList < World > ();
	int summon_ticks;
	int now_summon_ticks = 0;
	@Override
	public List<Location> get_loc() {
		List<Location> fin = new ArrayList<Location>();
		//如果倒计时没满，不刷怪
		if(now_summon_ticks < summon_ticks) {
			now_summon_ticks++;
			return fin;
		}else {
			now_summon_ticks=0;
		}
		for(World wd: WorldList) {
			continueNextPlayer:
			for(Player p: wd.getPlayers()) {
				//判断黑白名单
				
				//ID黑白名单
				if(configer.getInt("id_list_setting.enable")==0) {}
				else if(configer.getInt("id_list_setting.enable")==1) {
					if(wbl_id.contains(p.getName())) {continue continueNextPlayer;}
				}else if(configer.getInt("id_list_setting.enable")==2) {
					if(!wbl_id.contains(p.getName())) {continue continueNextPlayer;}
				}

				//权限黑白名单
				if(configer.getInt("permissions_list_setting.enable")==0) {}
				else if(configer.getInt("permissions_list_setting.enable")==1) {
					for(String pp:wbl_pms) {if(p.hasPermission(pp)) {continue continueNextPlayer;}}
				}else if(configer.getInt("permissions_list_setting.enable")==2) {
					boolean tmp1 = false;
					for(String pp:wbl_pms) {if(p.hasPermission(pp)) {tmp1=true;break;}}
					if(!tmp1) {continue;}
				}
				//坐标检测
				if(configer.getStringList("area_list").size()!=0) {
					for(HashMap<String,Integer> line : area_list) {
						if(p.getLocation().getBlockX()>line.get("X_max")) {continue continueNextPlayer;}
						if(p.getLocation().getBlockX()<line.get("X_min")) {continue continueNextPlayer;}
						if(p.getLocation().getBlockY()>line.get("Y_max")) {continue continueNextPlayer;}
						if(p.getLocation().getBlockY()<line.get("Y_min")) {continue continueNextPlayer;}
						if(p.getLocation().getBlockZ()>line.get("Z_max")) {continue continueNextPlayer;}
						if(p.getLocation().getBlockZ()<line.get("Z_min")) {continue continueNextPlayer;}
					}
				}
				//如果达到了最大刷怪量，不继续刷怪
				if(configer.getBoolean("limit_mobs", false) && p.getLocation().getNearbyEntitiesByType(Monster.class, configer.getInt("max_radius", 64), configer.getInt("max_Y_radius", 5)).size() >= configer.getInt("max_mob_per_player", 15)) {
					continue;
				}
				//寻找刷怪位置
				Location spawnLoc = getSpwanLoc(p.getLocation(),configer);
				//如果没有找到合适的刷怪位置，不刷怪
				if(spawnLoc == null) {continue;}
				//亮度过高，不刷怪
				if(spawnLoc.getBlock().getLightLevel() > configer.getInt("max_light", 999)){continue;}
//				Helper.spawnMob(spawnLoc,p,tideID);
				//最终放入刷怪列表
				fin.add(spawnLoc);
			}
		}
		return fin;
	}

	/**
	 * 根据玩家坐标，获取一个实际可以刷怪的坐标<br>
	 * 如果找不到，则返回null
	 * @param loc 玩家坐标/中心坐标
	 * @return 找到的
	 */
	public static Location getSpwanLoc(Location loc,ConfigurationSection configer){
		List<String> disable_blocks = configer.getStringList("disable_blocks");
		if(disable_blocks.size()==0) {
			disable_blocks.add("AIR");
			disable_blocks.add("STATIONARY_WATER");
		}
		for(int num = 0; num <= configer.getInt("max_try_times", 15);num++){
			Location rloc = Helper.getRandomLoc(loc,configer);
			int maxY = rloc.getBlockY()+configer.getInt("max_Y_radius", 5);
			for(int y = maxY-(configer.getInt("max_Y_radius", 5)*2)+2;y<=maxY;y++){
				Location tmp1 = new Location(rloc.getWorld(),rloc.getBlockX(),y,rloc.getBlockZ());
				//测试是否适合刷怪(必须是两格空气，脚底下不能是黑名单方块)
				if(tmp1.getBlock().getType()!=Material.AIR) {continue;}
				tmp1.setY(y-1);
				if(tmp1.getBlock().getType()!=Material.AIR) {continue;}
				tmp1.setY(y-2);
//				if(tmp1.getBlock().getType()==Material.AIR) {continue;}
				boolean block_disabled = false;
				for(String block_name : configer.getStringList("disable_blocks")) {
					if(tmp1.getBlock().getType()==Material.getMaterial(block_name)) {block_disabled = true;break;}
				}
				if(block_disabled) {continue;}
				tmp1.setY(y-1);
				return tmp1;
			}
		}
		return null;
	}

	//预先准备黑白名单列表，提高效率
	List<String> wbl_id;
	List<String> wbl_pms;
	List<HashMap<String,Integer>> area_list = new ArrayList<HashMap<String,Integer>>();
	@Override
	public boolean init(DoMob tide,ConfigurationSection configer) {
		this.configer = configer;
		this.configer.addDefault("area_list",new ArrayList<>());
		this.summon_ticks = configer.getInt("spawn_ticks", 200);
		
		//检测世界是否存在
		List < String > WorldNameStringList = configer.getStringList("enable_worlds");
		for(String name: WorldNameStringList) {
			World tmp1 = tide.plugin.getServer().getWorld(name);
			if(tmp1 == null) {
				Tz.loger.warning("尸潮<"+tide.tideID+">指定的世界<" + name + ">不存在");
				continue;
			}
			WorldList.add(tmp1);
		}
		//检测
		for(String block_name : configer.getStringList("disable_blocks")) {
			if(Material.getMaterial(block_name)==null) {
				Tz.loger.warning("尸潮<"+tide.tideID+">指定的disable_blocks中的项目<" + block_name + ">不能识别");
			}
		}
		
		//黑白名单检测/预缓存
		//ID名单
		if(configer.getInt("id_list_setting.enable")==0) {}
		else if(configer.getInt("id_list_setting.enable")==1) {
			wbl_id = configer.getStringList("id_list_setting.white_list_player_id");
		}else if(configer.getInt("id_list_setting.enable")==2) {
			wbl_id = configer.getStringList("id_list_setting.black_list_player_id");
		}else{
			Tz.loger.warning("尸潮<"+tide.tideID+">的黑白名单启用设置<"+configer.getInt("id_list_setting.enable")+">不能识别");
		}
		//权限名单
		if(configer.getInt("permissions_list_setting.enable")==0) {}
		else if(configer.getInt("permissions_list_setting.enable")==1) {
			wbl_pms = configer.getStringList("permissions_list_setting.white_list_permissions");
		}else if(configer.getInt("permissions_list_setting.enable")==2) {
			wbl_pms = configer.getStringList("permissions_list_setting.black_list_permissions");
		}else{
			Tz.loger.warning("尸潮<"+tide.tideID+">的黑白名单启用设置<"+configer.getInt("permissions_list_setting.enable")+">不能识别");
		}
		
		//初始化坐标范围
		if(configer.getStringList("area_list").size()!=0) {
			for(String line : configer.getStringList("area_list")) {
				HashMap<String,Integer> tmp2 = new HashMap<String,Integer>();
				String[] tmp1 = line.split("&");//分离三个坐标段
				if(tmp1.length!=3) {Tz.loger.warning("尸潮<"+tide.tideID+">的坐标范围<"+line+">格式错误,不能识别");continue;}
				int tmp6,tmp7;
				String[] tmp3 = tmp1[0].split("~");
				if(tmp3.length!=2){Tz.loger.warning("尸潮<"+tide.tideID+">的X轴坐标范围<"+line+">格式错误,不能识别");continue;}
				try {tmp6=Integer.parseInt(tmp3[0]);tmp7=Integer.parseInt(tmp3[1]);}catch(NumberFormatException  e) {Tz.loger.warning("尸潮<"+tide.tideID+">的X轴坐标范围<"+line+">数字格式错误,不能识别");continue;}
				if(tmp6>tmp7) {
					tmp2.put("X_max", tmp6);
					tmp2.put("X_min", tmp7);
				}else {
					tmp2.put("X_max", tmp7);
					tmp2.put("X_min", tmp6);
				}
				String[] tmp4 = tmp1[1].split("~");
				if(tmp4.length!=2){Tz.loger.warning("尸潮<"+tide.tideID+">的Y轴坐标范围<"+line+">格式错误,不能识别");continue;}
				try {tmp6=Integer.parseInt(tmp4[0]);tmp7=Integer.parseInt(tmp4[1]);}catch(NumberFormatException  e) {Tz.loger.warning("尸潮<"+tide.tideID+">的Y轴坐标范围<"+line+">数字格式错误,不能识别");continue;}
				if(tmp6>tmp7) {
					tmp2.put("Y_max", tmp6);
					tmp2.put("Y_min", tmp7);
				}else {
					tmp2.put("Y_max", tmp7);
					tmp2.put("Y_min", tmp6);
				}
				String[] tmp5 = tmp1[2].split("~");
				if(tmp5.length!=2){Tz.loger.warning("尸潮<"+tide.tideID+">的Z轴坐标范围<"+line+">格式错误,不能识别");continue;}
				try {tmp6=Integer.parseInt(tmp5[0]);tmp7=Integer.parseInt(tmp5[1]);}catch(NumberFormatException  e) {Tz.loger.warning("尸潮<"+tide.tideID+">的Z轴坐标范围<"+line+">数字格式错误,不能识别");continue;}
				if(tmp6>tmp7) {
					tmp2.put("Z_max", tmp6);
					tmp2.put("Z_min", tmp7);
				}else {
					tmp2.put("Z_max", tmp7);
					tmp2.put("Z_min", tmp6);
				}
				area_list.add(tmp2);
			}
		}
		
		Helper.broadcastMessageToWorld(WorldList,configer.getString("start_message", "§b[§ctideZombie§b]§r:尸潮已经开始..."));
		return true;
	}

	@Override
	public boolean stop() {
		Helper.broadcastMessageToWorld(WorldList,configer.getString("end_message", "§b[§ctideZombie§b]§r:尸潮已经停止..."));
		return true;
	}
}
