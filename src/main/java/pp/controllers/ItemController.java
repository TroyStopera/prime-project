package pp.controllers;

import lib.persistence.entities.Item;
import pp.HTMLController;

public class ItemController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		Item item;
		if( req().params("id") == null )
		{
			item = null;
		}
		else
		{
			long itemId = Long.parseLong(req().params("id"));
			item = bl().getItem( itemId );
		}

		addJS("/static/jquery.js");
		addJS("/static/primes.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");

		outputView("/www/views/header.hbs");

		println("<div class='main-content-wrapper'>");
		println("<div class='container container-1'>");
		println("<span class='panel panel-1'>");

		outputView("/www/views/item.hbs")
				.bindData("item", item == null ? null : new ControllerItem(item) );

		println("</span>");
		println("</div>");

		println("<div class='container container-1'>");
		println("<span class='panel panel-1'>");
		outputView("/www/views/reviews.hbs");
		println("</span>");
		println("</div>");
		println("</div>");

		outputView("/www/views/footer.hbs");
	}
}
