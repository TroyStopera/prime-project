package pp.controllers;

import pp.Controller;
import spark.Spark;

public class ItemImageController extends Controller
{
	public void executeController() throws Exception
	{
		long itemId = Long.parseLong( req().params("id") );

		String mime = bl().getItemImageMime( itemId );
		byte[] imageData = bl().getItemImageData( itemId );

		if( mime == null || imageData == null )
		{
			Spark.halt(404);
		}
		else
		{
			res().status(200);
			res().header("Content-Type", mime);
			res().raw().getOutputStream().write( imageData );
		}
	}
}
