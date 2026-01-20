package online.smyhw.tideZombie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Tz extends JavaPlugin {

    public static JavaPlugin thisPlugin;
    public static Logger loger;
    public static ConfigurationSection configer;

    public static String errorMsg = null;

    // 调试模式开关
    public static boolean debug = false;

    //存储尸潮列表
    public static Map<String, SingleTide> tides;

    @Override
    public void onEnable() {
        getLogger().info("tideZombie加载中...");
        getLogger().info("正在加载环境...");
        loger = getLogger();
        configer = getConfig();
        thisPlugin = this;
        tides = new ConcurrentHashMap<String, SingleTide>();
        Metrics metrics = new Metrics(this, 28946);
        getLogger().info("正在加载配置...");
        saveDefaultConfig();
        debug = getConfig().getBoolean("debug", false);
        if (debug) {
            getLogger().info("调试模式已启用");
        }
        ConfigTools.loadConfigs();
        getLogger().info("加载尸潮...");
        Helper.initAllTides();
        getLogger().info("tideZombie加载完成");
    }

    @Override
    public void onDisable() {
        getLogger().info("tideZombie卸载中...");
        //关闭所有尸潮
        Helper.stopAllTides();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equals("tz")) {
            return false;
        }
        if (!sender.hasPermission("tideZombie.admin")) {
            sender.sendMessage("§b[§ctideZombie§b]§r:权限不足");
            return true;
        }
        if (errorMsg != null) {
            sender.sendMessage("§b[§ctideZombie§b]§r:插件初始化错误，请检查后尝试重新加载，错误信息: " + errorMsg);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§b[§ctideZombie§b]§r:缺少参数，帮助: tz help");
            return true;
        }
        switch (args[0]) {
            case "start":
                if (args.length != 2) { //参数不对
                    sender.sendMessage("§b[§ctideZombie§b]§r:参数错误，用法:tz start <尸潮ID>");
                }

                SingleTide newTide = tides.get(args[1]);
                if (newTide == null) {
                    sender.sendMessage("§b[§ctideZombie§b]§r:尸潮ID<" + args[1] + ">不存在");
                    return true;
                }
                sender.sendMessage("§b[§ctideZombie§b]§r:手动启动尸潮<" + args[1] + ">...");
                boolean startResult = newTide.startTide();
                if (!startResult) {
                    sender.sendMessage("§b[§ctideZombie§b]§r:尸潮<" + args[1] + ">启动失败，查看控制台获取更多信息");
                }
                return true;
            case "stop":
                if (args.length != 2) { //参数不对
                    sender.sendMessage("§b[§ctideZombie§b]§r:参数错误，用法:tz stop <尸潮ID>");
                    return true;
                }
                newTide = tides.get(args[1]);
                if (newTide == null) {
                    sender.sendMessage("§b[§ctideZombie§b]§r:尸潮ID<" + args[1] + ">不存在");
                    return true;
                }
                sender.sendMessage("§b[§ctideZombie§b]§r:手动终止尸潮<" + args[1] + ">...");
                boolean stopResult = newTide.stopTide();
                if (!stopResult) {
                    sender.sendMessage("§b[§ctideZombie§b]§r:尸潮<" + args[1] + ">启动失败，查看控制台获取更多信息");
                }
                return true;
            case "list":
                sender.sendMessage("§b[§ctideZombie§b]§r:目前有<" + tides.size() + ">个已加载的尸潮");
                for (String id : tides.keySet()) {
                    SingleTide list_tide = tides.get(id);
                    String status = (list_tide.Ins == null) ? "§a未运行§r" : "§c运行中§r";
                    sender.sendMessage("§b[§ctideZombie§b]§r: <" + status + "> | §7" + id);
                }
                return true;
            case "reload":
                sender.sendMessage("§b[§ctideZombie§b]§r:卸载插件...");
                this.onDisable();
                sender.sendMessage("§b[§ctideZombie§b]§r:重载配置文件...");
                this.reloadConfig();
                configer = getConfig();
                sender.sendMessage("§b[§ctideZombie§b]§r:重新装载插件...");
                this.onEnable();
                sender.sendMessage("§b[§ctideZombie§b]§r:重载完成...");
                return true;
            case "help":
            default:
                sender.sendMessage("§b[§ctideZombie§b]§r:命令帮助§r\n" + "/tz start <尸潮ID> §7§o#启动指定尸潮§r\n" + "/tz list §7§o#列出所有尸潮及其状态§r\n" + "/tz stop <尸潮ID> §7§o#结束指定尸潮§r\n" + "/tz reload §7§o#重载配置文件§r");
                return true;
        }
    }
}