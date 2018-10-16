<%@ page language="java" contentType="text/html; charset=UTF-8"
    import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<form action="testConversionServiceConverter" method="POST">
		<!-- lastName-email-gender-department.id 例如: GG-gg@163.com-0-105 -->
		Employee:<input type="text" name="employee"/>
		<input type="submit" value="Submit"/>
	</form>
	<br><br>
	<!-- 
		1.为什么使用 form 标签？
		可以更快速的开发出表单页面，而且可以更方便的进行表单值的回显
		2.注意:
		可以通过 ModelAttribute 属性指定绑定的模型属性，
		若没有指定该属性，则默认从 request 域对象中读取 command 的表单 bean，
		如果该属性值也不存在，则会发生错误。
	 -->
	 <br><br>
	 <form:form action="emp" method="POST" modelAttribute="employee">
	 
	 	<form:errors path="*"></form:errors>
	 	<br>
	 
	 	<c:if test="${employee.id==null }">
	 		<!-- path 属性对应 html 表单标签的 name 属性值 -->
	 		LastName:<form:input path="lastName"/>
	 		<form:errors path="lastName"></form:errors>
	 	</c:if>
	 	<c:if test="${employee.id!=null }">
	 		<form:hidden path="id"/>
	 		<input type="hidden" name="_method" value="PUT"/>
	 		<%-- 
	 			对于 _method 不能使用 form:hidden 标签，因为 ModelAttribute 对应的 bean 中 
				没有 _method 这个属性	 		
	 		--%>
	 	</c:if>
	 	
	 	<br>
	 	Email:<form:input path="email"/>
	 	<form:errors path="email"></form:errors>
	 	<br>
	 	<%
	 		Map<String,String> genders = new HashMap();
	 		genders.put("1","Male");
	 		genders.put("0","Female");
	 		
	 		request.setAttribute("genders",genders);
	 	%>
	 	Gender:
	 	<br>
	 	<form:radiobuttons path="gender" items="${genders }" delimiter="<br>"/>
	 	<br>
	 	Department:<form:select path="department.id" 
	 		items="${departments }" itemLabel="departmentName" itemValue="id"></form:select>
	 	<br>
	 	<!-- 
	 		1.数据转化问题
	 		2.数据格式问题
	 		3.数据校验问题
	 		1).如何校验？注解？
	 		①使用 JSR 303 标准
	 		②加入 hibernate validator 验证框架的 jar 包
	 		③需要在 SpringMVC 配置文件中添加 <mvc:annotation-driven/>
	 		④需要在 Bean 的属性上添加对应的注解
	 		⑤在目标方法 Bean 类型的前面添加 @Valid 注解
	 		2).验证出错转向到哪一个页面
	 		注意：需要校验的 Bean 对象和其绑定结果对象或错误对象是成对出现的，他们之间不允许声明其他的入参
	 		3).错误消息?如何显示，如何把消息进行国际化 
	 	 -->
  		Birth:<form:input path="birth"/>
  		<form:errors path="birth"></form:errors>
		<br>
		Salary:<form:input path="salary"/>
		<br>
	 	<input type="submit" value="Submit"/>
	 </form:form>
	 
</body>
</html>