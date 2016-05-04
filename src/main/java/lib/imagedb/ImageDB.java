package lib.imagedb;

import java.awt.image.BufferedImage;

public interface ImageDB
{
	/**
	 * Adds the given image to the DB
	 * @param imageId the ID of the image
	 * @param img the image
	 * @throws ImageDBException
	 */
	void put(long imageId, BufferedImage img) throws ImageDBException;

	/**
	 * @param imageId the ID of the image
	 * @return the mime type of the given image, or null if there is no image with that ID
	 * @throws ImageDBException
	 */
	String getImageType(long imageId) throws ImageDBException;

	/**
	 * @param imageId the ID of the image
	 * @return the image as a byte array, or null if there is no image with that ID
	 * @throws ImageDBException
	 */
	byte[] getImageData(long imageId) throws ImageDBException;
}