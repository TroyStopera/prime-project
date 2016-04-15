package pp;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;

import spark.Request;
import spark.Response;
import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;

public abstract class Controller
{
	private Request req;
	private Response res;
	private final List<String> externalCSS = new ArrayList<>();
	private final List<String> externalJS = new ArrayList<>();
	private final List<View> output = new ArrayList<>();
	private final Map<String, Object> controllerContext = new HashMap<>();
	private final PageGenerationTimer timer = new PageGenerationTimer();

	private class PageGenerationTimer
	{
		private final long start = System.nanoTime();
		public double secondsToCreatePage()
		{
			long nsTime = System.nanoTime() - start;
			double secTime = nsTime / (double)TimeUnit.SECONDS.toNanos(1);
			return secTime;
		}
	}

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

	protected final void bindData(String key, Object value)
	{
		controllerContext.put( key, value );
	}

	protected final void println(String line)
	{
		output.add( new TextView(line+ "\n") );
	}

	protected final HandlebarsView outputView(String viewResource) throws IOException
	{
		final HandlebarsView v = new HandlebarsView(viewResource);
		output.add( v );
		return v;
	}

	private interface View
	{
		String get() throws Exception;
	}

	private class TextView implements View
	{
		private final String text;

		private TextView(String text)
		{
			this.text = text;
		}

		public String get()
		{
			return text;
		}
	}

	protected class HandlebarsView implements View
	{
		private final String viewResource;
		private final Map<String, Object> viewContext = new HashMap<>();

		private HandlebarsView(String viewResource)
		{
			this.viewResource = viewResource;
		}

		public HandlebarsView bindData(String key, Object value)
		{
			viewContext.put( key, value );
			return this;
		}

		public String get() throws IOException
		{
			//get the template
			String templateText = Utils.getFileText( PrimeProject.class.getResourceAsStream( viewResource ) );

			//create the context
			Context ctx = Context
				.newBuilder( Controller.this.timer )
				.combine( Controller.this.controllerContext )
				.combine( viewContext )
				.resolver( MapValueResolver.INSTANCE, MethodValueResolver.INSTANCE )
				.build();

			//compile and apply the template
			final Handlebars hbs = new Handlebars();
			Template template = hbs.compileInline( templateText );
			return template.apply( ctx );
		}
	}

	//lifecycle functions
	final void initController(Request req, Response res)
	{
		this.req = req;
		this.res = res;
	}

	public abstract void generatePage() throws Exception;

	final void finalizePageGeneration() throws Exception
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

		for( View v : output )
			page += v.get();

		page += "</body>\n";
		page += "</html>\n";

		res().body( page );
	}

	final void deinitController()
	{
	}
}
