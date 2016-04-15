package pp.controllers;

import pp.PrimeProject;
import pp.Controller;
import pp.Utils;

import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static spark.Spark.halt;

public class DBQueryDebugController extends Controller
{
	public void executeController() throws Exception
	{
		String query = req.queryParams("q");
		if( query == null || query.isEmpty() )
		{
			halt(404, "File not found");
		}
		else
		{
			res().status(200);
			res().type("text/plain");
			res().body( new String( runQuery(query), StandardCharsets.UTF_8 ) );
		}
	}

	private byte[] runQuery(String query)
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(byteOut);

		out.printf("Query: %s\n\n", query);

		try( Connection cnt = PrimeProject.createDBConnection();
			Statement stmt = cnt.createStatement() )
		{
			boolean hasResultSet = stmt.execute(query);
			if( hasResultSet )
			{
				ResultSet rs = stmt.getResultSet();

				//pretty-print the result set
				int[] columnWidths = new int[ rs.getMetaData().getColumnCount() ];
				for( int x = 0; x < columnWidths.length; x++ )
					columnWidths[x] = rs.getMetaData().getColumnDisplaySize(x+1);

				//column names
				for( int x = 0; x < columnWidths.length; x++ )
					out.printf("|%" +columnWidths[x]+ "s|", rs.getMetaData().getColumnName(x+1));
				out.println();

				//divider
				for( int x = 0; x < columnWidths.length; x++ )
				{
					out.printf("+");
					for( int y = 0; y < columnWidths[x]; y++ )
						out.printf("-");
					out.printf("+");
				}
				out.println();

				//row data
				while( rs.next() )
				{
					for( int x = 0; x < columnWidths.length; x++ )
						out.printf("|%" +columnWidths[x]+ "s|", rs.getObject(x+1).toString());
					out.println();
				}

				rs.close();
			}
			else
			{
				out.println( "update count = " +stmt.getUpdateCount() );
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(out);
		}

		out.flush();
		return byteOut.toByteArray();
	}
}
