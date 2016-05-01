package pp.controllers;

import pp.Controller;

public class PerformCreateReviewController extends Controller
{
	public void executeController() throws Exception
	{
		//get the item ID
		long itemId = Long.parseLong( req().params("id") );

		//get the review information
		int rating = Integer.parseInt( req().queryParams("rating") );
		String review = req().queryParams("review");

		//post the review
		bl().createReview(itemId, rating, review);

		//redirect the user back to the item page
		res().redirect("/item/" +itemId, 302);
	}
}
