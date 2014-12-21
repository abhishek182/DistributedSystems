import java.io.IOException;

public class Initiator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int id = Integer.parseInt(args[0]);
		int d = RandomRange.getRandomInteger(1, 5);
		int numberOfNodes = Integer.parseInt(args[1]);
		int port = 8000 + id;
		Node node = new Node(id, d, numberOfNodes);
		try {
			Thread listnerThread = new Thread(new Listener(port, node),
					"Listener");
			listnerThread.start();
			Thread senderThread = new Thread(new Sender(node),"Sender");
			senderThread.start();
			Thread.sleep(15000);
			Thread executionThread = new Thread(new Execution(node),
					"Execution");
			executionThread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
