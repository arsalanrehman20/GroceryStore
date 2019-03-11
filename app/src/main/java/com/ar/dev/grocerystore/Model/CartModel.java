package com.ar.dev.grocerystore.Model;

public class CartModel {
    private String productID;
    private String productName;
    private String productPrice;
    private String productQuantity;
    private String imgUrl;

    public static int TOTAL_AMOUNT=0;
    public CartModel(){

    }

    public CartModel(String productID, String productName, String productPrice, String productQuantity, String imgUrl) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.imgUrl = imgUrl;
    }


    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
