package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Cart extends Entity {

    private final long accountId;
    private final Set<CartItem> items = new HashSet<>();

    public Cart(long accountId, Collection<CartItem> items) {
        this.accountId = accountId;
        this.items.addAll(items);
    }

    public long getAccountId() {
        return accountId;
    }

    public Set<CartItem> getItems() {
        return items;
    }

    public static Cart fromJson(String json) {
        return gson.fromJson(json, Cart.class);
    }

    public static class CartItem {

        private final Item item;
        private int quantity;

        public CartItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public Item getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

    }

    public interface DAO extends Entity.DAO<Cart> {

        @Override
        Optional<Cart> get(long accountId) throws DataAccessException;

    }

}
