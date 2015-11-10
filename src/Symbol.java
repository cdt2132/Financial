
public class Symbol {
	String id;
	double pnl;
	
	public Symbol(String id, double pnl) {
		this.id = id;
		this.pnl = pnl;
	}
	
	public double getPnL() {
		return pnl;
	}
	
	public String getId() {
		return id;
	}
}
