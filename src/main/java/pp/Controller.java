package pp;

import spark.Request;
import spark.Response;

public abstract class Controller
{
	private Request req;
	private Response res;
	private SessionManager sm;
	private String sessionToken;

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	/**
	 * The user's current session token. Use this to identify the user across requests.
	 */
	protected final String sessionToken() { return sessionToken; }

	/**
	 * Invalidates the current session token and issues a new one.
	 * Call this when the user logs in or out.
	 * @return the new session token
	 */
	protected final String invalidateSessionToken()
	{
		sessionToken = sm.invalidateSessionToken(req, res);
		return sessionToken;
	}

	//lifecycle functions
	final void initController(Request req, Response res, SessionManager sm)
	{
		this.req = req;
		this.res = res;
		this.sm = sm;
		this.sessionToken = sm.getSessionToken(req, res);
	}

	public abstract void executeController() throws Exception;

	final void deinitController()
	{
	}
}
