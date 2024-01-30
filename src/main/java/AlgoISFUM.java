import java.io.*;
import java.util.*;

public class AlgoISFUM {

	String input;
	String output;
	long runtime ;
	long candidatesCount=0;
	long jointCount =0;
	int patternCount=0;
	double magnify;
	List<SURV> URmaxList=new ArrayList<>();


	Set<Integer> distinctItems=new TreeSet<>();
	Map<Integer, SURV> ISU_1= new HashMap<>();


	BufferedWriter writer = null;
    long tsNow;
    double eplison;
	//<F,<R,patterns>>
	Map<Integer,Map<Double,List<SkylineRFM>>> SRFM=new TreeMap<>();

	Comparator F1=new Comparator<SURV>() {
		@Override
		public int compare(SURV o1, SURV o2) {
			if (o1.getRow_support()-o2.getRow_support()==0){
				int compR=Double.compare(o1.getCln_rencency(),o2.getCln_rencency());
				if (compR==0){
					return (o1.getMax_util()-o2.getMax_util());
				}else {
					return compR;
				}
			}else {
				return o1.getRow_support()-o2.getRow_support();
			}
		}
	};
	Comparator R1=new Comparator<SURV>() {
		@Override
		public int compare(SURV o1, SURV o2) {
			int compR=Double.compare(o1.getCln_rencency(),o2.getCln_rencency());
			if (compR==0){
				int compF=o1.getRow_support()-o2.getRow_support();
				if (compF==0){
					return (o1.getMax_util()-o2.getMax_util());
				}else {
					return compF;
				}

			}else {
				return compR;
			}
		}
	};
	public AlgoISFUM() {
	}

	public void runAlgorithm(String input, int DataSize, long tsnow, double div,double eplison,  String output) throws IOException {

		this.input=input;
        this.eplison=eplison;
		this.output=output;
		this.tsNow=tsnow;
		this.magnify = Math.pow(10, div);
		long startTimestamp = System.currentTimeMillis();

		// 	 * get distinctItems
		//	 * get ISU_1
		FirstScan(DataSize);

		List<URtilityList> listOfUtilityLists = new ArrayList<>();
		Map<Integer, URtilityList> mapItemToUtilityList = new HashMap<>();
		for(Integer item: distinctItems){
			URtilityList uList = new URtilityList();
			uList.itemset.add(item);
			mapItemToUtilityList.put(item, uList);
			listOfUtilityLists.add(uList);
		}
		//   * build utilitylist for each 1-item
		SecondScan(mapItemToUtilityList,DataSize);

		URmaxList.addAll(ISU_1.values());

		batchUpdateURmax(URmaxList,true);

		MemoryLogger.getInstance().checkMemory();

		D_Mine(null, listOfUtilityLists);

		runtime = System.currentTimeMillis()-startTimestamp;
		writeOut(SRFM);

	}
	/**
	 * 先将utility存到urmax里(大于对应的值才存)
	 * 再从左到右，从上到下更新整个urmax
	 * @param
	 */
	private void batchUpdateURmax(List<SURV> list,boolean isF1) {
		if (isF1){
			Collections.sort(list, F1);
		}else {
			Collections.sort(list, R1);
		}

		for (int i = 1; i < list.size(); i++) {
			for (int j = 0; j < i; j++) {
				SURV uRmaxi=list.get(i);
				SURV uRmaxj=list.get(j);
				if (uRmaxj.getRow_support()<=uRmaxi.getRow_support()&&Double.compare(uRmaxj.getCln_rencency(),uRmaxi.getCln_rencency())<=0&&uRmaxj.getMax_util()<uRmaxi.getMax_util()){
					list.remove(j);
					i--;
					j--;
				}
			}
		}

	}


	private void batchUpdateURmaxSRFM(SURV surv) {

		for (int i = 0; i < URmaxList.size(); i++) {
			SURV uRmaxi=URmaxList.get(i);
			if (uRmaxi.getRow_support()<=surv.getRow_support()&&Double.compare(uRmaxi.getCln_rencency(),surv.getCln_rencency())<=0&&uRmaxi.getMax_util()<surv.getMax_util()){
//				System.out.println("remove element "+i+" in urmaxlist");
				URmaxList.remove(i);
				i--;

				if (SRFM.get(uRmaxi.getRow_support())!=null&&!SRFM.get(uRmaxi.getRow_support()).isEmpty()&&SRFM.get(uRmaxi.getRow_support()).get(uRmaxi.getCln_rencency())!=null&&!SRFM.get(uRmaxi.getRow_support()).get(uRmaxi.getCln_rencency()).isEmpty()) {
//					System.out.println("clear SRFM["+uRmaxi.getRow_support()+"]["+uRmaxi.getCln_rencency()+"]");
					SRFM.get(uRmaxi.getRow_support()).get(uRmaxi.getCln_rencency()).clear();
				}
			}

		}

	}

