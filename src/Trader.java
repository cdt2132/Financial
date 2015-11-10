
public class Trader {
	int id;
	double pnl;
	
	public Trader(int id, double pnl) {
		this.id = id;
		this.pnl = pnl;
	}
	
	public double getPnL() {
		return pnl;
	}
	
	public double getId() {
		return id;
	}
}
