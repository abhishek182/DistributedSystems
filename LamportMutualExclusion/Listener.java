import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Listener implements Runnable {

	private ServerSocket serverSocket;
	private Node node;

	public Listener(int port, Node node) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.node = node;
	}

	@Override
	public void run() {
		while (true) {
			try {
				/*
				 * System.out.println("Node " + node.getId() +
				 * " listening on port " + serverSocket.getLocalPort() + "...");
				 */
				Socket server = serverSocket.accept();
				// System.out.println("Just connected to "
				// + server.getRemoteSocketAddress());
				ObjectInputStream in = new ObjectInputStream(
						server.getInputStream());
				Message inMessage = (Message) in.readObject();
				System.out.println("Node " + node.getId() + " <-- "
						+ inMessage.getType() + " <-- Node "
						+ inMessage.getFrom() + " --- "
						+ inMessage.getTimestamp());
				node.deliverMessage(inMessage);
				/*if (replyMessage != null) {
					ObjectOutputStream out = new ObjectOutputStream(
							server.getOutputStream());
					out.writeObject(replyMessage);
					System.out.println("Node " + node.getId() + " --> "
							+ replyMessage.getType() + " --> Node "
							+ inMessage.getFrom() + " --- "
							+ replyMessage.getTimestamp());
				}*/
				in.close();
				server.close();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				break;
			}
		}

	}

}
