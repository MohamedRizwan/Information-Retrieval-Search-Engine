package naiveBayes;

public class NBTest {

	public static void main(String[] args) {
		try {
			String workingDir = System.getProperty("user.dir");

			String negFolderName = "neg";
			String posFolderName = "pos";
			String negfolderPath = workingDir + "\\" + args[0];
			String posfolderPath = workingDir + "\\" + args[0];
			
			if(args[0].contains("dev")){
				negfolderPath = negfolderPath + "\\" + negFolderName;
				posfolderPath = posfolderPath + "\\" + posFolderName;
			}

			int cfv = 10;

			long start = System.currentTimeMillis();

			NBTestHelper eval = new NBTestHelper();

			eval.readNEGFolder(negfolderPath);
			eval.readPOSFolder(posfolderPath);

			int numberOfNEGFiles = eval.getNegArrayList().size();
			int numberOfPOSFiles = eval.getPosArrayList().size();

			int numberOfNEGFilesPerBlock = numberOfNEGFiles / cfv;
			int numberOfPOSFilesPerBlock = numberOfPOSFiles / cfv;

			double avgOfBlocks[] = new double[cfv];
			for (int itr = 0; itr < cfv; itr++) {

				String lmFileName = "model" + itr + ".txt";

				String lmPath = System.getProperty("user.dir") + "\\"
						+ lmFileName;

				eval.readModelFile(lmPath);

				eval.negTest(
						(itr * numberOfNEGFilesPerBlock),
						((itr * numberOfNEGFilesPerBlock) + (numberOfNEGFilesPerBlock - 1)),
						negfolderPath);

				eval.posTest(
						(itr * numberOfPOSFilesPerBlock),
						((itr * numberOfPOSFilesPerBlock) + (numberOfPOSFilesPerBlock - 1)),
						posfolderPath);

				double avg = (eval.getNumberOfNEGClassifiedCorrectly() + eval
						.getNumberOfPOSClassifiedCorrectly())
						/ (double) (numberOfNEGFilesPerBlock + numberOfPOSFilesPerBlock);
				avgOfBlocks[itr] = avg;

				System.out
						.println(" Classified Correctly : "
								+ (eval.getNumberOfNEGClassifiedCorrectly() + eval
										.getNumberOfPOSClassifiedCorrectly())
								+ "/"
								+ (numberOfNEGFilesPerBlock + numberOfPOSFilesPerBlock)
								+ " = " + avg);
				
				

				eval.setNumberOfNEGClassifiedCorrectly(0);
				eval.setNumberOfPOSClassifiedCorrectly(0);
				eval.clear();
			}
			System.out.println("Correct pos files");
			System.out.println(eval.getPosCorrect().toString());
			System.out.println("Correct neg files");
			System.out.println(eval.getNegCorrect().toString());
			
			System.out.println("Positive score of files");
			System.out.println(eval.getPosFileProbs().toString());
			System.out.println("Negative score of files");
			System.out.println(eval.getNegFileProbs().toString());

			double avg = 0.0;
			for (int itr = 0; itr < cfv; itr++) {

				System.out.print(avgOfBlocks[itr] + " ");
				avg = avg + avgOfBlocks[itr];
			}
			System.out.println();

			System.out.println("Average : " + (avg / cfv) * 100);
			System.out.println();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
