package online.smyhw.tideZombie;

import online.smyhw.tideZombie.exceptions.Summoner_Init_Exception;
import online.smyhw.tideZombie.exceptions.Tide_Init_Exception;
import online.smyhw.tideZombie.location_generators.StandardGenerator;
import online.smyhw.tideZombie.summoners.StandardSummoner;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * 代表一个正在运行的尸潮<br>
 * <p>
 * 由一个坐标生成器generator和一个实体生成器summoner组成<br>
 * 每ticks向坐标生成器索要一次坐标列表，并把该列表发送到实体生成器
 *
 * @author smyhw
 *
 */
public class DoMob extends BukkitRunnable {

    public String tideID;//这个尸潮的ID
    public ConfigurationSection config;//这个尸潮对应的配置文件
    public StandardGenerator generator;//本尸潮的坐标生成器
    public StandardSummoner summoner;//本尸潮的实体生成器

    public DoMob(ConfigurationSection config) throws Tide_Init_Exception {
        this.config = config;
        this.tideID = config.getString("name");

        //反射生成坐标生成器
        String generator_type = this.config.getString("generate_loc_type", "around_player");
        Utils.debug("初始化坐标生成器<" + generator_type + ">...");
        try {
            Class<?> generator_class = Class.forName("online.smyhw.tideZombie.location_generators." + generator_type);
            generator = (StandardGenerator) generator_class.getDeclaredConstructor().newInstance();
            generator.init(this, this.config);
        } catch (ClassNotFoundException e) {
            throw new Tide_Init_Exception("坐标生成器<" + generator_type + ">未知,检查<generate_loc_type>配置项目有没有拼写错误!");
        } catch (Exception e) {//理论上，其他异常不应该被触发
            e.printStackTrace();
            throw new Tide_Init_Exception("初始化坐标生成器<" + generator_type + ">出现意料外的错误！(麻烦报告给作者!)");
        }

        //反射生成实体生成器
        String summoner_type = this.config.getString("summon_mob_type", "def");
        Utils.debug("初始化实体坐标生成器<" + summoner_type + ">...");
        try {
            Class<?> summoner_class = Class.forName("online.smyhw.tideZombie.summoners." + summoner_type);
            summoner = (StandardSummoner) summoner_class.getDeclaredConstructor().newInstance();
            summoner.init(this, this.config);
        } catch (ClassNotFoundException e) {
            throw new Tide_Init_Exception("实体生成器<" + summoner_type + ">未知,请检查<summon_mob_type>配置项目有没有拼写错误!");
        } catch (Summoner_Init_Exception e) {
            throw new Tide_Init_Exception("初始化实体生成器<" + summoner_type + ">失败！ -> " + e.getMessage());
        } catch (Exception e) {//理论上，其他异常不应该被触发
            e.printStackTrace();
            throw new Tide_Init_Exception("初始化实体生成器<" + summoner_type + ">出现意料外的错误！(麻烦报告给作者!)");
        }

        //启动自己
        this.runTaskTimer(Tz.thisPlugin, 0, 1);
        Utils.debug("尸潮id<" + this.tideID + ">启动流程完成！");
    }

    @Override
    public void run() {
        List<Location> loc = generator.get_loc();
//        System.out.println(loc.size());
        if (loc.isEmpty()) {
            return;
        }
        Utils.debug("成功获取到坐标列表，准备生成实体...");
        summoner.do_summon(loc);
    }

    /**
     * 取消这个尸潮
     */
    public void tzCancel() {
        generator.stop();
        summoner.stop();
        //关闭自己的定时循环
        this.cancel();
    }
}

