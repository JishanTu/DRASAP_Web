package tyk.drasap.springfw.bean;

import java.util.List;

public class Action {
	private String name;
	private String path;
	private String type;
	private List<Forward> forwards;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Forward> getForwards() {
		return forwards;
	}

	public void setForwards(List<Forward> forwards) {
		this.forwards = forwards;
	}
}
