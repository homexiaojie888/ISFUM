import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a UtilityList as used by the HUI-Miner algorithm.
 *
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 */

class URtilityList {
	List<Integer> itemset=new ArrayList<>();  // the item
	int sumIutils = 0;  // the sum of item utilities
	int sumRutils = 0;  // the sum of remaining utilities

	double rentcency=0;



	List<Element> elements = new ArrayList<Element>();  // the elements
	
		/**
	 * Method to add an element to this utility list and update the sums at the same time.
	 */
	public void addElement(Element element){
		sumIutils += element.iutils;
		sumRutils += element.rutils;
		rentcency += element.rencency;
		elements.add(element);
	}
	public int getSupport(){
		return elements.size();
	}

	public List<Integer> getItemset() {
		return itemset;
	}

	public void setItemset(List<Integer> itemset) {
		this.itemset = itemset;
	}

	public int getSumIutils() {
		return sumIutils;
	}

	public void setSumIutils(int sumIutils) {
		this.sumIutils = sumIutils;
	}

	public int getSumRutils() {
		return sumRutils;
	}

	public void setSumRutils(int sumRutils) {
		this.sumRutils = sumRutils;
	}
	public double getRentcency() {
		return rentcency;
	}

	public void setRentcency(double rentcency) {
		this.rentcency = rentcency;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public SURV getSURV(){
		return new SURV(getSupport(),getSumIutils(),getRentcency());
	}
}
