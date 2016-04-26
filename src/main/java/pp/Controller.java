package pp;

import lib.persistence.dao.SQLiteDAO;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public abstract class Controller
{
	private Request req;
	private Response res;
	private SessionManager sm;
	private String sessionToken;
	private BusinessLogic bl;

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	protected final BusinessLogic bl() { return bl; }

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

	/**
	 * Attempts to log the user in.
	 * @return true if the user is logged in, false if the user supplied the wrong credentials
	 * @throws Exception if login failed due to server errors
	 */
	protected final boolean login(String username, String password, String salt) throws Exception
	{
		if( sm.login(req, res, username, password, salt) )
		{
			sessionToken = sm.getSessionToken(req, res);
			return true;
		}
		else
		{
			return false;
		}
	}

	/** Logs the user out */
	protected final void logout()
	{
		sm.logout(req, res);
		sessionToken = sm.getSessionToken(req, res);
	}

	/** @return true if the user is logged in */
	protected boolean isUserLoggedIn()
	{
		//TODO implement
		return false;
	}

	/** @return the logged in user's username (or "" is the user isn't logged in) */
	protected String username()
	{
		//TODO implement
		return "";
	}

	//lifecycle functions
	final void initController(Request req, Response res, SessionManager sm) throws SQLException
	{
		this.req = req;
		this.res = res;
		this.sm = sm;
		this.sessionToken = sm.getSessionToken(req, res);
		this.bl = new BusinessLogic(this, new SQLiteDAO());
	}

	public abstract void executeController() throws Exception;

	final void deinitController()
	{
	}
}
