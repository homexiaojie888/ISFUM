import java.util.List;

public class SURV {
    int row_support;
    double cln_rencency;
    int max_util;
    boolean isValid=true;
    List<Integer> itemset=null;



    public List<Integer> getItemset() {
        return itemset;
    }

    public void setItemset(List<Integer> itemset) {
        this.itemset = itemset;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public SURV(int row_support,int max_util,double cln_rencency) {
        this.row_support = row_support;
        this.max_util = max_util;
        this.cln_rencency = cln_rencency;
    }


    public int getRow_support() {
        return row_support;
    }

    public void setRow_support(int row_support) {
        this.row_support = row_support;
    }

    public double getCln_rencency() {
        return cln_rencency;
    }

    public void setCln_rencency(double cln_rencency) {
        this.cln_rencency = cln_rencency;
    }

    public int getMax_util() {
        return max_util;
    }

    public void setMax_util(int max_util) {
        this.max_util = max_util;
    }

    public void addSupport(int support) {
        row_support+=support;
    }

    public void addUtility(int utility) {
        max_util+=utility;
    }

    public void addRencency(double rencency) {
        cln_rencency+=rencency;
    }

//    public int compareTo(SURV o) {
//        if (this.getRow_support()-o.getRow_support()==0){
//            return Double.compare(this.getCln_rencency(),o.getCln_rencency());
//        }else {
//            return this.getRow_support()-o.getRow_support();
//        }
//    }

}
