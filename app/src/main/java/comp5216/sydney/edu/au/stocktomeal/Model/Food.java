package comp5216.sydney.edu.au.stocktomeal.Model;

public class Food {


    private String userID;
    private String name;
    private String amount;
    private String picture;
    private String time;


    public Food() {
    }

    public Food(String userID, String name, String amount, String time) {
        this.name = name;
        this.amount = amount;
        this.time = time;
        this.userID = userID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
