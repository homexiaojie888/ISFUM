import java.util.ArrayList;
import java.util.List;

class SkylineRFMList {

    List<SkylineRFM> SkylineRFMlist= new ArrayList<>();

    public SkylineRFM get(int index) {
        return SkylineRFMlist.get(index);
    }

    public void add(SkylineRFM e) {
        SkylineRFMlist.add(e);
    }

    public void remove(int index) {
        SkylineRFMlist.remove(index);
    }

    public int size(){
        return SkylineRFMlist.size();
    }
    public void clear(){
        SkylineRFMlist.clear();
    }

}
