package pp;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Utils
{
	public static void copyStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buf = new byte[ 1024 * 4 ];
		while( true )
		{
			int bytesRead = in.read(buf);
			if( bytesRead == -1 )
				break;

			out.write(buf, 0, bytesRead);
		}
	}

	/** @return the extension of the file */
	public static String getExt(String filename)
	{
		final int pos = filename.lastIndexOf(".");
		if( pos == -1 || pos + 1 == filename.length() )
			return "";
		else
			return filename.substring(pos+1);
	}

	/** Attempts to guess the mime type of a file based on file extension */
	public static String guessMimeType(String filename)
	{
		switch( getExt(filename) )
		{
			case "html": case "htm": return "text/html";
			case "css": return "text/css";
			case "js": return "text/javascript";
			default: return "text/plain";
		}
	}

	public static String getFileText(InputStream in) throws IOException
	{
		try( ByteArrayOutputStream out = new ByteArrayOutputStream() )
		{
			if( in == null )
				throw new FileNotFoundException();

			Utils.copyStream(in, out);
			byte[] fileData = out.toByteArray();
			return new String(fileData, StandardCharsets.UTF_8);
		}
	}

	public static int randInt(int a, int b)
	{
		return (int)(a + (Math.random() * (b-a)));
	}
}
