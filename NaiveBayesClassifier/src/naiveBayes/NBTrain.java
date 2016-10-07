package naiveBayes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class NBTrain {
	private static ArrayList<String> negArrayList = new ArrayList<String>();
	private static ArrayList<String> posArrayList = new ArrayList<String>();

	private static HashMap<String, String> wordMap = new HashMap<String, String>();

	private static HashMap<String, Integer> negWordCount = new HashMap<String, Integer>();
	private static HashMap<String, Integer> posWordCount = new HashMap<String, Integer>();

	private static HashMap<String, Double> negWordProb = new HashMap<String, Double>();
	private static HashMap<String, Double> posWordProb = new HashMap<String, Double>();

	private static int totalNumberOfPOSWords = 0;
	private static int totalNumberOfNEGWords = 0;

	private static int distinctPOSWords = 0;
	private static int distinctNEGWords = 0;

	public static ArrayList<String> getNegArrayList() {
		return negArrayList;
	}

	public void setNegArrayList(ArrayList<String> negArrayList) {
		this.negArrayList = negArrayList;
	}

	public static ArrayList<String> getPosArrayList() {
		return posArrayList;
	}

	public void setPosArrayList(ArrayList<String> posArrayList) {
		this.posArrayList = posArrayList;
	}

	public HashMap<String, Integer> getNegWordCount() {
		return negWordCount;
	}

	public void setNegWordCount(HashMap<String, Integer> negWordCount) {
		this.negWordCount = negWordCount;
	}

	public HashMap<String, Integer> getPosWordCount() {
		return posWordCount;
	}

	public void setPosWordCount(HashMap<String, Integer> posWordCount) {
		this.posWordCount = posWordCount;
	}

	public HashMap<String, Double> getNegWordProb() {
		return negWordProb;
	}

	public void setNegWordProb(HashMap<String, Double> negWordProb) {
		this.negWordProb = negWordProb;
	}

	public HashMap<String, Double> getPosWordProb() {
		return posWordProb;
	}

	public void setPosWordProb(HashMap<String, Double> posWordProb) {
		this.posWordProb = posWordProb;
	}

	public int getTotalNumberOfPOSWords() {
		return totalNumberOfPOSWords;
	}

	public void setTotalNumberOfPOSWords(int totalNumberOfPOSWords) {
		this.totalNumberOfPOSWords = totalNumberOfPOSWords;
	}

	public int getTotalNumberOfNEGWords() {
		return totalNumberOfNEGWords;
	}

	public void setTotalNumberOfNEGWords(int totalNumberOfNEGWords) {
		this.totalNumberOfNEGWords = totalNumberOfNEGWords;
	}

	public int getDistinctPOSWords() {
		return distinctPOSWords;
	}

	public void setDistinctPOSWords(int distinctPOSWords) {
		this.distinctPOSWords = distinctPOSWords;
	}

	public int getDistinctNEGWords() {
		return distinctNEGWords;
	}

	public void setDistinctNEGWords(int distinctNEGWords) {
		this.distinctNEGWords = distinctNEGWords;
	}

	public static void readNEGFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {

				negArrayList.add(listOfFiles[i].getName());

			} else if (listOfFiles[i].isDirectory()) {

				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}

	public static void negTrain(int start, int end) {

		for (int i = start; i < end; i++) {

			String negFilePath = System.getProperty("user.dir") + "\\textcat\\train\\neg\\" + negArrayList.get(i);
			readNEGFile(negFilePath);
		}
	}

	public static void readNEGFile(String fileName) {

		FileReader file = null;
		BufferedReader reader = null;
		String line = "";

		try {
			file = new FileReader(fileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(line);

				while (st.hasMoreTokens()) {

					String word = st.nextToken().toLowerCase();

					if (negWordCount.containsKey(word)) {

						negWordCount.put(word, negWordCount.get(word) + 1);

					} else {

						negWordCount.put(word, 1);
						distinctNEGWords++;
					}

					if (!wordMap.containsKey(word)) {
						wordMap.put(word, "");
					}

					totalNumberOfNEGWords++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (file != null) {
				try {
					reader.close();
					file.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void readPOSFolder(String folderPath) {

		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {

				posArrayList.add(listOfFiles[i].getName());

			} else if (listOfFiles[i].isDirectory()) {

				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}

	public static void posTrain(int start, int end) {

		for (int i = start; i < end; i++) {

			String posFilePath = System.getProperty("user.dir") + "\\textcat\\train\\pos\\" + posArrayList.get(i);

			readPOSFile(posFilePath);
		}
	}

	public static void readPOSFile(String fileName) {

		FileReader file = null;
		BufferedReader reader = null;
		String line = "";

		try {
			file = new FileReader(fileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(line);

				while (st.hasMoreTokens()) {

					String word = st.nextToken().toLowerCase();

					if (posWordCount.containsKey(word)) {

						posWordCount.put(word, posWordCount.get(word) + 1);

					} else {

						posWordCount.put(word, 1);
						distinctPOSWords++;
					}

					if (!wordMap.containsKey(word)) {
						wordMap.put(word, "");
					}

					totalNumberOfPOSWords++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (file != null) {
				try {
					reader.close();
					file.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void computeProb() {

		for (Entry<String, Integer> entry : negWordCount.entrySet()) {

			String word = entry.getKey().toLowerCase();
			Integer negCount = entry.getValue();

			double negProb = 0.0;
			if (negCount > 0)
				negProb = (double) negCount / (double) totalNumberOfNEGWords;
			else
				negProb = (double) 1.0 / (double) (totalNumberOfNEGWords + distinctNEGWords);

			negProb = (double) Math.log10(negProb) / (double) Math.log10(2);

			negWordProb.put(word, negProb);
		}

		for (Entry<String, Integer> entry : posWordCount.entrySet()) {

			String word = entry.getKey().toLowerCase();
			Integer posCount = entry.getValue();

			double posProb = 0.0;
			if (posCount > 0)
				posProb = (double) posCount / (double) totalNumberOfPOSWords;
			else
				posProb = (double) 1.0 / (double) (totalNumberOfPOSWords + distinctPOSWords);

			posProb = (double) Math.log10(posProb) / (double) Math.log10(2);

			posWordProb.put(word, posProb);
		}
	}

	public static void genrateModelFile(String fileName) {

		File file = new File(fileName);
		FileWriter fileWriter = null;
		BufferedWriter writer = null;

		try {

			fileWriter = new FileWriter(fileName);
			writer = new BufferedWriter(fileWriter);
			
			if (!file.exists()) {
				file.createNewFile();
			}

			for (Entry<String, String> entry : wordMap.entrySet()) {

				String word = entry.getKey();
				int negCount = 0;
				int posCount = 0;

				double negProb = 0.0;
				if (negWordCount.containsKey(word)) {

					negCount = negWordCount.get(word);
					negProb = (double) negCount / (double) totalNumberOfNEGWords;
				} else {

					negProb = (double) 1.0 / (double) (totalNumberOfNEGWords + distinctNEGWords);
				}
				negProb = (double) Math.log10(negProb) / (double) Math.log10(2);

				double posProb = 0.0;
				if (posWordCount.containsKey(word)) {

					posCount = posWordCount.get(word);
					posProb = (double) posCount / (double) totalNumberOfPOSWords;
				} else {

					posProb = (double) 1.0 / (double) (totalNumberOfPOSWords + distinctPOSWords);
				}

				posProb = (double) Math.log10(posProb) / (double) Math.log10(2);

				if((posCount + negCount) >=5 ) 
					writer.write(word + " " + posProb + " " + negProb + "\n");
			}

			posWordCount.clear();
			posWordProb.clear();
			negWordCount.clear();
			negWordCount.clear();
			wordMap.clear();

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (fileWriter != null) {
				try {
					writer.close();
					fileWriter.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void printReport() {

		System.out.println("Number of pos files : " + posArrayList.size());
		System.out.println("Number of neg files : " + negArrayList.size());

		System.out.println("Total Number Of Positive Words : " + totalNumberOfPOSWords);
		System.out.println("Total Number Of Negative Words : " + totalNumberOfNEGWords);
		System.out.println("Distinct Positive Words : " + distinctPOSWords);
		System.out.println("Distinct Negative Words : " + distinctNEGWords);

	}
	
	public static void main(String[] args) {

		try {

			String workingDir = System.getProperty("user.dir");

			String negFolderName = "neg";
			String posFolderName = "pos";
			String lmPath = System.getProperty("user.dir");

			String negfolderPath = workingDir + "\\" + args[0] + "\\" + negFolderName;
			String posfolderPath = workingDir + "\\" + args[0] + "\\" + posFolderName;

			int cfv = 10;

			long start = System.currentTimeMillis();

			readNEGFolder(negfolderPath);
			readPOSFolder(posfolderPath);

			int numberOfNEGFiles = getNegArrayList().size();
			int numberOfPOSFiles = getPosArrayList().size();

			int numberOfNEGFilesPerBlock = numberOfNEGFiles / cfv;
			int numberOfPOSFilesPerBlock = numberOfPOSFiles / cfv;

			for (int itr = 0; itr < cfv; itr++) {

				negTrain(0, (itr * numberOfNEGFilesPerBlock) - 1);
				negTrain(((itr * numberOfNEGFilesPerBlock) + numberOfNEGFilesPerBlock), numberOfNEGFiles);

				posTrain(0, (itr * numberOfPOSFilesPerBlock) - 1);
				posTrain(((itr * numberOfPOSFilesPerBlock) + numberOfPOSFilesPerBlock), numberOfPOSFiles);

				String lmFileName = "model" + itr + ".txt";

				lmPath = System.getProperty("user.dir") + "\\" + lmFileName;

				genrateModelFile(lmPath);
			}

			printReport();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
