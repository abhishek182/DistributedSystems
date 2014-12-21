import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
	@SuppressWarnings("unchecked")
	public static void addEntryToFileList(String keyword, String filename) {

		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			String subDir = hostName.split("\\.")[0];
			String fullDir = subDir.concat("/FileList.json");
			JSONParser parser = new JSONParser();
			JSONArray fileList = (JSONArray) parser.parse(new FileReader(
					fullDir));
			JSONObject newObject = new JSONObject();
			JSONArray keywordArray = new JSONArray();
			keywordArray.add(keyword);
			newObject.put("keywords", keywordArray);
			newObject.put("filename", filename);
			fileList.add(newObject);
			FileWriter writer = new FileWriter(fullDir, false);
			writer.write(fileList.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<JSONObject> getAllFileEntryByKeyword(String searchKey) {
		List<JSONObject> validEntries = new LinkedList<JSONObject>();
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			String subDir = hostName.split("\\.")[0];
			String fullDir = subDir.concat("/FileList.json");
			JSONParser parser = new JSONParser();
			JSONArray fileList = (JSONArray) parser.parse(new FileReader(
					fullDir));
			Iterator<JSONObject> fileListIterator = fileList.iterator();
			while (fileListIterator.hasNext()) {
				JSONObject fileEntry = (JSONObject) fileListIterator.next();
				JSONArray keywordArray = (JSONArray) fileEntry.get("keywords");
				for (int i = 0; i < keywordArray.size(); i++) {
					if (searchKey.compareToIgnoreCase((String) keywordArray.get(i)) == 0) {
						validEntries.add(fileEntry);
					} else {
						continue;
					}
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return validEntries;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getFileEntryByFileName(String searchKey) {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			String subDir = hostName.split("\\.")[0];
			String fullDir = subDir.concat("/FileList.json");
			JSONParser parser = new JSONParser();
			JSONArray fileList = (JSONArray) parser.parse(new FileReader(
					fullDir));
			Iterator<JSONObject> fileListIterator = fileList.iterator();
			while (fileListIterator.hasNext()) {
				JSONObject fileEntry = (JSONObject) fileListIterator.next();
				String filename = (String) fileEntry.get("filename");
				if (searchKey.compareToIgnoreCase(filename) == 0) {
					return fileEntry;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<Tuple> getResultTuplesFromValidEntry(
			JSONObject validFileEntry, String id, String searchKey) {
		List<Tuple> resultTuples = new LinkedList<Tuple>();
		Tuple newTuple = new Tuple(searchKey,
				(String) validFileEntry.get("filename"), id);
		resultTuples.add(newTuple);
		return resultTuples;
	}

	public static List<Tuple> getResultTuplesFromValidEntries(
			List<JSONObject> validFileEntries, String id, String searchKey) {
		List<Tuple> resultTuples = new LinkedList<Tuple>();
		for (JSONObject jsonObject : validFileEntries) {
			Tuple newTuple = new Tuple(searchKey,
					(String) jsonObject.get("filename"), id);
			resultTuples.add(newTuple);
		}
		return resultTuples;
	}

}
