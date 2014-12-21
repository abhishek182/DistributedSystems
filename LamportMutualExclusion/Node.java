import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Node {
	private int id;
	private int d;
	private int numberOfNodes;
	private Integer logicalClockValue = 0;
	private PriorityQueue<Message> requestQueue = new PriorityQueue<Message>(
			15, new MessageComparator());
	private int criticalSectionCounter = 0;
	private int applicationMsgsSentCounter = 0;
	private int replyMsgCounter = 0;
	private int requestMsgCounter = 0;
	private int releaseMsgCounter = 0;
	private int totalProtocolMessages = 0;
	private boolean updateClockLock = true;
	private boolean criticalSectionRequested = false;
	private Queue<Message> sendingQueue = new LinkedList<Message>();

	private class MessageComparator implements Comparator<Message> {

		@Override
		public int compare(Message m1, Message m2) {
			if (m1.getTimestamp() != m2.getTimestamp()) {
				return m1.getTimestamp() - m2.getTimestamp();
			} else {
				return m1.getFrom() - m2.getFrom();
			}
		}

	}

	public boolean isUpdateClockLock() {
		return updateClockLock;
	}

	public void setUpdateClockLock(boolean updateClockLock) {
		this.updateClockLock = updateClockLock;
	}

	public void setLogicalClockValue(Integer logicalClockValue) {
		this.logicalClockValue = logicalClockValue;
	}

	public Node(int id, int d, int numberOfNodes) {
		this.id = id;
		this.d = d;
		this.numberOfNodes = numberOfNodes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	public int getLogicalClockValue() {
		return logicalClockValue;
	}

	public void setLogicalClockValue(int logicalClockValue) {
		this.logicalClockValue = logicalClockValue;
	}

	public Queue<Message> getRequestQueue() {
		return requestQueue;
	}

	public void setRequestQueue(PriorityQueue<Message> requestQueue) {
		this.requestQueue = requestQueue;
	}

	public int getCriticalSectionCounter() {
		return criticalSectionCounter;
	}

	public void setCriticalSectionCounter(int criticalSectionCounter) {
		this.criticalSectionCounter = criticalSectionCounter;
	}

	public int getApplicationMsgsSentCounter() {
		return applicationMsgsSentCounter;
	}

	public void setApplicationMsgsSentCounter(int applicationMsgsSentCounter) {
		this.applicationMsgsSentCounter = applicationMsgsSentCounter;
	}

	public int getReplyMsgCounter() {
		return replyMsgCounter;
	}

	public void setReplyMsgCounter(int replyMsgCounter) {
		this.replyMsgCounter = replyMsgCounter;
	}

	public int getRequestMsgCounter() {
		return requestMsgCounter;
	}

	public void setRequestMsgCounter(int requestMsgCounter) {
		this.requestMsgCounter = requestMsgCounter;
	}

	public int getReleaseMsgCounter() {
		return releaseMsgCounter;
	}

	public void setReleaseMsgCounter(int releaseMsgCounter) {
		this.releaseMsgCounter = releaseMsgCounter;
	}

	public int getTotalProtocolMessages() {
		return totalProtocolMessages;
	}

	public void setTotalProtocolMessages(int totalProtocolMessages) {
		this.totalProtocolMessages = totalProtocolMessages;
	}

	public Queue<Message> getSendingQueue() {
		return sendingQueue;
	}

	public void setSendingQueue(Queue<Message> sendingQueue) {
		this.sendingQueue = sendingQueue;
	}

	public void deliverMessage(Message msg) {
		// Message replyMessage = null;
		updateClockFromOwnMsg();
		updateClockFromMsg(msg);
		if (msg.getType().compareTo("REQUEST") == 0) {

			addRequestToQueue(msg);
			System.out.println("Adding REPLY MSG to queue");
			sendMsgToNode(new Message("REPLY", logicalClockValue, id,
					msg.getFrom()));
		} else {
			/*
			 * if (!updateClockLock) { System.out.println(Thread.currentThread()
			 * + " has permission to deliver Msg"); } else {
			 * System.out.println(Thread.currentThread() +
			 * " don't have permission to deliver Msg"); } if (updateClockLock)
			 * { try { wait(); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }
			 */

			if (msg.getType().compareTo("REPLY") == 0) {
				replyMsgCounter++;
				/*
				 * System.out.println("Eligible " +
				 * eligibleToEnterCriticalSection());
				 */
				if (eligibleToEnterCriticalSection()) {
					enterCriticalSection();
				}

			} else if (msg.getType().compareTo("RELEASE") == 0) {
				synchronized (requestQueue) {
					if (requestQueue.peek().getFrom() == msg.getFrom()) {
						requestQueue.remove();
					} else {
						Iterator<Message> i = requestQueue.iterator();
						while (i.hasNext()) {
							Message m = i.next();
							if (m.getFrom() == msg.getFrom()) {
								requestQueue.remove(m);
							}

						}

					}
				}
				/*
				 * System.out.println("Eligible " +
				 * eligibleToEnterCriticalSection());
				 */
				if (eligibleToEnterCriticalSection()) {
					enterCriticalSection();
				}

			}
			/*
			 * updateClockLock = true; notify();
			 */
		}

	}

	/*
	 * public synchronized void processMsgFromOtherNodes(Message msg) {
	 * System.out.println("Lock in processMsgFromOtherNodes:" +
	 * updateClockLock); if (updateClockLock) { try { wait(); } catch
	 * (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * } if (msg.getType().compareTo("REPLY") == 0) { replyMsgCounter++;
	 * System.out.println(eligibleToEnterCriticalSection()); if
	 * (eligibleToEnterCriticalSection()) { enterCriticalSection(); }
	 * 
	 * } else if (msg.getType().compareTo("RELEASE") == 0) {
	 * requestQueue.remove();
	 * System.out.println(eligibleToEnterCriticalSection()); if
	 * (eligibleToEnterCriticalSection()) { enterCriticalSection(); }
	 * 
	 * } updateClockLock = true; notify(); }
	 */

	public void processReplyMsgFromOtherNodes(Message replyMessage) {
		/*
		 * if (updateClockLock) { System.out.println(Thread.currentThread() +
		 * " can deliver reply Msg"); } else {
		 * System.out.println(Thread.currentThread() +
		 * " cannot  deliver reply Msg"); } if (!updateClockLock) { try {
		 * wait(); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 */
		replyMsgCounter++;
		// System.out.println("Eligible " + eligibleToEnterCriticalSection());
		if (eligibleToEnterCriticalSection()) {
			enterCriticalSection();
		}
		/*
		 * updateClockLock = false; notify();
		 */
	}

	public void updateClockFromMsg(Message msg) {
		synchronized (logicalClockValue) {
			logicalClockValue = Math.max(logicalClockValue, msg.getTimestamp()
					+ d);
		}

	}

	public void updateClockFromOwnMsg() {
		synchronized (logicalClockValue) {
			logicalClockValue = logicalClockValue + d;
		}

	}

	public void prepareApplicationMsg(int target) {
		/*
		 * if (updateClockLock) { System.out.println(Thread.currentThread() +
		 * " can prepare application Msg"); } else {
		 * System.out.println(Thread.currentThread() +
		 * " cannot  prepare application Msg"); } if (!updateClockLock) { try {
		 * wait(); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 */
		updateClockFromOwnMsg();
		Message appMessage = new Message("APPLICATION", logicalClockValue, id,
				target);
		sendMsgToNode(appMessage);
		applicationMsgsSentCounter = applicationMsgsSentCounter + 1;
		/*
		 * updateClockLock = false; notify();
		 */
	}

	public void prepareRequestMsg() {
		/*
		 * if (updateClockLock) { System.out.println(Thread.currentThread() +
		 * " can prepare request Msg"); } else {
		 * System.out.println(Thread.currentThread() +
		 * " cannot  prepare request Msg"); } if (!updateClockLock) { try {
		 * wait(); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 */
		updateClockFromOwnMsg();
		int requestTimeStamp = logicalClockValue;
		for (int i = 0; i < numberOfNodes; i++) {
			if (i != id) {
				Message toSend = new Message("REQUEST", requestTimeStamp, id, i);
				requestMsgCounter++;
				sendMsgToNode(toSend);
			} else {
				addRequestToQueue(new Message("REQUEST", requestTimeStamp, id,
						i));
			}
		}
		// Message requestMessage = new Message("REQUEST", logicalClockValue,
		// id);
		// addRequestToQueue(requestMessage);
		// requestMsgCounter = requestMsgCounter + numberOfNodes - 1;
		// sendMsgToAllNodes(requestMessage, numberOfNodes);

		/*
		 * updateClockLock = false; notify();
		 */
	}

	public void prepareReleaseMsg() {
		/*
		 * if (updateClockLock) { System.out.println(Thread.currentThread() +
		 * " can prepare request Msg"); } else {
		 * System.out.println(Thread.currentThread() +
		 * " cannot  prepare request Msg"); } if (!updateClockLock) { try {
		 * wait(); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 */
		updateClockFromOwnMsg();
		int releaseTimestamp = logicalClockValue;
		for (int i = 0; i < numberOfNodes; i++) {
			if (i != id) {
				Message toSend = new Message("RELEASE", releaseTimestamp, id, i);
				releaseMsgCounter++;
				sendMsgToNode(toSend);
			}
		}
		/*
		 * updateClockLock = false; notify();
		 */
	}

	/*
	 * public Message getReplyMessage() { updateClockFromOwnMsg(); return new
	 * Message("REPLY", logicalClockValue, id);
	 * 
	 * }
	 */

	public void addRequestToQueue(Message msg) {
		synchronized (requestQueue) {
			requestQueue.add(msg);
			System.out.println("Node " + id + " --> Node " + msg.getFrom()
					+ "'s REQUEST --> QUEUE --- " + msg.getTimestamp());
		}

	}

	public void enterCriticalSection() {
		criticalSectionCounter = criticalSectionCounter + 1;
		System.out.println("Node " + id + " --> CRITICAL SECTION count: "
				+ criticalSectionCounter + " --- "
				+ requestQueue.peek().getTimestamp());

		log(new LogMessage(id, "Entering", logicalClockValue));
		// Thread.sleep(20);
		synchronized (requestQueue) {
			if (requestQueue.peek().getFrom() == id) {

				requestQueue.remove();
			}

			else {
				Iterator<Message> i = requestQueue.iterator();
				while (i.hasNext()) {
					Message m = i.next();
					if (m.getFrom() == getId()) {
						requestQueue.remove(m);
					}

				}
			}

		}
		log(new LogMessage(id, "Leaving", logicalClockValue));
		setCriticalSectionRequested(false);
		System.out.println(Thread.currentThread()
				+ " Setting criticalSectionRequest to "
				+ criticalSectionRequested);
		prepareReleaseMsg();

		totalProtocolMessages = totalProtocolMessages + requestMsgCounter
				+ replyMsgCounter + releaseMsgCounter;
		replyMsgCounter = 0;
		requestMsgCounter = 0;
		releaseMsgCounter = 0;
		// notifyAll();

	}

	public boolean eligibleToEnterCriticalSection() {
		if (requestQueue.size() != 0) {
			if (requestQueue.peek().getFrom() == id) {
				if (replyMsgCounter == numberOfNodes - 1) {
					System.out.println("Number of replies are : "
							+ replyMsgCounter);
					System.out.println("Node at head of queue is : "
							+ requestQueue.peek().getFrom());
					return true;
				} else {
					System.out.println("You dont have enough replies i.e. "
							+ replyMsgCounter);
					return false;
				}
			} else {
				System.out.println("You are not on top of queue");
				return false;
			}

		} else {
			System.out.println("False because queue is empty");
			return false;
		}

	}

	/*
	 * public void sendMsgToAllNodes(Message outMessage, int numberOfNodes) {
	 * 
	 * 
	 * String serverNamePart1 = "net3"; String serverNamePart2 =
	 * ".utdallas.edu"; int port = 8000; for (int i = 0; i < numberOfNodes; i++)
	 * { if (i != id) { try {
	 * 
	 * System.out.println("Connecting to node " + i + " on port " + port);
	 * 
	 * Integer nodeId = i; Socket client = new Socket(serverNamePart1.concat(
	 * nodeId.toString()).concat(serverNamePart2), port + i); OutputStream
	 * outputStream = client.getOutputStream(); ObjectOutputStream
	 * objectOutputStream = new ObjectOutputStream( outputStream);
	 * objectOutputStream.writeObject(outMessage); System.out.println("Node " +
	 * id + " --> " + outMessage.getType() + " --> Node " + i + " --- " +
	 * outMessage.getTimestamp()); if (outMessage.getType().compareTo("REQUEST")
	 * == 0) { InputStream inFromServer = client.getInputStream();
	 * ObjectInputStream in = new ObjectInputStream( inFromServer); Message
	 * inMessage = (Message) in.readObject(); System.out.println("Node " + id +
	 * " <-- " + inMessage.getType() + " <-- Node " + inMessage.getId() +
	 * " --- " + inMessage.getTimestamp());
	 * processReplyMsgFromOtherNodes(inMessage); } outputStream.flush();
	 * outputStream.close(); client.close(); } catch (IOException e) {
	 * e.printStackTrace(); } catch (ClassNotFoundException e) {
	 * e.printStackTrace(); } } }
	 * 
	 * 
	 * }
	 */

	public void sendMsgToNode(Message outMessage) {
		synchronized (sendingQueue) {
			sendingQueue.add(outMessage);
			System.out.println("Msg added to queue.");
		}
		/*
		 * String serverNamePart1 = "net3"; String serverNamePart2 =
		 * ".utdallas.edu"; int port = 8000; try {
		 * 
		 * System.out.println("Connecting to node " + targetNode + " on port " +
		 * port);
		 * 
		 * Integer nodeId = targetNode; Socket client = new
		 * Socket(serverNamePart1
		 * .concat(nodeId.toString()).concat(serverNamePart2), port +
		 * targetNode); OutputStream outputStream = client.getOutputStream();
		 * ObjectOutputStream objectOutputStream = new ObjectOutputStream(
		 * outputStream); objectOutputStream.writeObject(outMessage);
		 * System.out.println("Node " + id + " --> " + outMessage.getType() +
		 * " --> Node " + targetNode + " --- " + outMessage.getTimestamp());
		 * 
		 * if (outMessage.getType().compareTo("APPLICATION") != 0) { InputStream
		 * inFromServer = client.getInputStream(); ObjectInputStream in = new
		 * ObjectInputStream(inFromServer); Message inMessage = (Message)
		 * in.readObject(); System.out.println("Node " + id + " <-- " +
		 * inMessage.getType() + " <-- Node " + inMessage.getId() + " --- " +
		 * inMessage.getTimestamp()); processReplyMsgFromOtherNodes(inMessage);
		 * }
		 * 
		 * client.close(); } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	public void log(LogMessage logMessage) {
		String serverName = "net40.utdallas.edu";
		int port = 9000;
		try {
			/*
			 * System.out.println("Connecting to node " + i + " on port " +
			 * port);
			 */
			Socket client = new Socket(serverName, port);
			OutputStream outputStream = client.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outputStream);
			objectOutputStream.writeObject(logMessage);

			/*
			 * System.out.println("Node " + id + " sent a " +
			 * outMessage.getType() + " message to node " + i +
			 * " with timestamp " + outMessage.getTimestamp());
			 */

			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isCriticalSectionRequested() {
		return criticalSectionRequested;
	}

	public void setCriticalSectionRequested(boolean criticalSectionRequested) {
		this.criticalSectionRequested = criticalSectionRequested;
	}

}
