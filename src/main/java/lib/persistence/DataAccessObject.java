package lib.persistence;

import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

public abstract class DataAccessObject {

    public abstract Account.DAO accountAccessor();

    public abstract Cart.DAO cartAccessor();

    public abstract Item.DAO itemAccessor();

    public abstract ItemReview.DAO reviewAccessor();

    protected void setId(Entity entity, long id) {
        entity.id = id;
    }

}
