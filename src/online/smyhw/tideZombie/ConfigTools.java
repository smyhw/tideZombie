package online.smyhw.tideZombie;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigTools {

    public static Map<String, ConfigurationSection> tidesConfigs;

    public static boolean loadConfigs() {
        File file = new File(Tz.thisPlugin.getDataFolder(), "tides");
        if (!file.exists()) {
            Tz.loger.warning("配置目录不存在，尝试创建默认配置...");
            try {
                file.mkdirs();
                Utils.saveResource("examples/example_full.yml", "tides");
                Utils.saveResource("examples/example_single.yml", "tides");
            } catch (Exception e) {
                Tz.loger.warning("初始化配置目录时出错，请检查！ -> " + e.getMessage());
                Tz.errorMsg = "创建配置目录时出错，请检查！ -> " + e.getMessage();
                return false;
            }
        }

        File[] files = file.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null || files.length == 0) {
            Tz.loger.warning("没有找到任何尸潮配置文件，请检查！");
            return false;
        }
        tidesConfigs = new HashMap<String, ConfigurationSection>();
        for (File f : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

            boolean enable = config.getBoolean("enable", false);
            if (!enable) {
                Tz.loger.info("配置文件 " + f.getName() + " 未启用，跳过加载");
                continue;
            }
            String name = f.getName().substring(0, f.getName().lastIndexOf("."));
            config.set("name", name);
            Tz.loger.info("读取尸潮配置 -> " + name);
            tidesConfigs.put(name, config);
        }
        return true;
    }

}
