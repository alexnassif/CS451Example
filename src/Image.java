/*******************************************************
 CS451 Multimedia Software Systems
 @ Author: Elaine Kang

 This image class is for a 24bit RGB image only.
 *******************************************************/

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Image
{
  private int width;				// number of columns
  private int height;				// number of rows
  private int pixelDepth=3;			// pixel depth in byte
  BufferedImage img;
  private Map<Integer, int[]> map;// image array to store rgb values, 8 bits per channel
  private int K;
  int heightResize;
  int widthResize;
  private double[][] Y;
  private double[][] Cb;
  private double[][] Cr;
  private double[][] Cb1;
  private double[][] Cr1;
  int newHeightResize;
  int newWidthResize;
  
  public void readJPEG(String filename){
	  
	  
	  try {
	      img = ImageIO.read(new File(filename));
	      width = img.getWidth();
	      height = img.getHeight();
	      
	      
	      
	      
	  } catch (IOException e) {
	  }
	  
	  
	  
  }
  
  public void write2JPEG(String filename)
  // wrrite the image data in img to a PPM file
  {
	  
			  File f = new File(filename);
			  try {
				ImageIO.write(img, "JPEG", f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		System.out.println("Wrote into "+filename+" Successfully.");

	} // try
	
  
  
  public Image(String fileName, boolean s)
  // Create an image and read the data from the file
  {
	  readJPEG(fileName);
	  System.out.println("Created an image from " + fileName+ " with size "+width+"x"+height);
  }
  
  public Image(int w, int h)
  // create an empty image with width and height
  {
	width = w;
	height = h;

	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	System.out.println("Created an empty image with size " + width + "x" + height);
  }
  
  public Image(int K)
  // create an empty image with width and height
  {
	width = 512;
	height = 512;
	this.K = K;
	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	System.out.println("Created an empty image with size " + width + "x" + height);
  }
  public Image(int w, int h, int K)
  // create an empty image with width and height
  {
	width = w/K;
	height = h/K;
	this.K = K;
	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	System.out.println("Created an empty image with size " + width + "x" + height);
  }

  public Image(String fileName)
  // Create an image and read the data from the file
  {
	  readPPM(fileName);
	  System.out.println("Created an image from " + fileName+ " with size "+width+"x"+height);
  }
  
  public Image(String fileName, int hw)
  // Create an image and read the data from the file
  {
	  readPPMDCT(fileName);
	  System.out.println("Created an image from " + fileName+ " with size "+width+"x"+height);
  }


  public int getW()
  {
	return width;
  }

  public int getH()
  {
	return height;
  }

  public int getSize()
  // return the image size in byte
  {
	return width*height*pixelDepth;
  }

  public void setPixel(int x, int y, byte[] rgb)
  // set rgb values at (x,y)
  {
	int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
	img.setRGB(x,y,pix);
	
  }

  public void setPixel(int x, int y, int[] irgb)
  // set rgb values at (x,y)
  {
	byte[] rgb = new byte[3];

	for(int i=0;i<3;i++)
	  rgb[i] = (byte) irgb[i];

	setPixel(x,y,rgb);
  }

  public void getPixel(int x, int y, byte[] rgb)
  // retreive rgb values at (x,y) and store in the array
  {
  	int pix = img.getRGB(x,y);

  	rgb[2] = (byte) pix;
  	rgb[1] = (byte)(pix>>8);
  	rgb[0] = (byte)(pix>>16);
  }


  public void getPixel(int x, int y, int[] rgb)
  // retreive rgb values at (x,y) and store in the array
  {
	int pix = img.getRGB(x,y);

	byte b = (byte) pix;
	byte g = (byte)(pix>>8);
	byte r = (byte)(pix>>16);

    // converts singed byte value (~128-127) to unsigned byte value (0~255)
	rgb[0]= (int) (0xFF & r);
	rgb[1]= (int) (0xFF & g);
	rgb[2]= (int) (0xFF & b);
  }

  public void displayPixelValue(int x, int y)
  // Display rgb pixel value at (x,y)
  {
	int pix = img.getRGB(x,y);

	byte b = (byte) pix;
	byte g = (byte)(pix>>8);
	byte r = (byte)(pix>>16);

    System.out.println("RGB Pixel value at ("+x+","+y+"):"+(0xFF & r)+","+(0xFF & g)+","+(0xFF & b));
   }

  public void readPPM(String fileName)
  // read a data from a PPM file
  {
	FileInputStream fis = null;
	DataInputStream dis = null;

	try{
		fis = new FileInputStream(fileName);
		dis = new DataInputStream(fis);

		System.out.println("Reading "+fileName+"...");

		// read Identifier
		if(!dis.readLine().equals("P6"))
		{
			System.err.println("This is NOT P6 PPM. Wrong Format.");
			System.exit(0);
		}

		// read Comment line
		String commentString = dis.readLine();

		// read width & height
		String[] WidthHeight = dis.readLine().split(" ");
		width = Integer.parseInt(WidthHeight[0]);
		height = Integer.parseInt(WidthHeight[1]);

		// read maximum value
		int maxVal = Integer.parseInt(dis.readLine());

		if(maxVal != 255)
		{
			System.err.println("Max val is not 255");
			System.exit(0);
		}

		// read binary data byte by byte
		int x,y;
		//fBuffer = new Pixel[height][width];
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		byte[] rgb = new byte[3];
		int pix;

		for(y=0;y<height;y++)
		{
	  		for(x=0;x<width;x++)
			{
				rgb[0] = dis.readByte();
				rgb[1] = dis.readByte();
				rgb[2] = dis.readByte();
				setPixel(x, y, rgb);
			}
		}
		dis.close();
		fis.close();

		System.out.println("Read "+fileName+" Successfully.");

	} // try
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
  }
  public void readPPMDCT(String fileName)
  // read a data from a PPM file
  {
	FileInputStream fis = null;
	DataInputStream dis = null;

	try{
		fis = new FileInputStream(fileName);
		dis = new DataInputStream(fis);

		System.out.println("Reading "+fileName+"...");

		// read Identifier
		if(!dis.readLine().equals("P6"))
		{
			System.err.println("This is NOT P6 PPM. Wrong Format.");
			System.exit(0);
		}

		// read Comment line
		String commentString = dis.readLine();

		// read width & height
		String[] WidthHeight = dis.readLine().split(" ");
		width = Integer.parseInt(WidthHeight[0]);
		height = Integer.parseInt(WidthHeight[1]);
		this.heightResize = height;
		this.widthResize = width;
		
		  
		  if(this.height % 8.0 != 0)
		  {
			  heightResize = (int)Math.ceil(this.height/ 8.0) * 8;
			  
		  }
		  
		  if(this.width % 8.0 != 0)
		  {
			  widthResize = (int)Math.ceil(this.width/ 8.0) * 8;
			  
		  }

		// read maximum value
		int maxVal = Integer.parseInt(dis.readLine());

		if(maxVal != 255)
		{
			System.err.println("Max val is not 255");
			System.exit(0);
		}

		// read binary data byte by byte
		int x,y;
		//fBuffer = new Pixel[height][width];
		img = new BufferedImage(widthResize, heightResize, BufferedImage.TYPE_INT_RGB);
		byte[] rgb = new byte[3];
		int pix;

		for(y=0;y<heightResize;y++)
		{
	  		for(x=0;x<widthResize;x++)
			{
	  			if(y < height && x < width){
				rgb[0] = dis.readByte();
				rgb[1] = dis.readByte();
				rgb[2] = dis.readByte();
				setPixel(x, y, rgb);}
	  			
	  			else{
	  				
	  				rgb[0] = 0;
					rgb[1] = 0;
					rgb[2] = 0;
					setPixel(x, y, rgb);
	  				
	  			}
			}
		}

		
		
		dis.close();
		fis.close();

		System.out.println("Read "+fileName+" Successfully.");

	} // try
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
  }
  
	public void addPadding() {
		this.heightResize = height;
		this.widthResize = width;
		
		
		

		if (this.height % 8.0 != 0) {
			heightResize = (int) Math.ceil(this.height / 8.0) * 8;

		}

		if (this.width % 8.0 != 0) {
			widthResize = (int) Math.ceil(this.width / 8.0) * 8;

		}
		
		Y = new double[heightResize][widthResize];
		Cb = new double[heightResize][widthResize];
		Cr = new double[heightResize][widthResize];
		
		int x = 0;
		int y = 0;
		int[] rgb1 = new int[3];

		for (y = 0; y < heightResize; y++) {
			for (x = 0; x < widthResize; x++) {

				if (y < height && x < width) {
					getPixel(x, y, rgb1);
					Y[y][x] = rgb1[0];
					Cb[y][x] = rgb1[1];
					Cr[y][x] = rgb1[2];
				} else {
					Y[y][x] = 0;
					Cb[y][x] = 0;
					Cr[y][x] = 0;

				}
			}
		}

	}
	
	public Image arrayToImage(){
		Image image = new Image(widthResize, heightResize);
		int x = 0;
		int y = 0;
		int[] rgb1 = new int[3];

		for (y = 0; y < heightResize; y++) {
			for (x = 0; x < widthResize; x++) {
				rgb1[0] = (int)Y[y][x];
				if(y < newHeightResize && x < newWidthResize){
					
					rgb1[1] = (int)Cb1[y][x];
					rgb1[2] = (int)Cr1[y][x];
					}
				else{
					
					rgb1[1] = 0;
					rgb1[2] = 0;
					
				}
				image.setPixel(x, y, rgb1);
				
			}
		}
		return image;
	}

  public Image removePadding(){
	  
	  
	  Image removePadding = new Image(width, height);
	  
	  int x,y;
		//fBuffer = new Pixel[height][width];
		
		int[] rgb = new int[3];
		int pix;

		for(y=0;y<height;y++)
		{
	  		for(x=0;x<width;x++)
			{

	  			rgb[0] = (int)Y[y][x];
	  			rgb[1] = (int)Cb[y][x];
	  			rgb[2] = (int)Cr[y][x];
	  			
	  			removePadding.setPixel(x, y, rgb);
			}
		}
		
		return removePadding;
	  
  }
  
  
  public void cpTransform(){
	  
	  int x, y;
	  
	  for(y=0;y<heightResize;y++)
	  {
		  for(x=0;x<widthResize;x++)
		  {
				
				
				double yy  = ( 0.299   * Y[y][x] + 0.587   * Cb[y][x] + 0.114   * Cr[y][x]);
				if(yy > 255)
					yy = 255;
				else if (yy<0)
					yy = 0;
				double cb = (-0.16874 * Y[y][x] - 0.33126 * Cb[y][x] + 0.50000 * Cr[y][x]);
				if(cb > 127.5)
					cb = 127.5;
				else if (cb < -127.5)
					cb = -127.5;
				double cr = ( 0.50000 * Y[y][x] - 0.41869 * Cb[y][x] - 0.08131 * Cr[y][x]);
				if(cr > 127.5)
					cr = 127.5;
				else if (cb < -127.5)
					cr = -127.5;
				
				Y[y][x] = (int)(yy - 128);
				Cb[y][x] = (int)(cb - .5);
				Cr[y][x] = (int)(cr - .5);
				
				

		  }
			
	  }
	  
  }
  
	public void subSampleCrCb() {
		
		newHeightResize = heightResize/2;
		newWidthResize = widthResize / 2;
		
		//System.out.println("new height  and new width " + newHeightResize + " " + newWidthResize);
		
		if ((this.heightResize/2) % 8.0 != 0) {
			newHeightResize = (int) Math.ceil((heightResize/2) / 8.0) * 8;

		}

		if ((widthResize / 2) % 8.0 != 0) {
			newWidthResize = (int) Math.ceil((widthResize / 2) / 8.0) * 8;

		}
		
		
		System.out.println("new height  and new width " + newHeightResize + " " + newWidthResize);
		Cb1 = new double[newHeightResize][newWidthResize];
		Cr1 = new double[newHeightResize][newWidthResize];

		for (int y = 0; y < heightResize - 1; y += 2) {
			

			for (int x = 0; x < widthResize - 1; x += 2) {
				double sumCR = 0;
				double sumCB = 0;
				sumCR += (Cr[y][x] + Cr[y][x + 1] + Cr[y + 1][x]
						+ Cr[y + 1][x + 1])/4.0;
				sumCB += (Cb[y][x] + Cb[y][x + 1] + Cb[y + 1][x]
						+ Cb[y + 1][x + 1])/4.0;

				Cr1[y / 2][x / 2] = sumCR;
				Cb1[y / 2][x / 2] = sumCB;

			}

		}
		
		
	}
 
	public void superSample() {

		double[][] cb1 = new double[heightResize][widthResize];
		double[][] cr1 = new double[heightResize][widthResize];

		for (int y = 0; y < heightResize; y+=2) {
			for (int x = 0; x < widthResize; x+=2) {

				cb1[y][x] = Cb1[y/2][x/2];
				cb1[y][x + 1] = Cb1[y/2][x/2];
				cb1[y + 1][x] = Cb1[y/2][x/2];
				cb1[y + 1][x + 1] = Cb1[y/2][x/2];
				cr1[y][x] = Cr1[y/2][x/2];
				cr1[y][x + 1] = Cr1[y/2][x/2];
				cr1[y + 1][x] = Cr1[y/2][x/2];
				cr1[y + 1][x + 1] = Cr1[y/2][x/2];

			}

		}
		
		

		for (int y = 0; y < heightResize; y++) {
			for (int x = 0; x < widthResize; x++) {
				
				Y[y][x] += 128;
				cb1[y][x] += .5;
				cr1[y][x] += .5;
				double R = ((1.0000 * Y[y][x]) + (0 * cb1[y][x]) + (1.4020 * cr1[y][x]));
				if (R > 255)
					R = 255;
				else if (R < 0)
					R = 0;
				
				double G = ((1.0000 * Y[y][x]) + (-.3441 * cb1[y][x]) + (-.7141
						* cr1[y][x]));
				if (G > 255)
					G = 255;
				else if (G < 0)
					G = 0;
				
				double B = ((1.0000 * Y[y][x]) + (1.7720 * cb1[y][x]) + (0 * cr1[y][x]));
				if (B > 255)
					B = 255;
				else if (B < 0)
					B = 0;
				
				Y[y][x] = R;
				Cb[y][x] = G;
				Cr[y][x] = B;

			}

		}

		
	}
	
	public double[][] DCT(double[][] input){
		  
		  double[][] coefficients = new double[8][8];
		  
		  for(int u = 0; u < 8; u++){
			  for(int v = 0; v < 8; v++){
				  
				  double sum = 0;
				  for(int x = 0; x < 8; x++){
					  for(int y = 0; y < 8; y++){
						  
						  sum += input[x][y] * (Math.cos((((2.0 * x) + 1) * v * Math.PI)/16)*Math.cos((((2.0 * y) + 1) * u * Math.PI)/16));
							  
					  }
				  }
				  double value;
					if (u == 0 && v == 0) {

						value = sum * (1.0 / 4) * (1/Math.sqrt(2))
								* (1/Math.sqrt(2));

						if (value > Math.pow(2, 10))
							value = Math.pow(2, 10);
						else if (value < -1024)
							value = -1024;

					} else if (u == 0 && v != 0) {
						value = sum * (1.0 / 4) * (1/Math.sqrt(2));
						if (value > Math.pow(2, 10))
							value = Math.pow(2, 10);
						else if (value < -1024)
							value = -1024;

					} else if (v == 0 && u != 0) {
						value = sum * (1.0 / 4) * (1/Math.sqrt(2));
						if (value > Math.pow(2, 10))
							value = Math.pow(2, 10);
						else if (value < -1024)
							value = -1024;

					} else {
						value = sum * (1.0 / 4);
						if (value > Math.pow(2, 10))
							value = Math.pow(2, 10);
						else if (value < -1024)
							value = -1024;

					}
					coefficients[u][v] = value;
				  
			  }
			  
		  }
		  
		  
		  
		  return coefficients;
		  
	  }
	
	public void DCTY() {

		double[][] Yholder = new double[8][8];

		for (int u = 0; u < heightResize; u += 8) {
			for (int v = 0; v < widthResize; v += 8) {
				double [][] dct = new double[8][8];
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						Yholder[i - u][j - v] = Y[i][j];
						
					}
				}
				
				dct = DCT(Yholder);
				
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Y[i + u][j + v] = dct[i][j];
						
					}
				}
				
			
			}

		}

	}

	public void DCTCr() {

		double[][] Crholder = new double[8][8];

		for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				
				
						double [][] dct = new double[8][8];
						for(int i = u; i < u+8; i++){
							for(int j = v; j < v+8; j++){
								
								Crholder[i - u][j - v] = Cr1[i][j];
								
							}
						}
						
						dct = DCT(Crholder);
						
						
						for(int i = 0; i < 8; i++){
							for(int j = 0; j < 8; j++){
								
								Cr1[i + u][j + v] = dct[i][j];
								
							}
						}
				
				
			}

		}

	}

	public void DCTCb() {

		double[][] Cbholder = new double[8][8];

		for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				
						double [][] dct = new double[8][8];
						for(int i = u; i < u+8; i++){
							for(int j = v; j < v+8; j++){
								
								Cbholder[i - u][j - v] = Cb1[i][j];
								
							}
						}
						
						dct = DCT(Cbholder);
						
						
						for(int i = 0; i < 8; i++){
							for(int j = 0; j < 8; j++){
								
								Cb1[i + u][j + v] = dct[i][j];
								
							}
						}
				
				

				
			}

		}

	}

	 public double[][] IDCT(double[][] dct){
		 
		 double[][] inverseDCT = new double[8][8];
		 
		 for(int u = 0; u < 8; u++){
			  for(int v = 0; v < 8; v++){
				  
				  double sum = 0;
				  for(int x = 0; x < 8; x++){
					  for(int y = 0; y < 8; y++){
						  
						  if(x == 0 &&  y == 0)
							  sum +=  (1.0/4) *(1/Math.sqrt(2))*(1/Math.sqrt(2)) * dct[x][y] 
									  * (Math.cos((((2.0 * v) + 1) * x * Math.PI)/16)*Math.cos((((2.0 * u) + 1) * y * Math.PI)/16));
						  else if (x == 0 && y != 0)
							  sum += (1.0/4) *(1/Math.sqrt(2)) * dct[x][y] 
									  * (Math.cos((((2.0 * v) + 1) * x * Math.PI)/16)*Math.cos((((2.0 * u) + 1) * y * Math.PI)/16));
						  else if (y == 0 && x !=0)
							  sum += (1.0/4) *(1/Math.sqrt(2)) * dct[x][y] 
									  * (Math.cos((((2.0 * v) + 1) * x * Math.PI)/16)*Math.cos((((2.0 * u) + 1) * y * Math.PI)/16));
						  else
							  sum += (1.0/4) *dct[x][y] 
									  * (Math.cos((((2.0 * v) + 1) * x * Math.PI)/16)*Math.cos((((2.0 * u) + 1) * y * Math.PI)/16));
							  
							  
					  }
				  }
				  
				  inverseDCT[u][v] = sum;
				  
			  }
			  
		  }
		 
		 
		 
		 
		 
		 return inverseDCT;
		 
	 }
	public void yIDCT() {

		double[][] inverseDCT = new double[8][8];

		for (int u = 0; u < heightResize; u += 8) {
			for (int v = 0; v < widthResize; v += 8) {
				
				double [][] dct = new double[8][8];
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						inverseDCT[i - u][j - v] = Y[i][j];
						
					}
				}
				
				dct = IDCT(inverseDCT);
				
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Y[i + u][j + v] = dct[i][j];
						
					}
				}
			
			}

		}


	}

	public void CrIDCT() {

		double[][] inverseDCT = new double[8][8];

		for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				double [][] dct = new double[8][8];
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						inverseDCT[i - u][j - v] = Cr1[i][j];
						
					}
				}
				
				dct = IDCT(inverseDCT);
				
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Cr1[i + u][j + v] = dct[i][j];
						
					}
				}
				
			}
		}

		

	}

	public void CbIDCT() {

		double[][] inverseDCT = new double[8][8];

		for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				double [][] dct = new double[8][8];
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						inverseDCT[i - u][j - v] = Cb1[i][j];
						
					}
				}
				
				dct = IDCT(inverseDCT);
				
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Cb1[i + u][j + v] = dct[i][j];
						
					}
				}
				
				
			}
		}
		
	}
	
	
	public void quantizeY(int n){
		
		//double[][] inverseDCT = new double[8][8];
		int [][] yTable = {
				{4, 4, 4, 8, 8, 16, 16, 32},
				{4, 4, 4, 8, 8, 16, 16, 32},
				{4, 4, 8, 8, 16, 16, 32, 32},
				{8, 8, 8, 16, 16, 32, 32, 32},
				{8, 8, 16, 16, 32, 32, 32, 32},
				{16, 16, 16, 32, 32, 32, 32, 32},
				{16, 16, 32, 32, 32, 32, 32, 32},
				{32, 32, 32, 32, 32, 32, 32, 32}
		};
		
		
		for (int u = 0; u < heightResize; u += 8) {
			for (int v = 0; v < widthResize; v += 8) {
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Y[i + u][j + v] = Math.round(Y[u + i][v + j]/(yTable[i][j]*Math.pow(2, n)));
						
					}
				}
				
				
				
			}
			}
		
		
	}
	
