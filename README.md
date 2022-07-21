# tideZombie
minecraft bukkit plugin

### 插件介绍

* 一个尸潮插件该有的基本功能
* 支持夜间自动开启尸潮
* 不会把怪物刷进方块里
* 支持像yum,pluginMan等插件的热重载/加载
* 可以创建定时尸潮
* 支持其他怪物强化插件(例如MythicMobs的VanillaMobs.yml)
* 可以使用指令生成怪物
* 理论全版本,(最高测试到bukkit1.19，最低测试到bukkit1.17.10)，实际可能更宽
* 更多详细功能见[配置文件](https://github.com/smyhw/tideZombie/blob/main/src/config.yml)

***
可以配置使用指令刷怪或原生刷怪，  
指令刷怪配合一些其他怪物增强插件时可能很有帮助，  
例如你可以这样生成一个MythicMobs的怪物:`mm m s testMob 1 %world%,%x%,%y%,%z%`  
或者某些插件只支持设定xyz轴坐标而没有world，默认刷怪刷在主世界，你可以使用`player:`前缀，例如  
`player:summon Zombie %x% %y% %z%`,这会给予目标玩家(被刷怪玩家)临时的OP权限并执行刷怪指令  
原生刷怪也会受到全局怪物强化影响，例如SuperMonster等
***
### 指令
|cmd|info
:-|:-
| /tz start <尸潮ID> | 开启一个尸潮 |
| /tz list | 显示目前所有正在运行的尸潮以及它们的详细信息 |
| /tz stop <实例ID> | 结束一个尸潮,使用"/tz list"指令获取正在运行的尸潮的实例ID |
| /tz reload | 重载配置文件 |

***

### 权限

有且只有一条`tideZombie.admin`,可以执行本插件所有指令