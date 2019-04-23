import java.util.ArrayList;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class Main {

	float C1 = 17.37f;
	float C2 = 22;
	float C3 = 900;
	float t = 2;

	int S = 2000;
	int s = 600;

	int initialStock = 2000;

	float avg18 = 414.34f; // calcular

	ArrayList<Float> avg2018;
	ArrayList<Float> avg2019;
	ArrayList<Float> stdDev;
	ArrayList<Float> trend;

	public void add2018() {
		this.avg2018 = new ArrayList<>();
		this.avg2018.add(419.1f);
		this.avg2018.add(554.2f);
		this.avg2018.add(334.6f);
	}

	public void calculate2019() {
		this.trend = new ArrayList();
		this.trend.add(this.avg18*(1+((16+1)/2/50)*0.038f));
		this.trend.add(this.avg18*(1+((17+28)/2/50)*0.038f));
		this.trend.add(this.avg18*(1+((29+50)/2/50)*0.038f));

		this.avg2019 = new ArrayList<>();
		this.avg2019.add(this.avg2018.get(0)-this.avg18+this.trend.get(0));
		this.avg2019.add(this.avg2018.get(1)-this.avg18+this.trend.get(1));
		this.avg2019.add(this.avg2018.get(2)-this.avg18+this.trend.get(2));

		this.stdDev = new ArrayList();
		this.stdDev.add(this.avg2019.get(0)*0.087f);
		this.stdDev.add(this.avg2019.get(1)*0.087f);
		this.stdDev.add(this.avg2019.get(2)*0.087f);
	}

	public int randL() {
		double rand = Math.random();

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

	public void round() throws org.apache.commons.math.MathException {
		ArrayList<Integer> stocks = new ArrayList<>();
		stocks.add(initialStock);

		ArrayList<NormalDistributionImpl> normal = new ArrayList<>();

		NormalDistributionImpl normal1 = new NormalDistributionImpl(avg2018.get(0), stdDev.get(0));
		NormalDistributionImpl normal2 = new NormalDistributionImpl(avg2018.get(1), stdDev.get(1));
		NormalDistributionImpl normal3 = new NormalDistributionImpl(avg2018.get(2), stdDev.get(2));
		normal.add(normal1); normal.add(normal2); normal.add(normal3);

		for (int i = 1; i <= 50; i++) {
			int bought = 0;
			int r = (int)(normal.get(this.period(i)).sample());

			int l = randL();
			boolean delivery = (i - l) % 2 == 0;
			boolean buy = i > 2 && stocks.get(i-l) < this.s;
			if (delivery && buy) {
				bought = this.S - stocks.get(i-l);
			}

			int newStock = stocks.get(i-1) - r * l + bought;

			stocks.add(newStock);

			System.out.println("Stock " + i + ": " + newStock + ". Recebeu? " + buy + " " + l);
		}

		System.out.println("Final Cost: " + this.cost(stocks));

	}

	private float cost(ArrayList<Integer> stocks) {
		int sum = 0;
		float lost;

		for (int i = 1; i < stocks.size(); i++) {
			if (stocks.get(i) >= 0) sum += stocks.get(i);
			else {
				if (stocks.get(i-1) >= 0) {
					float wallet = root(i - 1, i, stocks.get(i - 1), stocks.get(i)) - (i - 1);
					lost = - wallet * stocks.get(i) / 2;
				} else {
					lost = - (float)(stocks.get(i) + stocks.get(i-1)) / 2;
				}

				System.out.println("Lost: " + i + " " + lost);
			}
		}

		float avgStock = ((float) sum) / stocks.size();

		return avgStock;

	}

	private float root(Integer a, Integer b, Integer fa, Integer fb) {

		Float m = ((float) fb - fa) / (b - a);

		float c = fb - b * m;

		return -c / m;
	}

	public static void main(String[] args) throws org.apache.commons.math.MathException {

		Main main = new Main();

		main.add2018();
		main.calculate2019();

		main.round();

	}
}