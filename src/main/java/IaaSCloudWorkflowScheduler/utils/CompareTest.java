package IaaSCloudWorkflowScheduler.utils;

public class CompareTest {

    public static void main(String[] args) {

        double lft = 10;
        double sd = 5;
        double currentFT = 9;
        double p = (Math.max(0, (lft - currentFT))
                / (lft - sd));

        System.out.println("p:" + p);
        System.out.println(Math.pow(p, 3));
    }
}
