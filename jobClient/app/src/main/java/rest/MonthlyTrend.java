package rest;

/**
 * Created by ivan on 14.02.15..
 */
public class MonthlyTrend {

    private String month;
    private Long counter;

    public MonthlyTrend() {}

    public MonthlyTrend(String month, Long counter) {
        super();
        this.setMonth(month);
        this.setCounter(counter);
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
