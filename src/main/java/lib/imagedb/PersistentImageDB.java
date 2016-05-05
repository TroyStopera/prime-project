package lib.imagedb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PersistentImageDB implements ImageDB
{
	private static final String IMAGE_DB_DIR = "imagedb";

	private final File dbDir = new File( IMAGE_DB_DIR );

	public PersistentImageDB() throws ImageDBException
	{
		dbDir.mkdirs();

		if( !dbDir.isDirectory() || !dbDir.canRead() || !dbDir.canWrite() )
			throw new ImageDBException("Need access to the DB dir");
	}

	@Override
	public void put(long imageId, BufferedImage img) throws ImageDBException
	{
		File f = getImageFile(imageId);

		try( FileOutputStream fos = new FileOutputStream(f) )
		{
			fos.write( getImageData(img) );
		}
		catch(IOException e)
		{
			throw new ImageDBException(e);
		}
	}

	@Override
	public String getImageType(long imageId) throws ImageDBException
	{
		if( getImageFile(imageId).exists() )
			return "text/png";
		else
			return null;
	}

	@Override
	public byte[] getImageData(long imageId) throws ImageDBException
	{
		File f = getImageFile(imageId);

		if( !f.exists() )
			return null;

		try( FileInputStream fis = new FileInputStream(f) )
		{
			byte[] data = new byte[ (int)f.length() ];

			for( int pos = 0; pos < data.length; )
			{
				int bytesRead = fis.read( data, pos, data.length - pos );
				if( bytesRead == -1 )
					break;

				pos += bytesRead;
			}

			return data;
		}
		catch(IOException e)
		{
			throw new ImageDBException(e);
		}
	}

	private File getImageFile(long imageId)
	{
		return new File( dbDir, imageId+ ".png" );
	}

	private static byte[] getImageData(BufferedImage img)
	{
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

		return imageData;
	}
}
