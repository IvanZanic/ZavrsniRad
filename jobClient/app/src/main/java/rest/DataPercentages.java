package rest;

import java.math.BigDecimal;

/**
 * Created by ivan on 14.02.15..
 */
public class DataPercentages {

    private Long id;
    private BigDecimal percentage;

    public DataPercentages() {}

    public DataPercentages(Long id, BigDecimal percentage) {
        super();
        this.id = id;
        this.percentage = percentage;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
