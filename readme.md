## 介绍
- Apipost IDEA 插件 「Apipost IDEA Helper」现已开源 ！自动解析注解、快速同步文档、高效协作，现在只需一个「Apipost IDEA Helper」！
- 同时，Apipost发起召集牛逼的Java攻城狮活动！一起自创 IDEA插件，贡献代码即可获得精美礼品，更有机会获得妙控键盘！（活动详情请阅读整个内容）

- Apipost IDEA Helper 集成在于IDEA 中，基于 javadoc（Java）、KDoc（Kotlin）、ScalaDoc（Scala）解析 API 文档。在后端研发完成API编写后，只需在IDEA中右键点击“Upload to Apipost”，即可快速生成完整的API文档并同步到Apipost项目中，无需导出操作。

- 召集牛逼的Java攻城狮，教你做自己的IDEA插件
![开源github活动海报](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/cec357e6-d7c0-4476-92d8-dcfd861002fe)


## 如何安装
- 下载插件：（目前支持23.1以上版本安装使用哦）

最新插件更新链接：https://pan.baidu.com/s/1wBuMxQFw2ba-a7zldXuB5A 
提取码：post

- 使用压缩包安装
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3f8a93fe-1fe8-4a96-ad0a-734dd003485b)
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/3c48eaa4-64bf-4b71-b30a-41da287a1240)
- 配置方法

安装成功后要将 IDEA 内的项目与 Apipost的项目进行关联。在Apipost 「项目设置」>「对外能力」>「open API」中复制 API-token、项目ID，再进入IDEA设置界面 >「Apipost Config」 中填写 API-token、项目ID。
![image](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/587886cb-e8e8-4138-b1d8-b1a3b1dabc53)

如遇到云端地址没有或者不对情况，请填写：https://sync-project.apipost.cn/api/convert

已更新可以导入前置URL域名拼接，示意图如下：
![导入前置URL0612](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/37547479-0eae-45a7-a7ea-2687a95b5fca)
使用流程：点击Add，选择文件目录，填写前置URL，如http://127.0.0.1:8080 等，点击旁边空白处，在点击Apply（必操作项），在点击确认，即可保存前置URL；

已更新可以按目录导入，多目录选择导入等
![目录导入功能](https://github.com/Apipost-Team/Apipost-idea-plugin/assets/134056636/b215d8aa-783b-49fd-b3e8-7d111e702552)
使用流程：1、在自定义目录中，直接写要上传的目录名称，目录和目录之间用英文逗号隔开；2、在Apipost先新建目录或者查看已有目录，将目录名写入自定义目录中；点击upgrade后，可以筛选导入的目录中；

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

1、暂时不支持按目录导入，选择目录导入----已修复，可在IDEA中新建目录导入，感谢Java攻城狮~JouTao

2、暂时不支持23.1以前版本安装，版本兼容问题，21.3之后插件可安装，但无法上传，21.2之前版本插件安装不了；----已修复，21.3版本安装后无法上传的问题，感谢Java攻城狮~Linyuan

3、Api导入URL没有拼域名----已修复，可在IDEA中按服务输入前缀URL并拼入接口URL中，感谢Java攻城狮~Linyuan

4、项目ID不能保存----已修复，感谢Java攻城狮~Zhuoyuan1 

5、暂时不支持导入Apipost已存在的目录中----已修复，可在Apipost中新建目录，从IDEA直接导入新建的目录中，感谢Java攻城狮~JouTao

6、IDEA导入Api，参数格式是application/x-www-form-urlencoded，而不是From-data，需排查插件书写格式问题或者是否是个BUG；

7、不支持注释来提取注释的目录名字导入；

8、IDEA接口更新问题，如果Api在Apipost总url有更改，就会更新一个新的接口；

- 以上是我们个人使用用户反馈，欢迎牛逼的Java攻城狮，一起完善好产品，做出自己的IEDA插件，让开发测试更简单；
## 联系我们
- 活动规则：

1、提交自己设计的IDEA插件功能；

2、提交修复Pr BUG；

Apipost官方审核通过合并后，即为参与成功；

- 活动详情与报名，请扫码二维码↓

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
 - 2023/06/19 发布：合并bug修复和21.3版本安装后无法上传问题修复，已验证待合并----感谢Java攻城狮~Linyuan
 - 
   
 ## 特别鸣谢
