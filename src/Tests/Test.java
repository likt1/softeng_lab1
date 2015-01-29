package Tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class Test {

	@org.junit.Test
	public void testGetMapForRestoringAcronyms() {
		Map<String, String> actualMap  = new HashMap<String, String>();
		Map<String, String> expectedMap  = new HashMap<String, String>();
		String fileContents = "LHCP: Licensed Health Care Professional.\r\n"
				+ "ER: Emergency Responder.\r\n"
				+ "LT: Laboratory Technician.\r\n"
				+ "PHA: Public Health Agent.\r\n"
				+ "OB/GYN: Obstetrics and Gynaecology.\r\n";
		String[] fileContentsArray = fileContents.split("(:\\s)|(\\.\r\n)");
		actualMap.put("LHCP", "Licensed Health Care Professional");
		actualMap.put("ER", "Emergency Responder");
		actualMap.put("LT", "Laboratory Technician");
		actualMap.put("PHA", "Public Health Agent");
		actualMap.put("OB/GYN", "Obsterics and Gynaecology");
		for (int i=0; i<fileContentsArray.length; i=i+2) {
			expectedMap.put(fileContentsArray[i].toString(), fileContentsArray[i+1].toString());
		}
		assertTrue(actualMap.keySet().equals(expectedMap.keySet()));
	}

	@org.junit.Test
	public void testRestoreAcronyms() {
		String outPutString = "";
		String[] wordArray = {"An", "admin", "creates", "a", "LHCP", "an", "ER", "a", "LT", "or", "a", "PHA"};
		Map<String, String> actualMap  = new HashMap<String, String>();
		actualMap.put("LHCP", "Licensed Health Care Professional");
		actualMap.put("ER", "Emergency Responder");
		actualMap.put("LT", "Laboratory Technician");
		actualMap.put("PHA", "Public Health Agent");
		actualMap.put("OB/GYN", "Obsterics and Gynaecology");
		String expectedOutPut = "An admin creates a Licensed Health Care Professional an "
				+ "Emergency Responder a Laboratory Technician or a Public Health Agent";
		for (int i=0; i < wordArray.length; i++) {
			for (String key : actualMap.keySet()) {
				if (wordArray[i].equals(key)) {
					wordArray[i] = (String)actualMap.get(key);
				}
			}
			outPutString = outPutString + wordArray[i] + " ";
		}
		// removes last space, string aren't equal otherwise
		outPutString = outPutString.substring(0, outPutString.length()-1);
		assertEquals(outPutString, expectedOutPut);
	}

}