public void quantizeCr(int n){
		
		//double[][] inverseDCT = new double[8][8];
		int [][] crTable = {
				{8, 8, 8, 16, 32, 32, 32, 32},
				{8, 8, 8, 16, 32, 32, 32, 32},
				{8, 8, 16, 32, 32, 32, 32, 32},
				{16, 16, 32, 32, 32, 32, 32, 32},
				{32, 32, 32, 32, 32, 32, 32, 32},
				{32, 32, 32, 32, 32, 32, 32, 32},
				{32, 32, 32, 32, 32, 32, 32, 32},
				{32, 32, 32, 32, 32, 32, 32, 32}
		};
		
		
		for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						
						Cr1[i + u][j + v] = Math.round(Cr1[u + i][v + j]/(crTable[i][j]*Math.pow(2, n)));
						
					}
				}
				
				
				
			}
			}
		
		
	}

public void quantizeCb(int n){
	
	//double[][] inverseDCT = new double[8][8];
	int [][] cbTable = {
			{8, 8, 8, 16, 32, 32, 32, 32},
			{8, 8, 8, 16, 32, 32, 32, 32},
			{8, 8, 16, 32, 32, 32, 32, 32},
			{16, 16, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32}
	};
	
	
	for (int u = 0; u < newHeightResize; u += 8) {
		for (int v = 0; v < newWidthResize; v += 8) {
			
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					
					Cb1[i + u][j + v] = Math.round(Cb1[u + i][v + j]/(cbTable[i][j]*Math.pow(2, n)));
					
				}
			}
			
			
			
		}
		}
	
	
}
public void dequantizeY(int n){
	
	//double[][] inverseDCT = new double[8][8];
	int [][] yTable = {
			{4, 4, 4, 8, 8, 16, 16, 32},
			{4, 4, 4, 8, 8, 16, 16, 32},
			{4, 4, 8, 8, 16, 16, 32, 32},
			{8, 8, 8, 16, 16, 32, 32, 32},
			{8, 8, 16, 16, 32, 32, 32, 32},
			{16, 16, 16, 32, 32, 32, 32, 32},
			{16, 16, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32}
	};
	
	
	for (int u = 0; u < heightResize; u += 8) {
		for (int v = 0; v < widthResize; v += 8) {
			
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					
					Y[i + u][j + v] = Y[u + i][v + j]*(yTable[i][j]*Math.pow(2, n));
					
				}
			}
			
			
			
		}
		}
	
	
}

