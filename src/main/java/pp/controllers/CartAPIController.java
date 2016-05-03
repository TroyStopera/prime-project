package pp.controllers;

import com.google.gson.Gson;
import pp.Controller;
import spark.Spark;

public class CartAPIController
{
	private static class Cart
	{
		public final int count;
		public final String total;

		public Cart(int count, String total)
		{
			this.count = count;
			this.total = total;
		}
	}

	public static class AddToCartAPIController extends Controller
	{
		public void executeController() throws Exception
		{
			if( req().queryParams("itemId") == null || req().queryParams("quantity") == null )
				Spark.halt(400);

			long itemId = Long.parseLong( req().queryParams("itemId") );
			int quantity = Integer.parseInt( req().queryParams("quantity") );

			bl().addItem(itemId, quantity);

			res().status(200);
			res().header("Content-Type", "application/json");

			final Gson gson = new Gson();
			final Cart cart = new Cart( bl().getCartCount(), bl().getCartTotal() );
			res().body( gson.toJson(cart) );
		}
	}

	public static class UpdateCartQuantityAPIController extends Controller
	{
		public void executeController() throws Exception
		{
			if( req().queryParams("itemId") == null || req().queryParams("quantity") == null )
				Spark.halt(400);

			long itemId = Long.parseLong( req().queryParams("itemId") );
			int quantity = Integer.parseInt( req().queryParams("quantity") );

			bl().updateItemQuantity(itemId, quantity);

			res().status(200);
			res().header("Content-Type", "application/json");

			final Gson gson = new Gson();
			final Cart cart = new Cart( bl().getCartCount(), bl().getCartTotal() );
			res().body( gson.toJson(cart) );
		}
	}

	public static class ListCartAPIController extends Controller
	{
		public void executeController() throws Exception
		{
			res().status(200);
			res().header("Content-Type", "application/json");

			final Gson gson = new Gson();
			final Cart cart = new Cart( bl().getCartCount(), bl().getCartTotal() );
			res().body( gson.toJson(cart) );
		}
	}
}
