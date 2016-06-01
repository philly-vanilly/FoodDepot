package Management.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import Management.Model.Message;
import Management.Model.box.BoxClient;
import Management.Model.box.BoxImpl;
import Management.Service.BoxService;

@RestController
public class BoxController {
	
	private BoxService boxService;
	
	@Autowired
	public BoxController(BoxService boxService){
		this.boxService = boxService;
	}
	
	@RequestMapping("/openBox")
    public @ResponseBody Message openBox(@RequestParam(value="boxId") String boxId){
		
		boolean result = boxService.openBox(boxId);
		
		if(result){
			return new Message(true, "The Box opened");
		} else {
			return new Message(false, "You couldn't open the Box");
		}

	}
    	
	
	
	 @RequestMapping("/searchBox")
	    public @ResponseBody Message searchBox(@RequestParam(value="latitude") String latitude, @RequestParam(value="longitude") String longitude,
	    				@RequestParam(value="keys", defaultValue="World") String keys) {
	    	
		 double[] location1 = {53.599657,9.933604};
		 double[] location2 = {53.598603,9.931024};
		 double[] location3 = {53.593102,9.944177};
		 
	    	BoxClient box1 = new BoxImpl("1231232","Uni-Box 1", location1 ,"This is the content");
	    	
	    	BoxClient box2 = new BoxImpl("1231232s2a","Uni-Box 2", location2 ,"This is the content");
	    	
	    	BoxClient box3 = new BoxImpl("123123s2","Hagenbecks Box", location3 ,"This is the content");
	    	
	    	BoxClient[] data = {box1, box2, box3};
	    	
	    	
	    	return new Message(true, data);
	    }
}