public void dequantizeCr(int n){
	
	//double[][] inverseDCT = new double[8][8];
	int [][] crTable = {
			{8, 8, 8, 16, 32, 32, 32, 32},
			{8, 8, 8, 16, 32, 32, 32, 32},
			{8, 8, 16, 32, 32, 32, 32, 32},
			{16, 16, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32},
			{32, 32, 32, 32, 32, 32, 32, 32}
	};
	
	
	for (int u = 0; u < newHeightResize; u += 8) {
		for (int v = 0; v < newWidthResize; v += 8) {
			
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					
					Cr1[i + u][j + v] = Cr1[u + i][v + j]*(crTable[i][j]*Math.pow(2, n));
					
				}
			}
			
			
			
		}
		}
	
	
}

public void dequantizeCb(int n){

//double[][] inverseDCT = new double[8][8];
int [][] cbTable = {
		{8, 8, 8, 16, 32, 32, 32, 32},
		{8, 8, 8, 16, 32, 32, 32, 32},
		{8, 8, 16, 32, 32, 32, 32, 32},
		{16, 16, 32, 32, 32, 32, 32, 32},
		{32, 32, 32, 32, 32, 32, 32, 32},
		{32, 32, 32, 32, 32, 32, 32, 32},
		{32, 32, 32, 32, 32, 32, 32, 32},
		{32, 32, 32, 32, 32, 32, 32, 32}
};


for (int u = 0; u < newHeightResize; u += 8) {
	for (int v = 0; v < newWidthResize; v += 8) {
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				
				Cb1[i + u][j + v] = Cb1[u + i][v + j]*(cbTable[i][j]*Math.pow(2, n));
				
			}
		}
		
		
		
	}
	}


}

  public void write2PPM(String fileName)
  // wrrite the image data in img to a PPM file
  {
	FileOutputStream fos = null;
	PrintWriter dos = null;

	try{
		fos = new FileOutputStream(fileName);
		dos = new PrintWriter(fos);

		System.out.println("Writing the Image buffer into "+fileName+"...");

		// write header
		dos.print("P6"+"\n");
		dos.print("#CS451"+"\n");
		dos.print(width + " "+height +"\n");
		dos.print(255+"\n");
		dos.flush();

		// write data
		int x, y;
		byte[] rgb = new byte[3];
		for(y=0;y<height;y++)
		{
			for(x=0;x<width;x++)
			{
				getPixel(x, y, rgb);
				fos.write(rgb[0]);
				fos.write(rgb[1]);
				fos.write(rgb[2]);

			}
			fos.flush();
		}
		dos.close();
		fos.close();

		System.out.println("Wrote into "+fileName+" Successfully.");

	} // try
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
  }
  
  public Image greyScale(){
	  
	  Image image = new Image(this.width, this.height);
	  int x, y;
	  float grey;
	  int greyScale;
	  int[] rgb = new int[3];
		for(y=0;y<height;y++)
		{
			for(x=0;x<width;x++)
			{
				getPixel(x, y, rgb);
				grey = (float) ((0.299 * rgb[0]) + (0.587 * rgb[1]) + (0.114 * rgb[2]));
				greyScale = range0to255(Math.round(grey));
				
				rgb[0] = greyScale;
				rgb[1] = greyScale;
				rgb[2] = greyScale;
				image.setPixel(x, y, rgb);

			}
			
		}
		return image;
	  
  }
  
  public int range0to255(int gS){
	  
	  if(gS < 0)
		  return 0;
	  else if(gS > 255)
		  return 255;
	  else
		  return gS;
	  
  }
  
  public Image biLevel(){
	  
	  
	  Image biImage = this.greyScale();
	  int gSAvg = greyScaleAvg();
	  int x, y;
	  float grey;
	  int white = 255;
	  int black = 0;
	  int[] rgb = new int[3];
	  for(y=0;y<biImage.height;y++)
	  {
		  for(x=0;x<biImage.width;x++)
		  {
				biImage.getPixel(x, y, rgb);
				
				if(rgb[0] > gSAvg){
					rgb[0] = white;
					rgb[1] = white;
					rgb[2] = white;}
				else if(rgb[0] <= gSAvg){
					rgb[0] = black;
					rgb[1] = black;
					rgb[2] = black;
					
				}
				biImage.setPixel(x, y, rgb);

		  }
			
	  }
	  
	  return biImage;
	  
  }
  
