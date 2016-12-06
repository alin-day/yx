# YX SDK

## 项目简述
1. 统一使用UTF-8进行编码;
2. BuildToolsVersion = 24.0.0;
3. MinSdkVersion = 15;
4. TargetSdkVersion = 24;
5. CompileSdkVersion = 24;

## 文件夹功能描述
1. yx_sdk_core_m目录放置yx sdk多平台代码;
2. yx_sdk_core_s目录放置yx sdk单平台代码;
3. BuildSystem目录放置通用配置文件dependencies.gradle、签名、签名配置文件;

## 基本文件结构
yx_sdk_core //yx sdk 基本工程
    |
    |-- yx_sdk_core_m //yx多平台工程
    |       |
    |       |-- libs //依赖jar包资源
    |       |
    |       |-- src //代码&资源&配置文件放置目录
    |       |   |
    |       |   |-- androidTest //测试相关
    |       |   |
    |       |   |-- main //代码&资源放置目录
    |       |   |     |
    |       |   |     |-- aidl //aidl资源
    |       |   |     |-- assets //assets资源
    |       |   |     |-- java //java代码资源
    |       |   |     |-- jniLibs //so库
    |       |   |     |-- res //资源文件
    |       |   |     |-- AndroidManifest.xml //manifest资源
    |       |   |
    |       |   |-- build.gradle //gradle module 相关配置
    |       |   |
    |       |   |-- proguard-rules-yx-sdk-s-v.1.0.0.pro //混淆配置
    |       |   |
    |       |   |-- README.md //module下reademe文件,主要用于记录module信息,更新注意事项等
    |
    |-- yx_sdk_core_s
                 |       |
    |       |-- libs //依赖jar包资源
    |       |
    |       |-- src //代码&资源&配置文件放置目录
    |       |   |
    |       |   |-- androidTest //测试相关
    |       |   |
    |       |   |-- main //代码&资源放置目录
    |       |   |     |
    |       |   |     |-- aidl //aidl资源
    |       |   |     |-- assets //assets资源
    |       |   |     |-- java //java代码资源
    |       |   |     |-- jniLibs //so库
    |       |   |     |-- res //资源文件
    |       |   |     |-- AndroidManifest.xml //manifest资源
    |       |   |
    |       |   |-- build.gradle //gradle module 相关配置
    |       |   |
    |       |   |-- proguard-rules-yx-sdk-s-v1.0.0.pro //混淆脚本
    |       |   |
    |       |   |-- README.md //module下reademe文件,主要用于记录module信息,更新注意事项等
    |
    |-- yx_sdk_res //存放资源的module
    |
    |-- BuildSystem //主要存在公共所需资源以及配置脚本
    |       |
    |       |-- yx.keystore //yx release 签名
    |       |-- dependencies.gradle //项目通用依赖配置
    |       |-- signing.properties //签名配置
    |
    |-- gradle //gradleWrapper
    |     |
    |     |-- wrapper
    |     |     |
    |     |     |-- gradle-wrapper.jar //wrapper jar 主要用于检测wrapper版本以及下载
    |     |     |-- gradle-wrapper.properties //gradle-wrapper配置
    |
    |-- build.gradle //项目gradle build配置
    |
    |-- gradle.properties //gradle配置,主要配置gradle运行项
    |
    |-- gradlew //gradle在unix下运行脚本
    |
    |-- gradlew.bat //gradle在window下运行脚本
    |
    |--README.md //项目readme文件
    |
    |-- settings.gradle //gradle setting文件,主要配置有哪些模块加进构建

## Git提交规范
### 注释规范
```
对提交的信息采用明晰的标注,规范如下:
+) 表示增加了功能
*) 表示对某些功能进行了更改
-) 表示删除了文件，或者对某些功能进行了裁剪，删除，屏蔽。
b) 表示修正了具体的某个bug
```
### 提交目录规范
要求需按照上一节说的文件结构提交,严禁开发人员把本地build文件,配置文件,无关文件进行提交。

### 分支说明
|
|-- master  主分支
|
|-- dev 开发分支
|

