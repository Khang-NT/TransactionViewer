package com.github.khangnt.transactionviewer.model;

import static com.github.khangnt.transactionviewer.utils.Preconditions.checkNotEmpty;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class Transaction {
    private String currency;
    private String sku;
    private float amount;

    private Transaction() {
    }

    public String getCurrency() {
        return currency;
    }

    public String getSku() {
        return sku;
    }

    public float getAmount() {
        return amount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        Transaction transaction = new Transaction();

        public Builder currency(String currency) {
            transaction.currency = currency;
            return this;
        }

        public Builder sku(String sku) {
            transaction.sku = sku;
            return this;
        }

        public Builder amount(float amount) {
            transaction.amount = amount;
            return this;
        }

        public Transaction build() {
            checkNotEmpty(transaction.currency, "Invalid transaction: `currency` is missed or empty.");
            checkNotEmpty(transaction.sku, "Invalid transaction: `sku` is missed or empty.");
            return transaction;
        }
    }
}
