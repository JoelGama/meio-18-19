import java.util.*;

class Order {

    int delivery;
    int quantity;

    Order(int delivery, int quantity) {
        this.delivery = delivery;
        this.quantity = quantity;
    }
}

public class Main {

    static final Random randGen = new Random();
    static Main main;
    double C1 = 17.37;
    double C2 = 22;
    double C3 = 900;
    double b = 96.5;
    double t = 2;
    double price = 120;

    double avg18 = 414.34; // (419.1 * 16 + 554.2 * 12 + 334.6 * 22)/50

    ArrayList<Double> avg2018;
    ArrayList<Double> cicle2019;
    ArrayList<Double> stdDev;
    ArrayList<Double> trend;

    public void add2018() {
        this.avg2018 = new ArrayList<>();
        this.avg2018.add(419.1);
        this.avg2018.add(554.2);
        this.avg2018.add(334.6);
    }

    public void calculate2019() {
        this.cicle2019 = new ArrayList<>();
        this.cicle2019.add(this.avg2018.get(0)-this.avg18);
        this.cicle2019.add(this.avg2018.get(1)-this.avg18);
        this.cicle2019.add(this.avg2018.get(2)-this.avg18);

        this.stdDev = new ArrayList<>();
        this.stdDev.add(this.cicle2019.get(0)*0.087);
        this.stdDev.add(this.cicle2019.get(1)*0.087);
        this.stdDev.add(this.cicle2019.get(2)*0.087);
    }

    public double getTrend(int week) {
        return this.avg18*(1+(week/50)*0.038);
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

    public double round(int S, int s) {
        ArrayList<Integer> stockOnHand = new ArrayList<>();
        ArrayList<Integer> stocks = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        stockOnHand.add(S);
        stocks.add(S);

        double cost = S*b + C3;
        double revenue = 0;

        for (int i = 1; i <= 50; i++) {
            int bought = 0;
            int delivered = 0;
            int r = (int) (randGen.nextGaussian() * stdDev.get(this.period(i)) + cicle2019.get(this.period(i)) + this.getTrend(i));

            for (Order o : orders) {
                o.delivery--;
                if (o.delivery == 0) {
                    delivered += o.quantity;
                }
            }

            for (int j = 0; j < orders.size(); j++) {
                if (orders.get(j).delivery < 0) {
                    orders.remove(j);
                }
            }

            int l = randL();
            boolean delivery = i % 2 == 0;
            boolean buy = stockOnHand.get(i-1) < s;
            if (delivery && buy) {
                bought = S - stockOnHand.get(i-1);
                orders.add(new Order(l+1, bought));
            }

            int newStockOnHand = stockOnHand.get(i-1) - r + bought;
            int newStock = stocks.get(i-1) - r + delivered;
            stockOnHand.add(newStockOnHand);
            stocks.add(newStock);

            revenue += r * price;
            cost += bought * b + (bought > 0 ? C3 : 0);
            if (newStock < 0) {
                cost += -newStock * C2;
            }

            //System.out.println(i + ": " + " Stock   " + newStock + "    stockOnHand " + newStockOnHand + "    l " + l);
        }

        int total = 0;
        int i;
        for (i = 1; i < stocks.size() ; i++) {
            int stock = stocks.get(i);
            if (stock > 0) {
                total += stock;
            }
        }

        cost += C1 * ((double) total) / (i-1);
        //System.out.println("Cost: " + cost + " Revenue: " + revenue + " Profit: " + (revenue - cost));

        return (revenue - cost); //profit
    }

    private double cost(ArrayList<Integer> stocks) {
        int sum = 0;
        double lost;

        for (int i = 1; i < stocks.size(); i++) {
            if (stocks.get(i) >= 0) sum += stocks.get(i);
            else {
                if (stocks.get(i-1) >= 0) {
                    double wallet = root(i - 1, i, stocks.get(i - 1), stocks.get(i)) - (i - 1);
                    lost = - wallet * stocks.get(i) / 2;
                } else {
                    lost = - (double)(stocks.get(i) + stocks.get(i-1)) / 2;
                }

                //System.out.println("Lost: " + i + " " + lost);
            }
        }

        double avgStock = ((double) sum) / stocks.size();

        return avgStock;

    }

    private double root(Integer a, Integer b, Integer fa, Integer fb) {

        Double m = ((double) fb - fa) / (b - a);

        double c = fb - b * m;

        return -c / m;
    }

    private double avgProfit(int S, int s, int numRounds) {

        double profit = 0;
        for (int i = 0; i < numRounds; i++) {
            profit += round(S, s);   
        }

        System.out.println("AvgProfit: " + profit/numRounds);
        return profit/numRounds;
    }

    public static void main(String[] args) {

        int numRounds = 100000;

        main = new Main();

        main.add2018();
        main.calculate2019();

        int S = randGen.nextInt(10000);
        int s = S - randGen.nextInt(S);
        double p = main.avgProfit(S, s, numRounds);
        for (int i = 0; i < 50; i++) {
            int newS = randGen.nextInt(10000);
            int news = newS - randGen.nextInt(newS);
            System.out.println("S " + newS + " s " + news);
            double newp = main.avgProfit(newS, news, numRounds);
            if (newp > p) {
                p = newp;
                S = newS;
                s = news;
            }
        }
        
        System.out.println();

        Double ps[] = new Double[4];
        int changeS[] = {50, -50, 0, 0};
        int changes[] = {0, 0, 50, -50};
        int done = 10;
        while (done != 0) {
            ps[0] = main.avgProfit(S+50, s, numRounds);
            ps[1] = main.avgProfit(S-50, s, numRounds);
            ps[2] = main.avgProfit(S, s+50, numRounds);
            ps[3] = main.avgProfit(S, s-50, numRounds);
            List<Double> l = Arrays.asList(ps);
            double max = Collections.max(l);
            int maxIndex = l.indexOf(max);
            if (max > p) {
                p = max;
                S += changeS[maxIndex];
                s += changes[maxIndex];
                done = 10;
            } else {
                done--;
            }
            System.out.println("S " + S + " s " + s + " done " + done);
        }
        System.out.println("Profit: " + p);
    }
}
