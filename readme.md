## 介绍
- Apipost IDEA 插件 「Apipost IDEA Helper」现已开源 ！自动解析注解、快速同步文档、高效协作，现在只需一个「Apipost IDEA Helper」！
- 同时，Apipost发起召集牛逼的Java攻城狮活动！一起自创 IDEA插件，贡献代码即可获得精美礼品，更有机会获得妙控键盘！（活动详情请阅读整个内容）

- Apipost IDEA Helper 集成在于IDEA 中，基于 javadoc（Java）、KDoc（Kotlin）、ScalaDoc（Scala）解析 API 文档。在后端研发完成API编写后，只需在IDEA中右键点击“Upload to Apipost”，即可快速生成完整的API文档并同步到Apipost项目中，无需导出操作。

- 召集牛逼的Java攻城狮，教你做自己的IDEA插件
![开源github活动海报](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/cec357e6-d7c0-4476-92d8-dcfd861002fe)


## 如何安装
- 使用压缩包安装
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3f8a93fe-1fe8-4a96-ad0a-734dd003485b)
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3c48eaa4-64bf-4b71-b30a-41da287a1240)
- 配置方法
安装成功后要将 IDEA 内的项目与 Apipost的项目进相关联，需要配置令牌。进入设置界面 Preferences(Settings) > Apipost IDEA Helper 中填写 个人访问令牌、项目 ID：

![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/6aafde6c-9349-4203-8b9b-d90785c0d011)
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/91b513c8-c964-474f-a72e-756e04ec854c)

- 上传文件
后端研发在API代码编写、代码修改后右键点击“Upload to Apipost”，其他团队成员即可在 Apipost 中看到同步后的最新API文档，免去API设计环节。
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/814e53f5-d227-4da4-aa82-91f405d3589c)


## 如何开发
- sdk：必须使用idea的sdk，
![img.png](img.png)
- action：动作，可自定义，右键新建即可新建一个动作
![img_1.png](img_1.png)
> 动作就是按钮，你要点击后触发什么操作就在`Action`类中的`handle`方法中实现
![img_2.png](img_2.png)
- action所在位置，groups和anchor配合使用，groups是分组，anchor是在分组中的位置
- 动作配置：在plugin.xml中配置，配置的是动作的名称，描述，图标等
  ![img_3.png](img_3.png)
  ## 问题反馈
- 现存问题：

1、暂时不支持按目录导入

2、暂时不支持21.2以前版本安装

3、Api导入URL没有拼域名

4、项目ID不能保存
- 以上是我们个人使用用户反馈，欢迎牛逼的Java攻城狮，一起完善好产品，做出自己的IEDA插件，让开发测试更简单；
  ## 联系我们
- 活动规则：

1、提交自己设计的IDEA插件功能；

2、提交修复Pr BUG；

Apipost官方审核通过合并后，即为参与成功；

- 活动详情与报名，请扫码二维码↓

![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/31d77f0d-ab63-4d1b-bf28-242fa8306069)

   ## 进度更新
   ## 特别鸣谢
