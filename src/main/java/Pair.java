public class Pair {
    int item = 0;
    int utility = 0;

    public Pair(int item, int utility) {
        this.item = item;
        this.utility = utility;
    }

    public Pair() {

    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }
}
