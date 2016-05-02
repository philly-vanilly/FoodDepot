package Management.Model;

public class Registration {
	
	private String id = "asdas" ;
	private String firstName;
	private String lastName;
	
	private String eMail;
	private String userName;
	private String password;
	
	public Registration(String firstName, String lastName,String userName, String password){
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setPreName(String firstName) {
		this.firstName = firstName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	
 
	
}
