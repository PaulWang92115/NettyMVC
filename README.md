# NettyMVC

NettyMVC is a lightweight IOC, MVC framework which based on netty http procotol.

NettyMVC 是一个基于 Netty http 协议的轻量级高性能 IOC，MVC 框架。


## Introduction
1. NettyMVC is an MVC framework that implements the relevant functionality of the IOC container.
2. Support MVC annotations such as @controller, @requestparam, @requestmapping, etc.
3. Support @ Service, @ Repositry, @autowired IOC annotation.
4. URI route resolution, parameter mapping.
5. Request supports a variety of parameter types, including basic data types, List, Array, Map, and so on.

## 介绍
1. NettyMVC 是一个 MVC 框架，它实现了 IOC 容器的相关功能。
2. 支持 @Controller，@RequestParam，@RequestMapping 等 MVC 注解。
3. 支持 @Service，@Repositry，@Autowired 等 IOC 注解。
4. URI 路由解析，参数映射。
5. Request 中支持多种参数类型，包括基本数据类型，List，Array，Map等等。

## 结构图
![avatar](https://github.com/PaulWang92115/NettyMVC/blob/master/nettymvc.png)

## 快速开始
1. 在项目中引入 netty-mvc-core 模块。
2. 在 Maven 项目中的 resources 文件夹下创建 applicationContext.xml, 用来配置 IOC 的包扫描路径。
   ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans>
        <package-scan component-scan="org.test.demo" />
    </beans>
   ```
 3. @Controller 对应控制层注解，@Service 对应服务层注解，@Respostry 对应持有层注解， @Autowired 做自动注入，@RequestMapping 做路由，
    @RequestParam 做参数映射。
    ```java
    
      @Controller
      @RequestMapping("/user")
      public class UserController {

          @Autowired
          private UserService userService;

          @RequestMapping("/getUser")
          public FullHttpResponse getUserById(FullHttpRequest request,@RequestParam("userId") int id,@RequestParam("name") String name){

              String res = userService.getUser(id);
              return HttpUtil.constructText(res);
          }

      }

    ```
    ```java
    @Service("userService")
      public class UserServiceImpl implements UserService {

          @Autowired("userDao")
          private UserDao userDao;

          @Override
          public String getUser(int id) {
              return userDao.get(id);
          }
      }
    ```
    ```java
    @Repository
      public class UserDao {

          public String get(int id){
              if(id == 1){
                  return "paul";
              }else{
                  return "wang";
              }
          }
      }

    ```
