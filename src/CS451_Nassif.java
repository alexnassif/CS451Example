import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;

import java.util.Map;
import java.util.Scanner;

/*******************************************************
 * CS451 Multimedia Software Systems @ Author: Alex Nassif
 *******************************************************/

// Template Code

public class CS451_Nassif {

	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
			String args1 = "java.jpg";
			System.out.println("1. Enter an N from 0 to 5");
			int n = scan.nextInt();

			Image image = new Image(args1, true);
			image.display(args1 + " original ");
			image.write2JPEG("original " + args1 + ".jpg");
			image.addPadding();

			image.cpTransform();
			image.subSampleCrCb();
			image.DCTY();
			image.DCTCb();
			image.DCTCr();

			image.quantizeY(n);
			image.quantizeCb(n);
			image.quantizeCr(n);
			double cry = image.compressionRatioY(n);
			double crcb = image.compressionRatioCb(n);
			double crcr = image.compressionRatioCr(n);

			double totalCost = cry + crcb + crcr;
			double compressedR = image.getTotalSize() / totalCost;

			System.out.println("Original image cost " + image.getTotalSize());
			System.out.println("The Y values cost is " + cry + " bits.");
			System.out.println("The Cb values cost is " + crcb + " bits.");
			System.out.println("The Cr values cost is " + crcr + " bits.");
			System.out.println("The total compressed image cost is "
					+ totalCost + " bits.");
			System.out.println("The compressed ratio is " + compressedR
					+ " bits.");
			image.dequantizeCb(n);
			image.dequantizeCr(n);
			image.dequantizeY(n);

			image.CrIDCT();
			image.CbIDCT();

			image.yIDCT();

			image.superSample();
			Image image2 = image.removePadding();

