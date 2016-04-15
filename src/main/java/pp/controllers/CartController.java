package pp.controllers;

import pp.PrimeProject;
import pp.HTMLController;
import pp.Utils;

public class CartController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		addJS("/static/jquery.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");

		outputView("/www/views/header.hbs");
		outputView("/www/views/cart.hbs");
		outputView("/www/views/footer.hbs");
	}
}
