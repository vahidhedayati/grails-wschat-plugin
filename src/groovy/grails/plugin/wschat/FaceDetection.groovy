package grails.plugin.wschat

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

class FaceDetection {
	/*
	private static final String CASCADE_FILE = "resources/haarcascade_frontalface_alt.xml";
	
	   private int minsize = 20;
	   private int group = 0;
	   private double scale = 1.1;
	*/
	   /**
		* Based on FaceDetection example from JavaCV.
		*/
	/*
	   public byte[] convert(byte[] imageData) throws IOException {
		   // create image from supplied bytearray
		   IplImage originalImage = cvDecodeImage(cvMat(1, imageData.length,CV_8UC1, new BytePointer(imageData)))
	
		   // Convert to grayscale for recognition
		   IplImage grayImage = IplImage.create(originalImage.width(), originalImage.height(), IPL_DEPTH_8U, 1)
		   cvCvtColor(originalImage, grayImage, CV_BGR2GRAY)
	
		   // storage is needed to store information during detection
		   CvMemStorage storage = CvMemStorage.create()
	
		   // Configuration to use in analysis
		   CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE))
	
		   // We detect the faces.
		   CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, scale, group, minsize)
	
		   // We iterate over the discovered faces and draw yellow rectangles around them.
		   for (int i = 0; i < faces.total(); i++) {
			   CvRect r = new CvRect(cvGetSeqElem(faces, i))
			   cvRectangle(originalImage, cvPoint(r.x(), r.y()),
					   cvPoint(r.x() + r.width(), r.y() + r.height()),
					   CvScalar.YELLOW, 1, CV_AA, 0)
		   }
	
		   // convert the resulting image back to an array
		   ByteArrayOutputStream bout = new ByteArrayOutputStream()
		   BufferedImage imgb = originalImage.getBufferedImage()
		   ImageIO.write(imgb, "png", bout)
		   return bout.toByteArray()
	   }
	   */
}
