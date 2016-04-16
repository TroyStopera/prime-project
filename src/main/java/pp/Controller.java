package pp;

import spark.Request;
import spark.Response;

public abstract class Controller
{
	private Request req;
	private Response res;

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	//lifecycle functions
	final void initController(Request req, Response res)
	{
		this.req = req;
		this.res = res;
	}

	public abstract void executeController() throws Exception;

	final void deinitController()
	{
	}
}
