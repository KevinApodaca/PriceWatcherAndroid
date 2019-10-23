package edu.utep.cs.cs4330.mypricewatcher.DTO;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class will define Item object.
 */
public class ItemModel {
    private List<Item> items;

    public ItemModel(){
        this.items = new ArrayList<>();
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public Iterator<Item> getItems(){
        return items.iterator();
    }

    public void calculateCurrentPriceChange(){
        for(Item item : items){
            double change =  (((item.currentPrice - item.initialPrice) / item.initialPrice) * 100);
            item.setPriceChage(change);
        }
    }
/**
 * Method will get the item size.
 * @return the size.
 */
    public int getItemSize(){
        return items.size();
    }
/**
 * Method will update price, for testing.
 * @param index - the item number.
 * @param newPrice - the new price.
 */
    public void updatePrice(int index, double newPrice){
        Item item = items.get(index);
        item.setCurrentPrice(newPrice);
        Log.d("TESTING", "Item name selected: " + item.getCurrentPrice());
    }
/**
 * Method to remove item.
 * @param item - the item to be removed.
 */
    public void removeItem(Item item)
    {
        for(int i = 0; i < items.size(); i++){
            if(item.name == items.get(i).name){
                items.remove(i);
            }
        }
    }
    /**
     * Getter for the item.
     * @param index - the item number.
     * @return the item.
     */
    public Item getItem(int index){
        return items.get(index);
    }

}
