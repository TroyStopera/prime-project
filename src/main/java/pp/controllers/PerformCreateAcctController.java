package pp.controllers;

import pp.Controller;

public class PerformCreateAcctController extends Controller
{
	public void executeController() throws Exception
	{
		//get the user's information
		String email = req().queryParams("email"),
				username = req().queryParams("username"),
				password = req().queryParams("password");

		//get the redirect path
		String redirectPath = req().queryParams("returnTo");
		if( redirectPath == null )
			redirectPath = "/";

		if( email != null && username != null && password != null && createAccount(email, username, password) ) //account creation was successful
		{
			res().redirect(redirectPath, 302);
		}
		else
		{
			res().redirect("/createAccount?returnTo=" +redirectPath+ "&reason=failed", 301);
		}
	}
}
