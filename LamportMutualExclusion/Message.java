import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private int timestamp;
	private int from;
	private int to;

	public Message(String type, int timestamp, int from, int to) {
		this.type = type;
		this.timestamp = timestamp;
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || object.getClass() != getClass()) {
			return false;
		} else {
			Message message = (Message) object;
			if (this.from == message.getFrom()
					&& this.timestamp == message.getTimestamp()) {
				return true;
			} else {
				return false;
			}

		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 31 * hash + this.getFrom();
		hash = 31 * hash + this.getTimestamp();
		return hash;
	}
}
