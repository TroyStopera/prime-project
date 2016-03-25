package lib.persistence.dao;

import lib.persistence.DataAccessObject;
import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

//TODO implement this, or another DAO
public class DerbyDAO extends DataAccessObject {

    @Override
    public Account.DAO accountAccessor() {
        return null;
    }

    @Override
    public Cart.DAO cartAccessor() {
        return null;
    }

    @Override
    public Item.DAO itemAccessor() {
        return null;
    }

    @Override
    public ItemReview.DAO reviewAccessor() {
        return null;
    }
}
