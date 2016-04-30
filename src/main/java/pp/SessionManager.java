package pp;

import spark.Request;
import spark.Response;

import javax.servlet.http.Cookie;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class SessionManager
{
	private static final String cookieName = "prime"; //the name of the browser cookie we use to track sessions
	private static final int sessionKeyLengthInBytes = 32; //the length of the randomly-generated session key (in bytes)
	private static final long sessionLengthInSeconds = TimeUnit.MINUTES.toSeconds(15); //sessions expire after 15 minutes of inactivity

	private final SecureRandom random = new SecureRandom();
	private final ConcurrentMap<String, SessionData> cookieMap = new ConcurrentHashMap<>();

	private class SessionData
	{
		private final Object lock = new Object();
		private Instant expireTime = Instant.now().plus( Duration.ofSeconds(sessionLengthInSeconds) );
		private boolean loggedIn = false;
		private long loggedInAccountId = 0;

		public void invalidateTimeoutClock()
		{
			synchronized( lock )
			{
				expireTime = Instant.now().minus( Duration.ofSeconds(sessionLengthInSeconds) );
			}
		}

		public void refreshTimeoutClock()
		{
			synchronized( lock )
			{
				expireTime = Instant.now().plus(Duration.ofSeconds(sessionLengthInSeconds));
			}
		}

		public boolean isExpired()
		{
			synchronized( lock )
			{
				return Instant.now().isAfter(expireTime);
			}
		}

		public void setAccountId(long accountId)
		{
			synchronized( lock )
			{
				this.loggedIn = true;
				this.loggedInAccountId = accountId;
			}
		}

		public boolean isLoggedIn()
		{
			synchronized( lock )
			{
				return loggedIn;
			}
		}

		public long getAccountId()
		{
			synchronized( lock )
			{
				return loggedInAccountId;
			}
		}
	}

	/**
	 * @return the session token for this client
	 */
	private String getSessionToken(Request req, Response res)
	{
		final boolean userHasCookie = req.cookie(cookieName) != null && cookieMap.containsKey(req.cookie(cookieName));

		//does this user have a valid cookie?
		if( userHasCookie && !cookieMap.get(req.cookie(cookieName)).isExpired() )
		{
			//refresh the user's cookie
			final String sessionCookie = req.cookie(cookieName);
			issueSessionCookie(res, sessionCookie);
			cookieMap.get(sessionCookie ).refreshTimeoutClock();
			return sessionCookie;
		}
		else
		{
			//if the user's old cookie expired, remove it from the session map
			if( userHasCookie && cookieMap.get(req.cookie(cookieName)).isExpired() )
				cookieMap.remove(req.cookie(cookieName) );

			//generate a new cookie for this session
			final String sessionCookie = generateCookie();

			//add the new cookie to our set of valid cookies
			cookieMap.put(sessionCookie, new SessionData() ); //TODO handle cookie collisions here -- it'll cause nasty problems if it occurs

			//issue a cookie to this user
			issueSessionCookie(res, sessionCookie);

			return sessionCookie;
		}
	}

	/** Adds the given session cookie the the response */
	private void issueSessionCookie(Response res, String sessionCookie)
	{
		Cookie cookie = new Cookie(cookieName, sessionCookie);
		cookie.setMaxAge( (int) sessionLengthInSeconds ); //this cookie will expire in 15 minutes
		cookie.setHttpOnly(true); //this cookie can't be modified by JavaScript in the user's browser
		//cookie.setSecure(true); //this cookie will only be served over HTTPS //TODO are we using SSL?
		res.raw().addCookie( cookie );
	}

	/** Invalidates the user's session token. This will log the user out if they are logged in. */
	public void invalidateSession(Request req, Response res)
	{
		final String sessionToken = getSessionToken(req, res);
		cookieMap.remove( sessionToken ).invalidateTimeoutClock();
	}

	/**
	 * Marks the user's session as logged in
	 * @param accountId the account ID that the user logged in with
	 */
	public void setSessionAccountId(Request req, Response res, long accountId)
	{
		final String sessionToken = getSessionToken(req, res);
		cookieMap.get( sessionToken ).setAccountId(accountId);
	}

	/**
	 * @return true if the user's session is logged in
	 */
	public boolean isSessionLoggedIn(Request req, Response res)
	{
		final String sessionToken = getSessionToken(req, res);
		return cookieMap.get( sessionToken ).isLoggedIn();
	}

	/**
	 * @return the ID of the logged in user
	 */
	public long getSessionAccountId(Request req, Response res)
	{
		final String sessionToken = getSessionToken(req, res);
		return cookieMap.get( sessionToken ).getAccountId();
	}

	private String generateCookie()
	{
		byte[] b = new byte[ sessionKeyLengthInBytes ];
		random.nextBytes(b);
		return Base64.getEncoder().encodeToString(b);
	}
}
