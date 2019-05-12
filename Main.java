import java.util.*;

class Order {

    int delivery;
    int quantity;

    Order(int delivery, int quantity) {
        this.delivery = delivery;
        this.quantity = quantity;
    }
}

class Result {

    double profit;
    double service;

    Result(double profit, double service) {
        this.profit = profit;
        this.service = service;
    }
}

public class Main {

    static final Random randGen = new Random();
    static Main main;
    static int weeks = 52;
    double C1 = 17.37;
    double C2 = 22;
    double C3 = 900;
    double b = 96.5;
    double t = 2;
    double price = 120;

    double avg18 = (419.1 * 16 + 554.2 * 12 + 334.6 * 22)/weeks;

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
        return this.avg18*Math.exp(0.038*week/weeks);
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

    public Result round(int S, int s) {
        ArrayList<Integer> stockOnHand = new ArrayList<>();
        ArrayList<Integer> stocks = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        stockOnHand.add(S);
        stocks.add(S);

        double cost = S*b + C3;
        double revenue = 0;

        for (int i = 1; i <= weeks; i++) {
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
        int out_of_stock_count = 0;
        int i;
        for (i = 1; i < stocks.size() ; i++) {
            int stock = stocks.get(i);
            if (stock > 0) {
                total += stock;
            } else {
                out_of_stock_count++;
            }
        }

        double service = 1 - ((double)out_of_stock_count)/weeks;

        int lastStock = stocks.get(i-1);
        if (lastStock < 0) {
            if (stockOnHand.get(i-1) != lastStock) {
                cost -= (stockOnHand.get(i-1) - lastStock) * b;
            }
            revenue -= -lastStock * price;
        }

        cost += C1 * ((double) total) / (i-1);
        double profit = revenue - cost;
        //System.out.println("Cost: " + cost + " Revenue: " + revenue + " Profit: " + profit + " Service: " + service);

        Result r = new Result(profit, service);

        return r;
    }

    private double avgProfit(int S, int s, int numRounds) {

        double profit = 0;
        for (int i = 0; i < numRounds; i++) {
            profit += round(S, s).profit;
        }

        //System.out.println("AvgProfit: " + profit/numRounds);
        return profit/numRounds;
    }

    private double avgService(int S, int s, int numRounds) {

        double service = 0;
        for (int i = 0; i < numRounds; i++) {
            service += round(S, s).service;
        }

        //System.out.println("AvgService: " + service/numRounds);
        return service/numRounds;
    }

    private void hillClimbing(int S, int s, int numRounds) {
        double p = main.avgProfit(S, s, numRounds);
        System.out.println("S " + S + " s " + s);
        System.out.println();
        // Hill climbling
        Double ps[] = new Double[8];
        int changeS[] = {10, -10, 0, 0, 10, 10, -10, -10};
        int changes[] = {0, 0, 10, -10, 10, -10, 10, -10};
        int tries = 10;
        while (tries != 0) {
            ps[0] = main.avgProfit(S+changeS[0], s+changes[0], numRounds);
            ps[1] = main.avgProfit(S+changeS[1], s+changes[1], numRounds);
            ps[2] = main.avgProfit(S+changeS[2], s+changes[2], numRounds);
            ps[3] = main.avgProfit(S+changeS[3], s+changes[3], numRounds);

            ps[4] = main.avgProfit(S+changeS[4], s+changes[4], numRounds);
            ps[5] = main.avgProfit(S+changeS[5], s+changes[5], numRounds);
            ps[6] = main.avgProfit(S+changeS[6], s+changes[6], numRounds);
            ps[7] = main.avgProfit(S+changeS[7], s+changes[7], numRounds);
            List<Double> l = Arrays.asList(ps);
            double max = Collections.max(l);
            int maxIndex = l.indexOf(max);
            if (max > p) {
                p = max;
                S += changeS[maxIndex];
                s += changes[maxIndex];
                tries = 10;
            } else {
                tries--;
            }
            System.out.println("S " + S + " s " + s + " tries " + tries);
        }

        System.out.println("\nResults:");
        System.out.println("S " + S + " s " + s);
        System.out.println("Profit (optimal): " + main.avgProfit(S, s, numRounds));
        System.out.println("Service: " + main.avgService(S, s, numRounds));
    }

    private void grid(int minS, int maxS, int SStep, int mins, int maxs, int sStep) {
        int numRounds = 5000;
        for (int S = minS; S <= maxS; S+=SStep) {
            System.out.print(""+S+(S == maxS ? "" : ","));
        }
        System.out.println();

        for (int s = mins; s <= maxs; s+=sStep) {
            System.out.print(""+s+",");
            for (int S = minS; S <= maxS; S+=SStep) {
                //System.out.println("S " + S + " s " + s);
                double p = avgProfit(S, s, numRounds);
                //double p = avgService(S, s, numRounds);
                System.out.print(""+p+(S == maxS ? "" : ","));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        main = new Main();

        main.add2018();
        main.calculate2019();

        //main.grid(0, 15000, 100, 0, 10000, 100);
        main.hillClimbing(11000, 600, 100000);

    }
}
