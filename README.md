一、SpringIOC、DI、MVC的基本执行原理。
    
    （1）定位->加载->注册
    （2）寻找入口->开始实例化->选择实例化策略->执行实例化->准备依赖注入->解析注入规则->注入赋值
    （3）


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
    

（2）Spring IOC流程图

    

（3）Spring DI流程图

（4）Spring AOP流程图

（5）Spring MVC流程图


三、Spring有什么用？

    简化开发
    应用程序是由一组相互协作的对象组成。而在传统应用程序开发中，一个完整的应用是由一组相互协作的对象组成。所以
    开发一个应用除了要开发业务逻辑外，最多的是关注如何使这些对象协作来完成所需功能，而且要低耦合、高聚合。业务
    逻辑开发是不可避免的，那还需要有个框架出来帮我们来创建对象及管理这些对象之间的依赖关系。Spring框架刚出来
    时主要就是来完成这个功能的。
    
    Spring框架除了帮我们管理对象及其依赖关系，还提供像通用日志记录、性能统计、安全控制、异常处理等面向切面的
    能力，还能帮我们管理最头疼的数据库事务，本身提供了一套简单的JDBC访问实现，提供与第三方数据访问框架集成，
    与各种Java EE技术整合，提供一套自己的Web层框架Spring MVC、而且还能非常简单的与第三方Web框架集成。所以，
    我们也可以认为Spring是一个超级粘合大平台，除了自己提供功能外，还提供粘合其他技术和框架的能力，从而使我们
    可以更自己的选择到底使用什么技术进行开发。
    
    它是通过以下四个基本策略来实现简化开发的：
    1.基于POJO的轻量级和最小侵入性编程
    2.通过依赖注入和面向接口松耦合
    3.基于切面和惯性进行声明式编程
    4.通过切面和模板减少样板式代码
    而他主要是通过：面向Bean(BOP)、依赖注入(DI)以及面向切面(AOP)这三种方式来达成的。
    
    
    BOP
    Spring是面向Bean的编程(Bean Oriented Programming, BOP)，Bean在Spring中才是真正的主角。Bean在
    Spring中作用就像Object对OOP的意义一样，Spring中没有Bean也就没有Spring存在的意义。Spring提供了IOC
    容器通过配置文件或者注解的方式来管理对象之间的依赖关系。 
    
    
    依赖注入DI
    她的基本概念是：不创建对象，但是描述创建它们的方式。在代码中不直接与对象和服务连接，但在配置文件中描述哪一
    个组件需要哪一项服务。容器(在Spring框架中是IOC容器)负责将这些联系在一起。
    
    Spring设计的核心org.springframework.beans包(架构核心是org.springframework.core包)，它的设计目
    标是与JavaBean组件一起使用。这个包通常不是由用户直接使用，而是由服务器将其用作其他多数功能的底层中介。
    下一个最高级抽象是BeanFactory接口，它是工厂模式的实现，允许通过名称创建和检索对象。BeanFactory也可以管
    理对象之间的关系。
    
    BeanFactory最底层支持两个对象模型
    1.单例：
      提供了具有特定名称的全局共享实例对象，可以在查询时对其进行检索。Singleton是默认的也是最常用的对象模型。
    2.原型：
      确保每次检索都会创建单独的实例对象。在每个用户都需要自己的对象时，采用原型模式。
    Bean工厂的概念是Spring作为IOC容器的基础。IOC则将处理事情的责任从应用程序代码转移到框架。
    
    
    AOP
    面向切面编程，它允许程序员对横切关注点或横切典型的职责分界线的行为(例如日志和事务管理)进行模块化。AOP的核
    心构造是方面(切面)，它将那些影响多个类的行为封装到可重用的模块中。
    AOP和IOC是补充性技术，它们都运用模块化方式解决企业应用程序开发中的复杂问题。用Spring AOP编写的应用程序
    代码是松散耦合的。
    AOP的功能完全集成到了Spring事务管理、日志和其它各种特性的上下文中。
    AOP编程的常用场景有：Authentication(权限认证), Auto Caching(自动缓存处理), Error Handling(统一
    错误处理), Debuggin(调试信息输出), Logging(日志记录), Transactions(事务处理)等。