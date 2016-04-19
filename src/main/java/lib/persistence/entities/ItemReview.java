package lib.persistence.entities;

import lib.persistence.DataAccessException;
import lib.persistence.Entity;

import java.util.List;

public class ItemReview extends Entity {

    /* Columns */
    private int rating;
    private String review;
    private long accountId, itemId;

    public ItemReview(long itemId, int rating, String review, long accountId) {
        this.itemId = itemId;
        this.rating = rating;
        this.review = review;
        this.accountId = accountId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getItemId() {
        return itemId;
    }

    public static ItemReview fromJson(String json) {
        return gson.fromJson(json, ItemReview.class);
    }

    public interface DAO extends Entity.DAO<ItemReview> {

        List<ItemReview> reviewsFor(Item item) throws DataAccessException;

    }

}
