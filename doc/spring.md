一、SpringIOC、DI、MVC的基本执行原理。


二、相关的图
（1）Spring 系统架构图，模块之间的依赖图

    核心模块：
        spring-core     依赖注入IOC与DI的最基本实现
        spring-beans    Bean工厂与Bean的装配
        spring-context  定义基础的Spring的Context上下文即IOC容器
        spring-context-support  对Spring IOC容器的扩展支持，以及IOC子容器
        spring-context-indexer  Spring的类管理组件和Classpath扫描
        spring-expression       Spring表达式语言
         
    切面编程：
        spring-aop      面向切面编程的应用模块，整合ASM，CGLIB，JDKProxy
        spring-aspects  集成AspectJ，AOP应用框架
        spring-instrument   动态Class Loading模块
        
    数据访问与集成：
        spring-jdbc     Spring提供的JDBC抽象框架的主要实现模块，用于简化Spring JDBC操作
        spring-tx       Spring JDBC事务控制实现模块
        spring-orm      主要集成Hibernate, Java Persistence API(JPA)和Java Data Objects(JDO)
        spring-oxm      将Java对象映射成XML数据，或者将XML数据映射成Java对象
        spring-jms      Java Messaging Service能够发送和接收信息 
        
    Web组件
        spring-web      提供了最基础的Web支持，主要建立于核心容器之上，通过Servlet或者Listener来初始化IOC容器
        spring-webmvc   实现了Spring MVC(model-view-Controller)的web应用
        spring-websocket    主要是与Web前端的全双工通讯的协议
        spring-webflux      一个新的非堵塞函数式Reactive Web框架，可以用来建立异步的、非阻塞、事件驱动的服务
        
    通信报文
        spring-messaging    从Spring4开始新加入的一个模块，主要职责是为Spring框架集成一些基础的报文传送应用    
    

Spring IOC流程图
Spring DI流程图
Spring AOP流程图
Spring MVC流程图


三、Spring有什么用？
简化开发
它是通过以下四个基本策略来实现简化开发的：
1.基于POJO的轻量级和最小侵入性编程
2.通过依赖注入和面向接口松耦合
3.基于切面和惯性进行声明式编程
4.通过切面和模板减少样板式代码
    如日志，事务管理