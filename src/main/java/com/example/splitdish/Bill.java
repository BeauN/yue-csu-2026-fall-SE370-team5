package Model;

public class Bill{
    private Receipt receipt;
    private ArrayList<Diner> diners;
    private ArrayList<OrderShare> orderShares;
    private double tipPercentage;
    
    public Bill(Receipt receipt){
        this.receipt = receipt;
        return;
    }
    public Receipt getReceipt(){
        //possible vurnability: returning this.receipt might return the actual object and not a copy, but that might be intended behavior. 
        return this.receipt;

    }
    public ArrayList<Diner> getDiners(){
        return this.diners;


    }
    public void addDiner(Diner diner){
        this.diners.add(diner);
        return;
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
        return;
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
        return;
    }
    public double getTipPercentage(){
        return this.tipPercentage;

    }
    public void setTipPercentage(double tipPercentage){
        this.tipPercentage = tipPercentage;
    }
    public ArrayList<OrderShare> getSharesForDiner(Diner diner){
        //this should look for what orderShares are associated with a select diner.
        //orderShares knows what diners are apart of it, but diner does not store orderShares. 
        //if we wanted to re-code orderShares, we might want to consider having it so orderShare has an array of diners,
        //and each diner represents who shared that single item.
        private ArrayList<OrderShare> tempOrderShares;
        int tempIndex = 0;
        boolean didFindOrderShare = false;
        for(int i = 0; i < orderShares.size(); i++){
            //possible error: items[i] is not how you index.  And it might go out of bounds. or not do the end. 
            if(diner == orderShares[i].diner){
                print("found a orderShare related to diner. ");
                //we store the orderShare; i just want to inform the user/us of what items are releated to the diner, as to help debugging.
                print("the diner in this orderShare is releated to the order share item : " + orderShares[i].item + "at index " + i + " adding the orderShare to the array.");
                tempOrderShares[tempIndex] = orderShares[i];
                tempIndex +=1;
                didFindOrderShare = true;
                
            }
            
            
        }
        if(didFindOrderShare){
            return tempOrderShares;
        }

        print("Could not find the orderShares releated to given diner.");

        return;

    }
    public ArrayList<OrderShare> getSharesForItem(ReceiptItem item){
        //goes through our orderShares array, and scans the item for each orderShare object.
        //if the item inside said orderShare object is equal to the given item,-
        //-add it into the tempOrderShares array, and increase the tempIndex. 
        private ArrayList<OrderShare> tempOrderShares;
        int tempIndex = 0;
        boolean didFindOrderShare = false;
        for(int i = 0; i < orderShares.size(); i++){
            //possible error: items[i] is not how you index.  And it might go out of bounds. or not do the end. 
            if(item == orderShares[i].item){
                print("found a orderShare related to item. ");
                //we store the orderShare; i just want to inform the user/us of what diners are releated to the item, as to help debugging.
                print("this item is releated to the order share diner : " + orderShares[i].diner + "at index " + i + " adding the orderShare to the array.");
                tempOrderShares[tempIndex] = orderShares[i];
                tempIndex +=1;
                didFindOrderShare = true;
                
            }
            
            
        }
        if(didFindOrderShare){
            return tempOrderShares;
        }

        print("Could not find the orderShares releated to given item.");

        return;
    }

}
