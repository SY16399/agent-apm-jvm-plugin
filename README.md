# agent-plugin

#### 介绍
简化版jvm监控插件

#### 软件架构
软件架构说明


#### 安装教程

1.  直接下载
2.  使用jar包

#### 使用说明

##### jvm运行信息查看使用方法
1. AttachMain.java 中的`vm.loadAgent("E:\\WorkAndClass\\agent\\shen-agent\\target\\shen-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");`需要修改并绑定`maven-assembly-plugin`Maven插件编译出来的jar包的正确位置。
2. 直接运行 AttachMain.java或运行`shen-agent-1.0-SNAPSHOT-shaded.jar` Maven `pachage`打包出来的jar。
3. 运行后将看到本地存活的java进程，选择正确的进程id。
4. 由于没有像alibaba arthas 一样使用网络传输技术，所以只能在选择的java进程中进行如下操作：
5.   1、查看内存使用情况
     2、生成堆内存快照信息
     3、打印栈信息
     4、打印类加载器
     5、打印类源码
     6、打印方法参数和耗时
     7、退出

##### Application performance monitor (APM) 应用程序性能监控系统采集运行程序的实时数据

- 仅监测了`controller`方法的执行时间

1.  启动时添加如下虚拟机参数

```sh
-javaagent:shen-java-agent-plugin-jar-with-dependencies.jar=文件名.log
```

2. `-javaagent:`后面是项目生成的jar包所在位置  
3. `=`后面可添加自定义文件名如果不使用`=文件名.log` 默认文件名为 `apm.log`

··

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
