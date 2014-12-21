import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Listener implements Runnable {

	private ServerSocket serverSocket;
	private Node node;

	public Listener(Node node) throws IOException {
		this.serverSocket = new ServerSocket(8000);
		this.node = node;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket server = serverSocket.accept();
				ObjectInputStream inObjectInputStream = new ObjectInputStream(
						server.getInputStream());
				Message inMessage = (Message) inObjectInputStream.readObject();
				this.node.addMessageToRecievingQueue(inMessage);
				inObjectInputStream.close();
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
