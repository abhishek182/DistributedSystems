import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sender implements Runnable {
	private Node node;

	public Sender(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		System.out.println("Senders thread started");
		while (true) {
			synchronized (node.getSendingQueue()) {
				if (!node.getSendingQueue().isEmpty()) {
					Message topMessage = node.getSendingQueue().remove();
					String serverNamePart1 = "net3";
					String serverNamePart2 = ".utdallas.edu";
					int port = 8000;
					try {
						/*
						 * System.out.println("Connecting to node " + targetNode
						 * + " on port " + port);
						 */
						Integer nodeId = topMessage.getTo();
						Socket client = new Socket(serverNamePart1.concat(
								nodeId.toString()).concat(serverNamePart2),
								port + nodeId);
						OutputStream outputStream = client.getOutputStream();
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(
								outputStream);
						objectOutputStream.writeObject(topMessage);
						System.out.println("Node " + topMessage.getFrom()
								+ " --> " + topMessage.getType() + " --> Node "
								+ topMessage.getTo() + " --- "
								+ topMessage.getTimestamp());
						/*
						 * if (outMessage.getType().compareTo("APPLICATION") !=
						 * 0) { InputStream inFromServer =
						 * client.getInputStream(); ObjectInputStream in = new
						 * ObjectInputStream(inFromServer); Message inMessage =
						 * (Message) in.readObject(); System.out.println("Node "
						 * + id + " <-- " + inMessage.getType() + " <-- Node " +
						 * inMessage.getId() + " --- " +
						 * inMessage.getTimestamp());
						 * processReplyMsgFromOtherNodes(inMessage); }
						 */
						outputStream.flush();
						outputStream.close();
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}
