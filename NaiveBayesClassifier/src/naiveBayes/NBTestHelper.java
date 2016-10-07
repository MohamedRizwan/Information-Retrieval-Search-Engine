package naiveBayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NBTestHelper {
	
	private ArrayList<String> negArrayList = new ArrayList<String>();
	private ArrayList<String> posArrayList = new ArrayList<String>();

	private HashMap<String, Double> posWordProbs = new HashMap<String, Double>();
	private HashMap<String, Double> negWordProbs = new HashMap<String, Double>();

	private int numberOfNEGClassifiedCorrectly = 0;
	private int numberOfPOSClassifiedCorrectly = 0;
	
	private ArrayList<String> negCorrect = new ArrayList<String>();
	private ArrayList<String> posCorrect = new ArrayList<String>();
	
	private HashMap<String, Double> posFileProbs = new HashMap<String, Double>();
	private HashMap<String, Double> negFileProbs = new HashMap<String, Double>();

	public ArrayList<String> getNegArrayList() {
		return negArrayList;
	}

	public void setNegArrayList(ArrayList<String> negArrayList) {
		this.negArrayList = negArrayList;
	}

	public ArrayList<String> getPosArrayList() {
		return posArrayList;
	}

	public void setPosArrayList(ArrayList<String> posArrayList) {
		this.posArrayList = posArrayList;
	}

	public HashMap<String, Double> getPosWordProbs() {
		return posWordProbs;
	}

	public void setPosWordProbs(HashMap<String, Double> posWordProbs) {
		this.posWordProbs = posWordProbs;
	}

	public HashMap<String, Double> getNegWordProbs() {
		return negWordProbs;
	}

	public void setNegWordProbs(HashMap<String, Double> negWordProbs) {
		this.negWordProbs = negWordProbs;
	}

	public int getNumberOfNEGClassifiedCorrectly() {
		return numberOfNEGClassifiedCorrectly;
	}

	public void setNumberOfNEGClassifiedCorrectly(
			int numberOfNEGClassifiedCorrectly) {
		this.numberOfNEGClassifiedCorrectly = numberOfNEGClassifiedCorrectly;
	}

	public int getNumberOfPOSClassifiedCorrectly() {
		return numberOfPOSClassifiedCorrectly;
	}

	public void setNumberOfPOSClassifiedCorrectly(
			int numberOfPOSClassifiedCorrectly) {
		this.numberOfPOSClassifiedCorrectly = numberOfPOSClassifiedCorrectly;
	}

	public void readModelFile(String fileName) {

		FileReader file = null;
		BufferedReader reader = null;
		String line = "";

		try {
			file = new FileReader(fileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {

				String st[] = line.split(" ");

				if (st.length == 3) {

					String word = st[0].toLowerCase();
					Double negProb = Double.parseDouble(st[2]);
					Double posProb = Double.parseDouble(st[1]);

					negWordProbs.put(word, negProb);
					posWordProbs.put(word, posProb);

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

	public void readNEGFolder(String folderPath) {

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

	public void negTest(int start, int end, String path) {

		for (int i = start; i <= end; i++) {

			String negFilePath = path + "\\" + negArrayList.get(i);
			readNEGFile(negFilePath);
		}
	}

	public void readNEGFile(String fileName) {

		FileReader file = null;
		BufferedReader reader = null;
		String line = "";

		try {

			double negProb = 0.0;
			double posProb = 0.0;

			file = new FileReader(fileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {

				String st[] = line.split(" ");

				for (int iterator = 0; iterator < st.length; iterator++) {

					String word = st[iterator].toLowerCase();

					if (negWordProbs.containsKey(word))
						negProb = negProb + negWordProbs.get(word);

					if (posWordProbs.containsKey(word))
						posProb = posProb + posWordProbs.get(word);

				}
			}
			
			posFileProbs.put(fileName,posProb);
			negFileProbs.put(fileName, negProb);

			if (negProb > posProb) {
				numberOfNEGClassifiedCorrectly++;
				negCorrect.add(fileName);
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

	public void readPOSFolder(String folderPath) {

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

	public void posTest(int start, int end, String path) {

		for (int i = start; i < end; i++) {

			String posFilePath = path + "\\" + posArrayList.get(i);

			readPOSFile(posFilePath);
		}

	}

	public void readPOSFile(String fileName) {

		FileReader file = null;
		BufferedReader reader = null;
		String line = "";

		try {

			double negProb = 0.0;
			double posProb = 0.0;

			file = new FileReader(fileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {

				String st[] = line.split(" ");

				for (int iterator = 0; iterator < st.length; iterator++) {

					String word = st[iterator].toLowerCase();

					if (negWordProbs.containsKey(word))
						negProb = negProb + negWordProbs.get(word);

					if (posWordProbs.containsKey(word))
						posProb = posProb + posWordProbs.get(word);
				}
			}
			
			posFileProbs.put(fileName,posProb);
			negFileProbs.put(fileName, negProb);

			if (negProb < posProb) {
				numberOfPOSClassifiedCorrectly++;
				posCorrect.add(fileName);
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

	public HashMap<String, Double> getPosFileProbs() {
		return posFileProbs;
	}

	public void setPosFileProbs(HashMap<String, Double> posFileProbs) {
		this.posFileProbs = posFileProbs;
	}

	public HashMap<String, Double> getNegFileProbs() {
		return negFileProbs;
	}

	public void setNegFileProbs(HashMap<String, Double> negFileProbs) {
		this.negFileProbs = negFileProbs;
	}

	public ArrayList<String> getNegCorrect() {
		return negCorrect;
	}

	public void setNegCorrect(ArrayList<String> negCorrect) {
		this.negCorrect = negCorrect;
	}

	public ArrayList<String> getPosCorrect() {
		return posCorrect;
	}

	public void setPosCorrect(ArrayList<String> posCorrect) {
		this.posCorrect = posCorrect;
	}

	public void clear() {

		negWordProbs.clear();
		posWordProbs.clear();
	}

}
