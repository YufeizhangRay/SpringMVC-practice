package cn.zyf.springmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

//	@Autowired
//	private HelloWord helloWord;
	
	public UserService() {
		System.out.println("UserService Constructor...");
		//System.out.println(helloWord);
	}
}
