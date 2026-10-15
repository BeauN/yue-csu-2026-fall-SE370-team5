package Model;
public class Receipt {
    //might have to import recieptitem
    private String restaurantName;
    private ArrayList<ReceiptItem> items;
    private double tax;
    private double total;
    
    public Receipt(String restaurantName){
        this.restaurantName = restaurantName;
        //maybe consider default variables for everything else.

    }
    public String getRestaurantName(){
        return this.restaurantName;
        
    }
    public void setRestaurantName(String name){
        this.restaurantName = name;
        
    }
    public ArrayList<ReceiptItem> getItems(){
        //this is probably the wrong way to return it. 
        return items;

    }
    public void addItem(ReceiptItem item){
        //go into the items array list, add to the end. 
        this.items.add(ReceiptItem item);
        
    }
    public void removeItem(ReceiptItem item){
        //go into the items array, search for the recieptItem that is the same as item. 
        //possible error: because there's no speficied method for ReceiptItem==ReceiptItem in ReceiptItem, java could be attempting to check memory addresses rather than variable data.
        //items.forEach(ReceiptItem){} consider this if you wanna feel smart
        //could also use sort(Comparator<? super E> c)
        for(int i = 0; i < items.size(); i++){
            //possible error: items[i] is not how you index.  And it might go out of bounds. or not do the end. 
            if(items[i] = item){
                items.remove(i);
                return;
            }
            
        }

    }
    public double getSubtotal(){
        double runningSubTotal;
        for(int i = 0; i < items.size(); i++){
            //possible error: items[i] is not how you index. And it might go out of bounds. or it might not do the end.
            runningSubTotal  += items[i].getPrice();
            
        }
        return runningSubTotal;
        
    }
    public double getTax(){
        return this.tax;
    }
    public void setTax(double tax){
        this.tax = tax;
        
    }
    public double getTotal(){
        return this.total;
        
    }
    public void setTotal(double total){
        this.total = total;
    }
}
