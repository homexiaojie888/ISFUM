import java.io.*;
import java.net.URL;

public class convert {
    public static void main(String[] args) throws IOException {
        BufferedReader myInput = null;
        String thisLine;
        int tid = 1;
        String file = "retail.txt";
        String output = ".//new_retail.txt";
        long timestamp = 0l;
        String input = fileToPath(file);
        BufferedWriter writer = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            writer = new BufferedWriter(new FileWriter(output));
            // for each line (transaction) until the end of file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                // split the transaction according to the : separator
                String split[] = thisLine.split(":");
                // the first part is the list of items
                String items[] = split[0].split(" ");
                // the second part is the transaction utility
                int TU = Integer.parseInt(split[1]);
                //the third part is the utilities of items
                String itemUtils[] = split[2].split(" ");
                //the fourth part is the timestamp of items
                long rencency = Long.parseLong(split[3]);
                if (tid == 1) {
                    timestamp = rencency - 1;
                }
                long newrency = rencency - timestamp;
                writer.write(split[0] + ":" + split[1] + ":" + split[2] + ":" + newrency);
                writer.newLine();
                tid++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
            writer.flush();
            if (writer != null) {
                writer.close();
            }
        }
    }
    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = convert.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

}
