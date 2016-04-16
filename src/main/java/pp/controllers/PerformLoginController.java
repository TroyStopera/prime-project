package pp.controllers;

import pp.Controller;

public class PerformLoginController extends Controller
{
	public void executeController() throws Exception
	{
		//get the user's information
		String username = req().queryParams("username"),
				password = req().queryParams("password"),
				salt = ""; //TODO get the user's salt

		System.out.printf("login: u,p,s = %s, %s, %s\n", username, password, salt);

		//get the redirect path
		String redirectPath = req().queryParams("returnTo");
		if( redirectPath == null )
			redirectPath = "/";

		if( username != null && password != null && login(username, password, salt) ) //login was successful
		{
			res().redirect(redirectPath, 301);
		}
		else
		{
			res().redirect("/login?returnTo=" +redirectPath+ "&reason=failed", 301);
		}
	}
}
