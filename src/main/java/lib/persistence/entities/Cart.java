package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.*;

public class Cart extends Entity {

    private final long accountId;
    private final HashMap<Long, CartItem> items = new HashMap<>();

    public Cart(long accountId) {
        this.accountId = accountId;
        this.id = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    public Set<CartItem> getItems() {
        return new HashSet<>(items.values());
    }

    public int getSize() {
        return items.size();
    }

    public void updateCart(long itemId, int quantity) {
        if (quantity <= 0) items.remove(itemId);
        else {
            CartItem item = new CartItem(itemId, quantity);
            items.put(itemId, item);
        }
    }

    public static Cart fromJson(String json) {
        return gson.fromJson(json, Cart.class);
    }

    public static class CartItem {

        private final long itemId;
        private int quantity;

        public CartItem(long itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public long getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

    }

    public interface DAO {

        Cart get(long accountId) throws DataAccessException;

        Cart update(Cart entity) throws DataAccessException;

    }

}
