package Management.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import Management.Model.box.BoxImpl;

@Service
public class BoxServiceImpl implements BoxService {
	
	@Autowired  
	private MongoOperations operations; 
	
	
	@Override
	public boolean createBox(BoxImpl box) {
		if(box != null){
			operations.save(box, "boxes");
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public boolean openBox(String id) {
		Criteria idCrit = Criteria.where("_id").is(id);
    	BoxImpl box =  operations.findOne(new Query(idCrit),BoxImpl.class, "boxes");
        
    	if(box != null){
    		box.open();
    		return true;
    	} else {
    		return false;
    	}
	}
	
}
