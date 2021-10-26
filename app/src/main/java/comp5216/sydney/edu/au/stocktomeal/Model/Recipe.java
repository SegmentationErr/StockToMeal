package comp5216.sydney.edu.au.stocktomeal.Model;

public class Recipe {
    private String recipeID;
    private String recipeName;
    private String userID;

    public Recipe() {
    }

    public Recipe(String recipeID, String recipeName, String userID) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.userID = userID;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
