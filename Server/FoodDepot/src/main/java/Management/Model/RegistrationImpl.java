package Management.Model;

public class RegistrationImpl implements AccountClient{
	
	private String firstName;
	private String lastName;
	
	private String email;
	private String username;
	private String password;
	
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	


	public String getUsername() {
		return username;
	}


	public void setUsername(String userName) {
		this.username = userName;
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

	
 
	
}
