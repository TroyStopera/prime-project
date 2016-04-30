package pp;

import spark.Request;
import spark.Response;

import javax.servlet.http.Cookie;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class SessionManager
{
	private static final String cookieName = "prime"; //the name of the browser cookie we use to track sessions
	private static final int sessionKeyLengthInBytes = 32; //the length of the randomly-generated session key (in bytes)

	private final SecureRandom random = new SecureRandom();
	private final ConcurrentMap<String, Object> cookieSet = new ConcurrentHashMap<>();
	private final Object value = new Object();

	/**
	 * @return the session token for this client
	 */
	public String getSessionToken(Request req, Response res)
	{
		//does this user have a valid cookie?
		if( req.cookie(cookieName) != null && cookieSet.containsKey(req.cookie(cookieName)) )
		{
			return req.cookie(cookieName);
		}
		else
		{
			//generate a new cookie for this session
			final String sessionCookie = generateCookie();

			//add the new cookie to our set of valid cookies
			cookieSet.put( sessionCookie, value );

			//issue a cookie to this user
			Cookie cookie = new Cookie(cookieName, sessionCookie);
			cookie.setMaxAge( (int) TimeUnit.MINUTES.toSeconds(30) ); //this cookie will expire in 30 minutes
			cookie.setHttpOnly(true); //this cookie can't be modified by JavaScript in the user's browser
			//cookie.setSecure(true); //this cookie will only be served over HTTPS //TODO are we using SSL?
			res.raw().addCookie( cookie );

			return sessionCookie;
		}
	}

	/**
	 * Regenerates a session token for this client
	 * @return the new session token
	 */
	public String invalidateSessionToken(Request req, Response res)
	{
		//does this user have a cookie?
		if( req.cookie(cookieName) != null )
		{
			//invalidate this cookie
			final String oldSessionCookie = req.cookie(cookieName);
			cookieSet.remove(oldSessionCookie);
		}

		//generate a new one
		return getSessionToken(req, res);
	}

	/**
	 * Attempts to log the user in.
	 * @return true if login was successful, false if it failed
	 * @throws Exception if login failed due to server errors
	 */
	public boolean login(Request req, Response res, String email, String password) throws Exception
	{
		//get the user's session token
		final String sessionToken = getSessionToken(req, res);

		//get the user's account
		return false;
	}

	/** Logs the user out */
	public void logout(Request req, Response res)
	{
		//TODO implement!
	}

	private String generateCookie()
	{
		byte[] b = new byte[ sessionKeyLengthInBytes ];
		random.nextBytes(b);
		return Base64.getEncoder().encodeToString(b);
	}
}
