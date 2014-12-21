public class Search implements Runnable {
	private Node node;

	public Search(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()
				+ " : Search started");
		System.out.println(node.getRequestQueue().size());
		if (!node.getRequestQueue().isEmpty()) {
			Message topRequest = node.getRequestQueue().peek();
			if (topRequest.getFrom().compareTo(node.getId()) == 0) {
				System.out.println("Its my request");
				node.initiateSearch(topRequest);
			} else {
				node.initiateForwardSearch(topRequest);
			}

		}

	}

}
