package cn.zyf.springmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWord {

	@Autowired
	private UserService userService;
	
	public HelloWord() {
		System.out.println("HelloWorld Constructor...");
	}
	
	@RequestMapping("/helloworld")
	public String hello() {
		System.out.println("hello world success");
		System.out.println(userService);
		return "success";
	}
}
