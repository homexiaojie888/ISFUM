
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainTest_ISFUM {
	static List<Double> runTime=new ArrayList<>();
	static List<Double> memory=new ArrayList<>();
	static List<Long> candidates=new ArrayList<>();
	static List<Long> jointCount=new ArrayList<>();
	static List<Integer> patterns=new ArrayList<>();

	public static void main(String [] arg) throws IOException, InterruptedException {
		String[] strInput={"ecommerce.txt","foodmart.txt","fruithut.txt","mushroom.txt","retail.txt","kosarak.txt","DB_Utility2.txt"};

		int[][] DataSize={{4975, 6975, 8975, 10975, 12974, 14975},{1641, 2141, 2641, 3141, 3641, 4141},{31970, 61970, 91970, 121970, 151970, 181970},
				{1624, 2924, 4224, 5524, 6824, 8124},{14662, 29362, 44062, 58762, 73462, 88162},{134981, 269962, 404943, 539924, 674905, 809886},{6}};
        long[][] ts_now={{1332962,1738520,2221982,2652902,2974160,3224294},{314396,434816,545912,668234,779474,891362},{15982558002l,29641315002l,46893085002l,65547672002l,88996049002l,112818461002l},
		 {313190,617528,907856,1142426,1393184,1618430},{622874,1202390,1729970,2290328,2732522,3069476},{683000,1341110,1884650,2481338,2914484,129116814},{129116814}};
		double[] magnify= {2,2,7,2,2,4,0};
		double eplison=0.01;
		for (int i = 0; i < 2; i++) {
			String input = fileToPath(strInput[i]);
			String output = ".//Patterns_ISFUM_" + strInput[i];
            long[] tsNow=ts_now[i];
			double magfy=magnify[i];
			for (int j = 0; j < DataSize[i].length; j++) {
//				System.gc();
//				Thread.sleep(10000);
				MemoryLogger.getInstance().reset();
				AlgoISFUM algoSRFMminer = new AlgoISFUM();
				algoSRFMminer.runAlgorithm(input,DataSize[i][j],tsNow[j],magfy, eplison, output);
				algoSRFMminer.printStats(runTime, memory, candidates, jointCount, patterns);

			}
			OutputExp(DataSize[i],strInput[i]);
			runTime.clear();
			memory.clear();
			candidates.clear();
			jointCount.clear();
			patterns.clear();
		}

	}
	private static void OutputExp(int[] DataSize, String input) throws IOException {
		String experimentFile = ".//Exp_ISFUM_"+input;
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(experimentFile));
		bufferedWriter.write("DataSize: ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(String.valueOf(DataSize[i]));
			}else {
				bufferedWriter.write(DataSize[i]+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("Runtime (s): ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(runTime.get(i)+"");
			}else {
				bufferedWriter.write(runTime.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("Memory (MB): ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(memory.get(i)+"");
			}else {
				bufferedWriter.write(memory.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# candidates: ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(candidates.get(i)+"");
			}else {
				bufferedWriter.write(candidates.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# jointCount: ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(jointCount.get(i)+"");
			}else {
				bufferedWriter.write(jointCount.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# patterns: ");
		for (int i = 0; i < DataSize.length; i++) {
			if (i==DataSize.length-1){
				bufferedWriter.write(patterns.get(i)+"");
			}else {
				bufferedWriter.write(patterns.get(i)+",");
			}

		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTest_ISFUM.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
