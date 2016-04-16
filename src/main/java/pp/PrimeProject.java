package pp;

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

		before("/debug/*", (req, res) -> {
			if( !DEBUG )
				halt(404, "File Not Found");
		});

		get("/", (req, res) -> useController( new HomepageController(), req, res ) );

		get("/cart", (req, res) -> useController( new CartController(), req, res ) );

		get("/item/:id", (req, res) -> useController( new ItemController(), req, res ) );

		get("/login", (req, res) -> useController( new LoginController(), req, res ) );

		get("/createAccount", (req, res) -> useController( new CreateAcctController(), req, res ) );

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
		ctrl.initController(req, res, sm);
		ctrl.executeController();
		ctrl.deinitController();
		return res.body();
	}

	public static Connection createDBConnection() throws IOException, SQLException
	{
		return DriverManager.getConnection("jdbc:derby:memory:myDB;create=true");
	}
}
