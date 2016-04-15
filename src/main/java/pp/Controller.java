package pp;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

import spark.Request;
import spark.Response;

public abstract class Controller
{
	private Request req;
	private Response res;
	private final List<String> externalCSS = new ArrayList<>();
	private final List<String> externalJS = new ArrayList<>();
	private final List<String> output = new ArrayList<>();

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	protected final void addCSS(String cssURL)
	{
		externalCSS.add( cssURL );
	}

	protected final void addJS(String jsURL)
	{
		externalJS.add( jsURL );
	}

	protected final void println(String line)
	{
		output.add( line+ "\n" );
	}

	protected final void outputView(String viewResource) throws IOException
	{
		try( InputStream in = PrimeProject.class.getResourceAsStream( viewResource );
			ByteArrayOutputStream out = new ByteArrayOutputStream() )
		{
			if( in == null )
				throw new FileNotFoundException();

			Utils.copyStream(in, out);
			byte[] fileData = out.toByteArray();
			output.add( new String(fileData, StandardCharsets.UTF_8) );
		}
	}

	//lifecycle functions
	final void initController(Request req, Response res)
	{
		this.req = req;
		this.res = res;
	}

	public abstract void generatePage() throws Exception;

	final void finalizePageGeneration()
	{
		String page = "";
		page += "<!DOCTYPE html>\n";
		page += "<html>\n";
		page += "<head>\n";

		for( String css : externalCSS )
			page += String.format("<link href='%s' type='text/css' rel='stylesheet' />\n", css);

		for( String js : externalJS )
			page += String.format("<script src='%s'></script>\n", js);

		page += "</head>\n";
		page += "<body>\n";

		for( Object obj : output )
			page += obj.toString();

		page += "</body>\n";
		page += "</html>\n";

		res().body( page );
	}

	final void deinitController()
	{
	}
}
