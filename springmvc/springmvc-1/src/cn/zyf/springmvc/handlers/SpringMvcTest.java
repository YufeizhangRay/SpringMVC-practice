package cn.zyf.springmvc.handlers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import cn.zyf.springmvc.entities.User;

@SessionAttributes(value = {"user"}, types = {String.class})
@RequestMapping("/springmvc")
@Controller
public class SpringMvcTest {
	
	private static final String SUCCESS = "Success";
	
	@RequestMapping("/testRedirect")
	public String testRedirect() {
		System.out.println("testRedirect");
		return "redirect:/index.jsp";
	}
	
	@RequestMapping("/testView")
	public String testView() {
		System.out.println("testView");
		return "helloView";
	}
	
	@RequestMapping("/testViewAndViedResolver")
	public String testViewAndViedResolver() {
		System.out.println("testViewAndViedResolver");
		return SUCCESS;
	}
	
	/**
	 * 1. 有 @ModelAttribute 标记的方法，会在每个目标方法执行之前被 SpringMVC 调用！
	 * 2. @ModelAttribute 也可以来修饰目标方法 POJO 类型的入参，其 value 属性值有如下的作用：
	 * 1).SpringMVC 会使用 value 属性值在 implicitModel 中查找对应的对象，若存在则会直接传入到目标方法的入参中
	 * 2).SpringMVC 会以 value 为 key，POJO 类型的的对象为 value，存入到 request 中
	 */
	@ModelAttribute
	public void getUesr(@RequestParam(value = "id", required = false) Integer id,
			Map<String,Object> map) {
		System.out.println("ModelAttribute Method ");
		if(id!=null) {
			//模拟从数据库中获取对象
			User user = new User("Ray","123456", "ray@qq.com",24, 1);
			System.out.println("从数据库中获取一个对象: "+user);
			
			map.put("user",user);
		}
	}
	
	/**
	 * 执行 @ModelAttribute 注解修饰的方法：从数据库中取出对象，把对象放到了 Map 中，键为：user
	 * SpringMVC 从 Map 中取出 User 对象，并把表单的请求参数赋给 User 对象的对应属性
	 * SpringMVC 把上述对象传入目标方法的参数
	 * 
	 * 注意：在@ModelAttribute 修饰的方法中，放入到 Map 的键需要和目标方法入参类型的第一个字母为小写的字符串一致！
	 * 
	 * SpringMVC 确定目标方法 POJO 类型入参的过程
	 * 1.确定一个key
	 * 1).若目标方法的 POJO 类型的参数没有使用 @ModelAttribute 作为修饰，则 key 为 POJO 类名第一个字母的小写
	 * 2).若使用了 @ModelAttribute 来修饰，则 key 为 @ModelAttribute 注解的 value 属性值
	 * 2.在 implicitModel 中查找 key 对应的对象，若存在，则作为入参传入
	 * 1).若在 @ModelAttribute 标记的方法中在 Map 中保存过，且 key 和 1 确定的 key 一致，则会获取到
	 * 3.若 implicitModel 中不存在 key 对应的对象，则检查当前的 Handler 是否使用 @SessionAttributes 注解修饰
	 *   若使用了该注解，且 @SessionAttributes 注解的 value 属性值中包含了 key，则会从 HttpSession 中获取 key 
	 *   所对应的 value 值，若存在则直接传入到目标方法的入参中，若不存在则将抛出异常
	 * 4.若 Handler 没有标识 @SessionAttributes 注解或 @SessionAttributes 的 value 值中不包含 key， 则
	 *   会通过反射来创建 POJO 类型的参数，传入为目标方法的参数
	 * 5.SpringMVC 会把 key 和 POJO 类型的对象保存到 implicitModel 中，进而会保存到 request 中
	 * 
	 * 源代码分析的流程
	 * 1.调用 @ModelAttribute 注解修饰的方法，实际上是把 @ModelAttribute 方法中 Map 中的数据放在了 implicitModel 中
	 * 2.解析请求处理器的目标参数，实际上该目标的参数来自于 WebDataBinder 对象的 target 属性
	 * 1).创建 WebDataBinder 对象
	 * ①.确定 objectName 属性：若传入的 attrName 属性值为""，则 objectName 为类名第一个字母小写
	 * 注意：attrName. 若目标方法的 POJO 属性使用了 @ModelAttribute 来修饰，则 attrName 值即为 @ModelAttribute
	 * 的 value 属性值
 	 * ②.确定 target 属性：
 	 * > 在 implicitModel 中查找 attrName 对应的属性值. 若存在，ok
 	 * > 若不存在：验证当前 Handler 是否使用了 @SessionAttributes 进行修饰，若使用了，则尝试从 Session 中
 	 *   获取 attrName 所对应的属性值. 若 Session 中没有对应的属性值，则抛出异常
 	 * > 若 Handler 没有使用 @SessionAttributes 进行修饰，或 @SessionAttributes 没有使用 value 值指定的 key
 	 *   和 attrName 相匹配，则通过反射创建了 POJO 对象
 	 * 2).SpringMVC 把表单的参数赋给了 WebDataBinder 的 target 对应的属性
 	 * 3).SpringMVC 会把 WebDataBinder 的 attrName 和 target 给到 implicitModel. 进而传到 request 对象域中
 	 * 4).把 WebDataBinder 的 target 作为参数传递给目标参数的入参 
	 */
	@RequestMapping("/testModelAttribute")
	public String testModelAttribute(@ModelAttribute("user") User user) {
		System.out.println("修改： "+user);
		return SUCCESS;
	}
	
