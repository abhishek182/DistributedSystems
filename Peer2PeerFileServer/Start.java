import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Start {

	/**
	 * @param args
	 */
	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) {

		String hostName = null;

		JSONArray hosts = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			Node node = new Node(hostName);
			Thread listnerThread = new Thread(new Listener(node), "Listener");
			listnerThread.start();
			Thread senderThread = new Thread(new Sender(node), "Sender");
			senderThread.start();
			Thread processingThread = new Thread(new Processor(node),
					"Processor");
			processingThread.start();

			while (true) {
				System.out
						.println(Thread.currentThread().getName()
								+ " : Please enter any valid command JOIN/DEPART/SEARCH/EXIT ");
				Scanner inputCommand = new Scanner(System.in);
				InputCommands command = null;
				try {
					command = InputCommands.valueOf(inputCommand.nextLine());
				} catch (IllegalArgumentException e) {
					System.out
							.println("Invalid Command. Valid commands are JOIN/DEPART/SEARCH/EXIT>");
					continue;
				}
				switch (command) {
				case JOIN:
				case join:
					JSONParser parser = new JSONParser();
					hosts = (JSONArray) parser.parse(new FileReader(
							"Hosts.json"));
					node.setHosts(hosts);
					if (hosts.size() != 0) {
						Random rnd = new Random();
						int randno = rnd.nextInt(hosts.size());
						String targetHostName = (String) hosts.get(randno);
						node.joinPeerGroup(targetHostName);
						System.out.println("My neighbours are : "
								+ node.getNeighbourList().toString());
					} else {
						node.getHosts().add(hostName);
						node.setJoinedPeer(true);
						try {
							FileWriter writer = new FileWriter("Hosts.json",
									false);
							writer.write(node.getHosts().toJSONString());
							writer.flush();
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out
								.println("No other node present, Created a peer group.");
					}

					break;
				case DEPART:
				case depart:
					if (!node.isJoinedPeer()) {
						System.out
								.println("You are not joined to the peer group. Please join a peer group.");
					} else {
						node.leavegroup();
					}
					break;
				case SEARCH:
				case search:
					if (!node.isJoinedPeer()) {
						System.out
								.println("You are not joined to the peer group. Please join a peer group.");
					} else {
						System.out
								.println("Enter a filename or keywords to be searched : ");
						String searchKey = inputCommand.nextLine();
						node.initiateSearchRequest(searchKey);
						System.out.println("Search over.");
						LinkedList<Tuple> resultTuples = (LinkedList<Tuple>) node
								.getResultTuples();
						System.out.println("Total results : "
								+ resultTuples.size());

						if (resultTuples == null || resultTuples.size() == 0) {
							System.out.println("File not Found among peers.");
						} else {
							System.out
									.println("Following is the search result:");
							int resultCount = 0;
							for (Tuple tuple : resultTuples) {
								resultCount++;
								System.out.println(resultCount + " - "
										+ tuple.getKeyword() + "  "
										+ tuple.getFilename() + "  "
										+ tuple.getNodeId() + "  "
										+ tuple.getHopCount() + "  "
										+ (double) tuple.getElapsedTime() / 1000.0 + "s");
							}
							System.out
									.println("Please enter the result number from where the file can be fetched:");
							// Scanner inputCommand = new Scanner(System.in);
							int resultChoice = Integer.parseInt(inputCommand
									.nextLine());
							node.fetchFileFrom(resultTuples.get(--resultChoice));

						}
					}
					break;
				case EXIT:
				case exit:
					System.exit(0);
				default:
					System.out
							.println("Invalid Command. Valid commands are JOIN/DEPART/SEARCH/EXIT>");
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
