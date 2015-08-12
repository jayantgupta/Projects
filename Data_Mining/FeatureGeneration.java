import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Akhila Bhattarahalli S
 *
 */
public class FeatureGeneration {

	public String meanMedianSD(String[] input) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String each : input) {
			if (map.containsKey(each)) {
				map.put(each, map.get(each) + 1);
			} else {
				map.put(each, 1);
			}
		}
		int sum = 0;
		int count = 0;
		for (int value : map.values()) {
			sum = sum + value;
			count++;
		}
		DecimalFormat df = new DecimalFormat("#.00");
		float mean = (float) sum / count;
		// System.out.println("mean: " + mean);
		String newMean = df.format(mean);
		// System.out.println("newMean: " + newMean);
		float median = findMedian(map.values());
		String newMedian = df.format(median);
		float sd = findSD(map.values(), mean);
		// System.out.println("sd: " + sd);
		String newSD = df.format(sd);
		// System.out.println("newSD: " + newSD);
		String output = newMean + "," + newMedian + "," + newSD;
		// System.out.println(output);
		return output;
	}

	public float findMedian(Collection<Integer> frequency) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int each : frequency) {
			list.add(each);
		}
		Collections.sort(list);
		int len = list.size();
		int mid = 0;
		float median;
		if (len == 1) {
			median = list.get(0);
		} else {
			if (len % 2 != 0) {
				median = list.get(len / 2);
			} else {
				mid = len / 2;
				median = (list.get(mid) + list.get(mid - 1)) / 2;
			}
		}
		// System.out.println("median: " + median);
		return median;
	}

	public String splitOneGramForMean(String input) {
		String[] oneGram = new String[input.length()];
		int j = 0;
		for (int i = 0; i < input.length(); i++) {
			oneGram[j++] = input.substring(i, i + 1);
		}

		/*
		 * for (int k = 0; k < oneGram.length; k++) {
		 * System.out.println(oneGram[k] + " "); }
		 */
		String output = meanMedianSD(oneGram);
		return output;
	}

	public String splitTwoGramForMean(String input) {
		String[] twoGram = new String[input.length() - 1];
		int j = 0;
		for (int i = 0; i < input.length() - 1; i++) {
			twoGram[j++] = input.substring(i, i + 2);
		}

		/*
		 * for (int k = 0; k < twoGram.length; k++) {
		 * System.out.println(twoGram[k] + " "); }
		 */
		String output = meanMedianSD(twoGram);
		return output;
	}

	public String entropy1LD(String input) {
		// System.out.println("input: " + input);
		String[] values = input.split("\\.");

		/*
		 * for (String each : values) { System.out.println("each: " + each); }
		 */

		float entropy = 0;
		int len = values.length;
		String curr = values[len - 1];
		// System.out.println("len: " + len);
		// System.out.println("11");
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (char each : curr.toCharArray()) {
			// System.out.println("each: " + each);
			if (map.containsKey(each)) {
				map.put(each, map.get(each) + 1);
			} else {
				map.put(each, 1);
			}
		}
		int currLen = curr.length();
		for (int value : map.values()) {
			// System.out.println("value: " + value);
			// System.out.println("currLen: " + currLen);
			float frac = ((float) value / currLen);
			// System.out.println("frac: " + frac);
			entropy = (float) (entropy + (frac)
					* (Math.log(frac) / Math.log(2)));
			// System.out.println("entropy: " + entropy);
		}
		entropy = -entropy;
		DecimalFormat df = new DecimalFormat("#.00");
		String newEntropy = df.format(entropy);
		// System.out.println("newEntropy: " + newEntropy);
		return newEntropy;
	}

	public float findSD(Collection<Integer> frequency, float mean) {
		float sum = 0;
		int n = frequency.size();
		for (int value : frequency) {
			// System.out.println("value: " + value);
			sum = (sum + (float) Math.pow((value - mean), 2));
		}
		// System.out.println("sum: " + sum);
		float var = sum / n;
		// System.out.println("var: " + var);
		// System.out.println("sqrt: " + (float) Math.sqrt(var));
		float sd = (float) Math.sqrt(var);
		return sd;
	}

	public int numberOfDistinctCharacters(String input) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (char each : input.toCharArray()) {
			if (map.containsKey(each)) {
				map.put(each, map.get(each) + 1);
			} else {
				map.put(each, 1);
			}
		}


		return map.size();
	}

	public void readFile() {
		BufferedReader bf = null;
		PrintWriter out = null;
		try {

			bf = new BufferedReader(
					new FileReader(
							"/Users/akhilabs/Documents/Akhila/Winter_Quarter_2015/Machine Learning/Project/Data/DGA_classification_data.csv"));
			String curr;
			out = new PrintWriter("/Users/akhilabs/Downloads/output.csv");
			// create data output stream
			while ((curr = bf.readLine()) != null) {
				// System.out.println(curr);
				String[] contents = curr.split(",");
				String append = curr + "," + splitOneGramForMean(contents[0])
						+ "," + splitTwoGramForMean(contents[0]) + ","
						+ entropy1LD(contents[0]) + ","
						+ numberOfDistinctCharacters(contents[0]);
				// System.out.println(append);
				out.print(append);
				out.println();
				// break;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bf != null) {
					bf.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FeatureGeneration fg = new FeatureGeneration();
		//fg.readFile();
		
		  System.out.println(fg
		  .splitOneGramForMean("hello"));
		/* * System.out.println(fg
		 * .splitTwoGramForMean("F6FC40E752943B92C914EABCDAFE9430.org")); */
	 System.out.println(fg
		 .entropy1LD("hello")); 
		 
		/*
		 * System.out .println(fg
		 * .numberOfDistinctCharacters("F6FC40E752943B92C914EABCDAFE9430.org"));
		 */

	}

}
