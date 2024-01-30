package old;

import java.util.ArrayList;
import java.util.List;

public class SkylineList{
    //skylinelist store different itemsets that have same frequency and same utility.
    List<Skyline> skylinelist= new ArrayList<>();

    public Skyline get(int index) {
        return skylinelist.get(index);
    }

    public void add(Skyline e) {
        skylinelist.add(e);
    }

    public void remove(int index) {
        skylinelist.remove(index);
    }

    public int size(){
        return skylinelist.size();
    }
}
