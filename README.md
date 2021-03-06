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
#这个配置文件主要由两个部分组成
#
#一个是"tides"节点，
#它负责配置尸潮，比如一个尸潮会刷哪些怪，刷多少。。。
#你可以在tides节点下定义多个不同的尸潮备用
#
#另一个部分是"triggers"节点
#它负责定义触发器，
#就是什么时候或者什么时机触发尸潮，触发哪一种尸潮
#这里的哪一种尸潮就是"tides"节点中你定义过的尸潮
#
#该版本仍然处于beta中，非常可能出现bug，欢迎在发布帖下留言回报你发现的问题！


#尸潮设设置
#你需要在这里自定义你的尸潮
#然后在triggers节点中配置何时启动这些尸潮
tides:
  #这是尸潮的ID，你可以随意设置，玩家不会看到这个ID
  #这个示例配置拥有所有可用的配置项目
  t1:
    #这个尸潮在哪些世界刷怪
    enable_worlds:
    - world

    #尸潮持续时间
    #设置为-1关闭它
    duration: 1200

    #该尸潮开启时广播给玩家的消息
    start_message: "§b[§ctideZombie§b]§r:尸潮已经开始..."

    #该尸潮结束时广播给玩家的消息
    end_message: "§b[§ctideZombie§b]§r:尸潮已经停止..."

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
    limit_mobs: true

    #如果限制玩家周围的怪物数量，那么:
    #每个玩家的最大刷怪半径中最多存在多少只怪
    max_mob_per_player: 15

    #刷怪的列表
    #每种怪物将随机生成
    enable_mob:
    - ZOMBIE

    #使用指令刷怪来替代原生刷怪
    #适用于和其他插件/mob的刷怪联动
    useCommands: false

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

  #这是第二个示例尸潮
  #你也可以仅配置你需要的项目，其余未配置的项目将使用默认值
  t2:
    enable_worlds:
    - world
    enable_mob:
    - ZOMBIE
    duration: 1200
    limit_mobs: true

#这些触发器可以让你更加灵活的开启尸潮
triggers:
  #这是你的触发器ID，随意更改，玩家不会看到这个ID
  tt1:
    #这是触发器的类别，不同的类别有不同的参数可以配置
    #类别"OnNightStartTrigger"的作用是在夜晚到来时开始尸潮
    #这相当与在14000ticks时开始目标尸潮
    #一般情况下,mc的夜晚有10000tick
    #所以,你可以在目标尸潮中配置"duration:10000"来创建一个只在夜晚开启的尸潮
    triggerType: "OnNightStartTrigger"
    #以哪个世界的时间为准
    on_night_world: world
    #需要触发的尸潮ID
    #这必须是你在上文tides中定义过的ID
    targetTide: "t1"

  tt2:
    #每间隔一段时间，开启尸潮
    triggerType: "IntervalTrigger"
    #间隔多长时间(单位:tick)
    intervalTime: 12000
    #需要触发的尸潮ID
    #建议目标尸潮设定持续时间，不然会一直叠加尸潮
    targetTide: "t1"

  tt3:
    #当第一个玩家进入服务器时，开启尸潮
    #适用于开启全天候的尸潮，开服启动，关服结束
    triggerType: "OnFirstPlayerLoginTrigger"
    targetTide: "t1"
```
