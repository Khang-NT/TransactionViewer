package com.github.khangnt.transactionviewer.model;

import static com.github.khangnt.transactionviewer.utils.Preconditions.checkArgument;
import static com.github.khangnt.transactionviewer.utils.Preconditions.checkNotEmpty;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class CurrencyRate {
    private String from;
    private String to;
    private float rate;

    private CurrencyRate() {
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public float getRate() {
        return rate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CurrencyRate currencyRate = new CurrencyRate();

        public Builder from(String from) {
            currencyRate.from = from;
            return this;
        }

        public Builder to(String to) {
            currencyRate.to = to;
            return this;
        }

        public Builder rate(float rate) {
            currencyRate.rate = rate;
            return this;
        }

        public CurrencyRate build() {
            checkNotEmpty(currencyRate.from, "Invalid currency: `from` is missed or empty.");
            checkNotEmpty(currencyRate.to, "Invalid currency: `to` is missed or empty.");
            checkArgument(currencyRate.rate > 0, "Invalid currency: `rate` must be > 0.");
            return currencyRate;
        }
    }
}
