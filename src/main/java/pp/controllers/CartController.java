package pp.controllers;

import pp.PrimeProject;
import pp.Controller;
import pp.Utils;

public class CartController extends Controller
{
	public void generatePage() throws Exception
	{
		addJS("/static/jquery.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");

		outputView("/www/views/header.view");
		outputView("/www/views/cart.view");
		outputView("/www/views/footer.view");
	}
}