	/**
	 * @SessionAttributes 除了可以通过属性名指定需要放到会话中的属性外(实际上使用的是 value 属性值)
	 * 还可以通过模型属性的对象指定哪些模型属性需要放到会话中(实际上使用的是 types 属性值，批量处理)
	 * 
	 * 会在方法中的 model.addAttribute("Users", users);
	 * 把键值对放入 model(map集合) 中，最终会放入 Request 域中，
	 * 键值对放入 Request 域中的同时，理解为把这个键值对的一个副本，同时放入了 HttpSession 域空间里
	 * 
	 * 注意：该注解只能放在类的上面，而不能放在方法上面
	 */
	@RequestMapping("/testSessionAttributes")
	public String testSessionAttributes(Map<String, Object> map) {
		User user = new User("Ray","123","ray@gmail.com",24);
		map.put("user", user);
		map.put("school", "NJ");
		return SUCCESS;
	}
	
	/**
	 * 目标方法可以添加 Map 类型(实际上也可以是 Model 类型或者 ModelMap 类型)的参数
	 */
	@RequestMapping("/testMap1")
	public String testMap1(Model model) {
		//虽然方法中 把键值对放进了 model(map集合) 中，这个 map 集合的数据最终会放入 Request 域中
		model.addAttribute("names", Arrays.asList("Tom", "Jerry", "Mike"));
		List<Object> users = new ArrayList<>();
		User user1 = new User("Tom", "123", "Tom", 11);
		User user2 = new User("Jerry", "123", "Jerry", 22);
		User user3 = new User("Mike", "123", "Mike", 33);
		users.add(user1);
		users.add(user2);
		users.add(user3);
		model.addAttribute("Users", users);
		return SUCCESS;
	}
	
	/**
	 * 目标方法可以添加 Map 类型(实际上也可以是 Model 类型或者 ModelMap 类型)的参数
	 */
	@RequestMapping("/testMap")
	public String testMap(Map<String, Object> map) {
		System.out.println(map.getClass().getName());
		map.put("names", Arrays.asList("Tom", "Jerry", "Mike"));
		List<Object> users = new ArrayList<>();
		User user1 = new User("Tom", "123", "Tom", 11);
		User user2 = new User("Jerry", "123", "Jerry", 22);
		User user3 = new User("Mike", "123", "Mike", 33);
		users.add(user1);
		users.add(user2);
		users.add(user3);
		map.put("Users", users);
		return SUCCESS;
	}
	
	/**
	 * 目标方法可以添加 List 类型
	 */
	@RequestMapping("/testList")
	public ModelAndView testList() {
		String viewName = SUCCESS;
		ModelAndView modelAndView = new ModelAndView(viewName);
		List<Object> users = new ArrayList<>();
		System.out.println(users.getClass().getName());
		User user1 = new User("Tom", "123", "Tom", 11);
		User user2 = new User("Jerry", "123", "Jerry", 22);
		User user3 = new User("Mike", "123", "Mike", 33);
		users.add(user1);
		users.add(user2);
		users.add(user3);
		modelAndView.addObject("users", users);
		return modelAndView;
	}
	
	/**
	 * 目标方法的返回值可以是 ModelAndView 类型。
	 * 其中可以包含视图和模型信息
	 * SpringMVC 会把 ModelAndView 的 model 中数据放入到 request 域对象中。
	 */
	@RequestMapping("/testModelAndView")
	public ModelAndView testModelAndView() {
		String viewName = SUCCESS;
		ModelAndView modelAndView = new ModelAndView(viewName);
		
		//添加模型数据到 ModelAndView 中
		modelAndView.addObject("time", new Date());
		
		return modelAndView;
	}
	
	/**
	 * 可以使用 Servlet 原生的 API 作为目标方法的参数具体支持以下类型
	 * 
	 * HttpServletRequest
	 * HttpServletRequest
	 * HttpSession
	 * java.security.Principal
	 * locale InputStream
	 * outputStream
	 * Reader
	 * Writer
	 * @throws IOException 
	 */
	@RequestMapping("/testServletAPI")
	public void testServletAPI(HttpServletRequest request,
			HttpServletResponse response, Writer out) throws IOException {
		System.out.println("testServletAPI: "+request+", "+response);
		out.write("hello springmvc");
		//return SUCCESS;
	}
	
