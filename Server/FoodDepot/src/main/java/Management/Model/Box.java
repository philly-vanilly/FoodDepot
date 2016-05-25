package Management.Model;

import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

public class Box {
	
	private String id;
	
	private String name;
	
	@GeoSpatialIndexed
	private double[] position;
	
	private String content;
	
	private boolean open = false;
	
	public Box(String id, String name, double[] position, String content){
		this.id = id;
		this.name = name;
		this.position = position;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}



	public void open() {
		this.open = true;
	}
	public void close() {
		this.open = false;
	}
	
}
