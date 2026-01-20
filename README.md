# tideZombie
minecraft bukkit plugin

### 插件介绍

* 一个尸潮插件该有的基本功能
* 动态配置，支持夜晚尸潮，全天尸潮等
* 不会把怪物刷进方块里(大概ovo)
* 支持热重载/加载（例如yum,pluginMan等）
* 支持其他怪物强化插件(例如MythicMobs等)
* 支持原版刷怪或指令刷怪
* 理论全版本,(最高测试到paper1.21，最低测试到bukkit1.7.10)，实际可能更宽

***
### 指令
|cmd| info     
:-|:---------
| /tz start <尸潮ID> | 手动启动一个尸潮 |
| /tz stop <实例ID> | 手动关闭一个尸潮 |
| /tz list | 列出已加载的尸潮 |
| /tz reload | 重载配置文件   |

***

### 从1.x升级
> 注意，1.x和2.x版本的配置文件改动有亿点大，互不兼容！
***

### 权限

有且只有一条`tideZombie.admin`,可以执行本插件所有指令

***
### 配置说明
第一次运行时将创建配置相关目录(./plugins/tideZombie/tides)和默认配置文件
tides目录下每一个文件代表一个尸潮。
完整的配置较为复杂，但这不是必要的，不存在的配置项将使用默认值，对于最简单的配置，参考这里：
```yaml
# 是否启用这个尸潮
# 若为false，则这个配置文件不会被加载！
enable: true

# 尸潮持续时间
# 设置为-1关闭它(即不会自动停止)
duration: 1200

# 刷怪的列表
enable_mob:
- ZOMBIE
- SKELETON
- CREEPER

# 触发器设置，这里定义了这个尸潮该何时启动
trigger:
  # 在夜间开启这个尸潮
  type: "OnNightStartTrigger"
  
  # 以哪个世界的时间为准
  on_night_world: world

  # 间隔，每次尸潮间间隔多少天(默认0天,即每天晚上都有尸潮)
  interval: 0
```
这将定义一个简单的夜晚尸潮，每天晚上刷僵尸、骷髅和苦力怕，持续时间20分钟。

需要更详细地定制尸潮？这里有完整的配置文件`examples/example_full.yml`
***

# 部分配置详解
## 触发器
插件内置了多种不同触发器来启动尸潮  
*触发器* 和 *尸潮持续时间* 共同决定了一个尸潮的生命周期

<details>
<summary>夜晚触发器</summary>
在每天晚上开启尸潮

```yaml
trigger:
    triggerType: "OnNightStartTrigger"
    #以哪个世界的时间为准
    on_night_world: world
    #间隔，每次尸潮间间隔多少天(默认0天,即每天晚上都有尸潮)
    interval: 0
```
</details>

<details>
<summary>固定间隔触发器</summary>
每间隔一段固定时间，开启尸潮

```yaml
trigger:
    triggerType: "IntervalTrigger"
    #间隔多长时间(单位:tick)
    intervalTime: 12000
```
</details>

<details>
<summary>首玩家登入触发器</summary>
在第一个玩家登入服务器时，开启尸潮

适用于开启全天候的尸潮，开服启动，关服结束
```yaml
trigger:
    triggerType: "OnFirstPlayerLoginTrigger"
```
</details>

<details>
<summary>概率累积触发器</summary>
每个周期都有概率开启尸潮

如果没有开启，则会累加概率  
比如，第一天有10%的概率触发尸潮,如果没有触发,则第二天会有20%的概率触发尸潮,以此类推，直到触发为止
```yaml
trigger:
    triggerType: "ProbabilityAccumulation"
    #周期时间
    #可以填写"10000tk"代表每10000ticks为一个周期进行判定
    #也可以填写"2dn"代表周期是2天,这会在每二天傍晚进行判定(适合每天晚上递增概率触发尸潮)
    probability: "1dn"
    #如果上面一项你填写的是以天为周期判断，那么以哪个世界的时间为准
    on_night_world: world
    #每次增加多少概率?（单位是百分比）
    accumulation: 10
```
</details>

## 刷怪方式
`summon_mob_type`决定了我们该如何生成怪物，目前有两种选项
<details>
<summary>指令刷怪</summary>
指令刷怪使用自定义指令来生成怪物

这在配合一些其他怪物增强插件时可能很有帮助，  
例如你可以这样生成一个MythicMobs的怪物:  
`mm m s testMob 1 %world%,%x%,%y%,%z%`

若某些插件只支持设定xyz轴坐标而没有world，默认刷怪刷在主世界之类的，则可以使用`player:`前缀，例如  
`player:summon Zombie %x% %y% %z%`  
这会给予目标玩家(被刷怪玩家)临时的OP权限并执行刷怪指令
</details>
<details>
<summary>原生刷怪</summary>
原生刷怪使用类似于原版怪物生成的方式产生怪物
这会受到全局怪物强化影响，例如SuperMonster等
</details>

***