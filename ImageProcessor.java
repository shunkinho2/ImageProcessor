import java.awt.*;
//import java.awt.event.*;
import java.awt.Color;
//import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
// import javax.swing.*;
import java.util.*;

public class ImageProcessor {
	
	// Create a clone of a buffered image
	// (The BufferedImage class describes an Image with an accessible buffer of image data.)
	public static BufferedImage copy(BufferedImage img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bi;
	}

	// Create a clone of a buffered image
	// (Another implementation)
/*
	public static BufferedImage copy(BufferedImage img) {
		 ColorModel cm = img.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = img.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
*/
	
	// Convert an input color image to grayscale image
	public static BufferedImage convertToGrayScale(BufferedImage src) {
		// Make a copy of the source image as the target image
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		// Scan through each row of the image
		for (int j = 0; j < height; j++) {
			// Scan through each column of the image
			for (int i = 0; i < width; i++) {
				// Get an integer pixel in the default RGB color model
				int pixel = target.getRGB(i, j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed(); 	// get red value
				int green = oldColor.getGreen();	// get green value
				int blue = oldColor.getBlue(); 	// get blue value

				// Convert RGB to grayscale using formula
				// gray = 0.299 * R + 0.587 * G + 0.114 * B
				double grayVal = 0.299 * red + 0.587 * green + 0.114 * blue;

				// Assign each channel of RGB with the same value
				Color newColor = new Color((int) grayVal, (int) grayVal, (int) grayVal);

				// Get back the integer representation of RGB color and assign it back to the original position
				target.setRGB(i, j, newColor.getRGB());
			}
		}
		// return the resulting image in BufferedImage type
		return target;
	}

	// Invert the color of an input image
	public static BufferedImage invertColor(BufferedImage src) {
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = target.getRGB(i, j);
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();
				
				// invert the color of each channel
				Color newColor = new Color(255 - red, 255 - green, 255 - blue);
				
				target.setRGB(i, j, newColor.getRGB());
			}
		}
		return target;
	}

	// Adjust the brightness of an input image
	public static BufferedImage adjustBrightness(BufferedImage src, int amount) {
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = target.getRGB(i, j);
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();

				int newRed = (red + amount > 255) ? 255 : red + amount;
				int newGreen = (green + amount > 255) ? 255 : green + amount;
				int newBlue = (blue + amount > 255) ? 255 : blue + amount;

				newRed = (newRed < 0) ? 0 : newRed;
				newGreen = (newGreen < 0) ? 0 : newGreen;
				newBlue = (newBlue < 0) ? 0 : newBlue;

				Color newColor = new Color(newRed, newGreen, newBlue);

				target.setRGB(i, j, newColor.getRGB());
			}
		}
		return target;
	}
	
	// Apply a blur effect to an input image by random pixel movement
	public static BufferedImage blur(BufferedImage src, int offset) {
		
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		Random generator = new Random();
		
		BufferedImage target = copy(src);
		
		for (int y = 0; y < sHeight; y++) {
			
			for (int x = 0; x < sWidth; x++) {
				
				int range=generator.nextInt(offset);
				int offsetX=range-offset/2;
				int offsetY=range-offset/2;
				int xnew=x-offsetX;
				int ynew=y-offsetY;
				
				if (xnew<sWidth&&ynew<sHeight&&xnew>=0&&ynew>=0)
					{
						int pixel = src.getRGB(xnew,ynew);
						target.setRGB(x, y, pixel);
					}
			}
		}
		return target;
	}

	// Scale (resize) an image
	public static BufferedImage scale(BufferedImage src, int tWidth, int tHeight) {

		int imageType=src.getType();
		
		BufferedImage target = new BufferedImage(tWidth, tHeight, imageType);
		
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		
		float widthScalingFactor = (float)sWidth / (float)tWidth;
		float heightScalingFactor = (float)sHeight / (float)tHeight;
		for (int j = 0; j < tHeight; j++) {
			for (int i = 0; i < tWidth; i++) {
				
				int src_i = (int)(i * widthScalingFactor);
				int src_j = (int)(j * heightScalingFactor);
				
				int pixel = src.getRGB(src_i, src_j);
				target.setRGB(i, j, pixel);
			}
		}

        /*BufferedImage target = new BufferedImage(tWidth,tHeight, src.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = target.createGraphics();
        g2d.drawImage(src, 0, 0, tWidth, tHeight, null);
        g2d.dispose();*/
		return target; 
	}
	
	// Rotate an image by angle degrees clockwise
	public static BufferedImage rotate(BufferedImage src, double angle) {

		int imageType=src.getType();
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		
		BufferedImage target = new BufferedImage(sWidth,sHeight,imageType);
		int cj = sHeight/2;
		
		double theta = Math.toRadians(angle);
		double cos=Math.cos(-theta);
		double sin=Math.sin(-theta);
		
		for (int sj = 0; sj < sHeight; sj++) {
			
			for (int si = 0; si < sWidth; si++) {
				
				int ti=(int)((si-cj)*cos-(sj-cj)*sin+cj);
				int tj=(int)((si-cj)*sin+(sj-cj)*cos+cj);
				
				if (ti<sWidth&&tj<sHeight&&ti>=0&&tj>=0)
					{
						int pixel = src.getRGB(ti,tj);
						target.setRGB(si, sj, pixel);
					}
			}
		}
		return target; 

		/*double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage target = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate(0, 0);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(src, 0, 0, null);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();*/
		}
	
	// Apply a swirl effect to an input image
	public static BufferedImage swirl(BufferedImage src, double degree) {
		
		int imageType=src.getType();
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		double midX = sWidth/2;
		double midY = sHeight/2;
		
		BufferedImage target = new BufferedImage(sWidth,sHeight,imageType);
		
		for (int ynew = 0; ynew < sHeight; ynew++) {
			
			for (int xnew = 0; xnew < sWidth; xnew++) {
				
				double dx=xnew-midX;
				double dy=ynew-midY;
				double theta=Math.atan2(dy,dx);
				double x2=Math.pow(dx,2),y2=Math.pow(dy, 2);
				double radius=Math.sqrt(x2+y2);
				int x = (int)(midX+radius*Math.cos(theta+degree*radius));
				int y = (int)(midY+radius*Math.sin(theta+degree*radius));
				
				if (x<sWidth&&y<sHeight&&x>=0&&y>=0)
				{
					int pixel = src.getRGB(x,y);
					target.setRGB(xnew, ynew, pixel);
				}
			}
		}
		return target; 
	}

	// Apply an effect to preserve partial colors of an image 
	public static BufferedImage preserveColor(BufferedImage src, boolean[][] mask, int colorVal, 
			int rgValue, int gbValue, int brValue) {
				
		int imageType = src.getType();
		int sHeight = src.getHeight();
		int sWidth = src.getWidth();
		
		BufferedImage target = new BufferedImage(sWidth,sHeight,imageType);
		Color ccv = new Color(colorVal);
		
		int redP = ccv.getRed();
		int greenP = ccv.getGreen();
		int blueP = ccv.getBlue();
		int diffRG = redP - greenP;
		int diffGB = greenP - blueP;
		int diffBR = blueP - redP;
		
		int RGlow = diffRG - rgValue;
		int RGhigh = diffRG + rgValue;
		int GBlow = diffGB - gbValue;
		int GBhigh = diffGB + gbValue;
		int BRlow = diffBR - brValue;
		int BRhigh = diffBR + brValue;
		
		for (int y = 0; y < sHeight ; y++) {
			for (int x = 0; x < sWidth ; x++) {
				
				int pixel = src.getRGB( x, y);
				
				Color oldColor = new Color(pixel);
				Color newColor = new Color(pixel);
				
				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue(); 
				
				if ((red - green > RGlow) && (red - green < RGhigh) && 
					(green - blue > GBlow) && (green - blue < GBhigh) && 
					(blue - red > BRlow) && (blue - red < BRhigh) ) {
					newColor = new Color(red,green,blue);
					mask[x][y]=true;
				}
				else{
					double grayVal = 0.299 * red + 0.587 * green + 0.114 * blue;
					newColor = new Color((int) (grayVal), (int) (grayVal), (int) (grayVal));
				}
				target.setRGB(x, y, newColor.getRGB());
			}
		}
		return target; 
	}

	// Perform edge detection for an input image

	public static BufferedImage detectEdges(BufferedImage src) {
		
			int[][] filter1 = {
				{ -1,  0,  1 },
				{ -2,  0,  2 },
				{ -1,  0,  1 }
			};

			int[][] filter2 = {
				{  1,  2,  1 },
				{  0,  0,  0 },
				{ -1, -2, -1 }
			};

			int imageType = src.getType();
			int width = src.getWidth();
			int height = src.getHeight();
			BufferedImage target = new BufferedImage(width, height,imageType);
			
			for (int y = 1; y < height - 1; y++) {
				for (int x = 1; x < width - 1; x++) {
					int[][] gray = new int[3][3];
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							int pixel = src.getRGB(x-1+i, y-1+j); 
							Color oldColor = new Color(pixel);
							
							int red = oldColor.getRed();
							int green = oldColor.getGreen();
							int blue = oldColor.getBlue();
							
							double grayVal = 0.299*red + 0.587*green + 0.114*blue;
							gray[i][j] = (int) grayVal;
						}
					}
					
					int gray1 = 0, gray2 = 0;
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							gray1 += gray[i][j] * filter1[i][j];
							gray2 += gray[i][j] * filter2[i][j];
						}
					}
					
					int orginal=0;
					int truncate = (int)(Math.sqrt(gray1*gray1 + gray2*gray2));
					
					if (truncate <   0) {
						orginal = 0;
						}
					else if (truncate > 255) {
						orginal = 255;
						}
					else{
						orginal = truncate;
						}
					
					int magnitude = 255 - orginal;
					Color grayscale = new Color(magnitude, magnitude, magnitude);
					target.setRGB(x, y, grayscale.getRGB());
				}
			}
		return target; 
	}
	//photomosaics functions {not yet complete}
	public static BufferedImage photomosaics(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage target=copy(src);
		BufferedImage img = null;
		int newred=0,newgreen=0,newblue=0;
		int []averageRed = new int[200];
		int []averageGreen = new int[200];
		int []averageBlue = new int[200];
		BufferedImage []newimg = new BufferedImage[10000];
		for (int i=1;i<71;i++) {
			try {
				img = ImageIO.read(new File("./images/"+i+".jpg"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				img = ImageIO.read(new File("./images/"+i+".png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			newimg[i]=scale (img,10,10);
			for (int j=0;j<10;j++) {
				for (int k=0;k<10;k++) {
					// Get an integer pixel in the default RGB color model
					int pixel = newimg[i].getRGB(k, j);
					// Convert the single integer pixel value to RGB color
					Color oldColor = new Color(pixel);
					
					int red = oldColor.getRed(); 	// get red value
					int green = oldColor.getGreen();	// get green value
					int blue = oldColor.getBlue(); 	// get blue value
					
					newred =red+newred;
					newgreen =green+newgreen;
					newblue=blue+newblue;
					
				}
			}

			
			averageRed[i]=newred/(10*10);
			averageGreen[i]=newgreen/(10*10);
			averageBlue[i]=newblue/(10*10);
		}
		for (int l=0;l<height;l++) {
			for (int m=0;m<width;m++) {
				// Get an integer pixel in the default RGB color model
				int srcPixel = src.getRGB(m, l);
				// Convert the single integer pixel value to RGB color
				Color oldSRCColor = new Color(srcPixel);
				
				int SRCred = oldSRCColor.getRed(); 	// get red value
				int SRCgreen = oldSRCColor.getGreen(); 	// get green value
				int SRCblue = oldSRCColor.getBlue(); 	// get blue value
				int []compareRed=new int[200];
				int []compareGreen=new int[200];
				int []compareBlue=new int[200];
				int get=0;
				compareRed[0]=255;compareGreen[0]=255;compareBlue[0]=255;
				for (int i=1;i<71;i++) {
					compareRed[i]	=Math.abs(SRCred	-averageRed[i]);
					compareGreen[i]=Math.abs(SRCgreen	-averageGreen[i]);
					compareBlue[i]	=Math.abs(SRCblue	-averageBlue[i]);
					if (compareRed[i]+compareGreen[i]+compareBlue[i]<=compareRed[i-1]+compareGreen[i-1]+compareRed[i-1]) {
						get=i;
					}
				}
				img=newimg[get];
				for (int j=0;j<10;j++) {
					for (int i=0;i<10;i++) {
					int pixel = img.getRGB(i,j);
					target.setRGB(i*m, j*l, pixel);	
					}
				}
				
			}
		}
		return target; 
	}
}