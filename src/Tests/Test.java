package Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
		String stringFromFile = "An admin creates a LHCP, an ER, a LT, or a PHA";
		Map<String, String> actualMap  = new HashMap<String, String>();
		actualMap.put("LHCP", "Licensed Health Care Professional");
		actualMap.put("ER", "Emergency Responder");
		actualMap.put("LT", "Laboratory Technician");
		actualMap.put("PHA", "Public Health Agent");
		actualMap.put("OB/GYN", "Obsterics and Gynaecology");
		String expectedOutPut = "An admin creates a Licensed Health Care Professional, an "
				+ "Emergency Responder, a Laboratory Technician, or a Public Health Agent";
			for (String key : actualMap.keySet()) {
				stringFromFile = stringFromFile.replace(key, actualMap.get(key));
			} 
			outPutString = stringFromFile;
		//}
		assertEquals(outPutString, expectedOutPut);
	}
	
	@org.junit.Test
	public void testRemoveStopWords() {
		String outPutString = "";
		String[] stopWordArray = {"a","able","about","across","after","all",
				"almost","also","am","among","an","and","any","are","as","at",
				"be","because","been","but","by","can","cannot","could","dear",
				"did","do","does","either","else","ever","every","for","from",
				"get","got","had","has","have","he","her","hers","him","his",
				"how","however","i","if","in","into","is","it","its","just",
				"least","let","like","likely","may","me","might","most","must",
				"my","neither","no","nor","not","of","off","often","on","only",
				"or","other","our","own","rather","said","say","says","she","should",
				"since","so","some","than","that","the","their","them","then","there",
				"these","they","this","tis","to","too","twas","us","wants","was","we",
				"were","what","when","where","which","while","who","whom","why","will",
				"with","would","yet","you","your"};
		Arrays.sort(stopWordArray);
		String[] initialStringArray = {"An", "admin", "creates", "a", "LHCP", "an", "ER", "a", "LT", "or", "a", "PHA"};
		String expectedOutput = "admin creates LHCP ER LT PHA";
		for (int i=0; i<initialStringArray.length; i++) {
			int index = Arrays.binarySearch(stopWordArray, initialStringArray[i].toLowerCase());
	        if (index >= 0) {} else {
	        	outPutString = outPutString + initialStringArray[i].toString() + " ";
	        }
		}
		outPutString = outPutString.substring(0, outPutString.length()-1);
		assertEquals(outPutString, expectedOutput);
	}
	
	@org.junit.Test
	// test new tokenizer
	// not accounting for camel case yet
	public void testAdvancedTokenizing() {
		List<String> str = new ArrayList<String>();
		str.add("function x_stuff 1 + 2 and 3 - 4 ");
		str.add("// hohafo_ 23 and @#$#@");
		String outPut = new String();
		for (String s : str) {
			if (s.startsWith("//") || s.startsWith("/*")) {
				outPut += s + " ";
			} else {
				String[] splitStringArray = s.split("[^\\w\n|[0-9]|[_])]");
				for (String sa : splitStringArray) {
					if (sa != null && sa.length() > 0) {
						outPut += sa + " ";
					}
				}
			}
		}
		outPut = outPut.substring(0, outPut.length()-1);
		String expectedOutPut = "function x stuff and // hohafo_ 23 and @#$#@";
		assertEquals(outPut, expectedOutPut);
	}
	
	@org.junit.Test
	public void testTekonizingCamelCase() {
		// need to check for DBException => DB Exception
		List<String> str = new ArrayList<String>();
		str.add("this is a camelCase text and should be splitUpIntoWords");
		String outPut = "";
		for (String s : str) {
			String[] splitStringArray = s.split("(?<=[a-z])(?=[A-Z])");
			for (String sa : splitStringArray) {
				if (sa != null && sa.length() > 0) {
					outPut += sa + " ";
				}
			}
		}

		outPut = outPut.substring(0, outPut.length()-1);
		String expectedOutPut = "this is a camel Case text and should be split Up Into Words";
		assertEquals(outPut, expectedOutPut);
	}

}
