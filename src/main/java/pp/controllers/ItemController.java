package pp.controllers;

import pp.PrimeProject;
import pp.Controller;
import pp.Utils;

public class ItemController extends Controller
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

		println("<div class='main-content-wrapper'>");
		println("<div class='container container-1'>");
		println("<span class='panel panel-1'>");
		outputView("/www/views/item.view");
		println("</span>");
		println("</div>");

		println("<div class='container container-1'>");
		println("<span class='panel panel-1'>");
		outputView("/www/views/reviews.view");
		println("</span>");
		println("</div>");
		println("</div>");

		outputView("/www/views/footer.view");
	}
}
