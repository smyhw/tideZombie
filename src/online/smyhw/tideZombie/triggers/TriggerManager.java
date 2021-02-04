package online.smyhw.tideZombie.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import online.smyhw.tideZombie.Tz;

public class TriggerManager {
	public static Map<String,StandardTrigger> triggerList = new HashMap<String,StandardTrigger>();
	
	/**
	 * 初始化所有启用的触发器<br>
	 * 同时会填充triggerList(有效触发器实例列表)
	 */
	public static void enable() {
		List<String> triggerNameList = Helper.getTriggerNameList();
		for(String triggerName:triggerNameList) {
			//反射实例化触发器
			try {
				Class<?> triggerClass = Class.forName("online.smyhw.tideZombie.triggers."+triggerName);
				StandardTrigger finClass = (StandardTrigger) triggerClass.getDeclaredConstructor().newInstance();
				triggerList.put(triggerName, finClass);
			}catch(ClassNotFoundException e) {
				Tz.loger.warning("触发名称<"+triggerName+">未知，检查一下有没有拼错!");
				continue;
			} catch (Exception e) {//理论上，其他异常不应该被触发
				Tz.loger.warning("初始化触发器<"+triggerName+">出错！");
				e.printStackTrace();
				Tz.loger.warning("跳过加载触发器<"+triggerName+">，插件和其他触发器将继续加载...");
				continue;
			}
		}
		//TODO
	}
	
	/**
	 * 用于通知各个触发器 插件正在关闭</br>
	 * 一般用于各个触发器关闭定时线程等资源
	 */
	public static void disable() {
		for(String triggerName:triggerList.keySet()) {
			triggerList.get(triggerName).disable();
		}
		triggerList = null;//置null让内存自动回收
	}
}

class Helper{
	
	/**
	 * 获取启用的触发器名称列表</br>
	 * @return 触发器名称列表
	 */
	public static List<String> getTriggerNameList(){
		List<String> re = new ArrayList<String>();
		Set<String> triggerList = Tz.configer.getConfigurationSection("triggers").getKeys(false);
		for(String triggerName : triggerList) {
			//如果没有启用，则跳过
			if(!Tz.configer.getBoolean("triggers."+triggerName+".enable", false)) {
				Tz.loger.info("<"+triggerName+"> : <禁用>");
				continue;
			}
			Tz.loger.info("<"+triggerName+"> : <启用>");
			re.add(triggerName);
		}
		return re;
	}
}
