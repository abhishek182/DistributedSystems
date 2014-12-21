import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Node {
	private String id;
	private Boolean joinedPeer;
	private Boolean replyTimeout;
	private Boolean receivedReply;
	private Queue<Message> requestQueue;
	private Queue<Message> senderQueue;
	private Queue<Message> recievingQueue;
	private List<String> neighbourList;
	private String myRequestId;
	private int requestCounter;
	private JSONArray hosts;
	private List<Tuple> resultTuples;
	private Boolean forwardSearchTimeout;
	private Integer currentHopCount;
	private long searchStartTime;

	public Node(String id) {
		this.id = id;
		this.joinedPeer = false;
		this.replyTimeout = false;
		this.requestQueue = new LinkedList<Message>();
		this.senderQueue = new LinkedList<Message>();
		this.recievingQueue = new LinkedList<Message>();
		this.neighbourList = new LinkedList<String>();
		this.requestCounter = 0;
		this.hosts = null;
		this.resultTuples = new LinkedList<Tuple>();
		this.forwardSearchTimeout = false;
		this.receivedReply = false;
	}

	public List<Tuple> getResultTuples() {
		return resultTuples;
	}

	public void setResultTuples(List<Tuple> resultTuples) {
		this.resultTuples = resultTuples;
	}

	public Boolean getForwardSearchTimeout() {
		return forwardSearchTimeout;
	}

	public void setForwardSearchTimeout(Boolean forwardSearchTimeout) {
		this.forwardSearchTimeout = forwardSearchTimeout;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isJoinedPeer() {
		return joinedPeer;
	}

	public void setJoinedPeer(boolean joinedPeer) {
		this.joinedPeer = joinedPeer;
	}

	public Queue<Message> getRequestQueue() {
		return requestQueue;
	}

	public void setRequestQueue(Queue<Message> requestQueue) {
		this.requestQueue = requestQueue;
	}

	public Queue<Message> getSenderQueue() {
		return senderQueue;
	}

	public void setSenderQueue(Queue<Message> senderQueue) {
		this.senderQueue = senderQueue;
	}

	public List<String> getNeighbourList() {
		return neighbourList;
	}

	public void setNeighbourList(List<String> neighbourList) {
		this.neighbourList = neighbourList;
	}

	public Queue<Message> getRecievingQueue() {
		return recievingQueue;
	}

	public void setRecievingQueue(Queue<Message> recievingQueue) {
		this.recievingQueue = recievingQueue;
	}

	public int getRequestCounter() {
		return requestCounter;
	}

	public void setRequestCounter(int requestCounter) {
		this.requestCounter = requestCounter;
	}

	public Boolean getReplyTimeout() {
		return replyTimeout;
	}

	public void setReplyTimeout(Boolean replyTimeout) {
		this.replyTimeout = replyTimeout;
	}

	public String getMyRequestId() {
		return myRequestId;
	}

	public void setMyRequestId(String myRequestId) {
		this.myRequestId = myRequestId;
	}

	public JSONArray getHosts() {
		return hosts;
	}

	public void setHosts(JSONArray hosts) {
		this.hosts = hosts;
	}

	public Boolean getJoinedPeer() {
		return joinedPeer;
	}

	public void setJoinedPeer(Boolean joinedPeer) {
		this.joinedPeer = joinedPeer;
	}

	public Boolean getReceivedReply() {
		return receivedReply;
	}

	public void setReceivedReply(Boolean receivedReply) {
		this.receivedReply = receivedReply;
	}

	public Integer getCurrentHopCount() {
		return currentHopCount;
	}

	public void setCurrentHopCount(Integer currentHopCount) {
		this.currentHopCount = currentHopCount;
	}

	public long getSearchStartTime() {
		return searchStartTime;
	}

	public void setSearchStartTime(long searchStartTime) {
		this.searchStartTime = searchStartTime;
	}

	public void deliverMessage(Message message) {
		switch (message.getType()) {
		case REQUEST:
			System.out.println("REQUEST message from " + message.getFrom()
					+ " with hop count " + message.getHopCount()
					+ " with REQ id :" + message.getRequestId());
			if (!checkIfMessageAlreadyInQueue(message)) {
				System.out.println(Thread.currentThread().getName()
						+ " : New Request Received...");
				handleRequestMessage(message);
			} else {
				System.out.println("Request with this id already handled.");
			}
			break;
		case REPLY:
			System.out.println("REPLY message from " + message.getFrom()
					+ " for REQ Id " + message.getRequestId());
			if (replyToMyRequest(message)) {
				this.setReceivedReply(true);
				if (!replyTimeout) {
					System.out.println("Number of result received : "
							+ message.getResultTuples().size());
					for (Tuple resultTuple : message.getResultTuples()) {
						// System.out.println("Result tuple : " + );
						if (!ifTupleAlreadyExists(resultTuple)) {
							resultTuple.setHopCount(this.currentHopCount);
							resultTuple
									.setElapsedTime(System.currentTimeMillis()
											- this.searchStartTime);
							this.resultTuples.add(resultTuple);
						}
					}

					System.out.println("Number of results so far : "
							+ this.resultTuples.size());
					// removeRequestFromQueueWithId(message.getRequestId());
					// System.out.println("After deleting my own request");
					printMyRequestQueueIds();
				} else {
					System.out.println("Reply rejected timeout occurred.");
				}
			} else {
				if (!this.forwardSearchTimeout) {
					String requester = findRequesterOfMessage(message);
					List<Tuple> resultToBeSent = message.getResultTuples();
					if (requester != null) {
						Message replyBackToRequester = new Message(
								MessageType.REPLY, message.getRequestId(),
								this.id, requester, message.getSearchKey(),
								null, null, null, resultToBeSent);
						addMessageToSenderQueue(replyBackToRequester);
					} else {
						System.out.println(Thread.currentThread().getName()
								+ ": Requester is null in the request queue.");
					}
				}

			}
			break;
		case JOIN:
			System.out.println("JOIN message from " + message.getFrom());
			this.neighbourList.add(message.getFrom());
			Message acceptMessage = new Message(MessageType.ACCEPT, null,
					this.id, message.getFrom(), null, null, null, null, null);
			addMessageToSenderQueue(acceptMessage);
			System.out.println("My neighbours are : "
					+ this.neighbourList.toString());
			break;
		case ACCEPT:
			System.out.println("ACCEPT message from " + message.getFrom());
			this.neighbourList.add(message.getFrom());
			this.setJoinedPeer(true);
			if (!hosts.contains(this.id)) {
				addSelfToHostsFile();
			} else {
				System.out.println("Host already present in the list.");
			}
			System.out.println("My neighbours are : "
					+ this.neighbourList.toString());
			break;
		case UPDATENEIGHBOUR:
			System.out.println("UPDATENEIGHBOR message from "
					+ message.getFrom());
			List<String> listOfNewNeighbor = message.getNeighbourList();
			System.out.println("Received neighbour list : "
					+ listOfNewNeighbor.toString());

			for (String neighbour : listOfNewNeighbor) {
				if (!this.neighbourList.contains(neighbour))
					if (neighbour.compareTo(this.id) != 0) {
						Message msg = new Message(MessageType.JOIN, null,
								this.id, neighbour, null, null, null, null,
								null);
						addMessageToSenderQueue(msg);
					}
			}
			System.out.println("My neighbours are : "
					+ this.neighbourList.toString());
			break;
		case DEPART:
			String frm = message.getFrom();
			for (String neighbour : this.neighbourList) {
				if (frm.compareTo(neighbour) == 0) {
					this.neighbourList.remove(neighbour);
					break;
				}
			}
			System.out.println("My neighbours are : "
					+ this.neighbourList.toString());
			break;
		case FETCH:
			try {
				int port = message.getHopCount();
				Socket socket = new Socket(message.getFrom(), port);
				String hostName = InetAddress.getLocalHost().getHostName();
				String subDir = hostName.split("\\.")[0];
				String fullDir = subDir.concat("/" + message.getFileName());
				File myFile = new File(fullDir);
				InputStream inputStream = new FileInputStream(myFile);
				OutputStream outputStream = socket.getOutputStream();
				byte[] buffer = new byte[8192];
				int len = 0;
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private boolean ifTupleAlreadyExists(Tuple resultTuple) {
		for (Tuple tuple : this.resultTuples) {
			if (tuple.getFilename().compareTo(resultTuple.getFilename()) == 0
					&& tuple.getKeyword().compareTo(resultTuple.getKeyword()) == 0
					&& tuple.getNodeId().compareTo(resultTuple.getNodeId()) == 0) {
				return true;
			}
		}
		return false;
	}

	private void printMyRequestQueueIds() {
		System.out.println("MyRequest Queue id are:");
		for (Message msg : requestQueue) {
			System.out.print(msg.getRequestId() + ",");
		}
		System.out.println();

	}

	private void removeRequestFromQueueWithId(String requestId) {
		synchronized (requestQueue) {
			for (Message requestMessage : this.requestQueue) {
				if (requestMessage.getRequestId().compareTo(requestId) == 0) {
					requestQueue.remove(requestMessage);
				}
			}
		}

	}

	private String findRequesterOfMessage(Message message) {
		for (Message requestMessage : this.requestQueue) {
			if (requestMessage.getRequestId().compareTo(message.getRequestId()) == 0) {
				System.out.println("Returning " + requestMessage.getFrom());
				return requestMessage.getFrom();
			}
		}
		return null;
	}

	private boolean replyToMyRequest(Message message) {
		if (this.myRequestId != null) {
			if (message.getRequestId().compareTo(this.myRequestId) == 0) {
				return true;
			}
		}
		return false;
	}

	public void joinPeerGroup(String targetHost) {
		Message joinMessage = new Message(MessageType.JOIN, null, this.id,
				targetHost, null, null, null, null, null);
		addMessageToSenderQueue(joinMessage);
	}

	@SuppressWarnings("unchecked")
	public void addSelfToHostsFile() {
		hosts.add(this.id);
		try {
			FileWriter writer = new FileWriter("Hosts.json", false);
			writer.write(hosts.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Host added to the peer group.");
	}

	public void addMessageToSenderQueue(Message message) {
		synchronized (this.senderQueue) {
			this.senderQueue.add(message);
		}
	}

	public void addMessageToRecievingQueue(Message message) {
		synchronized (this.recievingQueue) {
			this.recievingQueue.add(message);
		}
	}

	public void addRequestToQueue(Message message) {
		synchronized (this.requestQueue) {
			this.requestQueue.add(message);
		}
	}

	public void initiateSearchRequest(String searchKey) {
		this.requestCounter++;
		this.myRequestId = this.id + this.requestCounter;
		Message newSearchRequest = new Message(MessageType.REQUEST,
				myRequestId, id, null, searchKey, null, null, null, null);

		addRequestToQueue(newSearchRequest);
		System.out.println(Thread.currentThread().getName()
				+ " : Request added to queue");
		printMyRequestQueueIds();
		this.resultTuples.clear();
		Thread searchThread = new Thread(new Search(this), "Search");
		searchThread.start();
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void initiateSearch(Message requestMessage) {
		this.currentHopCount = 1;
		this.searchStartTime = System.currentTimeMillis();
		this.setReceivedReply(false);
		while (this.currentHopCount <= 16) {
			System.out
					.println("Search with hopcount : " + this.currentHopCount);
			requestMessage.setHopCount(this.currentHopCount);
			searchAround(requestMessage);
			this.setReplyTimeout(false);
			try {
				Thread.sleep(this.currentHopCount * 150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out
					.println("Timeout for hopcount : " + this.currentHopCount);

			this.setReplyTimeout(true);
			if (this.getReceivedReply()) {
				break;
			} else {
				this.currentHopCount = this.currentHopCount * 2;
			}
		}
		removeRequestFromQueueWithId(requestMessage.getRequestId());
		synchronized (this) {
			notifyAll();
		}

	}

	public void searchAround(Message requestMessage) {
		for (String neighbourNode : this.getNeighbourList()) {
			Message newReqMessage = new Message(requestMessage.getType(),
					requestMessage.getRequestId(), requestMessage.getFrom(),
					neighbourNode, requestMessage.getSearchKey(), null, null,
					requestMessage.getHopCount(), null);
			this.addMessageToSenderQueue(newReqMessage);
		}
	}

	public void initiateForwardSearch(Message requestMessage) {
		Message frwdRequest = new Message(requestMessage.getType(),
				requestMessage.getRequestId(), this.getId(), null,
				requestMessage.getSearchKey(), null, null,
				requestMessage.getHopCount() - 1, null);
		searchAround(frwdRequest);
		this.setForwardSearchTimeout(false);
		try {
			Thread.sleep((requestMessage.getHopCount() - 1) * 150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()
				+ " : Timeout for forward search of hopcount "
				+ requestMessage.getHopCount());
		this.setForwardSearchTimeout(true);
		removeRequestFromQueueWithId(requestMessage.getRequestId());
		System.out.println(Thread.currentThread().getName()
				+ " : After deleting other request");
		printMyRequestQueueIds();
	}

	public void handleRequestMessage(Message message) {
		String searchKey = message.getSearchKey();
		boolean fileFound = false;
		List<JSONObject> validFileEntries = null;
		JSONObject validFileEntry = Util.getFileEntryByFileName(searchKey);
		if (validFileEntry == null) {
			validFileEntries = Util.getAllFileEntryByKeyword(searchKey);
			if (validFileEntries.size() != 0) {
				fileFound = true;
			}
		} else {
			fileFound = true;
		}
		if (fileFound) {
			System.out.println("File found need to send reply.");
			List<Tuple> resultTuples = null;
			if (validFileEntry != null) {
				resultTuples = Util.getResultTuplesFromValidEntry(
						validFileEntry, this.id, searchKey);
			} else {
				resultTuples = Util.getResultTuplesFromValidEntries(
						validFileEntries, this.id, searchKey);
			}
			Message replyMessage = new Message(MessageType.REPLY,
					message.getRequestId(), this.id, message.getFrom(),
					searchKey, null, null, null, resultTuples);
			addMessageToSenderQueue(replyMessage);
			removeRequestFromQueueWithId(message.getRequestId());
		} else {
			if (message.getHopCount() > 1 && this.neighbourList.size() > 1) {
				System.out
						.println(Thread.currentThread().getName()
								+ " : Forwarding request to neighbours and adding it to queue.");
				addRequestToQueue(message);
				printMyRequestQueueIds();
				Thread searchThread = new Thread(new Search(this), "Search");
				searchThread.start();
			} else {
				removeRequestFromQueueWithId(message.getRequestId());
			}
		}

	}

	public boolean checkIfMessageAlreadyInQueue(Message message) {
		for (Message requestMessage : this.requestQueue) {
			if (requestMessage.getRequestId().compareTo(message.getRequestId()) == 0) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("resource")
	public void fetchFileFrom(Tuple tuple) {
		Random random = new Random();
		int randomPort = random.nextInt(50);
		int port = 8000 + randomPort;
		Message fetchMessage = new Message(MessageType.FETCH, null, this.id,
				tuple.getNodeId(), null, null, tuple.getFilename(), port, null);
		addMessageToSenderQueue(fetchMessage);
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			String hostName = InetAddress.getLocalHost().getHostName();
			String subDir = hostName.split("\\.")[0];
			String fullDir = subDir.concat("/" + tuple.getFilename());
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = new FileOutputStream(fullDir);
			byte[] buffer = new byte[8192];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			socket.close();
			inputStream.close();
			outputStream.close();
			Util.addEntryToFileList(tuple.getKeyword(), tuple.getFilename());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void leavegroup() {
		JSONParser parser = new JSONParser();
		try {
			this.hosts = (JSONArray) parser.parse(new FileReader("Hosts.json"));
			this.hosts.remove(this.id);
			FileWriter writer = new FileWriter("Hosts.json", false);
			writer.write(this.hosts.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("Host is removed from the Hosts file.");
		if (this.neighbourList.size() != 0) {
			for (String neighbour : this.neighbourList) {
				Message departmsg = new Message(MessageType.DEPART, null,
						this.id, neighbour, null, null, null, null, null);
				addMessageToSenderQueue(departmsg);
			}
			Random rno = new Random();
			int randno = rno.nextInt(this.neighbourList.size());
			String neighbourhost = this.neighbourList.get(randno);
			List<String> oldNeighbours = new LinkedList<String>();
			for (String neighbor : this.neighbourList) {
				oldNeighbours.add(neighbor);
			}
			Message updateneighbour = new Message(MessageType.UPDATENEIGHBOUR,
					null, this.id, neighbourhost, null, oldNeighbours, null,
					null, null);
			addMessageToSenderQueue(updateneighbour);
			this.neighbourList.clear();
		}
	}
}
