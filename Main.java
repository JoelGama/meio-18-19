public class Main {

	float C1 = 17.37;
	float C2 = 22;
	float C3 = 900;
	float t = 2;

	float S = 1000;
	float s = 600;

	float initialStock = 1000;

	float avg18 = 414.34; // calcular

	ArrayList<Float> avg2018;
	ArrayList<Float> avg2019;
	ArrayList<Float> stdDev;
	ArrayList<Float> trend;

	public void add2018() {
		this.avg2018 = new ArrayList<>();
		this.avg2018.add(419.1);
		this.avg2018.add(554.2);
		this.avg2018.add(334.6);
	}

	public void calculate2019() {
		this.trend = new ArrayList();
		this.trend.add(this.avg18*(1+((16+1)/2/50)*0.038));
		this.trend.add(this.avg18*(1+((17+28)/2/50)*0.038));
		this.trend.add(this.avg18*(1+((29+50)/2/50)*0.038));

		this.avg2019 = new ArrayList<>();
		this.avg2019.add(this.avg2018.get(0)-this.avg18+this.trend.get(0));
		this.avg2019.add(this.avg2018.get(1)-this.avg18+this.trend.get(1));
		this.avg2019.add(this.avg2018.get(2)-this.avg18+this.trend.get(2));

		this.stdDev = new ArrayList();
		this.stdDev.add(this.avg2019.get(0)*0.087);
		this.stdDev.add(this.avg2019.get(1)*0.087);
		this.stdDev.add(this.avg2019.get(2)*0.087);
	}

	public int randL() {
		int rand = Math.random();

		if (rand <= 0.6) {
			return 1;
		}

		return 2;			
	}

	private int period(int week) {
		if (week > 0 && week <= 16) {
			return 0;
		} else if (week <= 28) {
			return 1;
		} else {
			return 2;
		}
	}

	public void round() {
		ArrayList<Integer> stock = new ArrayList<>();
		stock.add(initialStock);
		for (int i = 1; i <= 50; i++) {
			int bought = 0;
			int r = (int)(new NormalDistributionImpl(avg2018.get(this.period(i)), stdDev.get(this.period(i)))).sample();
			int l = randL();
			boolean delivery = i - l % 2 == 0;
			boolean buy = i > 2 && stock.get(i-l) < this.s;
			if (delivery && buy) {
				bought = stock.get(i-l) - this.s;
			}
			stock.add(stock.get(i-1) - r + bought);
		}


	}

	public static void main(String[] args) {
		add2018();
		calculate2019();

	}
}