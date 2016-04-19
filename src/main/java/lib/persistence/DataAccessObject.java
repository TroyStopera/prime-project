package lib.persistence;

import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

public abstract class DataAccessObject {

    protected abstract Account.DAO accountAccessor();

    protected abstract Cart.DAO cartAccessor();

    protected abstract Item.DAO itemAccessor();

    protected abstract ItemReview.DAO reviewAccessor();

    protected void setId(Entity entity, long id) {
        entity.id = id;
    }

}
