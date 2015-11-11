/**
 * Trade Symbol class
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */


public class TradeSymbol {
	String id;
	double pnl;
	
	/** Constructor */
	public TradeSymbol(String id, double pnl) {
		this.id = id;
		this.pnl = pnl;
	}
	
	/** PnL accessor
	 * @return pnl double profit or loss
	 */
	public double getPnL() {
		return pnl;
	}
	
	/** Id accessor 
	 * @return id string symbol id
	 */
	
	public String getId() {
		return id;
	}
}
