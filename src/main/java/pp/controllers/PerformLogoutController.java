package pp.controllers;

import pp.Controller;

public class PerformLogoutController extends Controller
{
	public void executeController() throws Exception
	{
		//log the user out
		logout();

		//redirect the user
		String redirectPath = req().queryParams("returnTo");
		if( redirectPath == null )
			redirectPath = "/";

		res().redirect(redirectPath, 301);
	}
}