	public void addToSRFM(URtilityList X){
		Map<Double,List<SkylineRFM>> temp=SRFM.get(X.getSupport());
		if (temp==null){
			temp=new TreeMap<>();
			SRFM.put(X.getSupport(),temp);
		}
		List<SkylineRFM> list=temp.get(X.getRentcency());
		if (list==null){
			list=new ArrayList<>();
			temp.put(X.getRentcency(),list);
		}
		SkylineRFM skylineRFM=new SkylineRFM(X.getItemset(),X.getSupport(),X.getSumIutils(),X.getRentcency());
		list.add(skylineRFM);
	}

	public void updateSRFM(URtilityList X){
//		System.out.println("replace SRFM["+X.getSupport()+"]["+X.getRentcency()+"] with "+X.getItemset());
		Map<Double,List<SkylineRFM>> temp=SRFM.get(X.getSupport());
		if (temp==null){
			temp=new TreeMap<>();
			SRFM.put(X.getSupport(),temp);
		}

		List<SkylineRFM> list=new ArrayList<>();
		temp.put(X.getRentcency(),list);

		SkylineRFM skylineRFM=new SkylineRFM(X.getItemset(),X.getSupport(),X.getSumIutils(),X.getRentcency());
		list.add(skylineRFM);
	}

	/**
	 * build utilitylist for each 1-item
	 * store ISU_2	 *
	 * @param mapItemToUtilityList
	 * @param dataSize
	 * @throws IOException
	 */
	private void SecondScan(Map<Integer, URtilityList> mapItemToUtilityList, int dataSize) throws IOException {
		BufferedReader myInput = null;
		String thisLine;
		int tid=1;
		try {
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			while ((thisLine = myInput.readLine()) != null) {
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}
				String split[] = thisLine.split(":");
				String items[] = split[0].split(" ");
				int TU=Integer.valueOf(split[1]);
				String itemUtils[] = split[2].split(" ");
                long timestamp=Long.parseLong(split[3]);
				double rencency=Math.pow(1-eplison,(((double)(tsNow-timestamp)/(magnify))));

				int remainingUtility =0;
				List<Pair> revisedTransaction = new ArrayList<Pair>();
				// for each item
				for(int i=0; i <items.length; i++){
					Integer item = Integer.parseInt(items[i]);
					Integer utility=Integer.parseInt(itemUtils[i]);
					Pair pair = new Pair(item,utility);

					revisedTransaction.add(pair);
					remainingUtility += pair.utility;
				}
	            checkIsEqual(remainingUtility,TU,tid);

				Collections.sort(revisedTransaction, new Comparator<Pair>(){
					public int compare(Pair o1, Pair o2) {
						return compareItems(o1.item, o2.item);
					}});

				for(int i=0; i<revisedTransaction.size(); i++){

					Pair pair =  revisedTransaction.get(i);
					remainingUtility = remainingUtility - pair.utility;

					URtilityList utilityListOfItem = mapItemToUtilityList.get(pair.item);
					Element element = new Element(tid, pair.utility, remainingUtility, timestamp, rencency);
					utilityListOfItem.addElement(element);
					//-------consider eliminate-------------------------

					//-------consider eliminate-------------------------
				}
				if (tid==dataSize){
					return;
				}
				tid++;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
		}
	}


