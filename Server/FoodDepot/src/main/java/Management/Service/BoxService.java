package Management.Service;

import Management.Model.Box;

public interface BoxService {
	public boolean openBox(String id);
	public boolean createBox(Box box);
}
