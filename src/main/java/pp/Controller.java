package pp;

import lib.imagedb.ImageDB;
import lib.persistence.DataAccessObject;
import lib.persistence.entities.Account;
import spark.Request;
import spark.Response;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

public abstract class Controller
{
	private Request req;
	private Response res;
	private DataAccessObject dao;
	private ImageDB imageDB;
	private SessionManager sm;
	private BusinessLogic bl;

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	protected final BusinessLogic bl() { return bl; }

	/**
	 * Attempts to log the user in.
	 * @return true if the user is logged in, false if the user supplied the wrong credentials
	 * @throws Exception if login failed due to server errors
	 */
	protected final boolean login(String email, String password) throws Exception
	{
		//get the user's account
		final Optional<Account> acctOptional = dao.accountAccessor().get(email);
		if( !acctOptional.isPresent() )
			return false;

		final Account acct = acctOptional.get();

		//get the salt from the password
		final String[] split = acct.getPassword().split("\\$");
		if( split.length != 2 )
			throw new RuntimeException("Non-salted password");

		final String hashedActualPassword = split[0]; //TODO @Warren maybe this should be stored as a char[]?
		final byte[] salt = Base64.getDecoder().decode( split[1] );

		//hash the password provided by the user
		final String hashedPassword = Base64.getEncoder().encodeToString( hash(password.toCharArray(), salt) );

		//compare the passwords
		if( slowEquals(hashedActualPassword, hashedPassword) ) //the user supplied the correct password!
		{
			sm.setSessionAccountId(req, res, acct.getId()); //log the user in with this account ID
			return true;
		}
		else //the user provided the wrong password
		{
			return false;
		}
	}

	/**
	 * Attempts to create an account for this user
	 * @return true if successful, false if not
	 * @throws Exception if login failed due to server errors
	 */
	protected final boolean createAccount(String email, String username, String password) throws Exception
	{
		//TODO should password be stored as a char[]?

		//make sure that an account with this email doesn't already exist
		final Optional<Account> acctOptional = dao.accountAccessor().get(email);
		if( acctOptional.isPresent() )
			return false;

		//hash and salt the password
		byte[] salt = generateSalt();
		byte[] hashedPassword = hash( password.toCharArray(), salt );

		//Base64 encode the salt and password, and stick them together in a String for the DB
		final String dbStoredPassword = Base64.getEncoder().encodeToString(hashedPassword)+ "$" +Base64.getEncoder().encodeToString(salt);

		//create the account
		final Account acct = new Account(Account.TYPE_USER, username, email, dbStoredPassword);
		dao.accountAccessor().create(acct);

		//log in the user
		sm.setSessionAccountId(req, res, acct.getId());

		//return true
		return true;
	}

	/** Logs the user out */
	protected final void logout()
	{
		sm.invalidateSession(req, res);
	}

	/** @return true if the user is logged in */
	public boolean isUserLoggedIn()
	{
		return sm.isSessionLoggedIn(req, res);
	}

	/** @return the accountID of the logged in user */
	public long getUserAccountId()
	{
		return sm.isSessionLoggedIn(req, res)
				? sm.getSessionAccountId(req, res)
				: 0;
	}

	//lifecycle functions
	final void initController(Request req, Response res, DataAccessObject dao, ImageDB imageDB, SessionManager sm) throws SQLException
	{
		this.req = req;
		this.res = res;
		this.sm = sm;
		this.dao = dao;
		this.imageDB = imageDB;
		this.bl = new BusinessLogic(this, dao, imageDB);
	}

	public abstract void executeController() throws Exception;

	final void deinitController()
	{
	}

	//Password hashing code
	private final SecureRandom random = new SecureRandom();
	private final String hashAlgorithm = "PBKDF2WithHmacSHA1"; //Use PBKDF2 -- I think this implementation is included in all standard JVMs
	private final int iterations = 65535; //the number of iterations to use
	private final int desiredKeyLen = 128; //the length of our hashed password
	private final int saltLen = 128; //the length of salt (in bytes)
	private byte[] hash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		final KeySpec keySpec = new PBEKeySpec( password, salt, iterations, desiredKeyLen );
		final SecretKey key = SecretKeyFactory
				.getInstance(hashAlgorithm)
				.generateSecret( keySpec );
		return key.getEncoded();
	}

	private byte[] generateSalt()
	{
		byte[] salt = new byte[ saltLen ];
		random.nextBytes(salt);
		return salt;
	}

	/** used to compare password hashes in a way that avoids timing attacks */
	private boolean slowEquals(String str1, String str2)
	{
		int diff = str1.length() ^ str2.length();
		for( int x = 0; x < str1.length() && x < str2.length(); x++ )
			diff = str1.charAt(x) ^ str2.charAt(x);
		return diff == 0;
	}
}