	/**
	 * get distinct items into TreeSet
	 * store ISU_1
	 * @throws IOException
	 */
	private void FirstScan(int DataSize) throws IOException {
		BufferedReader myInput = null;
		String thisLine;
		int tid=0;
		try {
			myInput = new BufferedReader(new InputStreamReader( new FileInputStream(input)));
			while ((thisLine = myInput.readLine()) != null) {
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}
				String split[] = thisLine.split(":");
				String items[] = split[0].split(" ");
				String itemUtils[] = split[2].split(" ");
                long timestamp=Long.parseLong(split[3]);
				double rencency=Math.pow(1-eplison,(((double)(tsNow-timestamp)/(magnify))));

				if (Double.compare(rencency,0.0d)==0){
//					System.out.println("tid = "+tid+" rency = "+rencency);
				}
				for(int i=0; i <items.length; i++){
					Integer item = Integer.parseInt(items[i]);
					Integer utility=Integer.parseInt(itemUtils[i]);
					distinctItems.add(item);
					SURV itemSURV = ISU_1.get(item);
					if(itemSURV==null){
						SURV surv = new SURV(1,utility,rencency);
						ISU_1.put(item, surv);
					}else{
						itemSURV.addSupport(1);
						itemSURV.addUtility(utility);
						itemSURV.addRencency(rencency);
					}

				}
				tid++;
				if (tid==DataSize){
					//tsNow=timestamp+1;
					//System.out.println("tsnow="+tsNow);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
		}
	}
	private void checkIsEqual(int remainingUtility, int tu,int line) {
		if (remainingUtility!=tu){
//			System.out.println(line+" error " +tu+" "+remainingUtility);
		}
	}
	private int compareItems(int item1, int item2) {
		return item1 - item2;
	}

	private void D_Mine(URtilityList pUL, List<URtilityList> ULs){

		for(int i=0; i< ULs.size(); i++){
			candidatesCount++;

			URtilityList X = ULs.get(i);

			int utilty=X.getSumIutils();
			int sup=X.getSupport();
			double rency=X.getRentcency();

//			System.out.println("current itemset: "+X.getItemset());

			SURV surv=null;
			int index=0;
			int maxU=Integer.MIN_VALUE;
			for (int ind=0; ind < URmaxList.size(); ind++) {
				SURV cur=URmaxList.get(ind);
				if (cur.getRow_support()>=sup&&Double.compare(cur.getCln_rencency(),rency)>=0){
					if (cur.getMax_util()>maxU){
						surv=cur;
						index=ind;
						maxU=cur.getMax_util();
					}
				}
			}

			//1-itemset 有最大的F-score和R-score
			if (utilty>=surv.getMax_util()){
//				System.out.println("judge "+X.getItemset());
				judge(X,surv,index);
			}else{
//				System.out.println("verify invalid for "+X.getItemset());
			}

			if(X.getSumIutils()+X.getSumRutils() >= surv.getMax_util()){
				List<URtilityList> exULs = new ArrayList<>();
				for(int j=i+1; j < ULs.size(); j++){
					URtilityList Y = ULs.get(j);
					URtilityList Pxy = construct2(pUL, X, Y);
					if(Pxy != null && !Pxy.elements.isEmpty()){
						exULs.add(Pxy);
						jointCount++;
					} else if (Pxy != null && Pxy.elements.isEmpty()) {
//						System.out.println("breadth prune "+Pxy.getItemset()+" and its extensions");
					}
				}
				D_Mine(X, exULs);
			}else{
//				System.out.println("depth prune extensions of "+X.getItemset());
			}
		}
	}

	/**
	 * Method to judge whether the PSFUP is a SFUP
	 * @throws IOException
	 */
	private void judge(URtilityList X, SURV surv,int index){
		//=
		if (X.getSumIutils()==surv.getMax_util()&&Double.compare(X.getRentcency(),surv.getCln_rencency())==0&&X.getSupport()==surv.getRow_support()){
//			System.out.println("add "+ X.getItemset() + "to SRFM["+X.getSupport()+"]["+X.getRentcency()+"] without replacement");
			addToSRFM(X);
			//>
		} else if (X.getSumIutils()>surv.getMax_util()) {
			if (X.getSupport()==surv.getRow_support()&&Double.compare(X.getRentcency(),surv.getCln_rencency())==0){
//				System.out.println("update urmaxlist["+index+"] with new M-score = "+X.getSumIutils());
				URmaxList.set(index,X.getSURV());
			}else{
				URmaxList.add(X.getSURV());
//				System.out.println("add new element "+":(F="+X.getSupport()+" R="+X.getRentcency()+" M="+X.getSumIutils()+") to urmaxlist "+X.getItemset());
				Collections.sort(URmaxList, F1);
			}

			updateSRFM(X);
			batchUpdateURmaxSRFM(X.getSURV());
		}


	}


	public URtilityList construct2(URtilityList UL_P, URtilityList UL_Px, URtilityList UL_Py) {

		URtilityList UL_Pxy = new URtilityList();

		List<Integer> PxyItemset = UL_Pxy.getItemset();
		PxyItemset.addAll(UL_Px.getItemset());
		PxyItemset.add(UL_Py.getItemset().get(UL_Py.getItemset().size() - 1));

		List<Element> elementListPx = UL_Px.getElements();
		List<Element> elementListPy = UL_Py.getElements();

		int totalUtility = UL_Px.getSumIutils() + UL_Px.getSumRutils();
		int totalSup = UL_Px.getSupport();
		double totalRency = UL_Px.getRentcency();

		for (int i = 0, j = 0; i < elementListPx.size() && j < elementListPy.size(); ) {
			Element ex = elementListPx.get(i);
			Element ey = elementListPy.get(j);
			if (ex.tid == ey.tid) {
				if (UL_P == null) {
					Element newElement = new Element(ex.tid, ex.iutils + ey.iutils, ey.rutils, ex.timestamp, ex.rencency);
					UL_Pxy.addElement(newElement);
				} else {
					Element e = findElementWithTID(UL_P, ex.tid);
					if (e != null) {
						Element newElement = new Element(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils, ex.timestamp, ex.rencency);
						UL_Pxy.addElement(newElement);
					} else {
						Element newElement = new Element(ex.tid, ex.iutils + ey.iutils, ey.rutils, ex.timestamp, ex.rencency);
						UL_Pxy.addElement(newElement);
					}
				}

				i++;
				j++;
			} else if (ex.tid > ey.tid) {
				j++;
			} else if (ex.tid < ey.tid) {
				//== new optimization - LA-prune == /
				totalUtility -= (ex.iutils + ex.rutils);
				totalSup -= 1;
				totalRency -= ex.rencency;
				SURV surv=null;
				int maxU=Integer.MIN_VALUE;
				for (int k = 0; k < URmaxList.size(); k++) {
					SURV cur=URmaxList.get(k);
					if (cur.getRow_support()>=totalSup&&Double.compare(cur.getCln_rencency(),totalRency)>=0){
						if (cur.getMax_util()>maxU){
							maxU=cur.getMax_util();
							surv=cur;
						}
					}
				}
				if (totalUtility<surv.getMax_util()){
//					System.out.println("breadth prune "+PxyItemset+" and its extensions");
					return null;
				}
				i++;

			}

		}
		return UL_Pxy;
	}

	private Element findElementWithTID(URtilityList ulist, int tid){
		List<Element> list = ulist.getElements();
		// perform a binary search to check if  the subset appears in  level k-1.
        int first = 0;
        int last = list.size() - 1;
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


	private void writeOut(Map<Integer, Map<Double, List<SkylineRFM>>> SRFM) throws IOException {
		writer = new BufferedWriter(new FileWriter(output,true));
		StringBuilder buffer = new StringBuilder();
		for (Integer sup:SRFM.keySet()) {
			Map<Double, List<SkylineRFM>> listMap=SRFM.get(sup);
			if (listMap!=null&&!listMap.isEmpty()){
				for (Double rency:listMap.keySet()) {
					List<SkylineRFM> list=listMap.get(rency);
					if (list!=null&&!list.isEmpty()){
						for (int i = 0; i < list.size(); i++) {
							SkylineRFM skylineRFM=list.get(i);
							buffer.append(skylineRFM.getItemSet());
							buffer.append(" #sup:");
							buffer.append(skylineRFM.getSupport());
							buffer.append(" #utility:");
							buffer.append(skylineRFM.getUtility());
							buffer.append(" #rencency:");
							buffer.append(skylineRFM.getRecency());
							buffer.append(System.lineSeparator());
							patternCount=patternCount+1;

						}

					}
				}
			}
		}

		writer.write(buffer.toString());
		writer.write("----------------------------");
		writer.write("\n");
		writer.flush();
		writer.close();
	}

	public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Long> jointCountlist,List<Integer> patternlist) {

		runTimelist.add((double)runtime/1000);
		memorylist.add(MemoryLogger.getInstance().getMaxMemory());
		candidateslist.add(candidatesCount);
		jointCountlist.add(jointCount);
		patternlist.add(patternCount);
		System.out.println("runime(s): "+((double)runtime/1000));
		System.out.println("memor: "+MemoryLogger.getInstance().getMaxMemory());
		System.out.println("candidae: "+candidatesCount);
		System.out.println("join count: "+jointCount);
		System.out.println("pattern number: "+patternCount);

	}
}