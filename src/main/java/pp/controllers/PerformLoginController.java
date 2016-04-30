package pp.controllers;

import pp.Controller;

public class PerformLoginController extends Controller
{
	public void executeController() throws Exception
	{
		//get the user's information
		String email = req().queryParams("email"),
				password = req().queryParams("password");

		//get the redirect path
		String redirectPath = req().queryParams("returnTo");
		if( redirectPath == null )
			redirectPath = "/";

		if( email != null && password != null && login(email, password) ) //login was successful
		{
			res().redirect(redirectPath, 301);
		}
		else
		{
			res().redirect("/login?returnTo=" +redirectPath+ "&reason=failed", 301);
		}
	}
}
