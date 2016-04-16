package pp.controllers;

import pp.HTMLController;

public class LoginController extends HTMLController
{
	protected void generatePage() throws Exception
	{
		addJS("/static/jquery.js");
		addCSS("/static/header.css");
		addCSS("/static/footer.css");
		addCSS("/static/panel.css");
		addCSS("/static/item.css");
		addCSS("/static/homepage.css");
		addCSS("/static/login.css");

		bindData("isLoginPage", true);

		if( req().queryParams("reason") != null )
			bindData("previousLoginFailed", true);

		String returnTo = req().queryParams("returnTo");
		if( returnTo == null )
			returnTo = "/";

		bindData("returnTo", returnTo);

		outputView("/www/views/header.hbs");
		outputView("/www/views/login.hbs");
		outputView("/www/views/footer.hbs");
	}
}
