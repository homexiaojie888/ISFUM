package old;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AlgoMineEMSFUI_D{
	BufferedWriter Testwriter;
	List<Double> runTimelist=new ArrayList<>();
	long candidatesCount=0;
	List<Long> candidates=new ArrayList<>();
	List<Double> memory=new ArrayList<>();
	long jointCount =0;
	List<Long> jointcount=new ArrayList<>();
	List<Integer> pattern=new ArrayList<>();
	String input;
	String output;
	int[] global_umax;
//	int[] local_umax;
	FU minFU=new FU();
	Map<Integer, FU> global_ISU_1;
	Map<Integer, Map<Integer, FU>> global_ISU_2;
	Map<Integer,UtilityList2> oldMapItemToUL=new HashMap<>();
	Map<List<Integer>,FU> oldSkylineToNewFU=new HashMap<>();
	SkylineList[] SFUA;
	BufferedWriter writer = null;
	class Pair{
		int item = 0;
		int utility = 0;
	}
	class FU{
		int sup = 0;
		int utility = 0;

	}
	public AlgoMineEMSFUI_D() throws IOException {
	}

	public void runAlgorithm(String input, String output, int startLimit, int limit) throws IOException, InterruptedException {

		this.input=input;
		this.output=output;
		int lineCount = (int)Files.lines(Paths.get(input)).count();
		global_umax =new int[lineCount+1];
		global_ISU_1 = new HashMap<>();
		global_ISU_2 = new HashMap<>();
		SFUA = new SkylineList[lineCount+1];
		long start = System.currentTimeMillis();


		int index = IncreSkyMiner(0,startLimit);
		long end = System.currentTimeMillis();
		MemoryLogger.getInstance().checkMemory();


		pattern.add(oldSkylineToNewFU.size());
		runTimelist.add((double)(end-start)/1000);
		memory.add(MemoryLogger.getInstance().getMaxMemory());
		for (int i = 1; i <= 5; i++) {
			start=end;
			index = IncreSkyMiner(index,limit);
			end = System.currentTimeMillis();
			runTimelist.add((double)(end-start)/1000);
			memory.add(MemoryLogger.getInstance().getMaxMemory());
			pattern.add(oldSkylineToNewFU.size());
		}
		OutputExp(startLimit,limit,input);
		clearAll();
	}
	private void clearAll() {
		runTimelist.clear();
		memory.clear();
		candidates.clear();
		jointcount.clear();
		pattern.clear();
	}
	private void OutputExp(int startLimit,int limit, String input) throws IOException {
		String[] data=input.split("/");

		String experimentFile = ".//Exp_EMSFUID_old_"+data[data.length-1];
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(experimentFile));
		bufferedWriter.write("DataSize: ");
		for (int i = 1; i <= 6; i++) {
			if (i==1){
				bufferedWriter.write(String.valueOf(startLimit)+",");
			} else if(i==6){
				int size=startLimit+(i-1)*limit;
				bufferedWriter.write(String.valueOf(size));
			}else {
				int size=startLimit+(i-1)*limit;
				bufferedWriter.write(String.valueOf(size)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("Runtime (s): ");
		for (int i = 1; i <=6; i++) {
			if (i==6){
				bufferedWriter.write(runTimelist.get(i-1)+"");
			}else {
				bufferedWriter.write(runTimelist.get(i-1)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("Memory (MB): ");
		for (int i = 1; i <= 6; i++) {
			if (i==6){
				bufferedWriter.write(memory.get(i-1)+"");
			}else {
				bufferedWriter.write(memory.get(i-1)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# candidates: ");
		for (int i = 1; i <= 6; i++) {
			if (i==6){
				bufferedWriter.write(candidates.get(i-1)+"");
			}else {
				bufferedWriter.write(candidates.get(i-1)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# jointCount: ");
		for (int i = 1; i <= 6; i++) {
			if (i==6){
				bufferedWriter.write(jointcount.get(i-1)+"");
			}else {
				bufferedWriter.write(jointcount.get(i-1)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("# patterns: ");
		for (int i = 1; i <= 6; i++) {
			if (i==6){
				bufferedWriter.write(pattern.get(i-1)+"");
			}else {
				bufferedWriter.write(pattern.get(i-1)+",");
			}

		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	public int IncreSkyMiner(int start, int Num_incre) throws IOException {
		
			Map<Integer, FU> local_ISU_1 = new HashMap<>();
			//store local_ISU_1,oldSkylineToNewFU
			Files.lines(Paths.get(input)).skip(start).limit(Num_incre).forEach(line -> {
			String split[] = line.split(":");
			String items[] = split[0].split(" ");
			String itemUtils[] = split[2].split(" ");
			Map<Integer,Integer> mapItemToU=new HashMap<>();
			for (int i = 0; i < items.length; i++) {
				Integer item = Integer.parseInt(items[i]);
				mapItemToU.put(item,Integer.parseInt(itemUtils[i]));
				FU itemInfo = local_ISU_1.get(item);
				if(itemInfo==null){
					itemInfo=new FU();
					itemInfo.sup=1;
					itemInfo.utility=Integer.parseInt(itemUtils[i]);
					local_ISU_1.put(item, itemInfo);
				}else{
					itemInfo.sup+=1;
					itemInfo.utility+=Integer.parseInt(itemUtils[i]);
				}
			}
			for (List<Integer> itemset:oldSkylineToNewFU.keySet()) {
					if (mapItemToU.keySet().containsAll(itemset)){
						oldSkylineToNewFU.get(itemset).sup++;
						int util=0;
						for (int i = 0; i < itemset.size(); i++) {
							util+=mapItemToU.get(itemset.get(i));
						}
						oldSkylineToNewFU.get(itemset).utility+=util;
					}
			}
		});

		mergeISU(local_ISU_1);
		setMinFU(oldSkylineToNewFU.values());
		update(global_ISU_1.values(),global_umax);

		Map<Integer, UtilityList2> mapItemToUtilityList = new HashMap<>();
		for(Integer item: local_ISU_1.keySet()){
			UtilityList2 uList = oldMapItemToUL.get(item);
			if (uList==null){
				uList=new UtilityList2();
				uList.itemset.add(item);
				oldMapItemToUL.put(item,uList);
			}
			mapItemToUtilityList.put(item, uList);
		}


		AtomicInteger tid= new AtomicInteger(start+1);
		// SECOND DATABASE PASS TO CONSTRUCT THE UTILITY LISTS OF ALL 1-ITEMSETS
		Files.lines(Paths.get(input)).skip(start).limit(Num_incre).forEach(line -> {
			String split[] = line.split(":");
			String items[] = split[0].split(" ");
			String utilityValues[] = split[2].split(" ");

			int remainingUtility =0;

			List<Pair> revisedTransaction = new ArrayList<Pair>();
			for(int i=0; i <items.length; i++){
				Pair pair = new Pair();
				pair.item = Integer.parseInt(items[i]);
				pair.utility = Integer.parseInt(utilityValues[i]);
				revisedTransaction.add(pair);
				remainingUtility += pair.utility;
			}

			Collections.sort(revisedTransaction, new Comparator<Pair>(){
				public int compare(Pair o1, Pair o2) {
					return compareItems(o1.item, o2.item);
				}});
			for(int i=0; i<revisedTransaction.size(); i++){
				Pair pair =  revisedTransaction.get(i);
				remainingUtility = remainingUtility - pair.utility;
				UtilityList2 utilityListOfItem = mapItemToUtilityList.get(pair.item);
				Element2 element = new Element2(tid.get(), pair.utility, remainingUtility);
				utilityListOfItem.addElement(element);
				Map<Integer, FU> mapISU_2 = global_ISU_2.get(pair.item);
				if(mapISU_2 == null) {
					mapISU_2 = new HashMap<>();
					global_ISU_2.put(pair.item, mapISU_2);
				}
				for(int j = i+1; j< revisedTransaction.size(); j++){
					Pair pairAfter = revisedTransaction.get(j);
					FU info = mapISU_2.get(pairAfter.item);
					if(info==null){
						info = new FU();
						info.sup=1;
						info.utility=pair.utility+pairAfter.utility;
						mapISU_2.put(pairAfter.item, info);
					}else{
						info.sup= info.sup+1;
						info.utility =info.utility+pair.utility+pairAfter.utility;
					}
				}
			}
			tid.set(tid.get() + 1);
		});
		List<UtilityList2> listOfUtilityLists = mapItemToUtilityList.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
		update2(global_ISU_2,global_umax);
		candidatesCount=0;
		jointCount=0;
		Testwriter = new BufferedWriter(new FileWriter(".//test"+(start+Num_incre)));
		D_Mine(null, listOfUtilityLists);
		Testwriter.flush();
		candidates.add(candidatesCount);
		jointcount.add(jointCount);
		mergeUtlist(listOfUtilityLists);
		writeOut(SFUA);

		writer.close();
		return start+Num_incre;
	}

	private void update2(Map<Integer, Map<Integer, FU>> globalIsu2, int[] globalUmax) {
		List<FU> allFUObjects = global_ISU_2.values().stream().flatMap(innerMap -> innerMap.values().stream()).collect(Collectors.toList());
		update(allFUObjects,globalUmax);

	}


	private void setMinFU(Collection<FU> oldSkylineToNewFU) {
		if (oldSkylineToNewFU.isEmpty()){
			return;
		}
		int f=Integer.MAX_VALUE;
		int u=Integer.MAX_VALUE;
		for (FU fu:oldSkylineToNewFU) {
			if (fu.sup<f){
				f=fu.sup;
			}
			if (fu.utility<u){
				u=fu.utility;

			}
		}
		minFU.sup=f;
		minFU.utility=u;
	}

	private void mergeUtlist(List<UtilityList2> listOfUtilityLists) {
		for (int i = 0; i < listOfUtilityLists.size(); i++) {
			UtilityList2 ul=listOfUtilityLists.get(i);
			ul.setSumOldIutils(ul.sumOldIutils+ul.getSumNewIutils());
			ul.setSumOldRutils(ul.sumOldRutils+ul.getNewSumRutils());
			ul.setSumNewIutils(0);
			ul.setSumNewRutils(0);
			ul.setStartOfNew(ul.elements.size());
		}
	}

	private void mergeISU(Map<Integer, FU> local_ISU_1) {
		for (Integer item:local_ISU_1.keySet()) {
			if (global_ISU_1.keySet().contains(item)){
				global_ISU_1.get(item).sup+=local_ISU_1.get(item).sup;
				global_ISU_1.get(item).utility+=local_ISU_1.get(item).utility;
			}else {
				global_ISU_1.put(item,local_ISU_1.get(item));
			}
		}
	}

	private void update(Collection<FU> FUset, int[] umax) {
		//sort by utility desc
		List<FU> sortedList = FUset.stream()
				.sorted((fu1, fu2) -> Integer.compare(fu2.utility, fu1.utility))
				.collect(Collectors.toList());
		for (int i = 0; i < sortedList.size(); i++) {
			int itemSup = sortedList.get(i).sup;
			int itemUtil = sortedList.get(i).utility;
			for (int j = itemSup; j > 0; j--) {
				if(itemUtil>umax[j]){
					umax[j]=itemUtil;
				}else {
					break;
				}
			}
		}

	}

	private int compareItems(int item1, int item2) {
		return  item1-item2;
	}

	private void D_Mine(UtilityList2 pUL, List<UtilityList2> ULs) throws IOException {
		for(int i=0; i< ULs.size(); i++){
			UtilityList2 X=ULs.get(i);
//			if (X.itemset.size()==1&&X.itemset.get(0)==1){
//				System.out.print("");
//			}
			candidatesCount++;
			Testwriter.write(X.itemset.toString());
			Testwriter.newLine();
			//剪枝
//			if(X.getSumNewIutils()>=minFU.utility||X.getNewSupport()>=minFU.sup){
				if (X.getSumIutils()>=global_umax[X.getSupport()]){
					judge(X);
				}
//				else{
//					System.out.println("global prune");
//				}
//			}
//			else{
//				System.out.println("local prune");
//			}
//			if((X.getSumNewIutils() + X.getNewSumRutils()) >= minFU.utility||X.getNewSupport()>=minFU.sup){
					if (X.getSumIutils()+X.getSumRutils()>=global_umax[X.getSupport()]){
					List<UtilityList2> exULs = new ArrayList<>();
					for(int j=i+1; j < ULs.size(); j++){
						UtilityList2 Y = ULs.get(j);
						// we construct the extension pXY
						// and add it to the list of extensions of pX
						UtilityList2 Pxy = construct(pUL, X, Y);
						//Hup-Miner
						if(Pxy != null&&!Pxy.elements.isEmpty()){
							exULs.add(Pxy);
							jointCount++;
						}
					}
					// We make a recursive call to discover all itemsets with the prefix pXY
					D_Mine(X, exULs);
					}
//					else {
//					System.out.println("global prune");
//				}
//			}
//			else{
//				System.out.println("local prune");
//			}
		}
	}

	private UtilityList2 construct(UtilityList2 pUL, UtilityList2 x, UtilityList2 y) {
		UtilityList2 pxyUL = new UtilityList2();
		pxyUL.itemset.addAll(x.itemset);
		pxyUL.itemset.add(y.itemset.get(y.itemset.size()-1));
		int totalNewUtility = x.getSumNewIutils()+x.getNewSumRutils();
		int totalNewSup = x.getNewSupport();
		int totalUtility = x.getSumIutils()+x.getSumRutils();
		int totalSup = x.getSupport();
		// for each element in the utility list of pX
		List<Element2> elementListPx=x.elements;
		List<Element2> elementListPy=y.elements;
		for (int i = x.startOfNew,j = y.startOfNew; i < elementListPx.size()&&j < elementListPy.size(); ) {
			Element2 ex=elementListPx.get(i);
			Element2 ey=elementListPy.get(j);
			if (ex.tid==ey.tid){

				if(pUL == null){

					Element2 newElement = new Element2(ex.tid, ex.iutils + ey.iutils, ey.rutils);
					pxyUL.addElement(newElement);

				}else{

					Element2 e = findElementWithTID(pUL, ex.tid,pUL.startOfNew,pUL.elements.size()-1);
					if(e != null){

						Element2 newElement = new Element2(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils);
						pxyUL.addElement(newElement);

					}else {

						Element2 newElement = new Element2(ex.tid,ex.iutils + ey.iutils, ey.rutils);
						pxyUL.addElement(newElement);

					}
				}

				i++;j++;
			}else if (ex.tid>ey.tid){
				j++;
			}else if (ex.tid<ey.tid){
				//== new optimization - LA-prune == /
				totalNewUtility -= (ex.iutils+ex.rutils);
				totalNewSup-=1;
				totalUtility -= (ex.iutils+ex.rutils);
				totalSup-=1;
				if(totalUtility<global_umax[totalSup]){
					return null;
				}
//				if((totalUtility<global_umax[totalSup])||(totalNewUtility<minFU.utility&&totalNewSup<minFU.sup)){
////					System.out.println("local prune");
//					return null;
//				}
				i++;
			}

		}
		int numOfOld=0;
		for (int i = 0,j = 0; i < x.startOfNew&&j < y.startOfNew; ) {
			Element2 ex=elementListPx.get(i);
			Element2 ey=elementListPy.get(j);
			if (ex.tid==ey.tid){

				if(pUL == null){

					Element2 newElement = new Element2(ex.tid, ex.iutils + ey.iutils, ey.rutils);
					pxyUL.addElement(newElement,numOfOld);
					numOfOld++;

				}else{

					Element2 e = findElementWithTID(pUL, ex.tid,0,pUL.startOfNew-1);
					if(e != null){

						Element2 newElement = new Element2(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils);
						pxyUL.addElement(newElement,numOfOld);
						numOfOld++;

					}else {

						Element2 newElement = new Element2(ex.tid,ex.iutils + ey.iutils, ey.rutils);
						pxyUL.addElement(newElement,numOfOld);
						numOfOld++;

					}
				}

				i++;j++;
			}else if (ex.tid>ey.tid){
				j++;
			}else if (ex.tid<ey.tid){
				//== new optimization - LA-prune == /
				totalUtility -= (ex.iutils+ex.rutils);
				totalSup-=1;
				if(totalUtility<global_umax[totalSup]){
					return null;
				}
				i++;
			}

		}
		pxyUL.setStartOfNew(numOfOld);
		// return the utility list of pXY.
		return pxyUL;
	}

	/**
	 * Method to judge whether the PSFUP is a SFUP
	 * @throws IOException
	 */
	private void judge(UtilityList2 X) {

		if(X.getSumIutils()==global_umax[X.getSupport()]&&(X.getSupport()==global_umax.length-1||global_umax[X.getSupport()]>global_umax[X.getSupport()+1])){
			if(SFUA[X.getSupport()]!=null){
				Skyline pattern = new Skyline(X.getItemset(),X.getSupport(),X.getSumIutils());
				SFUA[X.getSupport()].add(pattern);
			}else{
				SkylineList skylineList= new SkylineList();
				Skyline temp=new Skyline(X.getItemset(),X.getSupport(),X.getSumIutils());
				skylineList.add(temp);
				SFUA[X.getSupport()]=skylineList;
			}
		}
		else if(X.getSumIutils()>global_umax[X.getSupport()]){
			global_umax[X.getSupport()]=X.getSumIutils();
			SkylineList skylineList= new SkylineList();
			Skyline temp=new Skyline(X.getItemset(),X.getSupport(),X.getSumIutils());
			skylineList.add(temp);
			SFUA[X.getSupport()]=skylineList;

			for(int i=1;i<X.getSupport();i++){
				if(X.getSumIutils()>global_umax[i]){
					global_umax[i]=X.getSumIutils();
					SFUA[i]=null;
				}
			}
		}
	}
	/**
	 * Do a binary search to find the element with a given tid in a utility list
	 * @param ulist the utility list
	 * @param tid  the tid
	 * @return  the element or null if none has the tid.
	 */
	private Element2 findElementWithTID(UtilityList2 ulist, int tid,int start,int end){
		List<Element2> list = ulist.elements;
		// perform a binary search to check if  the subset appears in  level k-1.
        int first = start;
        int last = end;
        // the binary search
        while( first <= last )
        {
        	int middle = ( first + last ) >>> 1; // divide by 2

            if(list.get(middle).tid < tid){
            	first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
            }
            else if(list.get(middle).tid > tid){
            	last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
            }
            else{
            	return list.get(middle);
            }
        }
		return null;
	}

	/**
	 * Method to write skyline frequent-utility itemset to the output file.
	 * @param skylineList The list of skyline frequent-utility itemsets
	 */
	private void writeOut(SkylineList skylineList[]) throws IOException {
		oldSkylineToNewFU.clear();
		//Create a string buffer
		writer = new BufferedWriter(new FileWriter(output,true));
		StringBuilder buffer = new StringBuilder();
		for(int i=1; i<skylineList.length; i++){
			if(skylineList[i]!=null){
				for(int j=0; j<skylineList[i].size(); j++){
					FU fu=new FU();
					oldSkylineToNewFU.put(skylineList[i].get(j).itemSet,fu);
					buffer.append(skylineList[i].get(j).itemSet);
					buffer.append(" #SUP:");
					buffer.append(skylineList[i].get(j).frequent);
					buffer.append(" #UTILITY:");
					buffer.append(skylineList[i].get(j).utility);
					buffer.append(System.lineSeparator());
				}
				skylineList[i].skylinelist.clear();
			}
		}
		writer.write(buffer.toString());
		//System.out.print(buffer.toString());
		writer.write("----------------------------");
		//System.out.println("----------------------------");
		writer.write("\n");
		writer.flush();
		writer.close();
	}

	/**
	 * Print statistics about the latest execution to System.out.
	 */
	public void printStats() {
//		System.out.println("=============  uEmax skyline ALGORITHM v 2.11 - STATS =============");
////		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
//		System.out.println(" Memory ~ " + maxMemory+ " MB");
//		System.out.println(" old.Skyline itemsets count : " + sfupCount);
//		System.out.println(" Join itemsets count : " + jointCount);
//		System.out.println("===================================================");
	}
}