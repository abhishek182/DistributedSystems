public class Execution implements Runnable {
	private Node node;

	public Execution(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		while (node.getCriticalSectionCounter() < 20) {
			int randomWait = RandomRange.getRandomInteger(10, 100);
			try {
				Thread.sleep(randomWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int action = RandomRange.getRandomInteger(1, 100);
			// System.out.println("Action number is " + action);
			if (action >= 1 && action <= 90) {
				int targetNode = RandomRange.getRandomInteger(0,
						node.getNumberOfNodes() - 1);

				// System.out.println("Target Node is " + targetNode);
				if (targetNode != node.getId()) {
					System.out.println("Sending application msg to node "
							+ targetNode);
					node.prepareApplicationMsg(targetNode);
				}
			} else if (action >= 91 && action <= 100) {
				System.out.println(Thread.currentThread()
						+ " Critical Section request set to "
						+ node.isCriticalSectionRequested());
				if (!node.isCriticalSectionRequested()) {
					System.out.println("Node " + node.getId()
							+ " trying to enter critical section");
					node.setCriticalSectionRequested(true);
					node.prepareRequestMsg();
				}
			}
			/*
			 * if(node.getCriticalSectionCounter()==10){
			 * node.setUpdateClockLock(false);
			 * System.out.println("Setting clock lock to false"); }
			 */
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Node " + node.getId()
				+ " entered CRITICAL SECTION "
				+ node.getCriticalSectionCounter() + " times");
		System.out.println("Node " + node.getId() + " exchanged "
				+ node.getTotalProtocolMessages()
				/ node.getCriticalSectionCounter()
				+ " MESSAGES per CRITICAL SECTION.");
		System.out.println("Node " + node.getId() + " sent "
				+ node.getApplicationMsgsSentCounter()
				+ " APPLICATIION messages");
		System.out.println("Node " + node.getId() + " Exchanged "
				+ node.getTotalProtocolMessages() + " PROTOCOL messages.");
	}
}
