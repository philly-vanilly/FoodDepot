package Management.Model;

public class Account {
	
	private String id = "asdas" ;
	private String preName;
	private String lastName;
	
	public Account(String preName, String lastName){
		this.preName = preName;
		this.lastName = lastName;
	}
	
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPreName() {
		return preName;
	}
	public void setPreName(String preName) {
		this.preName = preName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	
 
	
}
