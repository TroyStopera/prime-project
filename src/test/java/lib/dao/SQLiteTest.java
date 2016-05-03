package lib.dao;

import lib.persistence.dao.SQLiteDAO;
import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;

public class SQLiteTest {

    @Test
    public void testSQLiteCreation() throws Exception {
        new SQLiteDAO("Test.db");
        Assert.assertTrue(Paths.get("Test.db").toFile().exists());
    }

    @Test
    public void testAccount() throws Exception {
        Account.DAO dao = new SQLiteDAO("Test.db").accountAccessor();

        Account account = new Account(0, "testUser", "user@test.com", "password");
        account = dao.create(account);

        Optional<Account> optional = dao.get("user@test.com");
        Assert.assertTrue(optional.isPresent());
        Assert.assertTrue(optional.get().equals(account));

        dao.delete(account);
    }

    @Test
    public void testCart() throws Exception {
        Cart.DAO dao = new SQLiteDAO("Test.db").cartAccessor();

        //-1 for testing
        Cart cart = new Cart(-1);
        cart.updateCart(-1, 5);
        dao.update(cart);

        Cart cart2 = dao.get(-1);
        Assert.assertTrue(cart.getItems().size() == cart2.getItems().size());
        Assert.assertTrue(cart.equals(cart2));

        dao.update(cart);
    }

    @Test
    public void testItem() throws Exception {
        Item.DAO dao = new SQLiteDAO("Test.db").itemAccessor();

        Item item = new Item("Test", "The Best Item", 49, 99);
        item = dao.create(item);

        Optional<Item> optional = dao.get(item.getId());
        Assert.assertTrue(optional.isPresent());
        Assert.assertTrue(optional.get().equals(item));

        dao.delete(item);
    }

    @Test
    public void testReview() throws Exception {
        SQLiteDAO sqLiteDAO = new SQLiteDAO("Test.db");

        Item item = new Item("Test", "Test item", 99, 99);
        sqLiteDAO.itemAccessor().create(item);


        ItemReview.DAO dao = sqLiteDAO.reviewAccessor();

        ItemReview review = new ItemReview(item.getId(), 5, "Good", -1);
        review = dao.create(review);

        List<ItemReview> reviewList = dao.reviewsFor(item);
        Assert.assertTrue(reviewList.size() == 1);
        Assert.assertTrue(reviewList.get(0).equals(review));

        sqLiteDAO.itemAccessor().delete(item);
        dao.delete(review);
    }

}
