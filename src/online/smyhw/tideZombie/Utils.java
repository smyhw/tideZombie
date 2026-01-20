package online.smyhw.tideZombie;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public class Utils {
    /**
     * 输出调试信息，仅当debug模式开启时输出
     *
     * @param msg 调试信息
     */
    public static void debug(String msg) {
        if (Tz.debug) {
            Tz.loger.info("[DEBUG] " + msg);
        }
    }

    /**
     * 根据玩家坐标随机一个x,z坐标
     *
     * @param iloc 中心坐标/玩家坐标
     * @return 返随机坐标
     */
    public static Location getRandomLoc(Location iloc, ConfigurationSection configer) {
        Location loc = iloc.clone();
        int mr = configer.getInt("max_radius", 32);
        int ir = configer.getInt("min_radius", 9);
        int ramNumX = (int) ((mr - ir) * Math.random());
        int ramNumZ = (int) ((mr - ir) * Math.random());
        ramNumX = ramNumX + ir;
        ramNumZ = ramNumZ + ir;
        //随机正负
        if (Math.random() > 0.5) {
            loc.setX(loc.getX() - ramNumX);
        } else {
            loc.setX(loc.getX() + ramNumX);
        }

        if (Math.random() > 0.5) {
            loc.setZ(loc.getZ() - ramNumZ);
        } else {
            loc.setZ(loc.getZ() + ramNumZ);
        }
        return loc;
    }

    /**
     * 向给定世界中的所有玩家发送一条信息
     *
     * @param word_list 给定的世界列表
     * @param msg       要发送的信息
     */
    public static void broadcastMessageToWorld(List<World> word_list, String msg) {
        for (World world : word_list) {
            for (Player p : world.getPlayers()) {
                p.sendMessage(msg);
            }
        }
    }


    /**
     * 保存插件内置资源文件到插件数据文件夹中
     *
     * @param resourcePath 资源文件路径
     */
    public static void saveResource(String resourcePath, String targetPath) throws Exception {
        if (resourcePath == null) {
            return;
        }
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = Tz.thisPlugin.getResource(resourcePath);
        if (in == null) {
            throw new Exception("资源缺失！请确认插件文件未损坏，或向开发者报告！ -> " + resourcePath + " 不存在于插件jar包内");
        }
        //获取实际文件名
        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        File outPath = new File(Tz.thisPlugin.getDataFolder(), targetPath);
        File outFile = new File(outPath, fileName);

        try {
            OutputStream out = Files.newOutputStream(outFile.toPath());
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException ex) {
            throw new Exception("保存默认配置文件失败 -> " + resourcePath + " - " + ex.getMessage());
        }

    }

}
