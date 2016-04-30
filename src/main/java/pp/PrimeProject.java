package pp;

import lib.persistence.DataAccessException;
import lib.persistence.DataAccessObject;
import lib.persistence.dao.SQLiteDAO;
import lib.persistence.entities.Item;
import pp.controllers.*;
import spark.Request;
import spark.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.*;

public class PrimeProject
{
	private static final boolean DEBUG = true; //set this to FALSE in non-development builds
	private static SessionManager sm;

	public static void main(String[] args)
	{
		port(8080);

		sm = new SessionManager();

		if( DEBUG )
		{
			try
			{
				DataAccessObject dao = new SQLiteDAO();
				createRandomItems(dao);
			}
			catch( DataAccessException | SQLException e )
			{
				throw new RuntimeException("Could not create random items", e);
			}
		}

		before("/debug/*", (req, res) -> {
			if( !DEBUG )
				halt(404, "File Not Found");
		});

		get("/", (req, res) -> useController( new HomepageController(), req, res ) );

		get("/cart", (req, res) -> useController( new CartController(), req, res ) );

		get("/item/:id", (req, res) -> useController( new ItemController(), req, res ) );

		get("/itemImage/:id", (req, res) -> useController( new ItemImageController(), req, res ) );

		get("/login", (req, res) -> useController( new LoginController(), req, res ) );

		get("/createAccount", (req, res) -> useController( new CreateAcctController(), req, res ) );

		post( "/performAcctCreate", (req, res) -> useController( new PerformCreateAcctController(), req, res) );

		post("/performLogin", (req, res) -> useController( new PerformLoginController(), req, res ) );

		get("/performLogout", (req, res) -> useController( new PerformLogoutController(), req, res ) );

		get("/api/listCartItems", (req, res) -> useController( new CartAPIController.ListCartAPIController(), req, res ) );

		get("/api/addCartItem", (req, res) -> useController( new CartAPIController.AddToCartAPIController(), req, res ) );

		get("/api/removeCartItem", (req, res) -> useController( new CartAPIController.RemoveFromCartAPIController(), req, res ) );

		staticRoute("/static", "/www/static");

		get("/debug/db", (req, res) -> {
			try( InputStream in = PrimeProject.class.getResourceAsStream("/www/debug/db.html");
			     ByteArrayOutputStream out = new ByteArrayOutputStream() )
			{
				res.status(200);
				res.header("Content-Type", "text/html");
				return Utils.getFileText( in );
			}
		});

		get("/debug/dbQuery", (req, res) -> useController( new DBQueryDebugController(), req, res ) );

		after((req, res) -> {
			//does the user support gzip?
			if( req.headers("Accept-Encoding") != null && req.headers("Accept-Encoding").contains("gzip") )
				res.header("Content-Encoding", "gzip");
		});
	}

	private static void staticRoute(String urlPrefix, String resourceDirectory)
	{
		get(urlPrefix+ "/*", (req, res) -> {
			try( InputStream in = PrimeProject.class.getResourceAsStream( resourceDirectory+ "/" +req.splat()[0] ) )
			{
				//TODO make sure that the URL doesn't include any ".."s
				if( in == null )
					halt(404, "File Not Found");

				res.status(200);
				res.header("Content-Type", Utils.guessMimeType(req.splat()[0]));
				return Utils.getFileText( in );
			}
		});
	}

	private static String useController(final Controller ctrl, final Request req, final Response res) throws Exception
	{
		ctrl.initController(req, res, new SQLiteDAO(), sm);
		ctrl.executeController();
		ctrl.deinitController();
		return res.body();
	}

	private static void createRandomItems(DataAccessObject dao) throws DataAccessException
	{
		final String[] names = {
				"Earring",
				"Spoon",
				"Cup of Noodles",
				"Red Solo Cup",
				"Lamp",
				"Computer Monitor",
				"Watter Bottle",
				"Empty Snapple Bottle",
				"Coffee Maker",
				"Lint Roller"
		};

		for( String name : names )
		{
			final Item i = new Item(name, "This is a " + name, (int) (Math.random() * 100), (int) (Math.random() * 99));
			dao.itemAccessor().create(i);
		}
	}

	public static Connection createDBConnection() throws IOException, SQLException
	{
		return DriverManager.getConnection("jdbc:derby:memory:myDB;create=true");
	}
}
