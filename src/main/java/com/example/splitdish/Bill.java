package Model;

public class Bill{
    private Receipt receipt;
    private ArrayList<Diner> diners;
    private ArrayList<OrderShare> orderShares;
    private double tipPercentage;
    
    public Bill(Receipt receipt){
        this.receipt = receipt;
    }
    public Receipt getReceipt(){
        //possible vurnability: returning this.receipt might return the actual object and not a copy, but that might be intended behavior. 
        return this.receipt;

    }
    public ArrayList<Diner> getDiners(){
        retirn this.diners;


    }
    public void addDiner(Diner diner){
        this.diners.add(diner);
    }
    public void removeDiner(Diner diner){
        for(int i = 0; i < diners.size(); i++){
            //possible error: items[i] is not how you index.  And it might go out of bounds. or not do the end. 
            if(diners[i] = diner){
                print("found diner to remove. ");
                diners.remove(i);
                return;
            }
            
            
        }
        print("failed to remove diner, couldn't find it");
    }
    public ArrayList<OrderShare> getOrderShares(){
        return orderShares;
    }   
    public void addOrderShare(OrderShare share){
        this.orderShares.add(share);
        
    }
    public void removeOrderShare(OrderShare share){
        for(int i = 0; i < orderShares.size(); i++){
            //possible error: items[i] is not how you index.  And it might go out of bounds. or not do the end. 
            if(orderShares[i] = share){
                print("found orderShare to remove. ");
                orderShares.remove(i);
                return;
            }
            
            
        }
        print("failed to remove orderShare, couldn't find it");
    }
    public double getTipPercentage(){
        return this.tipPercentage;

    }
    public void setTipPercentage(double tipPercentage){
        this.tipPercentage = tipPercentage;
    }
    public ArrayList<OrderShare> getSharesForDiner(Diner diner){
        //	getSharesForDiner() answers: What assignments belong to this person?
        //so, go into ordershare. 
        //find when orderShares[i].diner = diner.
        // return what items are associated with this diner.

        return 

    }
    public ArrayList<OrderShare> getSharesForItem(ReceiptItem item){
        //	getSharesForItem() answers: Who is responsible for this item? It also makes it easy to check whether an item totals 1.00 (100%) assigned.
        //so, go into ordershare. 
        //find when orderShares[i].item = item.
        // return what diner are associated with this item.
    }

}