package de.lennartmeinhardt.android.moiree.imaging;//package de.lennartmeinhardt.android.moiree.images;
//
//import static android.graphics.Color.BLACK;
//import static android.graphics.Color.TRANSPARENT;
//
//import java.security.acl.Group;
//import java.util.Random;
//
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.media.Image;
//
//public class MoireeImageCreator {
//
//	/**
//	 * Draw the horizontal lines moiree image with given pixel size.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param size
//	 *            the pixel size
//	 */
//	public static void drawHorizontalLinesToImage(Bitmap image, int size) {
//		double width = image.getWidth();
//		double height = image.getHeight();
//
//		int argb = 0;
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				if ((y / size) % 2 == 0)
//					argb = BLACK;
//				else
//					argb = TRANSPARENT;
//
//				image.setPixel(x, y, argb);
//			}
//		}
//	}
//
//	/**
//	 * Draw the horizontal lines moiree image with given image settings.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param settings
//	 *            the image settings
//	 */
//	public static void drawHorizontalLinesToImage(Bitmap image, MoireeImageParameters settings) {
//		drawHorizontalLinesToImage(image, settings.getPixelSize());
//	}
//
//	/**
//	 * Draw the triangles moiree image with given pixel size.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param size
//	 *            the pixel size
//	 */
//	public static void drawTrianglesToImage(Bitmap image, double size) {
//		double width = image.getWidth();
//		double height = image.getHeight();
//
//		size *= 2;
//
//		Polygon topLeftTriangle = createTriangle(0, 0, size);
//		Polygon topRightTriangle = createTriangle(1, 0, size);
//		Polygon bottomLeftTriangle = createTriangle(-.5, 1, size);
//		Polygon bottomCenterTriangle = createTriangle(.5, 1, size);
//		Polygon bottomRightTriangle = createTriangle(1.5, 1, size);
//
//		Group patternCell = new Group(topLeftTriangle, topRightTriangle, bottomLeftTriangle, bottomCenterTriangle,
//				bottomRightTriangle);
//		Rectangle bounds2 = new Rectangle(2 * size * Math.sqrt(1.25), 2 * size);
//		patternCell.setClip(bounds2);
//
//		SnapshotParameters params = new SnapshotParameters();
//		params.setFill(Color.TRANSPARENT);
//		Image pattern = patternCell.snapshot(params, null);
//
//		ImagePattern imagePattern = new ImagePattern(pattern, 0, 0, size * 2 * Math.sqrt(1.25), size * 2, false);
//
//		Rectangle bounds = new Rectangle(width, height);
//		bounds.setFill(imagePattern);
//		bounds.snapshot(params, image);
//	}
//
//	/**
//	 * Draw the triangles moiree image with given image settings.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param settings
//	 *            the image settings
//	 */
//	public static void drawTrianglesToImage(Bitmap image, MoireeImageParameters settings) {
//		drawTrianglesToImage(image, settings.getPixelSize());
//	}
//
//	/**
//	 * Create a isosceles triangle with given height and translations.
//	 * Translations are given relatively. Translation x is relative to triangle
//	 * width, y is relative to triangle height.
//	 * 
//	 * @param translateXrel
//	 *            the triangle's x translation, to be scaled by width
//	 * @param translateYrel
//	 *            the triangle's y translation, to be scaled by height
//	 * @param height
//	 *            the triangle height
//	 * @return triangle as {@link Polygon} object
//	 */
//	private static Polygon createTriangle(double translateXrel, double translateYrel, double height) {
//		double triangleSideLength = height * Math.sqrt(1.25);
//		Polygon triangle = new Polygon(triangleSideLength / 2, 0, 0, height, triangleSideLength, height);
//		triangle.setTranslationX(translateXrel * triangleSideLength);
//		triangle.setTranslationY(translateYrel * height);
//		triangle.setFill(Color.BLACK);
//		return triangle;
//	}
//
//	/**
//	 * Draw the diagonal lines moiree image with given pixel size.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param size
//	 *            the pixel size
//	 */
//	public static void drawDiagonalLinesToImage(Bitmap image, int size) {
//		double width = image.getWidth();
//		double height = image.getHeight();
//
//		int argb = 0;
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				if (((x + y) / size) % 2 == 0)
//					argb = BLACK;
//				else
//					argb = TRANSPARENT;
//
//				image.setPixel(x, y, argb);
//			}
//		}
//	}
//
//	/**
//	 * Draw the diagonal lines moiree image with given image settings.
//	 * 
//	 * @param image
//	 *            the image to write to
//	 * @param settings
//	 *            the image settings
//	 */
//	public static void drawDiagonalLinesToImage(Bitmap image, MoireeImageParameters settings) {
//		drawDiagonalLinesToImage(image, settings.getPixelSize());
//	}
//
//	// no instances
//	private MoireeImageCreator() {
//	}
//}
