package old;

/**
 * This class represents an EMSFUI_B.Element of a utility list as used by the HUI-Miner algorithm.
 * 
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 */

class Element2 {
	// The three variables as described in the paper:
	/** transaction id */
	final int tid ;
	/** itemset utility */
	final int iutils;
	/** remaining utility */
	final int rutils;

	//final long timestamp;

	/**
	 * Constructor.
	 *
	 * @param tid       the transaction id
	 * @param iutils    the itemset utility
	 * @param rutils    the remaining utility

	 */
	public Element2(int tid, int iutils, int rutils){
		this.tid = tid;
		this.iutils = iutils;
		this.rutils = rutils;
		//this.timestamp = timestamp;
	}
}

