package com.ar.dev.grocerystore.Model;

import java.util.Comparator;

public class ProductModel {

    private String productID;
    private String name;
    private String price;
    private String desc;
    private String category;
    private String imgUrl1,imgUrl2,imgUrl3,imgUrl4;
    private String imgID1,imgID2,imgID3,imgID4;

    public ProductModel(){

    }

    public ProductModel(String productID, String name, String price, String desc, String category, String imgUrl1, String imgUrl2, String imgUrl3, String imgUrl4, String imgID1, String imgID2, String imgID3, String imgID4) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.category = category;
        this.imgUrl1 = imgUrl1;
        this.imgUrl2 = imgUrl2;
        this.imgUrl3 = imgUrl3;
        this.imgUrl4 = imgUrl4;
        this.imgID1 = imgID1;
        this.imgID2 = imgID2;
        this.imgID3 = imgID3;
        this.imgID4 = imgID4;
    }

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public String getImgUrl1() {
        return imgUrl1;
    }

    public String getImgUrl2() {
        return imgUrl2;
    }

    public String getImgUrl3() {
        return imgUrl3;
    }

    public String getImgUrl4() {
        return imgUrl4;
    }

    public String getImgID1() {
        return imgID1;
    }

    public String getImgID2() {
        return imgID2;
    }

    public String getImgID3() {
        return imgID3;
    }

    public String getImgID4() {
        return imgID4;
    }

    public static final Comparator<ProductModel> BY_ASCENDING = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel o1, ProductModel o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static final Comparator<ProductModel> BY_DESCENDING = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel o1, ProductModel o2) {
            return o2.getName().compareTo(o1.getName());
        }
    };
}
