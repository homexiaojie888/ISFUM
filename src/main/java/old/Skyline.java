package old;

import java.util.ArrayList;
import java.util.List;

public class Skyline {
    List<Integer> itemSet =new ArrayList<>();  //the itemset
    int frequent; 	//the frequency of itemset
    int utility; //the utility of itemset

    public Skyline(List<Integer> itemSet, int frequent, int utility) {
        this.itemSet = itemSet;
        this.frequent = frequent;
        this.utility = utility;
    }

    public List<Integer> getItemSet() {
        return itemSet;
    }

    public void setItemSet(List<Integer> itemSet) {
        this.itemSet = itemSet;
    }

    public int getFrequent() {
        return frequent;
    }

    public void setFrequent(int frequent) {
        this.frequent = frequent;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }
}