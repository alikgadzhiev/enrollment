package application.components;

import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class Service {

    private ItemsDao itemsDao;
    private ConnectionsDao connectionsDao;
    private UpdatesDao updatesDao;

    @Autowired
    public Service(ItemsDao itemsDao, ConnectionsDao connectionsDao, UpdatesDao updatesDao) {
        this.itemsDao = itemsDao;
        this.connectionsDao = connectionsDao;
        this.updatesDao = updatesDao;
    }


    public boolean formatValidation(String str, String required_format) {
        if (str == null)
            return true;

        if (required_format.equals("uuid")) {
            String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }

        String simpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String secondSimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        DateFormat sdf = new SimpleDateFormat(simpleDateFormat);
        sdf.setLenient(false);

        boolean flag = true;

        try {
            sdf.parse(str);
        } catch (ParseException e) {
            flag = false;
        }

        if (flag) {
            return true;
        }

        sdf = new SimpleDateFormat(secondSimpleDateFormat);
        sdf.setLenient(false);

        try {
            sdf.parse(str);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public long parseDateToLong(String date) {
        return Long.parseLong(date.substring(0, 4)) * 31_536_000 + Long.parseLong(date.substring(5, 7)) * 2_592_000 +
                Long.parseLong(date.substring(8, 10)) * 86_400 + Long.parseLong(date.substring(11, 13)) * 3_600 +
                Long.parseLong(date.substring(14, 16)) * 60 + Long.parseLong(date.substring(17, 19));
    }

    public long findMilli(String date){
        int size = date.length();

        return (size > 20 ? Long.parseLong(date.substring(size - 4, size)) : 0L);
    }

    public boolean isAbsentInConnections(String id) {
        ArrayList<Connection> connections = connectionsDao.selectConnections();

        for (Connection connection : connections) {
            if (connection.getChildId().equals(id)) {
                return false;
            }
        }

        return true;
    }

    public boolean isAbsentInItems(String id) {
        ArrayList<ShopUnit> items = itemsDao.selectItems();

        for (ShopUnit item : items) {
            if (item.getId().equals(id)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasDuplicateIds(ArrayList<ShopUnitImport> items) {
        int size = items.size();

        for (int i = 0; i + 1 < size; i++) {
            for (int j = i + 1; j < size; j++) {
                String firstId = items.get(i).getId();
                String secondId = items.get(j).getId();

                if (firstId.equals(secondId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isTheParentTheCategory(ArrayList<ShopUnitImport> given_items) {
        for (ShopUnitImport item : given_items) {
            for (ShopUnitImport item2 : given_items) {
                if (Objects.equals(item2.getParentId(), item.getId()) && !item.getType().equals(ShopUnitType.CATEGORY)) {
                    return false;
                }
            }
        }

        ArrayList<ShopUnit> items = itemsDao.selectItems();

        for (ShopUnitImport given_item : given_items) {
            for (ShopUnit item : items) {
                if (item.getId().equals(given_item.getParentId()) && !item.getType().equals(ShopUnitType.CATEGORY))
                    return false;
            }
        }

        return true;
    }

    public boolean nullnessOfNames(ArrayList<ShopUnitImport> items) {
        for (ShopUnitImport item : items) {
            if (item.getName() == null)
                return true;
        }

        return false;
    }

    public boolean nullnessOfPricesOfCategories(ArrayList<ShopUnitImport> items) {
        for (ShopUnitImport item : items) {
            if (item.getType().equals(ShopUnitType.CATEGORY) && item.getPrice() != null)
                return false;
        }

        return true;
    }

    public boolean updatingOfTypes(ArrayList<ShopUnitImport> given_items) {
        ArrayList<ShopUnit> items = itemsDao.selectItems();

        for (ShopUnitImport given_item : given_items) {
            for (ShopUnit item : items) {
                if (item.getId().equals(given_item.getId()) && !item.getType().equals(given_item.getType()))
                    return true;
            }
        }

        return false;
    }

    public boolean formatValidationOfIds(ArrayList<ShopUnitImport> items) {
        for (ShopUnitImport item : items) {
            if (!formatValidation(item.getId(), "uuid") || !formatValidation(item.getParentId(), "uuid"))
                return false;
        }

        return true;
    }

    public void importItem(ShopUnitImportRequest content) {

        String updateDate = content.getUpdateDate();
        ArrayList<ShopUnitImport> givenItems = content.getItems();
        ArrayList<ShopUnit> currentItems = itemsDao.selectItems();

        for (ShopUnitImport givenItem : givenItems) {
            if (!isAbsentInItems(givenItem.getId())) {
                for (ShopUnit currentItem : currentItems) {
                    String firstId = givenItem.getId();
                    String secondId = currentItem.getId();

                    if (firstId.equals(secondId)) {
                        if (givenItem.getType().equals(ShopUnitType.OFFER) &&
                                !Objects.equals(givenItem.getPrice(), currentItem.getPrice())) {

                            updatesDao.insertUpdate(givenItem.getId(), givenItem.getName(), updateDate,
                                    givenItem.getParentId(), givenItem.getType(), givenItem.getPrice());
                        }

                        connectionsDao.updateConnection(givenItem.getParentId(), givenItem.getId());

                        itemsDao.updateItem(firstId, givenItem.getName(), updateDate, givenItem.getParentId(),
                                givenItem.getType(), givenItem.getPrice());
                    }
                }
            } else {
                itemsDao.insertItem(givenItem.getId(), givenItem.getName(), updateDate,
                        givenItem.getParentId(), givenItem.getType(), givenItem.getPrice());

                updatesDao.insertUpdate(givenItem.getId(), givenItem.getName(), updateDate,
                        givenItem.getParentId(), givenItem.getType(), givenItem.getPrice());

                connectionsDao.insertConnection(givenItem.getId(), givenItem.getParentId());
            }
        }

        updateItems();
    }

    public void deleteItem(String id) {
        ArrayList<Connection> connections = connectionsDao.selectConnections();

        Queue<String> queue = new ArrayDeque<>();
        queue.add(id);

        ArrayList<String> list = new ArrayList<>();
        list.add(id);

        while (!queue.isEmpty()) {
            String current = queue.peek();
            queue.poll();
            for (Connection connection : connections) {
                String parentId = connection.getParentId();
                if (parentId == null)
                    continue;
                if (connection.getParentId().equals(current)) {
                    queue.add(connection.getChildId());
                    list.add(connection.getChildId());
                }
            }
        }

        for (String cur_id : list) {
            itemsDao.deleteItem(cur_id);
            updatesDao.deleteUpdate(cur_id);
            connectionsDao.deleteConnection(cur_id);
        }

        updateItems();
    }

    public ShopUnit getItem(String id) {

        ArrayList<Connection> connections = connectionsDao.selectConnections();
        ArrayList<ShopUnit> items = itemsDao.selectItems();

        ShopUnit answer;

        answer = getDfs(id, connections, items);

        return answer;
    }

    public ShopUnit getDfs(String current_id, ArrayList<Connection> connections, ArrayList<ShopUnit> items) {
        ShopUnit current_unit = new ShopUnit();

        for (ShopUnit item : items) {
            if (item.getId().equals(current_id)) {
                current_unit = item;
                break;
            }
        }


        for (Connection connection : connections) {
            if (Objects.equals(connection.getParentId(), current_id)) {
                if(current_unit.getChildren() == null)
                    current_unit.setChildren();
                current_unit.addChildren(getDfs(connection.getChildId(), connections, items));
            }
        }

        return current_unit;
    }

    public ShopUnitStatisticResponse getSales(String date) {
        long startDate = parseDateToLong(date);
        long milli = findMilli(date);

        ArrayList<ShopUnit> updates = updatesDao.selectUpdates();
        ShopUnitStatisticResponse answer = new ShopUnitStatisticResponse();

        for (ShopUnit update : updates) {
            long current_date = parseDateToLong(update.getDate());
            long current_milli = findMilli(update.getDate());
            long difference = current_date - startDate;

            if (difference >= 0 && difference <= 86_400) {
                if((difference == 0 && current_milli - milli < 0) || (difference == 86_400 && current_milli - milli > 0)){
                    continue;
                }
                ShopUnitStatisticUnit parsedItem = new ShopUnitStatisticUnit();

                parsedItem.setId(update.getId());
                parsedItem.setDate(update.getDate());
                parsedItem.setPrice(update.getPrice());
                parsedItem.setParentId(update.getParentId());
                parsedItem.setName(update.getName());
                parsedItem.setType(update.getType());

                if(answer.getItems() == null)
                    answer.setItems();

                answer.addItem(parsedItem);
            }
        }

        return answer;
    }

    public ShopUnitStatisticResponse getUpdates(String id, String dateStart, String dateEnd) {
        ArrayList<ShopUnit> updates = updatesDao.selectUpdates();
        ShopUnitStatisticResponse answer = new ShopUnitStatisticResponse();

        long start_time = parseDateToLong(dateStart);
        long end_time = parseDateToLong(dateEnd);
        long milli_start = findMilli(dateStart);
        long milli_end = findMilli(dateEnd);

        for (ShopUnit update : updates) {
            if (update.getId().equals(id)) {
                long cur_time = parseDateToLong(update.getDate());
                long milli = findMilli(update.getDate());

                if(update.getPrice() == null)
                    continue;

                if (cur_time >= start_time && cur_time <= end_time) {
                    if((cur_time == start_time && milli >= milli_start) || (cur_time == end_time && milli <= milli_end)){
                        continue;
                    }

                    ShopUnitStatisticUnit parsedItem = new ShopUnitStatisticUnit();

                    parsedItem.setId(update.getId());
                    parsedItem.setDate(update.getDate());
                    parsedItem.setPrice(update.getPrice());
                    parsedItem.setParentId(update.getParentId());
                    parsedItem.setName(update.getName());
                    parsedItem.setType(update.getType());

                    if(answer.getItems() == null)
                        answer.setItems();

                    answer.addItem(parsedItem);
                }
            }
        }

        return answer;
    }

    public void updateItems() {
        ArrayList<Connection> connections = connectionsDao.selectConnections();

        for (Connection connection : connections) {
            if (connection.getParentId() == null) {
                updateDfs(connection.getChildId());
            }
        }
    }

    public void updateDfs(String id){
        ArrayList<Connection> connections = connectionsDao.selectConnections();
        ArrayList<ShopUnit> items = itemsDao.selectItems();
        ArrayList<Long> answer = new ArrayList<>();
        String final_date = "";

        answer.add(0L);
        answer.add(0L);

        for(ShopUnit item : items){
            if(item.getId().equals(id)){
                if(item.getType().equals(ShopUnitType.OFFER))
                    return;
                final_date = item.getDate();
            }
        }

        long max_time = parseDateToLong(final_date);
        long max_millis = findMilli(final_date);

        for(Connection connection : connections){
            if(Objects.equals(connection.getParentId(), id)){
                updateDfs(connection.getChildId());
                ArrayList<Long> current = updatePrices(connection.getChildId());
                String cur_date = updateDates(connection.getChildId());
                answer.set(0, answer.get(0) + current.get(0));
                answer.set(1, answer.get(1) + current.get(1));
                long cur_time = parseDateToLong(cur_date);
                long cur_millis = findMilli(cur_date);
                if(cur_time > max_time || (cur_time == max_time && cur_millis > max_millis)){
                    max_time = cur_time;
                    max_millis = cur_millis;
                    final_date = cur_date;
                }
            }
        }

        for(ShopUnit item : items){
            if(item.getId().equals(id)){
                Long current_price = (answer.get(1) == 0 ? null : answer.get(0) / answer.get(1));
                if(!Objects.equals(current_price, item.getPrice())){
                    updatesDao.insertUpdate(item.getId(), item.getName(), final_date,
                            item.getParentId(), item.getType(), current_price);
                }
                itemsDao.updateItem(item.getId(), item.getName(), final_date,
                        item.getParentId(), item.getType(), current_price);
            }
        }
    }

    public ArrayList<Long> updatePrices(String id) {

        ArrayList<Connection> connections = connectionsDao.selectConnections();
        ArrayList<ShopUnit> items = itemsDao.selectItems();
        ArrayList<Long> answer = new ArrayList<>();

        answer.add(0L);
        answer.add(0L);

        for(ShopUnit item : items){
            if(item.getId().equals(id) && item.getType().equals(ShopUnitType.OFFER)){
                answer.set(0, item.getPrice());
                answer.set(1, 1L);
                return answer;
            }
        }

        for(Connection connection : connections){
            if(Objects.equals(connection.getParentId(), id)){
                ArrayList<Long> current = updatePrices(connection.getChildId());
                answer.set(0, answer.get(0) + current.get(0));
                answer.set(1, answer.get(1) + current.get(1));
            }
        }

        return answer;
    }

    public String updateDates(String id){
        ArrayList<ShopUnit> items = itemsDao.selectItems();
        ArrayList<Connection> connections = connectionsDao.selectConnections();

        String final_date = "";

        for(ShopUnit item : items){
            if(item.getId().equals(id)){
                final_date = item.getDate();
                if(item.getType().equals(ShopUnitType.OFFER)) {
                    return final_date;
                }
                break;
            }
        }

        long max_time = parseDateToLong(final_date);
        long max_millis = findMilli(final_date);

        for(Connection connection : connections){
            if(Objects.equals(connection.getParentId(), id)){
                String cur_date = updateDates(connection.getChildId());
                long cur_time = parseDateToLong(cur_date);
                long cur_millis = findMilli(cur_date);
                if(cur_time > max_time || (cur_time == max_time && cur_millis > max_millis)){
                    max_time = cur_time;
                    max_millis = cur_millis;
                    final_date = cur_date;
                }
            }
        }

        return final_date;
    }
}
