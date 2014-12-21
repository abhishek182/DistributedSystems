import java.io.Serializable;

public class LogMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String action;
	private int localTimestamp;

	public LogMessage(int id, String action, int localTimestamp) {
		this.id = id;
		this.action = action;
		this.localTimestamp = localTimestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getLocalTimestamp() {
		return localTimestamp;
	}

	public void setLocalTimestamp(int localTimestamp) {
		this.localTimestamp = localTimestamp;
	}
	
	
}
