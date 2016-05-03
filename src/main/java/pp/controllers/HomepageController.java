package pp.controllers;

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

		outputView("/www/views/header.hbs");
		outputView("/www/views/homepage.hbs")
				.bindData("featuredItem", new ControllerItem(bl().getFeaturedItem()) );
		outputView("/www/views/footer.hbs");
	}
}
