package tyk.drasap.springfw.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionMappings {
	@JsonProperty("actions")
	private List<Action> actions;

	public List<Action> getActions() {
		return this.actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
}
