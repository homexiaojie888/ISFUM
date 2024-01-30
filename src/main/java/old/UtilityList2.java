package old;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a UtilityList as used by the HUI-Miner algorithm.
 *
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 */

public class UtilityList2 {
	List<Integer> itemset=new ArrayList<>();  // the item
	int sumOldIutils = 0;  // the sum of item utilities
	int sumOldRutils = 0;  // the sum of remaining utilities

	int sumNewIutils = 0;  // the sum of item utilities
	int sumNewRutils = 0;  // the sum of remaining utilities


	int startOfNew=0;
	List<Element2> elements = new ArrayList<Element2>();  // the elements
	
		/**
	 * Method to add an element to this utility list and update the sums at the same time.
	 */
	public void addElement(Element2 element){
		sumNewIutils += element.iutils;
		sumNewRutils += element.rutils;
		elements.add(element);
	}
	public void addElement(Element2 element,int index){
		sumOldIutils += element.iutils;
		sumOldRutils += element.rutils;
		elements.add(index,element);
	}
	public int getSupport(){
		return elements.size();
	}

	public int getNewSupport(){
		return elements.size()-startOfNew;
	}

	public List<Integer> getItemset() {
		return itemset;
	}

	public void setItemset(List<Integer> itemset) {
		this.itemset = itemset;
	}

	public void setStartOfNew(int startOfNew) {
		this.startOfNew = startOfNew;
	}

	public int getSumNewIutils() {
		return sumNewIutils;
	}

	public int getNewSumRutils() {
		return sumNewRutils;
	}

	public int getSumIutils() {
		return sumNewIutils+sumOldIutils;
	}

	public int getSumRutils() {
		return sumNewRutils+sumOldRutils;
	}

	public void append(UtilityList2 oldL) {
		if (oldL!=null&&!oldL.elements.isEmpty()){
			elements.addAll(0,oldL.elements);
			startOfNew=oldL.elements.size();
			sumOldIutils=oldL.sumOldIutils;
			sumOldRutils=oldL.sumOldRutils;
		}
	}

	public void setSumOldIutils(int sumOldIutils) {
		this.sumOldIutils = sumOldIutils;
	}

	public void setSumOldRutils(int sumOldRutils) {
		this.sumOldRutils = sumOldRutils;
	}

	public void setSumNewIutils(int sumNewIutils) {
		this.sumNewIutils = sumNewIutils;
	}

	public void setSumNewRutils(int sumNewRutils) {
		this.sumNewRutils = sumNewRutils;
	}
}
