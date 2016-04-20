package pp;

public class BusinessLogic
{
	private final Controller ctrl;

	BusinessLogic(Controller ctrl)
	{
		this.ctrl = ctrl;
	}

	/**
	 * @return the identifier for this user's session
	 */
	private String sessionToken()
	{
		return ctrl.sessionToken();
	}

	/**
	 * @return the number of items in this user's cart
	 */
	public int getCartCount()
	{
		//TODO implement
		return 0;
	}

	/**
	 * @return the total value of this user's cart
	 */
	public String getCartTotal()
	{
		//TODO implement
		return "$0.00";
	}

	/**
	 * @param itemId the ID of the item
	 * @return the mime-type of the given item's image, or null if the item has no image
	 */
	public String getItemImageMime(long itemId)
	{
		//TODO implement
		return null;
	}

	/**
	 * @param itemId the ID of the item
	 * @return the item's image, or null if the item has no image
	 */
	public byte[] getItemImageData(long itemId)
	{
		//TODO implement
		return null;
	}

	/**
	 * Adds an item to the user's cart
	 * @param itemId the id of the item
	 * @param quantity the quantity of item
	 */
	public void addItem(long itemId, int quantity)
	{
		//TODO implement
	}

	/**
	 * Removes an item to the user's cart
	 * @param itemId the id of the item
	 * @param quantity the quantity of item
	 */
	public void removeItem(long itemId, int quantity)
	{
		//TODO implement
	}
}