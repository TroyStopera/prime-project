package pp;

import lib.imagedb.ImageDB;
import lib.persistence.DataAccessException;
import lib.persistence.DataAccessObject;
import lib.persistence.entities.Account;
import lib.persistence.entities.Cart;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BusinessLogic
{
	private final Controller ctrl;
	private final DataAccessObject dao;
	private final ImageDB imageDB;

	public BusinessLogic(Controller ctrl, DataAccessObject dao, ImageDB imageDB)
	{
		this.ctrl = ctrl;
		this.dao = dao;
		this.imageDB = imageDB;
	}

	/** @return true if the user is logged in */
	private boolean isUserLoggedIn()
	{
		return ctrl.isUserLoggedIn();
	}

	/** @return returns the Account of the user that is logged in, or null if no one is logged in */
	private Account getAccount() throws DataAccessException
	{
		if( ctrl.isUserLoggedIn() )
		{
			assert dao.accountAccessor().get( ctrl.getUserAccountId() ).isPresent() : "user should not be able to log in with an account that doesn't exist";
			return dao.accountAccessor().get( ctrl.getUserAccountId() ).get();
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
	public Item getItem(long itemId) throws DataAccessException
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

	/** Creates the given ItemReview to the database. */
	private void createReview(ItemReview review) throws DataAccessException
	{
		dao.reviewAccessor().create( review );
	}

	/** @return a list of items in the currently logged in user's cart, or an empty list */
	public List<Cart.CartItem> getCartItems() throws DataAccessException
	{
		Cart userCart = getCart();
		return new ArrayList<Cart.CartItem> (userCart.getItemsMap().values());
	}

	/**
	 * @return the number of items in this user's cart
	 */
	public int getCartCount() throws DataAccessException
	{	
		int totalCount = 0;
		ArrayList<Cart.CartItem> items;
		if(getCart() == null){
			return 0;
		}
		else{
			items = (ArrayList<Cart.CartItem>)getCartItems();
			//Iterator itr = userCart.getItemsMap().entrySet().iterator();
			for(int i=0;i<items.size();i++){
				totalCount += items.get(i).getQuantity();
			}
			return totalCount;
		}
	}

	/**
	 * @return the total value of this user's cart
	 */
	public String getCartTotal() throws DataAccessException
	{
		int dollars = 0; int cents = 0; int removeCents = 0;
		Item temp;
		String result;
		ArrayList<Cart.CartItem> items;
		if(getCart() == null){
			return "0.00";
		}
		else{
			items = (ArrayList<Cart.CartItem>)getCartItems();
			for(int i=0;i<items.size();i++){
				temp = getItem(items.get(i).getItemId());
				dollars += temp.getCostDollar()* (items.get(i).getQuantity());
				cents += temp.getCostCents()* (items.get(i).getQuantity());
			}
			removeCents = (int)(cents/100);
			dollars += removeCents; cents -= (removeCents*100);

			if(cents < 10){
				result = dollars+".0"+cents;
			}
			else{
				result = dollars+"."+cents;	
			}
			
		}
		return result;
	}

	/**
	 * @param itemId the ID of the item
	 * @return the mime-type of the given item's image, or null if the item has no image
	 */
	public String getItemImageMime(long itemId) throws DataAccessException
	{
		if(imageDB.getImageType(itemId) == null){
			return null;
		}
		else{
			return imageDB.getImageType(itemId);
		}
	}

	/**
	 * @param itemId the ID of the item
	 * @return the item's image, or null if the item has no image
	 */
	public byte[] getItemImageData(long itemId) throws DataAccessException
	{
		if(imageDB.getImageType(itemId) == null){
			return null;
		}
		else{
			return imageDB.getImageData(itemId);
		}
	}

	/**
	 * Adds an item to the user's cart
	 * @param itemId the id of the item
	 * @param quantity the quantity of item
	 */
	public void addItem(long itemId, int quantity) throws DataAccessException
	{
		ArrayList<Cart.CartItem> cartItems = (ArrayList<Cart.CartItem>) getCartItems();
		int totalQuantity = 0;
		//make sure quantity is posotive and itemId exists
		if(quantity > 0 && getItem(itemId) != null){
			Cart userCart = getCart(); //will be null if the user isn't logged in
			for(int i=0;i<cartItems.size();i++){
				if(cartItems.get(i).getItemId() == itemId){
					totalQuantity += cartItems.get(i).getQuantity();
				}
			}
			totalQuantity += quantity;
			userCart.updateCart(itemId, totalQuantity);//add item and quantity to cart
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
		ArrayList<Cart.CartItem> cartItems = (ArrayList<Cart.CartItem>) getCartItems();
		int totalQuantity = 0;
		//make sure quantity is posotive and itemId exists
		if(quantity < 0 && getItem(itemId) != null){
			Cart userCart = getCart(); //will be null if the user isn't logged in
			for(int i=0;i<cartItems.size();i++){
				if(cartItems.get(i).getItemId() == itemId){
					totalQuantity += cartItems.get(i).getQuantity();
				}
			}
			totalQuantity -= quantity;
			userCart.updateCart(itemId, totalQuantity);//remove item and quantity to cart
			writeCart(userCart);//write the cart the database	
		}	
		//ADD ERROR SAYING NEEDS TO BE NEGATIVE AND A REAL ITEM
		//ALSO NO ZERO
	}

	/**
	 * Sets the quantity of the item in the user's cart
	 * @param itemId the id of the item
	 * @param quantity the new quantity of the item
	 */
	public void updateItemQuantity(long itemId, int quantity) throws DataAccessException
	{
		Cart userCart = getCart();
		if(quantity == 0){
			ArrayList<Cart.CartItem> cartItems = (ArrayList<Cart.CartItem>) getCartItems();
			for(int i=0;i<cartItems.size();i++){
				if(cartItems.get(i).getItemId() == itemId){
					cartItems.remove(i);
					i--;
				}
			}
		}
		else{
			userCart.updateCart(itemId, quantity);
			writeCart(userCart);
		}

	}

	/** @return the username of the user that is logged in, or "" if the user isn't logged in */
	public String getUsername() throws DataAccessException
	{
		if(getAccount() == null){
			return " ";
		}
		else{
			return getAccount().getUsername();
		}
	}

	/** @return the username of the user with the given accountId, or "" if there is no such user */
	public String getUsername(long accountId) throws DataAccessException
	{
		//if(dao.accountAccessor().get( accountId ).get() != null){}
		if(dao.accountAccessor().get( accountId ).get() != null){
			return dao.accountAccessor().get( accountId ).get().getUsername();
		}
		return "";
	}

	/** @return the featured item for the current user */
	public Item getFeaturedItem() throws DataAccessException
	{
		List<Item> items = dao.itemAccessor().allItems();
		final int index = Utils.randInt( 0, items.size() );
		return items.get( index );
	}

	/** @return the featured item for the current user */
	public List<Item> getPopularItems(int count) throws DataAccessException
	{
		List<Item> items = dao.itemAccessor().allItems();
		List<Item> popularItems = new ArrayList<>();
		for( int x = 0; x < count; x++ )
		{
			final int index = Utils.randInt( 0, items.size() );
			popularItems.add( items.remove(index) );
		}

		return popularItems;
	}

	/** @return a list of reviews for the given item */
	public List<ItemReview> getReviewsFor(Item item) throws DataAccessException
	{
		List<ItemReview> reviews = (List<ItemReview>)dao.reviewAccessor().reviewsFor( item );
		return reviews;
	}

	/**
	 * Posts a review as the currently logged in user
	 * @param itemId the id of the item this review is for
	 * @param rating the rating of this item
	 * @param review the review text
	 */
	public void createReview(long itemId, int rating, String review) throws DataAccessException
	{
		long accountId = getCart().getAccountId();
		ItemReview newReview = new ItemReview(itemId, rating, review, accountId);
		createReview(newReview);
	}

	public List<Item> searchItems(String searchString) throws DataAccessException
	{
		List<Item> results = new ArrayList<>();

		searchString = searchString.toLowerCase();

		if( !searchString.isEmpty() )
			for( Item i : dao.itemAccessor().allItems() )
				if( i.getName().toLowerCase().contains(searchString) || i.getDescription().toLowerCase().contains(searchString) )
					results.add( i );

		return results;
	}
}
