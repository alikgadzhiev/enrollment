package application.components;

import java.util.ArrayList;

public class ShopUnit {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private ShopUnitType type;
    private Long price;
    private ArrayList<ShopUnit> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ShopUnitType getType() {
        return type;
    }

    public void setType(ShopUnitType type) {
        this.type = type;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public ArrayList<ShopUnit> getChildren() {
        return children;
    }

    public void addChildren(ShopUnit children){
        this.children.add(children);
    }

    public void setChildren(){
        this.children = new ArrayList<>();
    }
}
