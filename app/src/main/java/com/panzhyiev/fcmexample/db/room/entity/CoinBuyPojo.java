package com.panzhyiev.fcmexample.db.room.entity;

public class CoinBuyPojo {

    private String id;

    private String name;

    private String symbol;

    private String amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * No args constructor for use in serialization
     */
    public CoinBuyPojo() {
    }

    public CoinBuyPojo(String id, String name, String symbol, String amount) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CoinBuyPojo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoinBuyPojo coinBuyPojo = (CoinBuyPojo) o;

        return id != null ? id.equals(coinBuyPojo.id) : coinBuyPojo.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
