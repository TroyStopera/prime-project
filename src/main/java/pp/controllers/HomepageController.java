package pp.controllers;

import lib.persistence.entities.Item;
import pp.HTMLController;

import java.util.ArrayList;
import java.util.List;

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

		ControllerItem featuredItem = new ControllerItem(bl().getFeaturedItem());
		List<ControllerItem> popularItems = new ArrayList<>();
		for( Item i : bl().getPopularItems(3) )
			popularItems.add( new ControllerItem(i) );

		outputView("/www/views/header.hbs");
		outputView("/www/views/homepage.hbs")
				.bindData("featuredItem", featuredItem)
				.bindData("popularItem1", popularItems.get(0))
				.bindData("popularItem2", popularItems.get(1))
				.bindData("popularItem3", popularItems.get(2));
		outputView("/www/views/footer.hbs");
	}
}
