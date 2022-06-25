package application.components;

import java.util.ArrayList;

public class ShopUnitStatisticResponse {
    private ArrayList<ShopUnitStatisticUnit> items;

    public ArrayList<ShopUnitStatisticUnit> getItems() {
        return items;
    }

    public void addItem(ShopUnitStatisticUnit item) {
        this.items.add(item);
    }

    public void setItems(){
        this.items = new ArrayList<>();
    }
}