	/**
	 * Spring MVC 会按请求参数名和 POJO属性名进行自动匹配
	 * 自动为该对象填充属性值，支持级联属性。
	 * 如address.province等
	 */
	@RequestMapping("/testPojo")
	public String testPojo(User user) {
		System.out.println("testPojo:"+user);
		return SUCCESS;
	}
	
	/**
	 * 了解：
	 * @CookieValue ：映射一个 Cookie 值，属性同 @RequestParam
	 */
	@RequestMapping("/testCookieValue")
	public String testCookieValue(@CookieValue("JSESSIONID") String sessionId) {
		System.out.println("testRequestHeader: sessionId: "+sessionId);
		return SUCCESS;
	}
	
	/**
	 * 了解:
	 * 映射请求头信息
	 * 方法同 @RequestParam
	 */
	@RequestMapping("/testRequestHeader")
	public String testRequestHeader(@RequestHeader(value = "Accept-Language") String al) {
		System.out.println("testRequestHeader, Accept-Language:"+al);
		return SUCCESS;
	}
	
	/**
	 * @RequestParam 来映射请求参数，GET 和 POST 都可以
	 * value 即请求参数的参数名
	 * 若没有设置 value 则参数名必须和表单里的 name 名称一致
	 * required 该参数是否必须
	 * defaultValue 请求参数的默认值
	 */
	@RequestMapping(value = "/testRequestParam")
	public String testRequestParam(@RequestParam(value = "username") String un,
			@RequestParam(value = "age",required = false, defaultValue = "0") int age) {//int 不能复制 null
		System.out.println("testRequestParam, username:" + un + ", age:" + age);
		return SUCCESS;
	}
	
	/**
	 * Rest 风格的 URL
	 * 以 CRUD 为例：
	 * 新增：/order POST
	 * 修改：/order/1 PUT     update?id=1
	 * 获取：/order/1 GET     get?id=1
	 * 删除：/order/1 DELETE  delete?id=1
	 * 
	 * 如何发送 PU T请求和 DELETE 请求？
	 * 1.配置HiddenHttpMethodFilter
	 * 2.需要发送POST请求
	 * 3.发送POST请求时携带一个name = “_method”的隐藏域，值为DELETE或PUT
	 * 
	 * 在SpringMVC中的目标方法中如何得到id呢？
	 * 使用@PathVariable注解
	 */
	@RequestMapping(value = "/testRest/{id}",method = RequestMethod.PUT)
	@ResponseBody
	public String testRestPut(@PathVariable Integer id) {
		System.out.println("testRest PUT: "+id);
		return SUCCESS;
	}
	
	@RequestMapping(value = "/testRest/{id}",method = RequestMethod.DELETE)
	@ResponseBody
	public String testRestDelete(@PathVariable Integer id) {
		System.out.println("testRest DELETE: "+id);
		return SUCCESS;
	}
	
	@RequestMapping(value = "/testRest",method = RequestMethod.POST)
	public String testRest() {
		System.out.println("testRest POST");
		return SUCCESS;
	}
	
	@RequestMapping(value = "/testRest/{id}",method = RequestMethod.GET)
	public String testRest(@PathVariable Integer id) {
		System.out.println("testRest GET: "+id);
		return SUCCESS;
	}
	
	/**
	 * @PathVariable 可以来映射URL的占位符到目标参数方法中
	 */
	@RequestMapping("/testPathVariable/{id}")
	public String testPathVariable(@PathVariable("id") Integer id) {
		System.out.println("testPathVariable: "+id);
		return SUCCESS;
	}
	
	@RequestMapping("/testAntPath/*/abc")
	public String testAntPath() {
		System.out.println("testAntPath");
		return SUCCESS;
	}
	
	/**
	 * 了解：
	 * 可以使用 params 和 headers 更加精确地映射请求。 params 和 headers 支持简单表达式。
	 */
	@RequestMapping(value="testParamsAndHeaders",params= {"username","age!=10"},headers = {"Accept-Language=en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"})
	public String testParamsAndHeaders() {
		System.out.println("testParamsAndHeaders");
		return SUCCESS;
	}
	
	/**
	 * 常用：
	 * 使用 method 属性来指定请求方式
	 */
	@RequestMapping(value = "/testMethod",method = RequestMethod.POST)
	public String testMethod() {
		System.out.println("testMethod");
		return SUCCESS;
	}
	
	/**
	 * 1. @RequestMapping 除了修饰方法，也可以修饰类
	 * 2. 类定义处：提供初步请求的映射信息。相对于 WEB 应用的根目录
	 *    方法处：提供进一步的细分映射信息。相对于类定义处的 URL 。
	 *    若类定义处未标注 @RequestMapping ,则方法处标记的URL相对于 WEB 应用的根目录
	 */
	@RequestMapping("/testRequsetMapping")
	public String testRequsetMapping() {
		System.out.println("testRequsetMapping");
		return SUCCESS;  //返回的对应的视图的名字(jsp文件)
	}

}
