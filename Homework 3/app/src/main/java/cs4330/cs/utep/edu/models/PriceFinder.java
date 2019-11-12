package cs4330.cs.utep.edu.models;

public class PriceFinder extends Item {

    private Item item;

    public PriceFinder(){

    }

    public PriceFinder(String name, String url, double price, String image, String id) {
        this.item = new Item(price, name, url, image, id);
    }

    public String getName(){
        return this.item.getName();
    }

    public String getUrl() {
        return this.item.getLink();
    }

    public String getImage() { return this.item.getImage(); }

    public double getPrice() {
        return this.item.getPrice();
    }

    public double getNewPrice(){
        return this.item.getNewPrice();
    }

    public String getId() {return this.item.getId(); }

    public void randomPrice() {
        double MAX_PRICE = 20000.00;
        double MIN_PRICE = 500.00;
        this.item.setNewPrice(Math.random() * (MAX_PRICE - MIN_PRICE) + 1 + MIN_PRICE);
    }

    public double calculatePrice() {
        return (this.item.getNewPrice()*100/this.item.getPrice())-100;
    }

    public boolean changePositive(){
        return item.getNewPrice() > item.getPrice();
    }

    public void setPrice(double price){
        item.setPrice(price);
    }

    public void setName(String name) {
        item.setName(name);
    }

    public void setLink(String link){item.setLink(link);}

    public void setNewPrice(double newPrice) {
        item.setNewPrice(newPrice);
    }

    public void setImage(String image) {item.setImage(image);}

}
