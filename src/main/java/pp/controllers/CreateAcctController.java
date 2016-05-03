package pp.controllers;

import pp.HTMLController;

public class CreateAcctController extends HTMLController
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
		addCSS("/static/login.css");

		bindData("isLoginPage", true);

		if( req().queryParams("reason") != null )
		{
			bindData("previousLoginFailed", true);
			bindData("loginFailReason", req().queryParams("reason"));
		}

		String returnTo = req().queryParams("returnTo");
		if( returnTo == null )
			returnTo = "/";

		bindData("returnTo", returnTo);

		outputView("/www/views/header.hbs");
		outputView("/www/views/create-acct.hbs");
		outputView("/www/views/footer.hbs");
	}
}
