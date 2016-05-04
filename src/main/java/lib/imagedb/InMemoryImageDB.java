package lib.imagedb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryImageDB implements ImageDB
{
	private final ConcurrentMap<Long, byte[]> imageMap = new ConcurrentHashMap<>();

	@Override
	public void put(long imageId, BufferedImage img) throws ImageDBException
	{
		//write the image as a .png to a byte array
		byte[] imageData;
		try
		{
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ImageIO.write(img, "png", byteOut);
			imageData = byteOut.toByteArray();
		}
		catch( IOException e )
		{
			//This exception should never happen; ByteArrayOutputStream doesn't throw IOExceptions
			assert false;
			throw new ImageDBException(e);
		}

		//add the image to the map
		imageMap.put( imageId, imageData );
	}

	@Override
	public String getImageType(long imageId) throws ImageDBException
	{
		if( imageMap.containsKey(imageId) )
			return "image/png";

		return null;
	}

	@Override
	public byte[] getImageData(long imageId) throws ImageDBException
	{
		return imageMap.get(imageId);
	}
}
