import java.io.Serializable;

public class Tuple implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String keyword;
	private String filename;
	private String nodeId;
	private Integer hopCount;
	private long elapsedTime;

	public Tuple(String keyword, String filename, String nodeId) {
		this.keyword = keyword;
		this.filename = filename;
		this.nodeId = nodeId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getHopCount() {
		return hopCount;
	}

	public void setHopCount(Integer hopCount) {
		this.hopCount = hopCount;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

}
