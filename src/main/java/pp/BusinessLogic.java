package pp;

import lib.persistence.DataAccessException;
import lib.persistence.DataAccessObject;
import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;

import java.util.Optional;

public class BusinessLogic
{
	private final Controller ctrl;
	private final DataAccessObject dao;

	public BusinessLogic(Controller ctrl, DataAccessObject dao)
	{
		this.ctrl = ctrl;
		this.dao = dao;
	}

	/**
	 * @return the identifier for this user's session
	 */
	private String sessionToken()
	{
		return ctrl.sessionToken();
	}

	/** @return true if the user is logged in */
	private boolean isUserLoggedIn()
	{
		return ctrl.isUserLoggedIn();
	}

	/** @return returns the email of the user that is logged in, or null if no one is logged in */
	private String getUserEmail()
	{
		return ctrl.isUserLoggedIn() ? ctrl.email() : null;
	}

	/** @return returns the Account of the user that is logged in, or null if no one is logged in */
	private Account getAccount() throws DataAccessException
	{
		if( ctrl.isUserLoggedIn() )
		{
			assert dao.accountAccessor().get( getUserEmail() ).isPresent() : "user should not be able to log in with an account that doesn't exist";
			return dao.accountAccessor().get( getUserEmail() ).get();
		}
		else
		{
			return null;
		}
	}

	/** @return returns the Cart of the user that is logged in, or null if no one is logged in */
	private Cart getCart() throws DataAccessException
	{
		if( ctrl.isUserLoggedIn() )
		{
			final Account acct = getAccount();
			assert acct == null : "user should not be able to log in with an account that doesn't exist";
			return dao.cartAccessor().get( acct.getId() );
		}
		else
		{
			return null;
		}
	}

	/** @return the Item with the given ID, or null if such an item doesn't exist */
	private Item getItem(long itemId) throws DataAccessException
	{
		final Optional<Item> itemOptional = dao.itemAccessor().get( itemId );
		return itemOptional.isPresent() ? itemOptional.get() : null;
	}

	/** Writes the given Account to the database. */
	private void writeAccount(Account acct) throws DataAccessException
	{
		dao.accountAccessor().update( acct );
	}

	/** Writes the given Cart to the database. */
	private void writeCart(Cart cart) throws DataAccessException
	{
		dao.cartAccessor().update( cart );
	}

	/**
	 * @return the number of items in this user's cart
	 */
	public int getCartCount() throws DataAccessException
	{	
		Cart userCart = getCart();
		return 0;
	}

	/**
	 * @return the total value of this user's cart
	 */
	public String getCartTotal() throws DataAccessException
	{
		//TODO implement
		return "$0.00";
	}

	/**
	 * @param itemId the ID of the item
	 * @return the mime-type of the given item's image, or null if the item has no image
	 */
	public String getItemImageMime(long itemId) throws DataAccessException
	{
		//TODO implement
		return null;
	}

	/**
	 * @param itemId the ID of the item
	 * @return the item's image, or null if the item has no image
	 */
	public byte[] getItemImageData(long itemId) throws DataAccessException
	{
		//TODO implement
		return null;
	}

	/**
	 * Adds an item to the user's cart
	 * @param itemId the id of the item
	 * @param quantity the quantity of item
	 */
	public void addItem(long itemId, int quantity) throws DataAccessException
	{
		//make sure quantity is posotive and itemId exists
		if(quantity > 0 && getItem(itemId) != null){
			Cart userCart = getCart(); //will be null if the user isn't logged in
			userCart.updateCart(itemId, quantity);//add item and quantity to cart
			writeCart(userCart);//write the cart the database	
		}	
		//ADD ERROR SAYING NEEDS TO BE POSOTIVE AND A REAL ITEM
		//ALSO NO ZERO
	}

	/**
	 * Removes an item to the user's cart
	 * @param itemId the id of the item
	 * @param quantity the quantity of item
	 */
	public void removeItem(long itemId, int quantity) throws DataAccessException
	{
		//make sure quantity is posotive and itemId exists
		if(quantity < 0 && getItem(itemId) != null){
			Cart userCart = getCart(); //will be null if the user isn't logged in
			userCart.updateCart(itemId, quantity);//add item and quantity to cart
			writeCart(userCart);//write the cart the database	
		}	
		//ADD ERROR SAYING NEEDS TO BE NEGATIVE AND A REAL ITEM
		//ALSO NO ZERO
	}
}