			image2.display(args1 + " " + n);
			image2.write2JPEG(args1 + ".jpg");
		
		
		/**if (args.length == 2 && args[0].equals("1")) {
			if (args.length != 2) {
				usage();
				System.exit(1);
			}

			while (true) {
				menu(args[1]);

			}
		}

		else if (args.length == 1 && args[0].equals("2")) {

			while (true) {
				menuHW2();

			}

		}

		else if (args.length == 2 && args[0].equals("3")) {

			while (true) {
				menuHW3(args[1]);

			}

		}

		else if (args.length == 1 && args[0].equals("4")) {

			while (true) {
				menuHW4();
			}
		}**/
	}

	public static void usage() {
		System.out.println("\nUsage: java CS451_Main [inputfile]\n");
	}

	public static String motionCompensation(Image t, Image r) {
		String mv = "";
		int counter = 0;
		Image r1 = r.greyScale();
		Image t1 = t.greyScale();

		Image image = new Image(t.getW(), t.getH());

		int[][] erroR = new int[t.getH()][t.getW()];

		for (int u = 0; u < t.getH(); u += 16) {
			for (int v = 0; v < t.getW(); v += 16) {
				int[] rgb = new int[3];
				int[][] R = new int[16][16];

				for (int i = u; i < u + 16; i++) {
					for (int j = v; j < v + 16; j++) {
						t1.getPixel(j, i, rgb);
						R[i - u][j - v] = rgb[0];

					}
				}

				Map<String, Integer> map = search(R, r1, v, u);
				int[] irgb = new int[3];
				int[][] holderR = new int[16][16];

				int rX = map.get("x");
				int rY = map.get("y");
				for (int i = 0; i < 16; i++) {

					for (int j = 0; j < 16; j++) {
						r1.getPixel(rX + j, rY + i, irgb);
						holderR[i][j] = irgb[0];

					}
				}
				int[] i2rgb = new int[3];
				for (int i = u; i < u + 16; i++) {
					for (int j = v; j < v + 16; j++) {

						erroR[i][j] = R[i - u][j - v] - holderR[i - u][j - v];

					}
				}
				mv += "[" + (map.get("x") - v) + ", " + (map.get("y") - u)
						+ "]";

				counter++;
				if (counter % 12 == 0)
					mv += "\n";
			}

		}

		double[][] errorFrame = new double[image.getH()][image.getW()];
		for (int y = 0; y < image.getH(); y++) {
			for (int x = 0; x < image.getW(); x++) {

				errorFrame[y][x] = erroR[y][x];

			}

		}

		double minVal = errorFrame[0][0];
		double maxVal = errorFrame[0][0];

		for (int y = 0; y < image.getH(); y++) {

			for (int x = 1; x < image.getW(); x++) {

				if (errorFrame[y][x] < minVal)
					minVal = errorFrame[y][x];
				if (errorFrame[y][x] > maxVal)
					maxVal = errorFrame[y][x];

			}

		}

		double range = maxVal - minVal;
		double scale = 255.0 / range;

		for (int y = 0; y < image.getH(); y++) {
			for (int x = 0; x < image.getW(); x++) {
				
				double round = Math.round((errorFrame[y][x] - minVal) * scale);
				double min = Math.min(round,255.0);
				double m = Math.max(min, 0.0);
				
				errorFrame[y][x] = m;
			}
		}

		int[] rgb = new int[3];

		for (int y = 0; y < image.getH(); y++) {
			for (int x = 0; x < image.getW(); x++) {

				rgb[0] = rgb[1] = rgb[2] = (int) errorFrame[y][x];
				image.setPixel(x, y, rgb);

			}
		}
		image.display("error");
		image.write2PPM("error.ppm");

		return mv;

	}

	public static Map<String, Integer> search(int[][] R, Image ref, int x, int y) {

		int x1 = x - 12;
		int y1 = y - 12;

		int iLength = x + 16 + 12;
		int jLength = y + 16 + 12;

		double smallest = 0;
		if (x1 < 0) {
			x1 = 0;
		}
		if (y1 < 0) {
			y1 = 0;
		}

		if (iLength > ref.getW()) {
			iLength = ref.getW();
		}
		if (jLength > ref.getH()) {
			jLength = ref.getH();
		}

		int[] rgb = new int[3];

		int smallestX1 = x1;
		int smallestY1 = y1;

		for (int j = y1; j < jLength - 15; j++) {

			for (int i = x1; i < iLength - 15; i++) {
				double sum1 = 0;
				for (int s = 0; s < 16; s++) {
					for (int t = 0; t < 16; t++) {
						ref.getPixel(t + i, s + j, rgb);

						sum1 += Math.pow(R[s][t] - rgb[0], 2);

					}

				}
				sum1 = (1.0 / (16.0 * 16.0)) * sum1;
				if (smallest == 0) {
					smallest = sum1;
				}
				if (sum1 < smallest) {
					smallest = sum1;
					smallestX1 = i;
					smallestY1 = j;

				}

			}

		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("x", smallestX1);
		map.put("y", smallestY1);

		return map;

	}

	public static void motionCompensationTask2(Image t, Image r) {

		Image r1 = r.greyScale();
		Image t1 = t.greyScale();
		Map<String, Integer> map1 = new HashMap<String, Integer>();

		for (int u = 0; u < t.getH(); u += 16) {
			for (int v = 0; v < t.getW(); v += 16) {
				int[] rgb = new int[3];
				int[][] R = new int[16][16];

				for (int i = u; i < u + 16; i++) {
					for (int j = v; j < v + 16; j++) {
						t1.getPixel(j, i, rgb);
						R[i - u][j - v] = rgb[0];

					}
				}

				Map<String, Integer> map = search(R, r1, v, u);

				if (map.get("x") - v == 0 && map.get("y") - u == 0) {

					map1.put("x", v);
					map1.put("y", u);

				} else {

					int[][] holderR = new int[16][16];
					int[][] holderB = new int[16][16];
					int[][] holderG = new int[16][16];
					int[] rgbC = new int[3];
					for (int i = map1.get("y"); i < map1.get("y") + 16; i++) {
						for (int j = map1.get("x"); j < map1.get("x") + 16; j++) {
							t.getPixel(j, i, rgbC);
							holderR[i - map1.get("y")][j - map1.get("x")] = rgbC[0];
							holderG[i - map1.get("y")][j - map1.get("x")] = rgbC[1];
							holderB[i - map1.get("y")][j - map1.get("x")] = rgbC[2];
						}
					}

					for (int i = u; i < u + 16; i++) {
						for (int j = v; j < v + 16; j++) {

							t.getPixel(j, i, rgbC);
							rgbC[0] = holderR[i - u][j - v];
							rgbC[1] = holderG[i - u][j - v];
							rgbC[2] = holderB[i - u][j - v];
							t.setPixel(j, i, rgbC);

						}

					}

				}

			}

		}

		t.display("Task2#1");
		t.write2PPM("Task2#1.ppm");

	}

	public static void motionCompensationTask3(Image t, Image r) {

		Image r1 = r.greyScale();
		Image t1 = t.greyScale();

		Image f5 = new Image("walk_005.ppm");

		for (int u = 0; u < t.getH(); u += 16) {
			for (int v = 0; v < t.getW(); v += 16) {
				int[] rgb = new int[3];
				int[][] R = new int[16][16];

				for (int i = u; i < u + 16; i++) {
					for (int j = v; j < v + 16; j++) {
						t1.getPixel(j, i, rgb);
						R[i - u][j - v] = rgb[0];

					}
				}

				Map<String, Integer> map = search(R, r1, v, u);

				if (map.get("x") - v != 0 || map.get("y") - u != 0) {
					int[][] holderR = new int[16][16];
					int[][] holderG = new int[16][16];
					int[][] holderB = new int[16][16];
					int[] rgbC = new int[3];
					for (int i = u; i < u + 16; i++) {
						for (int j = v; j < v + 16; j++) {
							f5.getPixel(j, i, rgbC);
							holderR[i - u][j - v] = rgbC[0];
							holderG[i - u][j - v] = rgbC[1];
							holderB[i - u][j - v] = rgbC[2];
						}
					}
					int[] rgb5 = new int[3];
					for (int i = u; i < u + 16; i++) {
						for (int j = v; j < v + 16; j++) {

							t.getPixel(j, i, rgb5);
							rgb5[0] = holderR[i - u][j - v];
							rgb5[1] = holderG[i - u][j - v];
							rgb5[2] = holderB[i - u][j - v];
							t.setPixel(j, i, rgb5);

						}

					}

				}

			}

		}

		t.display("fromFrame5");
		t.write2PPM("task2#2.ppm");

	}

	public static void menuHW4() {

		Scanner scan = new Scanner(System.in);
		System.out.println("Main Menu-----------------------------------");
		System.out.println("1. Block-Based Motion Compensation");
		System.out.println("2. Removing Moving Objects");
		System.out.println("3. Exit");
		System.out.println("Please enter the task number [1 - 3]: ");

		int task = scan.nextInt();

		if (task == 1) {
			System.out.println("Enter target frame: ");

			String tar = scan.next();

			System.out.println("Enter reference frame: ");

			String ref = scan.next();

			Image target = new Image(tar);
			Image reference = new Image(ref);

			String moVec = motionCompensation(target, reference);

			try {
				FileWriter writer = new FileWriter("mv.txt");
				writer.write("# Target image name: " + tar + "\n");
				writer.write("# Reference image name: " + ref + "\n");
				writer.write("# Name: Alex Nassif" + "\n");
				writer.write("# Number of target macro blocks: "
						+ (target.getW() / 16) + " x " + (target.getH() / 16)
						+ "\n");
				writer.write(moVec);

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (task == 2) {

			System.out.println("Enter target frame: ");

			int tar = scan.nextInt();

			if (tar > 99) {
				Image target = new Image("walk_" + tar + ".ppm");
				Image reference = new Image("walk_" + (tar - 2) + ".ppm");

				motionCompensationTask2(target, reference);

				Image target1 = new Image("walk_" + tar + ".ppm");
				Image reference1 = new Image("walk_" + (tar - 2) + ".ppm");
				motionCompensationTask3(target1, reference1);
			}

			else {

				Image target = new Image("walk_0" + tar + ".ppm");
				Image reference = new Image("walk_0" + (tar - 2) + ".ppm");

				motionCompensationTask2(target, reference);

				Image target1 = new Image("walk_0" + tar + ".ppm");
				Image reference1 = new Image("walk_0" + (tar - 2) + ".ppm");
				motionCompensationTask3(target1, reference1);

			}

		} else if (task == 3) {
			System.out.println("GoodBye");
			System.exit(0);

		}

	}

	public static void menuHW3(String args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Main Menu-----------------------------------");
		System.out.println("1. Enter 1 to Compress image");
		System.out.println("2. Exit");
		System.out.println("Please enter the task number [1 or 2]: ");

		int task = scan.nextInt();

		if (task == 1) {

			System.out.println("1. Enter an N from 0 to 5");
			int n = scan.nextInt();

			Image image = new Image(args);
			image.display(args + " original ");
			image.write2PPM("original " + args + ".ppm");
			image.addPadding();

			image.cpTransform();
			image.subSampleCrCb();
			image.DCTY();
			image.DCTCb();
			image.DCTCr();

			image.quantizeY(n);
			image.quantizeCb(n);
			image.quantizeCr(n);
			double cry = image.compressionRatioY(n);
			double crcb = image.compressionRatioCb(n);
			double crcr = image.compressionRatioCr(n);

			double totalCost = cry + crcb + crcr;
			double compressedR = image.getTotalSize() / totalCost;

			System.out.println("Original image cost " + image.getTotalSize());
			System.out.println("The Y values cost is " + cry + " bits.");
			System.out.println("The Cb values cost is " + crcb + " bits.");
			System.out.println("The Cr values cost is " + crcr + " bits.");
			System.out.println("The total compressed image cost is "
					+ totalCost + " bits.");
			System.out.println("The compressed ratio is " + compressedR
					+ " bits.");
			image.dequantizeCb(n);
			image.dequantizeCr(n);
			image.dequantizeY(n);

			image.CrIDCT();
			image.CbIDCT();

			image.yIDCT();

			image.superSample();
			Image image2 = image.removePadding();

			image2.display(args + " " + n);
			image2.write2PPM(args + ".ppm");
		}

		else if (task == 2) {
			System.out.println("GoodBye");
			System.exit(0);

		}

	}

	public static void menuHW2() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Main Menu-----------------------------------");
		System.out.println("1. Aliasing");
		System.out.println("2. Dictionary Coding");
		System.out.println("3. Quit");

		System.out.println("Please enter the task number [1-3]: ");

		int task = scan.nextInt();

		if (task == 1) {

			System.out.println("Enter M: ");
			int M = scan.nextInt();
			System.out.println("Enter N: ");
			int N = scan.nextInt();
			System.out.println("Enter K: ");
			int K = scan.nextInt();

			Image image = new Image(K);

			image.subSampling(M, N);
			image.display("white");
			image.write2PPM("white.ppm");

			Image imagenf = image.noFilter();
			imagenf.display("nf");
			imagenf.write2PPM("nf.ppm");

			Image imageavg = image.avgFilter();
			imageavg.display("avg");
			imageavg.write2PPM("avg.ppm");

			Image imageFilter1 = image.filter1();
			imageFilter1.display("filter");
			imageFilter1.write2PPM("filter.ppm");

			Image imageFilter2 = image.filter2();
			imageFilter2.display("filter2");
			imageFilter2.write2PPM("filter2.ppm");

		} else if (task == 2) {

			System.out.println("Enter a file: ");

			String file = scan.next();
			System.out.println("Enter dictionary size: ");

			int size = scan.nextInt();
			LZW lzw = new LZW(file, size);
			lzw.encode();
			lzw.printMessage();
			lzw.printdictoutput();
			System.out.println("Decoded Text:");
			String decode = lzw.decode();
			System.out.println(decode);
			lzw.decompressionRatio();

		} else if (task == 3) {
			System.out.println("GoodBye");
			System.exit(0);

		}

	}

	public static void menu(String args1) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Main menu-------------");
		System.out.println("1. Conversion to Gray-scale Image (24bits->8bits)");
		System.out.println("2. Conversion to Bi-level Image (24bits->1bit)");
		System.out.println("3. Conversion to N-level Image");
		System.out.println("4. Conversion to 8bit Indexed Color Image using "
				+ "Uniform Color Quantization (24bits->8bits)");
		System.out.println("5. Quit");

		System.out.println("Please enter the task number [1-5]: ");

		int task = scan.nextInt();

		Image image = new Image(args1);

		if (task == 1) {

			Image greyScale = image.greyScale();
			greyScale.display("Homework 1 grey scale");
			greyScale.write2PPM("grey.ppm");

		} else if (task == 2) {

			System.out.println("Please choose from the following: ");
			System.out.println("1. Conversion to Bi-Level using Threshold");
			System.out
					.println("2. Conversion to Bi-level using Error Diffusion");
			System.out.println("Please enter 1 or 2: ");

			int option = scan.nextInt();

			if (option == 1) {
				Image bilevel = image.biLevel();
				bilevel.display("Bi-Level");
				bilevel.write2PPM("bilevelthreshold.ppm");
			} else if (option == 2) {

				Image bilevel = image.ErrorDiffusion(2);
				bilevel.display("Error-Diffusion");
				bilevel.write2PPM("bilevelerrodiffusion.ppm");

			}
		} else if (task == 3) {
			System.out.println("Please enter a value for N: ");
			int nLevel = scan.nextInt();

			Image bilevel = image.ErrorDiffusion(nLevel);
			bilevel.display("N-Level " + nLevel);
			bilevel.write2PPM("nLevel.ppm");

		} else if (task == 4) {
			Image indexImage = image.indexImage();
			indexImage.display("index image");
			indexImage.write2PPM(args1 + "-index.ppm");
			image.displayLUT();
			Image imgb = new Image(args1 + "-index.ppm");
			imgb.UCQ();
			imgb.display("uniform color q");
			imgb.write2PPM("ucq.ppm");

		} else if (task == 5) {
			System.out.println("GoodBye");
			System.exit(0);
		}
	}

}