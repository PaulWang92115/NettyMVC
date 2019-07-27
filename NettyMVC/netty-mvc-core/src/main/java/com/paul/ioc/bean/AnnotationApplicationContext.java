package com.paul.ioc.bean;

import com.paul.http.NettyHttpServer;
import com.paul.ioc.annotation.*;
import com.paul.mvc.annotation.RequestMapping;


import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationApplicationContext extends ApplicationContext {

    //保存类路径的缓存
    private List<String> classCache = Collections.synchronizedList(new ArrayList<String>());

    //保存需要注入的类的缓存
    private List<Class<?>> beanDefinition = Collections.synchronizedList(new ArrayList<Class<?>>());

    //保存类实例的容器
    public Map<String,Object> beanFactory = new ConcurrentHashMap<>();

    // 完整路径和 方法的 mapping
    public Map<String,Object> handleMapping = new ConcurrentHashMap<>();

    // 类路径和controller 的 mapping
    public Map<String,Object> controllerMapping = new ConcurrentHashMap<>();

    public AnnotationApplicationContext(String configuration) {
        super(configuration);
        String path  = xmlUtil.handlerXMLForScanPackage(configuration);
        System.out.println(path);

        //执行包的扫描操作
        scanPackage(path);
        //注册bean
        registerBean();
        //把对象创建出来，忽略依赖关系
        doCreateBean();
        //执行容器管理实例对象运行期间的依赖装配
        diBean();
        //mvc 相关注解扫描
        mappingMVC();
        //启动 netty 服务器
        NettyHttpServer instance = NettyHttpServer.getInstance();
        try {
            instance.start(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * MVC 注解和路径扫描
     */
    private void mappingMVC() {
        //上一步已经完成了 Controller，service，respostry，autowired 等注解的扫描和注入
        //遍历容器，将 requestmapping 注解的路径和对应的方法以及 contoller 实例对应起来
        for(Map.Entry<String,Object> entry:beanFactory.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(Controller.class)){
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping2 = method.getAnnotation(RequestMapping.class);
                        String methodPath = requestMapping2.value();
                        String requestPath = classPath + methodPath;
                        handleMapping.put(requestPath,method);
                        controllerMapping.put(requestPath,instance);
                    }else{
                        continue;
                    }

                }
            }else{
                continue;
            }
        }
    }


    @Override
    protected Object doGetBean(String beanName) {
        return beanFactory.get(beanName);
    }

    /**
     * 扫描包下面所有的 .class 文件的类路径到上面的List中
     *
     */
    private void scanPackage(final String path) {
        System.out.println("path:"+path);
        URL url = this.getClass().getClassLoader().getResource(path.replaceAll("\\.", "/"));
        System.out.println("scanPackage:" + url.getPath());
        try {
            File file = new File(url.toURI());

            file.listFiles(new FileFilter(){
                //pathname 表示当前目录下的所有文件
                @Override
                public boolean accept(File pathname) {
                    //递归查找文件
                    if(pathname.isDirectory()){
                        scanPackage(path+"."+pathname.getName());
                    }else{
                        if(pathname.getName().endsWith(".class")){
                            String classPath = path + "." + pathname.getName().replace(".class","");
                            System.out.println("addClassPath:" +classPath );
                            classCache.add(classPath);
                        }
                    }
                    return true;
                }

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }


    /**
     * 根据类路径获得 class 对象
     */
    private void registerBean() {
        if(classCache.isEmpty()){
            return;
        }


        for(String path:classCache){
            try {
                //使用反射，通过类路径获取class 对象
                Class<?> clazz = Class.forName(path);
                //找出需要被容器管理的类，比如，@Component，@org.test.demo.Controller，@org.test.demo.Service，@Repository
                if(clazz.isAnnotationPresent(Repository.class)||clazz.isAnnotationPresent(Service.class)
                        ||clazz.isAnnotationPresent(Controller.class)|| clazz.isAnnotationPresent(Component.class)){
                    beanDefinition.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    /**
     *
     * 根据类对象，创建实例
     */
    private void doCreateBean() {
        if(beanDefinition.isEmpty()){
            return;
        }


        for(Class clazz:beanDefinition){
            try {
                Object instance = clazz.newInstance();
                //将首字母小写的类名作为默认的 bean 的名字
                String aliasName = lowerClass(clazz.getSimpleName());
                //先判断@ 注解里面是否给了 Bean 名字，有的话，这个就作为 Bean 的名字
                if(clazz.isAnnotationPresent(Repository.class)){
                    Repository repository = (Repository) clazz.getAnnotation(Repository.class);
                    if(!"".equals(repository.value())){
                        aliasName = repository.value();
                    }
                }
                if(clazz.isAnnotationPresent(Service.class)){
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    if(!"".equals(service.value())){
                        aliasName = service.value();
                    }
                }
                if(clazz.isAnnotationPresent(Controller.class)){
                    Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                    if(!"".equals(controller.value())){
                        aliasName = controller.value();
                    }
                }
                if(clazz.isAnnotationPresent(Component.class)){
                    Component component = (Component) clazz.getAnnotation(Component.class);
                    if(!"".equals(component.value())){
                        aliasName = component.value();
                    }
                }
                if(beanFactory.get(aliasName)== null){
                    beanFactory.put(aliasName, instance);
                }

                //判断当前类是否实现了接口
                Class<?>[] interfaces = clazz.getInterfaces();
                if(interfaces == null){
                    continue;
                }
                //把当前接口的路径作为key存储到容器中
                for(Class<?> interf:interfaces){
                    beanFactory.put(interf.getName(), instance);
                }



            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        for (Map.Entry<String, Object> entry : beanFactory.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }


    }

    /**
     * 对创建好的对象进行依赖注入
     */
    private void diBean() {
        if(beanFactory.isEmpty()){
            return;
        }

        for(Class<?> clazz:beanDefinition){
            String aliasName = lowerClass(clazz.getSimpleName());
            //先判断@ 注解里面是否给了 Bean 名字，有的话，这个就作为 Bean 的名字
            if(clazz.isAnnotationPresent(Repository.class)){
                Repository repository = clazz.getAnnotation(Repository.class);
                if(!"".equals(repository.value())){
                    aliasName = repository.value();
                }
            }
            if(clazz.isAnnotationPresent(Service.class)){
                Service service = clazz.getAnnotation(Service.class);
                if(!"".equals(service.value())){
                    aliasName = service.value();
                }
            }
            if(clazz.isAnnotationPresent(Controller.class)){
                Controller controller = clazz.getAnnotation(Controller.class);
                if(!"".equals(controller.value())){
                    aliasName = controller.value();
                }
            }
            if(clazz.isAnnotationPresent(Component.class)){
                Component component = clazz.getAnnotation(Component.class);
                if(!"".equals(component.value())){
                    aliasName = component.value();
                }
            }

            //根据别名获取到被装配的 bean 的实例
            Object instance = beanFactory.get(aliasName);
            System.out.println("1111111166666666611111");
            try{
                //从类中获取参数，判断是否有 @Autowired 注解
                Field[] fields = clazz.getDeclaredFields();
                for(Field f:fields){
                    if(f.isAnnotationPresent(Autowired.class)){
                        System.out.println("7000009");
                        //开启字段的访问权限
                        f.setAccessible(true);
                        Autowired autoWired = f.getAnnotation(Autowired.class);
                        if(!"".equals(autoWired.value())){
                            System.out.println("111999");
                            //注解里写了别名
                            f.set(instance, beanFactory.get(autoWired.value()));

                        }else{
                            System.out.println("2222999");
                            //按类型名称
                            String fieldName = f.getType().getName();
                            System.out.println("f:" +fieldName);
                            f.set(instance, beanFactory.get(fieldName));
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }


    private String lowerClass(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        String res = String.valueOf(chars);
        return res;
    }


}
