package pp;

import static spark.Spark.*;

public class PrimeProject
{
	public static void main(String[] args)
	{
		port(8080);
		get("/", (req, res) -> "Hello Whirled Wide Web!");
	}
}
