package Management.Service;

import Management.Model.box.BoxImpl;

public interface BoxService {
	public boolean openBox(String id);
	public boolean createBox(BoxImpl box);
}
