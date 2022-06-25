package application.paths;

import application.components.*;
import application.components.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
public class Paths {

    private final ItemsDao itemsDao;
    private final ConnectionsDao connectionsDao;
    private Service service;

    @Autowired
    public Paths(ItemsDao itemsDao, ConnectionsDao connectionsDao, Service service) {
        this.itemsDao = itemsDao;
        this.connectionsDao = connectionsDao;
        this.service = service;
    }

    @PostMapping(value = "/imports")
    @ResponseBody
    public ResponseEntity<?> imports(@RequestBody ShopUnitImportRequest content) {

        Error validation = new Error(400, "Validation Failed");

        String updateDate = content.getUpdateDate();
        ArrayList<ShopUnitImport> givenItems = content.getItems();

        if(!service.formatValidation(updateDate, "date-time") && service.hasDuplicateIds(givenItems) ||
                service.nullnessOfNames(givenItems) || !service.nullnessOfPricesOfCategories(givenItems) ||
                service.updatingOfTypes(givenItems) || !service.formatValidationOfIds(givenItems) ||
                !service.isTheParentTheCategory(givenItems)){
            return ResponseEntity.badRequest().body(validation);
        }

        service.importItem(content);

        return ResponseEntity.ok().body("Вставка или обновление прошли успешно.");
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable("id") String id) {
        Error validation = new Error(400, "Validation Failed");
        Error absence = new Error(404, "Item not found");

        if(service.isAbsentInConnections(id)){
            return ResponseEntity.notFound().build();
        }

        if(!service.formatValidation(id, "uuid")){
            return ResponseEntity.badRequest().body(validation);
        }

        service.deleteItem(id);

        return ResponseEntity.ok().body("Удаление прошло успешно.");
    }

    @GetMapping(value = "/nodes/{id}")
    public ResponseEntity<?> getUnit(@PathVariable("id") String id) {
        Error validation = new Error(400, "Validation Failed");
        Error absence = new Error(404, "Item not found");

        if(!service.formatValidation(id, "uuid")){
            return ResponseEntity.badRequest().body(validation);
        }

        if(service.isAbsentInConnections(id)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(service.getItem(id));
    }

    @GetMapping("/sales")
    public ResponseEntity<?> sales(@RequestParam(name="date") String date) {
        Error validation = new Error(400, "Validation Failed");

        if(!service.formatValidation(date, "date-time")){
            return ResponseEntity.badRequest().body(validation);
        }

        return ResponseEntity.ok().body(service.getSales(date));
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<?> getStatistic(@PathVariable("id") String id,
                                          @RequestParam(name="dateStart") String dateStart, @RequestParam(name="dateEnd") String dateEnd) {
        Error validation = new Error(400, "Validation Failed");
        Error absence = new Error(404, "Item not found");

        if(service.isAbsentInConnections(id)){
            return ResponseEntity.notFound().build();
        }

        if(!service.formatValidation(dateStart, "date-time") || !service.formatValidation(dateEnd, "date-time")){
            return ResponseEntity.badRequest().body(validation);
        }

        return ResponseEntity.ok().body(service.getUpdates(id, dateStart, dateEnd));
    }
}
