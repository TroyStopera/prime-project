package pp.controllers;

import lib.persistence.DataAccessException;
import lib.persistence.entities.Item;
import lib.persistence.entities.ItemReview;
import pp.HTMLController;

import java.util.ArrayList;
import java.util.List;

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

		if( item != null )
		{
			List<ControllerReview> reviewList = new ArrayList<>();
			for( ItemReview r : bl().getReviewsFor(item) )
				reviewList.add( new ControllerReview(r) );

			println("<div class='container container-1'>");
			println("<span class='panel panel-1'>");
			outputView("/www/views/reviews.hbs")
					.bindData("item", new ControllerItem(item))
					.bindData("review", reviewList);
			println("</span>");
			println("</div>");
			println("</div>");
		}

		outputView("/www/views/footer.hbs");
	}

	private class ControllerReview
	{
		public String rating, review;
		public String reviewerUsername;

		public ControllerReview(ItemReview r) throws DataAccessException
		{
			rating = r.getRating()+ " star" +(r.getRating() != 1 ? "s" : "" );
			review = r.getReview();
			reviewerUsername = bl().getUsername( r.getAccountId() );
		}
	}
}
