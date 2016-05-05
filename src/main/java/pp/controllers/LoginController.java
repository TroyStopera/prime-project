package pp.controllers;

import pp.HTMLController;

public class LoginController extends HTMLController
{
	protected void generatePage() throws Exception
	{
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
		outputView("/www/views/login.hbs");
		outputView("/www/views/footer.hbs");
	}
}
