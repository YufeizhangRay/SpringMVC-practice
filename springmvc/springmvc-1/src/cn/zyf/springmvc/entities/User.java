package cn.zyf.springmvc.entities;

public class User {
	
	private String username;
	private String password;
	
	private Address address;
	
	private String email;
	private int age; 
	
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	/*@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", address=" + address + ", email=" + email
				+ ", age=" + age + "]";
	}*/
	
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", email=" + email + ", age=" + age + ", id="
				+ id + "]";
	}
	public User(String username, String password, String email, int age) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.age = age;
	}

	public User(String username, String password, String email, int age, Integer id) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.age = age;
		this.id = id;
	}

	public User() {
		
	}
	
}
