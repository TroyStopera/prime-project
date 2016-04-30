package lib.persistence.dao;

import lib.persistence.DataAccessObject;
import lib.persistence.Entity;
import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteDAO extends DataAccessObject {

	private static final String DRIVER_NAME = "org.sqlite.JDBC";
    public static final String DATABASE_NAME = ":memory:";//"Prime.db";

    protected final Connection connection;

    public SQLiteDAO() throws SQLException {
	    //load the driver class so that it can register itself for DriverManager to find
	    try
	    {
		    Class.forName(DRIVER_NAME);
	    }
	    catch( ClassNotFoundException e )
	    {
		    throw new SQLException("Could not find SQL driver");
	    }

	    connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

        //ensure Item table exists
        PreparedStatement createItem = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Item(" +
                        "id INTEGER PRIMARY KEY, " +
                        "Name TEXT, " +
                        "PriceDollars INT, " +
                        "PriceCents INT, " +
                        "Description TEXT" +
                        ");"
        );
        createItem.setQueryTimeout(30);
        createItem.execute();

        //ensure Account table exists
        PreparedStatement createAccount = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Account(" +
                        "id INTEGER PRIMARY KEY, " +
                        "Pass TEXT, " +
                        "Type INT, " +
                        "Username TEXT, " +
                        "Email TEXT UNIQUE" +
                        ");"
        );
        createAccount.setQueryTimeout(30);
        createAccount.execute();

        //ensure CartEntry table exists
        PreparedStatement createCartEntry = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS CartEntry(" +
                        "id INTEGER PRIMARY KEY, " +
                        "Quantity INT, " +
                        "Item_id INTEGER, " +
                        "Account_id INTEGER, " +
                        "FOREIGN KEY(Item_id) REFERENCES Item(id), " +
                        "FOREIGN KEY(Account_id) REFERENCES Account(id)" +
                        ");"
        );
        createCartEntry.setQueryTimeout(30);
        createCartEntry.execute();

        //ensure ItemReview table exists
        PreparedStatement createItemReview = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ItemReview(" +
                        "id INTEGER PRIMARY KEY, " +
                        "Rating INT, " +
                        "Item_id INTEGER, " +
                        "Account_id INTEGER, " +
                        "Description TEXT, " +
                        "FOREIGN KEY(Item_id) REFERENCES Item(id), " +
                        "FOREIGN KEY(Account_id) REFERENCES Account(id)" +
                        ");"
        );
        createItemReview.setQueryTimeout(30);
        createItemReview.execute();
    }

    @Override
    public Account.DAO accountAccessor() {
        return new AccountDAO(this);
    }

    @Override
    public Cart.DAO cartAccessor() {
        return new CartDAO(this);
    }

    @Override
    public Item.DAO itemAccessor() {
        return new ItemDAO(this);
    }

    @Override
    public ItemReview.DAO reviewAccessor() {
        return new ItemReviewDAO(this);
    }

    @Override
    protected void setId(Entity entity, long id) {
        super.setId(entity, id);
    }
}