public void white(){
	 
	  int x, y;
	  int white = 255;
	  int[] rgb = new int[3];
	  for(y=0;y<this.height;y++)
	  {
		  for(x=0;x<this.width;x++)
		  {
				this.getPixel(x, y, rgb);
				
					rgb[0] = white;
					rgb[1] = white;
					rgb[2] = white;

				this.setPixel(x, y, rgb);

		  }
			
	  }
	  
	  
  }


  
  public Image ErrorDiffusion(int n){
	  
	  Image biImage = this.greyScale();
	  int x, y;
	  int value;
	  double finalError;
	  int[] rgb = new int[3];

	  double[][] array2D = new double[height][width];
	  for(y=0;y<biImage.height;y++)
	  {
		  for(x=0;x<biImage.width;x++)
		  {
			  biImage.getPixel(x, y, rgb);
			  
			  array2D[y][x] = rgb[0];
			  
		  }
	  }
	  for(y=0;y<height;y++)
	  {
		  for(x=0;x<width;x++)
		  {	
			 
			  value = N(n, array2D[y][x]);
			  
			  biImage.getPixel(x, y, rgb);
			
			  rgb[0] = value;
			  rgb[1] = value;
			  rgb[2] = value;
			  biImage.setPixel(x, y, rgb);
			  
				
			  finalError = array2D[y][x] - value;
			  if(x == 0 && y < height-1){
				  
				  array2D[y + 1][x] =   (array2D[y + 1][x] + finalError * (5.0f/16));
				  array2D[y+1][x+1] =  (array2D[y+1][x+1] + finalError * (1.0f/16));
				  array2D[y][x+1] = (array2D[y][x+1] + finalError * (7.0f/16));
				  
			  }
			  if((x > 0 && x < width-1) && y < height - 1){
				  array2D[y + 1][x] =   (array2D[y + 1][x] + finalError * (5.0f/16));
				  array2D[y+1][x+1] =  (array2D[y+1][x+1] + finalError * (1.0f/16));
				  array2D[y][x+1] =  (array2D[y][x+1] + finalError * (7.0f/16));
				  array2D[y+1][x-1] =  (array2D[y+1][x-1] + finalError * (3.0f/16));
				}
			  if(y == height-1 && x < width-1){
		  			
				  array2D[y][x+1] =  (array2D[y][x+1] + finalError * (7.0f/16));
		  		}
			   if(x == width-1 && y < height-1){
				  array2D[y + 1][x] =   (array2D[y + 1][x] + finalError *(5.0f/16));
					
				  array2D[y+1][x-1] =  (array2D[y+1][x-1] + finalError * (3.0f/16));
		  			
		  		}
			  
			  
			  
		  }
	  }
	  

			
	  
	  
	  return biImage;
	  
  }
  

  public static int N(int n, double color){
	  if(color < 0)
		  color = 0;
	  if(color > 255)
		  color = 255;
	  float divide = 255/(n-1);
	  int value = 0;
	  
	  int i = (int) (color/divide);
	  
	  double less = color - (divide * i);
	  //System.out.println("less " + less);
	  double more = (divide * (i + 1)) - color;
	  //System.out.println("more " + more);
	  if(less < more)
		  value = (int)(divide * i);
	  else if ( less == more)
		  value = (int)(divide * (i + 1));
	  else{
		  value = (int)(divide * (i + 1));
		  
	  }
	  //System.out.println("value " + value);
	  return value;
  }
  
  public int zeroOr255(int arg){
	  
	  if(arg < 128)
		  return 0;
	  else
		  return 255;
	  
  }
  public int greyScaleAvg(){
	  
	  int x, y;
	  float grey;
	  int greyScale;
	  float total = 0;
	  int avg;
	  int[] rgb = new int[3];
		for(y=0;y<height;y++)
		{
			for(x=0;x<width;x++)
			{
				getPixel(x, y, rgb);
				grey = (float) ((0.299 * rgb[0]) + (0.587 * rgb[1]) + (0.114 * rgb[2]));
				greyScale = range0to255(Math.round(grey));
				greyScale = Math.round(grey);
				total += greyScale;

			}
			
		}
		
		avg = (int) (total/(height*width));
		
		return avg;
	  
  }
  
  public void UCQ(){
	  
	  lookUpTable();
	  
	  int x, y;
	  
	  int[] rgb = new int[3];
		for(y=0;y<height;y++)
		{
			for(x=0;x<width;x++)
			{
				getPixel(x, y, rgb);
				int index = rgb[0];
				int[] rgb1 = map.get(index);
				
				setPixel(x, y, rgb1);

			}
			
		}
	  
	  
	  //return ucq;
	  
	  
  }
  
  public Image indexImage(){
	  
	  lookUpTable();
	  Image ucq = new Image(this.width, this.height);
	  int x, y;
	  
	  int[] rgb = new int[3];
		for(y=0;y<height;y++)
		{
			for(x=0;x<width;x++)
			{
				getPixel(x, y, rgb);
				int red = rgb[0]/32;
				int green = rgb[1]/32;
				int blue = rgb[2]/64;
				int index = (32 * red) + (4*green) + (1*blue);
				rgb[0] = index;
				rgb[1] = index;
				rgb[2] = index;
				
				ucq.setPixel(x, y, rgb);

			}
			
		}
	  
	  
	  return ucq;
	  
	  
  }
  public void lookUpTable(){
	  
	  	map = new HashMap<Integer, int[]>();
		for(int r = 0; r < 8; r++){
			for(int g = 0; g < 8; g++){
				for(int b = 0; b < 4; b++){
					int[] array = new int[3];
					int red = 32 * r + 16;
					array[0] = red;
					int green = 32 * g + 16;
					array[1] = green;
					int blue = 64 * b + 32;
					array[2] = blue;
					int index = (r * 8 *4) + (g * 4) + b;
					
					map.put(index, array);
				}
				
			}
		}
		
		
  }
  
