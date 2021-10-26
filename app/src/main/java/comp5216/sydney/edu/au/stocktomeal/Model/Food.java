package comp5216.sydney.edu.au.stocktomeal.Model;

public class Food {
    private String foodID;
    private String userID;
    private String name;
    private String amount;
    private String picture;
    private String time;

    public Food() {
    }

    public Food(String foodID, String userID, String name, String amount) {
        this.foodID = foodID;
        this.userID = userID;
        this.name = name;
        this.amount = amount;
    }

    public Food(String foodID, String userID, String name, String amount, String picture, String time) {
        this.foodID = foodID;
        this.userID = userID;
        this.name = name;
        this.amount = amount;
        this.picture = picture;
        this.time = time;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
