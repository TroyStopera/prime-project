package pp;

import pp.controllers.*;

import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static spark.Spark.*;

public class PrimeProject
{
	private static final boolean DEBUG = true; //set this to FALSE in non-development builds

	public static void main(String[] args)
	{
		port(8080);

		before("/debug/*", (req, res) -> {
			if( !DEBUG )
				halt(404, "File Not Found");
		});

		get("/", (req, res) -> {
			final Controller ctrl = new HomepageController();
			ctrl.initController(req, res);
			ctrl.executeController();
			ctrl.deinitController();
			return res.body();
		});

		get("/cart", (req, res) -> {
			final Controller ctrl = new CartController();
			ctrl.initController(req, res);
			ctrl.executeController();
			ctrl.deinitController();
			return res.body();
		});

		get("/item/:id", (req, res) -> {
			final Controller ctrl = new ItemController();
			ctrl.initController(req, res);
			ctrl.executeController();
			ctrl.deinitController();
			return res.body();
		});

		get("/login", (req, res) -> {
			final Controller ctrl = new LoginController();
			ctrl.initController(req, res);
			ctrl.executeController();
			ctrl.deinitController();
			return res.body();
		});

		staticRoute("/static", "/www/static");

		get("/debug/db", (req, res) -> {
			try( InputStream in = PrimeProject.class.getResourceAsStream("/www/debug/db.html");
				ByteArrayOutputStream out = new ByteArrayOutputStream() )
			{
				res.status(200);
				res.header("Content-Type", "text/html");
				Utils.copyStream(in, out);
				byte[] fileData = out.toByteArray();
				return new String(fileData, StandardCharsets.UTF_8);
			}
		});

		get("/debug/dbQuery", (req, res) -> {
			final Controller ctrl = new DBQueryDebugController();
			ctrl.initController(req, res);
			ctrl.executeController();
			ctrl.deinitController();
			return res.body();
		});

		after( (req, res) -> {
			//does the user support gzip?
			if( req.headers("Accept-Encoding") != null && req.headers("Accept-Encoding").contains("gzip") )
				res.header("Content-Encoding", "gzip");
		} );
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
				byte[] fileData = Utils.getBytesFromStream(in);
				return new String(fileData, StandardCharsets.UTF_8);
			}
		});
	}

	public static Connection createDBConnection() throws IOException, SQLException
	{
		return DriverManager.getConnection("jdbc:derby:memory:myDB;create=true");
	}
}
