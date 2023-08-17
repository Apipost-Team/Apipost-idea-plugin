## 介绍
- Apipost IDEA 插件 「Apipost IDEA Helper」现已开源 ！自动解析注解、快速同步文档、高效协作，现在只需一个「Apipost IDEA Helper」！
- 插件市场：https://plugins.jetbrains.com/plugin/22063-apipost-helper
  
- Apipost IDEA Helper 集成在于IDEA 中，基于 javadoc（Java）、KDoc（Kotlin）、ScalaDoc（Scala）解析 API 文档。在后端研发完成API编写后，只需在IDEA中右键点击“Upload to Apipost”，即可快速生成完整的API文档并同步到Apipost项目中，无需导出操作。

- 欢迎大家随时共创，提交Pr，我们审核通过，会继续合并优秀作品~~~

## 如何安装（已上架IDEA插件市场，搜索Apipost即可安装使用）
- IDEA插件市场安装：
小伙伴们现在可以在IDEA > Settings > Plugins 中自行搜索安装，如下图：
  <img width="540" alt="640" src="https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/d38c05c7-c143-4503-8a5b-d7d94d5f4ecd">

- 下载插件安装：（目前支持2020.03--2023版本安装使用哦）

最新插件更新链接：https://pan.baidu.com/s/1wBuMxQFw2ba-a7zldXuB5A 
提取码：post

- 使用压缩包安装
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3f8a93fe-1fe8-4a96-ad0a-734dd003485b)
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3c48eaa4-64bf-4b71-b30a-41da287a1240)
- 配置方法

安装成功后要将 IDEA 内的项目与 Apipost的项目进行关联。在Apipost 「项目设置」>「对外能力」>「open API」中复制 API-token、项目ID，再进入IDEA设置界面 >「Apipost Config」 中填写 API-token、项目ID。
![20230706-181040](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/89a9c48d-1972-4516-ab08-797a047ad183)


如遇到云端地址没有或者不对情况，请填写：https://sync-project.apipost.cn/api/convert

同步API时可选择同步目录：

可以在 Settings > Apipost Config 「自定义目录」中配置。多目录可以用英文逗号分隔，子目录可以用斜杠分隔。

示例：Test1/Test1.2,Test2
 
   <img width="540" alt="6410" src="https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/ac5dc965-fc10-4a80-8690-08ccdbb6ce71">

前置URL配置：

可以根据所选模块的配置文件获取对应的访问前缀，没有对应的配置文件，则需要自己填写，若配置文件中未配置端口号以及访问总路径则使用默认端口号8080。

配置方法：

同样在Settings > Apipost Config 「前置URL配置」中配置，配置完成后，同步API时，会根据选择的目录将Perfix Url自动填入到Apipost接口中。

   <img width="540" alt="6430" src="https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/840c8816-3e06-43a8-934e-aaf50c2773b1">

- 上传文件

后端研发在API代码编写、代码修改后右键点击“Upload to Apipost”，其他团队成员即可在 Apipost 中看到同步后的最新API文档，免去API设计环节。
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/217b3065-40a3-4472-9e02-c2c6f790fe3d)


## 如何开发
- IDEA官网插件文档：https://plugins.jetbrains.com/docs/intellij/basic-action-system.html
- 视频教程链接：https://b23.tv/DuuDW6E

- sdk：必须使用idea的sdk，
![img.png](img.png)
- action：动作，可自定义，右键新建即可新建一个动作
![img_1.png](img_1.png)
> 动作就是按钮，你要点击后触发什么操作就在`Action`类中的`handle`方法中实现
![img_2.png](img_2.png)
- action所在位置，groups和anchor配合使用，groups是分组，anchor是在分组中的位置
- 动作配置：在plugin.xml中配置，配置的是动作的名称，描述，图标等
  ![img_3.png](img_3.png)
  
- 开发文档

导入目录文档：https://console-docs.apipost.cn/preview/38ff26b7bfddc906/3f7ac8ce1fdfe8d0 

## 问题反馈
- 现存问题：

1、IDEA接口更新问题，如果Api在Apipost总url有更改，就会更新一个新的接口；----已验证，更新跟随url变更，url不变则不会新增接口，会在原接口基础上更新；

2、字段为list<str>时候，上传Apipost参数展示bug
[图片]![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/35a58880-ab85-40a5-b0f0-cf35e1a2ec15)


- 以上是我们个人使用用户反馈，欢迎牛逼的Java攻城狮，一起完善好产品，做出自己的IEDA插件，让开发测试更简单；
## 联系我们
- 请扫码二维码↓

![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/31d77f0d-ab63-4d1b-bf28-242fa8306069)
   
 ## 进度更新
 - 已提交的Java攻城狮们，点击这里登记提交您的信息哦，我们会在进度中更新，审核合并后，会在特别鸣谢中公示；
 - 信息登记：https://www.wjx.cn/vm/QCafRUd.aspx# 
 
 - 2023/05/26 发布：合并Pr项目ID不能保存的问题----感谢Java攻城狮~Zhuoyuan1 
 - 2023/05/31 发布：合并支持IDEA中新建目录导入的问题----感谢Java攻城狮~JouTao
 - 2023/06/01 发布：合并支持在Apipost中新建目录，从IDEA直接导入新建的目录中----感谢Java攻城狮~JouTao
 - 2023/06/08 发布：合并Pr项目支持Api导入URL拼域名----感谢Java攻城狮~Linyuan
 - 2023/06/13 感谢：前置URL优化项目提议和Pr推送，攻城狮Linyuan已优化好，虽未被采纳，但十分感谢----感谢Java攻城狮~heart-flowing
 - 2023/06/14 发布：合并目录导入和前置URL功能，修复合并BUG----感谢Java攻城狮~Linyuan
 - 2023/06/15 发布：合并IDEA报错configfile的BUG修复----感谢Java攻城狮~Liuhaoduoduo
 - 2023/06/19 发布：合并bug修复和可支持19--21.3之间版本的IDEA了----感谢Java攻城狮~Linyuan
 - 2023/06/26 发布：IDEA应用市场插件Api兼容问题修复----感谢Java攻城狮~Linyuan
 - 2023/06/26 发布：前置URL通过YML文件配置开发环境----感谢Java攻城狮~JouTao
   
 ## 特别鸣谢
 - 感谢Java攻城狮~Zhuoyuan1：https://github.com/Zhuoyuan1/Apipost-idea-plugin
 - 感谢Java攻城狮~JouTao：https://github.com/JouTao/Apipost-idea-plugin
 - 感谢Java攻城狮~Linyuan：https://github.com/linyuan-design/Apipost-idea-plugin
 - 感谢Java攻城狮~Liuhaoduoduo：https://github.com/liuhaoduoduo/Apipost-idea-plugin
 - 感谢Java攻城狮~heart-flowing
