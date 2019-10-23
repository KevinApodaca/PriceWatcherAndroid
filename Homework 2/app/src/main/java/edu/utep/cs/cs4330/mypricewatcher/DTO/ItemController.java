package edu.utep.cs.cs4330.mypricewatcher.DTO;

import android.util.Log;
import java.util.Random;
import edu.utep.cs.cs4330.mypricewatcher.MainActivity;

/**
 * Class will handle the item controller.
 */
public class ItemController {
    private ItemModel model;
    private MainActivity view;
    private PriceFinder priceFinder;

/**
 * Method will give information to the controller of item.
 * @param model - the model.
 * @param view - the view for the item.
 */
    public ItemController(ItemModel model, MainActivity view){
        this.model = model;
        this.view = view;
        this.priceFinder = new PriceFinder();
    }

/**
 * Method will update the price of item.
 * @param i - some number.
 */
    public void updatePrice(int i){
        model.updatePrice(i, priceFinder.createRandom());
        Log.d("TESTING", "update price method called " + priceFinder.createRandom());
        updateView();
    }

/**
 * Method will add item to the model.
 * @param item - the item.
 */
    public void addItem(Item item){
        model.addItem(item);
        updateView();
    }
/**
 * Method will allow for item to be edited.
 * @param index - which item.
 * @param name - item name.
 */
    public void editItem(int index, String name){
        Item item = model.getItem(index);
        item.setName(name);
        updateView();
    }
/**
 * Method will delete item.
 * @param item - the item to be deleted.
 */
    public void removeItem(Item item){
        model.removeItem(item);
        updateView();
    }
/**
 * Method will send changes to view.
 */
    public void updateView(){
        view.clearItems();
       for(int i = 0; i < model.getItemSize(); i ++){
           Item item = model.getItem(i);
           view.displayItem(item.getName(), item.getInitialPrice(), item.getUrl(), item.getPriceChage(), item.getCurrentPrice());
       }
    }
/**
 * Method will create random price.
 * @return the new price.
 */
    public double createRandom() {
        double min = 250;
        double max = 400;

        Random random = new Random();
        double holder = min + (max - min) * random.nextDouble();
        return Math.round(holder * 100.00) / 100.0;
    }

}
