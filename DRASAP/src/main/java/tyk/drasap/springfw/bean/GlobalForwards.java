package tyk.drasap.springfw.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalForwards {

	@JsonProperty("forwards")
	private List<Forward> forwards;

	public List<Forward> getForwards() {
		return forwards;
	}

	public void setForwards(List<Forward> forwards) {
		this.forwards = forwards;
	}
}
