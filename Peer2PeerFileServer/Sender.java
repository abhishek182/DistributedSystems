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
		while (true) {
			synchronized (node.getSenderQueue()) {
				if (!node.getSenderQueue().isEmpty()) {
					Message topMessage = node.getSenderQueue().remove();
					int port = 8000;
					try {
						String targetNode = topMessage.getTo();
						Socket client = new Socket(targetNode, port);
						OutputStream outputStream = client.getOutputStream();
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(
								outputStream);
						System.out.println(topMessage.getType() + " sent to " + topMessage.getTo());
						objectOutputStream.writeObject(topMessage);
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
