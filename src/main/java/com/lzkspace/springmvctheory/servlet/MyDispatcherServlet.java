package com.lzkspace.springmvctheory.servlet;

import com.lzkspace.springmvctheory.annotation.*;
import com.lzkspace.springmvctheory.web.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : liaozikai
 * file : DispatcherServlet.java
 */
public class MyDispatcherServlet extends HttpServlet {
    
    // 需要扫描的基包
    private String basePackage = "com.lzkspace.springmvctheory";

    // 基包下面所有带包路径的类 如com.lzkspace.springmvctheory.web.UserController
    private List<String> packageName = new ArrayList<>();
    
    // 注解实例化， 注解上的名称和实例化对象 如userController和对象
    private Map<String,Object> instanceMap = new HashMap<>();
    
    // 带包路径的类和对应的注解名称 如com.lzkspace.springmvctheory.web.UserController和userController
    private Map<String,String> nameMap = new HashMap<>();
    
    // url访问路径以及对应方法名称 如/user/insert和public void com.lzkspace.springmvctheory.web.UserController.insert()
    private Map<String, Method> urlMethodMap = new HashMap<>();
    
    // 存放方法名称和方法所在带包路径的类 如public void com.lzkspace.springmvctheory.web.UserController.insert()和com.lzkspace.springmvctheory.web.UserController
    private HashMap<Method,String> methodPackageMap = new HashMap<>();

    /**
     * 初始化所有变量
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            // 将基包下面的所有带包路径的类都存放在packageName中
            scanBasePackage(basePackage);
            // 遍历packageName，设置instanceMap和nameMap
            instance(packageName);
            // 将所有类中带Qualifier标签的字段进行实例化
            springIOC();
            // 设置urlMethodMap和methodPackageMap
            handlerUrlMethodMap();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (InstantiationException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void scanBasePackage(String basePackage) {
        // 将包名的“.”转化为"/"后，获取该包名的全路径 F:/springmvctheory/target/classes/com/lzkspace/springmvctheory
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.","/"));
        File basePackageFile = new File(url.getPath());//F:/springmvctheory/target/classes/com/lzkspace/springmvctheory
        System.out.println("scan: " + basePackageFile);//F:\springmvctheory\target\classes\com\lzkspace\springmvctheory
        File[] childFiles = basePackageFile.listFiles(); // 获取包名路径下的所有子文件
        for(File file : childFiles) {
            if(file.isDirectory()) {
                // 子文件为目录，则再次拼接组装成文件包名 如F:/springmvctheory/target/classes/com/lzkspace/springmvctheory/web
                scanBasePackage(basePackage + "." + file.getName()); 
            } else if(file.isFile()) {
                // 子文件为文件，则获取该文件的全路径名，并去掉后缀名称 如F:/springmvctheory/target/classes/com/lzkspace/springmvctheory/web/UserController
                packageName.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }
    
    private void instance(List<String> packageNames) throws ClassNotFoundException,IllegalAccessException,InstantiationException {
        if(packageNames.size() < 1) {
            return;
        }
        
        for(String string:packageNames) {
            
            // 获取文件名称的类对象
            Class c = Class.forName(string);
            // 判断该类是否为被注解Controller注解过
            if(c.isAnnotationPresent(Controller.class)) {
                // 获取注解类
                Controller controller = (Controller) c.getAnnotation(Controller.class);
                // 获取被Controller类注解过的类的名称 userController
                String controllerName = controller.value();
                // 创建对象，将注解的类名称和类实例放在instanceMap中
                instanceMap.put(controllerName,c.newInstance());
                // 将文件名称的类对象和注解类名称放在nameMap中
                nameMap.put(string,controllerName);
                System.out.println("Controller: " + string + ",value： " + controller.value());
            } else if (c.isAnnotationPresent(Service.class)) {
                Service service = (Service) c.getAnnotation(Service.class);
                String serviceName = service.value();

                instanceMap.put(serviceName,c.newInstance());
                nameMap.put(string,serviceName);
                System.out.println("Service: " + string + ",value：" + service.value());
            } else if (c.isAnnotationPresent(Repository.class)){
                Repository repository = (Repository) c.getAnnotation(Repository.class);
                String repositoryName = repository.value();

                instanceMap.put(repositoryName,c.newInstance());
                nameMap.put(string,repositoryName);
                System.out.println("repository: " + string + ",value： " + repository.value());
            }
        }
    }
    
    private void springIOC() throws IllegalAccessException {
        // 遍历所有被注解过的类对象
        for(Map.Entry<String,Object> entry : instanceMap.entrySet()) {
            // 获取所有指向该类对象的所有实例对象，以userService为例，所有Contrller类中，
            // 有用到@Qualifier("userService"),都能够被获取到。因为所有的实例对象，
            // 都指向了Class对象。<深入java虚拟机>一书中，堆存放对象，而对象会有一个指针指向方法区中存放的Class类型
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            
            // 实例化所有字段属性
            for(Field field : fields) {
                if(field.isAnnotationPresent(Qualifier.class)) {
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true); // 运行时取消 Java 语言访问检查，能提高性能
                    field.set(entry.getValue(),instanceMap.get(name)); // 属性注入 相当于 UserService userService = new UserService；
                }
            }
        }
    }
    
    private void handlerUrlMethodMap() throws ClassNotFoundException {
        if(packageName.size() < 1) {
            return;
        }

        for(String string:packageName) {
            Class c = Class.forName(string);

            if (c.isAnnotationPresent(Controller.class)) {
                
                Method[] methods = c.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if(c.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class); 
                    baseUrl.append(requestMapping.value());
                }
                
                for(Method method : methods) {
                    if(method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());

                        System.out.println(baseUrl + " ==== " + method);
                        urlMethodMap.put(baseUrl.toString(),method);
                        methodPackageMap.put(method,string);
                    }
                }
             }
        }

    }
    
    @Override
    protected  void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    
    @Override
    protected  void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll(contextPath,"");
        
        Method method = urlMethodMap.get(path);
        if(method != null) {
            String packageName= methodPackageMap.get(method);
            String controllerName = nameMap.get(packageName);

            UserController userController = (UserController) instanceMap.get(controllerName);
            try {
                method.setAccessible(true);
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
}
