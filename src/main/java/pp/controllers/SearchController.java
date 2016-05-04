package pp.controllers;

import pp.HTMLController;

public class SearchController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		//get the search string
		String searchString = req().queryParams("q");
		if( searchString == null )
			searchString = "";

		addJS("/static/jquery.js");
		addJS("/static/primes.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");

		bindData("isSearchPage", true);
		bindData("searchString", searchString);

		outputView("/www/views/header.hbs");
		outputView("/www/views/search.hbs")
			.bindData("item", bl().searchItems(searchString));
		outputView("/www/views/footer.hbs");
	}
}
