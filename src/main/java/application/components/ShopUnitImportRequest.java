package application.components;

import java.util.ArrayList;

public class ShopUnitImportRequest {

    private ArrayList<ShopUnitImport> items;
    private String updateDate;

    public ArrayList<ShopUnitImport> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShopUnitImport> items) {
        this.items = items;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

}
