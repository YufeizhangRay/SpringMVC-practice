# springmvc-practice
springmvc框架学习实践  
  
2018年5月  
  
说到`SpringMVC`就不得不再简单说一下`MVC`。  
`MVC`(`Model–view–controller`)是一种软件架构模式，把软件系统分为三个基本部分：`模型(Model)`、`视图(View)`和`控制器(Controller)`。   
![](https://github.com/YufeizhangRay/image/blob/master/%E5%9B%BE%E7%89%87/MVC-Process.jpg)  
`SpringMVC`是`Spring`框架最重要的的模块之一，有用`MVC`的特性。它以强大的`IoC`容器为基础，充分利用了容器的特性来简化它的配置。  
![](https://github.com/YufeizhangRay/image/blob/master/%E5%9B%BE%E7%89%87/springmvc.jpg)  
`SpringMVC`的工作过程：  
>①用户的`Request`首先会发送给`DispatcherServlet`  
`DispatcherServlet`是`SpringMVC` 中的`前端控制器`(`Front Controller`)，负责接收`Request`并将`Request`转发给对应的处理组件。  
②`HanlerMapping`接收到从`DispatcherServlet`传过来的`Request`  
`HanlerMapping`是`SpringMVC`中完成`URL`到`Controller`映射的组件。`DispatcherServlet`接收 `Request`，然后从 `HandlerMapping` 查找到处理 `Request` 的 `Controller`(因为容器`初始化时`会建立所有`URL`和`Controller`的对应关系，`一个Controller`中有`多个URL`，因为每一个方法都对应着一个不同的`URL`)，并返回给`DispatcherServlet`。  
③`DispatcherServlet`找到可以处理`Request`的对应的`Controller`并发送`Request`。`Controller`处理`Request`，并返回`ModelAndView`对象，`ModelAndView`是封装结果视图的组件。    
`ModelAndView`中的`Model`其实是一个`Map`的实现类，用来存储数据。而`View`代表的则是视图，可以指定需要渲染给客户端的页面。  
④:视图解析器解析`ModelAndView`对象并返回对应的`视图`给客户端.

实现原理：  
>在容器初始化时会建立所有 `URL` 和 `Controller`的对应关系,保存到 `Map<URL,Controller>` 中。  `Tomcat` 启动时会通知 `Spring` 初始化容器(加载 `Bean`的定义信息和初始化所有`单例Bean`)，然后 `SpringMVC` 会遍历容器中的 `Bean`，获取每一个 `Controller` 中的所有方法访问的 `URL`然后将`URL` 和 `Controller` 保存到一个 `Map` 中。  
这样就可以根据 `Request`快速定位到 `Controller`，因为最终处理 `Request`的是 `Controller` 中的方法，`Map` 中只保留了 `URL` 和 `Controller `中的对应关系，所以要根据` Request` 的 `URL `进一步确认` Controller` 中的 `Method`，这一步工作的原理就是拼接 `Controller` 的 `URL`(`Controller` 上 `@RequestMapping` 的值)和方法的 `URL`(`Method` 上`@RequestMapping` 的值)，与 `Request`的 `URL` 进行匹配，找到匹配的那个方法。  
确定处理请求的 `Method`后，接下来的任务就是参数绑定，把 `Request` 中参数绑定到方法的形式参数上。我们只要在方法参数前面声明`@RequestParam("a")`，就可以将 `Request` 中参数` a `的值绑定到方法的该参数上。但是参数的类型则需要使用`asm 框架`来完成。  
拥有了方法和参数类型，我们就可以通过`反射`的原理调用方法了。
