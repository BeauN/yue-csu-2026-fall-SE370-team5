public class OrderShare {
    private ReceiptItem item;
    private Diner diner;
    private double percentage;

    public OrderShare(ReceiptItem item, Diner diner, double percentage) {
        this.item = item;
        this.diner = diner;
        this.percentage = percentage;
    }

    public ReceiptItem getItem() {
        return item;
    }

    public Diner getDiner() {
        return diner;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}