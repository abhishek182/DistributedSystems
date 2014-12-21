public class Processor implements Runnable {
	private Node node;

	public Processor(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (node.getRecievingQueue()) {
				if (!node.getRecievingQueue().isEmpty()) {
					Message topMessage = node.getRecievingQueue().remove();
					this.node.deliverMessage(topMessage);
				}
			}

		}
	}

}
