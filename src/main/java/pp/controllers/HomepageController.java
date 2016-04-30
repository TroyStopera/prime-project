package pp.controllers;

import lib.persistence.DataAccessException;
import pp.HTMLController;

public class HomepageController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		addJS("/static/jquery.js");
		addJS("/static/primes.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");

		bindData("featuredItem", new Item(bl().getFeaturedItemId() ));

		outputView("/www/views/header.hbs");
		outputView("/www/views/homepage.hbs");
		outputView("/www/views/footer.hbs");
	}

	private class Item
	{
		public final long id;
		public final String name, desc, cost;

		private Item(long id) throws DataAccessException
		{
			this.id = id;
			this.name = bl().getItemName( id );
			this.desc = bl().getItemDescription( id );
			this.cost = bl().getItemCost( id );
		}
	}
}
