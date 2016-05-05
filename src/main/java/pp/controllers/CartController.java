package pp.controllers;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Cart;
import pp.HTMLController;

import java.util.ArrayList;
import java.util.List;

public class CartController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		if( !isUserLoggedIn() )
		{
			res().redirect("/", 302);
			return;
		}

		outputView("/www/views/header.hbs");

		List<ControllerCartItem> cartEntry = new ArrayList<>();
		for( Cart.CartItem item : bl().getCartItems() )
			cartEntry.add( new ControllerCartItem(item) );

		outputView("/www/views/cart.hbs")
			.bindData("cartEntry", cartEntry);

		outputView("/www/views/footer.hbs");
	}

	private class ControllerCartItem
	{
		public int quantity;
		public ControllerItem item;

		public ControllerCartItem(Cart.CartItem cartEntry) throws DataAccessException
		{
			this.quantity = cartEntry.getQuantity();
			this.item = new ControllerItem( bl().getItem(cartEntry.getItemId()) );
		}
	}
}
