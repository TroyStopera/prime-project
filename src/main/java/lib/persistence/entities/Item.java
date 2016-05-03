package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.List;

public class Item extends Entity {

    /* Columns */
    private String name, description;
    private int costDollar, costCents;

    public Item(String name, String description, int costDollar, int costCents) {
        this.name = name;
        this.description = description;
        this.costDollar = costDollar;
        this.costCents = costCents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCostDollar() {
        return costDollar;
    }

    public void setCostDollar(int costDollar) {
        this.costDollar = costDollar;
    }

    public int getCostCents() {
        return costCents;
    }

    public void setCostCents(int costCents) {
        this.costCents = costCents;
    }

    public static Item fromJson(String json) {
        return gson.fromJson(json, Item.class);
    }

    public boolean equals(Item item) {
        return name.equals(item.name)
                && description.equals(item.description)
                && costDollar == item.costDollar
                && costCents == item.costCents;
    }

    public interface DAO extends Entity.DAO<Item> {

        List<Item> allItems() throws DataAccessException;

    }

}
