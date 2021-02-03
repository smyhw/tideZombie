# tideZombie
minecraft bukkit plugin

#### 插件介绍

* 一个尸潮插件该有的基本功能  
* 支持夜间自动开启尸潮  
* 不会把怪物刷进方块里  
* 支持像yum,pluginMan等插件的热重载/加载  
* 可以创建定时尸潮  
* 支持其他怪物强化插件(例如MythicMobs的VanillaMobs.yml)  
* 可以使用指令生成怪物  
* 理论全版本*,(最高测试到paper1.16.4，最低测试到bukkit1.17.10)，实际可能更宽  
* 更多详细功能见配置文件  

配置文件
```
#在哪些世界开启
enable_worlds:
- world

#是否在第一个玩家进服之后就开始尸潮？
on_enable: true

#是否在夜晚到来时开始尸潮(并在早上结束)
#这相当与在14000ticks时开始一个持续10000tick的限时尸潮
on_night: false

#以哪个世界的时间为准来判定是否是夜晚
on_night_world: world

#最远刷怪半径(距离玩家)
max_radius: 64

#最近刷怪半径(距离玩家)
min_radius: 9

#刷怪高度半径(距离玩家)
max_Y_radius: 5

#多少ticks刷一次怪(每玩家刷一只)
spawn_ticks: 200

#最高刷怪亮度
#调低这个值来显著减少刷怪量
max_light: 999

#最高尝试次数
#最多尝试寻找多少次刷怪位置
#当你的玩家把他刷怪半径中的所有方块全部填实的时候,
#降低它以减少疯狂的玩家造成的卡顿
max_try_times: 15

#是否限制玩家周围的怪物数量
#1.12以下请关闭此项
limit_mobs: false

#如果限制玩家周围的怪物数量，那么:
#每个玩家的最大刷怪半径中最多存在多少只怪
max_mob_per_player: 15

#刷怪的列表
#每种怪物将随机生成
enable_mob:
- ZOMBIE

#使用指令刷怪来替代原生刷怪
#适用于和其他插件/mob的刷怪联动
useCommands: true

#如果上面"useCommands"为true的话，那么这是生成怪物所用的指令列表
#同样，每个指令将随机执行
#P.S.这里也可以干一点骚操作,
#比如"setblock %x% %y% %z% touch"
#%x%/%y%/%z%三个变量可用，替换为刷怪的坐标
#%world%将被替换为世界名称
#"player:"开头将给予被刷怪玩家临时的op权限并执行该刷怪指令，适用于不提供world选项，直接使用指令执行人所在的世界的刷怪命令
spawn_cmds:
- "mm mobs spawn Messenger 1 %world%,%x%,%y%,%z%"
- "player:summon Zombie %x% %y% %z%"
```
