import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private MessageType type;
	private String requestId;
	private String from;
	private String to;
	private String searchKey;
	private Integer hopCount;
	private List<String> neighbourList;
	private String fileName;
	private List<Tuple> resultTuples;

	public Message(MessageType type, String requestId, String from, String to,
			String searchKey, List<String> neighboutList, String fileName,
			Integer hopCount, List<Tuple> resultTuples) {
		this.type = type;
		this.requestId = requestId;
		this.from = from;
		this.to = to;
		this.searchKey = searchKey;
		this.neighbourList = neighboutList;
		this.fileName = fileName;
		this.hopCount = hopCount;
		this.resultTuples = resultTuples;
	}

	public List<Tuple> getResultTuples() {
		return resultTuples;
	}

	public void setResultTuples(List<Tuple> resultTuples) {
		this.resultTuples = resultTuples;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Integer getHopCount() {
		return hopCount;
	}

	public void setHopCount(Integer hopCount) {
		this.hopCount = hopCount;
	}

	public List<String> getNeighbourList() {
		return neighbourList;
	}

	public void setNeighbourList(List<String> neighbourList) {
		this.neighbourList = neighbourList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
