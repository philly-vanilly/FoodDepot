package Management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Management.Model.Box;
import Management.Model.Message;
import Management.Model.Login.Registration;

@RestController
public class BoxController {
	
	 @RequestMapping("/searchBox")
	    public Message searchBox(@RequestParam(value="latitude") String latitude, @RequestParam(value="longitude") String longitude,
	    				@RequestParam(value="keys", defaultValue="World") String keys) {
	    	
		 double[] location = {Double.parseDouble(latitude), Double.parseDouble(longitude)};
		 	
	    	Box box1 = new Box("1231232","Box 1", location ,"This is the content");
	    	
	    	Box box2 = new Box("1231232s2a","Box 2", location ,"This is the content");
	    	
	    	Box box3 = new Box("123123s2","Box 3", location ,"This is the content");
	    	
	    	Box[] data = {box1, box2, box3};
	    	
	    	
	    	return new Message(true, data);
	    }
}
