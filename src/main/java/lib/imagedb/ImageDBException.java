package lib.imagedb;

public class ImageDBException extends RuntimeException
{
	public ImageDBException(String str)
	{
		super(str);
	}

	public ImageDBException(Throwable t)
	{
		super(t);
	}
}
