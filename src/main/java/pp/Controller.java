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
	protected Request req;
	protected Response res;

	//helper functions
	protected final Request req() { return req; }
	protected final Response res() { return res; }

	//lifecycle functions
	final void initController(Request req, Response res)
	{
		this.req = req;
		this.res = res;
	}

	public abstract void executeController() throws Exception;

	final void deinitController()
	{
	}
}
