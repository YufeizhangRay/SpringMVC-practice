package cn.zyf.springmvc.handlers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorld {
	
	/**
	 * 1.使用@RequestMapping注解来映射url请求
	 * 2.返回值会通过视图解析器解析为实际的物理视图，对于InternalResourceViewResolver解析器，会做如下解析：
	 * 通过prefix + returnVal + 后缀这样的方式得到实际物理视图，然后做转发操作。
	 * 
	 * /WEB-INF/views/Success.jsp
	 * 
	 * @return
	 */
	
	@RequestMapping("/helloworld")
	public String hello() {
		System.out.println("hello world");
		return "Success";
	}

}