public void displayLUT(){
	
	System.out.println("Index \t r \t g \t b ");
	
	for(Entry<Integer, int[]> e: map.entrySet()){
		System.out.print(e.getKey() + " \t");
		int[] value1 = e.getValue();
		for(int i = 0; i < value1.length; i++){
			System.out.print(value1[i]+ " \t");
			
		}
		System.out.println();
	}
}

  public void display(String title)
  // display the image on the screen
  {
     // Use a label to display the image
      JFrame frame = new JFrame();
      JLabel label = new JLabel(new ImageIcon(img));
      frame.add(label, BorderLayout.CENTER);
      frame.setTitle(title);
      frame.pack();
      frame.setVisible(true);
  }
  
  public void subSampling(int K, int M, int N)
  {
  	  this.white();
  	  int cx = K/2;
  	  int cy = K/2;
  	  int radius = N;
  	  int white = 0;
  	  
  	  int[] rgb = new int[3];
  	  int numberOfCircles = (int)(cx/N);
  	  
  	  int thickness = 0;
  	  while (thickness < M)
  	  {
  		  int s = 0;
  		  while(s < (numberOfCircles))
  		  {
  			  for(int i = 0; i < 360; i++)
  			  {
  				  int x = (int) (cx + (radius * Math.cos(Math.toRadians(i))));
  				  int y = (int) (cy + (radius * Math.sin(Math.toRadians(i))));
  				  
  				  if(x >= K || y >= K)
  					  break;
  				  
  				  this.getPixel(x, y, rgb);
  				  rgb[0] = white;
  				  rgb[1] = white;
  				  rgb[2] = white;
  				  this.setPixel(x, y, rgb);
  			  }
  			  
  			  s++;
  			  radius += N;
  				  
  		  }
  		  thickness++;
  		  radius = N + thickness;
  	  }
  		  
  		  
  } 
  
  public void subSampling(int M, int N)
  {
  	  this.white();
  	  
  	  int cx = (this.height)/2;
  	  int cy = (this.height)/2;
  	  int radius = N;
  	  int white = 0;
  	
  	  int[] rgb = new int[3];
  	  int numberOfCircles = (int)(cx/N);
  	
  	  int thickness = 0;
  	  while (thickness < M)
  	  {
  		  int s = 0;
  		  while(s < (numberOfCircles))
  		  {
  			  for(double i = 0; i < 360; i+=.0150)
  			  {
  				  int x = (int) (cx + (radius * Math.cos(i)));
  				  int y = (int) (cy + (radius * Math.sin(i)));
  				  
  				  if(x >= this.height || y >= this.height)
  					  break;
  				  
  				  this.getPixel(x, y, rgb);
  				  rgb[0] = white;
  				  rgb[1] = white;
  				  rgb[2] = white;
  				  this.setPixel(x, y, rgb);
  			  }
  			  
  			  s++;
  			  radius += N;
  				  
  		  }
  		  thickness++;
  		  radius = N + thickness;
  	  }
  		  
  		  
  } 
  
  public Image noFilter(){
	  
	  Image image = new Image(this.width, this.height, this.K);
	  int[] rgb = new int[3];
	  int[] rgb1 = new int[3];
	  
	  
		  
			  for(int y = 0; y < this.height; y+=this.K)
			  {
				  for(int x = 0; x < this.width; x+=this.K)
				  {
					  this.getPixel(x, y, rgb);
					  
					  image.getPixel(x/this.K, y/this.K, rgb1);
						  
					  rgb1[0] = rgb[0];
					  rgb1[1] = rgb[1];
					  rgb1[2] = rgb[2];
						  
					  image.setPixel(x/this.K, y/this.K, rgb1);
				  }
				  
			  
			  }
		  

		return image;
	  
	  
  }
  
  public Image avgFilter(){
	  
	  Image image = new Image(this.width, this.height, this.K);
	  int[] rgb = new int[3];
	  int[] rgb1 = new int[3];
	  
	  
		  
			  for(int y = 0; y < this.height; y+=this.K)
			  {
				  for(int x = 0; x < this.width; x+=this.K)
				  {
					  int sumAvgR = 0;
					  int sumAvgG = 0;
					  int sumAvgB = 0;
				  	
					  
					  for(int i = y; i < y + this.K; i++){
						  for(int j = x; j < x + this.K; j++){
							  this.getPixel(j, i, rgb);
							  sumAvgR += rgb[0];
							  sumAvgG += rgb[1];
							  sumAvgB += rgb[2];
							  
						  }
					  }
					  
					  sumAvgR = sumAvgR/(K*K);
					  sumAvgG = sumAvgG/(K*K);
					  sumAvgB = sumAvgB/(K*K);
					  image.getPixel(x/this.K, y/this.K, rgb1);
						  
					  rgb1[0] = sumAvgR;
					  rgb1[1] = sumAvgG;
					  rgb1[2] = sumAvgB;
						  
					  image.setPixel(x/this.K, y/this.K, rgb1);
				  }
				  
			  
			  }
		  

		return image;
  }
  
  public Image filter1(){
	  
	  Image image = new Image(this.width, this.height, this.K);
	  int[] rgb = new int[3];
	  int[] rgb1 = new int[3];
	  
	  float[][] array2D = new float[height][width];
	  for(int y=0;y<this.height;y++)
	  {
		  for(int x=0;x<this.width;x++)
		  {
			  this.getPixel(x, y, rgb);
			  
			  array2D[y][x] = rgb[0];
			  
		  }
	  }
	  for(int y = 0; y < this.height; y+=this.K)
	  {
		  for(int x = 0; x < this.width; x+=this.K)
		  {
			  image.getPixel(x/this.K, y/this.K, rgb1);
				if ((x > 0 && x < width - 1) && (y < height - 1 && y > 0)) {

					rgb1[0] = rgb1[1] = rgb1[2] = (int) ((array2D[x - 1][y - 1] * (1f / 9))
							+ (array2D[x][y - 1] * (1f / 9))
							+ (array2D[x - 1][y + 1] * (1f / 9))
							+ (array2D[x + 1][y - 1] * (1f / 9))
							+ (array2D[x + 1][y + 1] * (1f / 9))
							+ (array2D[x + 1][y] * (1f / 9))
							+ (array2D[x - 1][y + 1] * (1f / 9))
							+ (array2D[x - 1][y] * (1f / 9)) + (array2D[x][y] * (1f / 9)));
				}
				
				image.setPixel(x/this.K, y/this.K, rgb1);
			    
			  
		  }
		  
	  
	  }
	  
	  return image;
	  
  }
  
  public Image filter2(){
	  
	  Image image = new Image(this.width, this.height, this.K);
	  int[] rgb = new int[3];
	  int[] rgb1 = new int[3];
	  
	  float[][] array2D = new float[height][width];
	  for(int y=0;y<this.height;y++)
	  {
		  for(int x=0;x<this.width;x++)
		  {
			  this.getPixel(x, y, rgb);
			  
			  array2D[y][x] = rgb[0];
			  
		  }
	  }
	  for(int y = 0; y < this.height; y+=this.K)
	  {
		  for(int x = 0; x < this.width; x+=this.K)
		  {
			  image.getPixel(x/this.K, y/this.K, rgb1);
				if ((x > 0 && x < width - 1) && (y < height - 1 && y > 0)) {

					rgb1[0] = rgb1[1] = rgb1[2] = (int) ((array2D[x - 1][y - 1] * (1f / 16))
							+ (array2D[x][y - 1] * (2f / 16))
							+ (array2D[x - 1][y + 1] * (1f / 16))
							+ (array2D[x + 1][y - 1] * (1f / 16))
							+ (array2D[x + 1][y + 1] * (1f / 16))
							+ (array2D[x + 1][y] * (2f / 16))
							+ (array2D[x - 1][y] * (2f / 16)) + (array2D[x][y] * (4f / 16))
							+ (array2D[x][y+1] * (2f / 16)));
				}
				
				image.setPixel(x/this.K, y/this.K, rgb1);
			  
			 
		  }
		  
	  
	  }
	  
	  return image;
	  
  }
  
  
  public double compressionRatioY(int n){
	  int[][] holder = new int[8][8];
	  double bits = 0;
	  for (int u = 0; u < heightResize; u += 8) {
			for (int v = 0; v < widthResize; v += 8) {
				
				
				ArrayList<Integer> list = new ArrayList<Integer>();
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						holder[i - u][j - v] = (int)Y[i][j];
						
					}
				}
				
				list = qStream(holder);
				
				bits += bitsInBlock(list, n);
				
				
			}
		}
	  
	  
	  
	  return bits;
	  
  }
  public double compressionRatioCr(int n){
	  int[][] holder = new int[8][8];
	  double bits = 0;
	  for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				
				ArrayList<Integer> list = new ArrayList<Integer>();
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						holder[i - u][j - v] = (int)Cr1[i][j];
						
					}
				}
				
				list = qStream(holder);
				
				bits += bitsInBlockC(list, n);
				
				
			}
		}
	  
	  
	  
	  
	  return bits;
	  
  }
  public double compressionRatioCb(int n){
	  int[][] holder = new int[8][8];
	  double bits = 0;
	  for (int u = 0; u < newHeightResize; u += 8) {
			for (int v = 0; v < newWidthResize; v += 8) {
				
				
				ArrayList<Integer> list = new ArrayList<Integer>();
				for(int i = u; i < u+8; i++){
					for(int j = v; j < v+8; j++){
						
						holder[i - u][j - v] = (int)Cb1[i][j];
						
					}
				}
				
				list = qStream(holder);
				
				bits += bitsInBlockC(list, n);
				
				
			}
		}
	  
	  
	  
	  return bits;
	  
  }
  
  
  public ArrayList<Integer> qStream(int [][] listholder){
	  ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(listholder[0][0]);
	  int x = 0;
		int y = 0;
		boolean oneStepX = false;
		boolean oneStepY = false;
		while (x < 8 && y < 8){
			
			if(y == 0 && oneStepX == false){
				
				//list.add(zigzag[y][x]);
				//System.out.print(zigzag[y][x] + " ");
				x +=1;
				list.add(listholder[y][x]);
				//System.out.print(zigzag[y][x] + " ");
				oneStepX = true;
				
			}
			else if(y == 0 && oneStepX == true){
				
				oneStepX = false;
				while(x > 0){
					
					y++;
					x--;
					list.add(listholder[y][x]);
					//System.out.print(zigzag[y][x] + " ");
					
				}
				
				
			}
			else if(x == 0 && oneStepY == false && y != 7){
				
				y += 1;
				list.add(listholder[y][x]);
				//System.out.print(zigzag[y][x] + " ");
				oneStepY = true;
				
				
			}
			else if(x == 0 && oneStepY == true){
				oneStepY = false;
				while( y > 0){
					
					x++;
					y--;
					list.add(listholder[y][x]);
					//System.out.print(zigzag[y][x] + " ");
				}
				
				
			}
			
			else if(y == 7 && oneStepX == false){
				
				oneStepX = true;
				x += 1;
				list.add(listholder[y][x]);
				
				
				
			}
			
			else if( y == 7 && oneStepX == true){
				
				oneStepX = false;
				while( x < 7){
					
					x++;
					y--;
					
					//System.out.print("y is " + y);
					//System.out.print("x is " + x);
					list.add(listholder[y][x]);
					//System.out.print(zigzag[y][x] + " ");
				}
				
				
			}
			else if(x == 7 && oneStepY == false){
				
				oneStepY = true;
				y+=1;
				list.add(listholder[y][x]);
			}
			else if(x==7 && oneStepY == true){
				oneStepY = false;
				while( y < 7){
					
					x--;
					y++;
					list.add(listholder[y][x]);
					//System.out.print(zigzag[y][x] + " ");
				}
				
			}
			
			if(list.size() == 64)
				break;
			
			
			
			
		}
		
		
	  return list;
	  
	  
  }
  public int bitsInBlock(ArrayList<Integer> list, int n){
  int i = 1;
	int j = 0;
	int bits = 0;
	while( i < list.size()-1){
		
		
		
		int s = i + 1;
		if(list.get(i) == list.get(s)){
			j++;
			while(list.get(i) == list.get(s) && s < list.size()-1){
				
				s++;
				
				if(s == list.size()-1)
					break;
			}
			
			
		}
		
		
		else
			j++;
		
		i = s;
		
		if(i == 63 && list.get(i) != list.get(i-1))
			j++;
		
		
		
		
	}
	
	 bits = (10-n) + ((10 - n + 6) * j);
	
	return bits;
  
  }
  public int bitsInBlockC(ArrayList<Integer> list, int n){
  int i = 1;
	int j = 0;
	int bits = 0;
	while( i < list.size()-1){
		
		
		
		int s = i + 1;
		if(list.get(i) == list.get(s)){
			j++;
			while(list.get(i) == list.get(s) && s < list.size()-1){
				
				s++;
				
				if(s == list.size()-1)
					break;
			}
			
			
		}
		
		
		else
			j++;
		
		i = s;
		
		if(i == 63 && list.get(i) != list.get(i-1))
			j++;
		
		
		
		
	}
	
	 bits = ( 9 - n) + ((9 - n + 6) * j);
	
	return bits;
  
  }
  
  public double getTotalSize(){
	  return width * height * 24;
  }
  


} // Image class