import old.AlgoMineEMSFUI_D;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Example of how to use the EMSFUI-D algorithm
 * Thanks to the SPMF library for providing the datasets and the compared algorithms' codes.
 */
public class MainTestEMSFUI_D {

	public static void main(String [] arg) throws IOException, InterruptedException {
		String[] strInput={"ecommerce.txt","foodmart.txt","fruithut.txt","mushroom.txt","retail.txt","kosarak.txt","DB_Utility2.txt"};
		int[] startLimit={4975,1641,31970,1624,14662,134981,4};
		int[] limit={2000,500,30000,1300,14700,134981,2};
		for (int i = 0; i < 2; i++) {
			String input = fileToPath(strInput[i]);
			String output = ".//Patterns_EMSFUID_" + strInput[i];
//			System.gc();
//			Thread.sleep(10000);
			MemoryLogger.getInstance().reset();
			AlgoMineEMSFUI_D EMSFUI_D = new AlgoMineEMSFUI_D();
			EMSFUI_D.runAlgorithm(input,output,startLimit[i],limit[i]);
			EMSFUI_D.printStats();

		}

	}




	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestEMSFUI_D.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
