import java.util.ArrayList;
import java.util.List;

public class SkylineRFM {
    List<Integer> itemSet = new ArrayList<>();
    int support;
    int utility;
    double recency;

    public SkylineRFM(List<Integer> itemSet, int support, int utility, double recency) {
        this.itemSet = itemSet;
        this.support = support;
        this.utility = utility;
        this.recency = recency;
    }

    public SkylineRFM(List<Integer> itemSet) {
        this.itemSet = itemSet;
    }

    public List<Integer> getItemSet() {
        return itemSet;
    }

    public void setItemSet(List<Integer> itemSet) {
        this.itemSet = itemSet;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public double getRecency() {
        return recency;
    }

    public void setRecency(long recency) {
        this.recency = recency;
    }
}
