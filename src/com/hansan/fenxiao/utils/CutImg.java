package com.hansan.fenxiao.utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class CutImg {
	/**
	 * 读取一张图片的RGB值
	 * 
	 * @throws Exception
	 */
	public static int[] getImagePixel(String image) throws Exception {
		
		File file = new File(image);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		System.out.println("width=" + width + ",height=" + height + ".");
		System.out.println("minx=" + minx + ",miniy=" + miny + ".");
		int[] rgb = new int[3];
		int pixel = bi.getRGB(minx, miny);//第一个像素点
		int x1=0,y1=0,x2=0,y2=0;
		rgb[0] = (pixel & 0xff0000) >> 16;
		rgb[1] = (pixel & 0xff00) >> 8;
		rgb[2] = (pixel & 0xff);
		int count = 1;
		boolean flag =false;
		for (int i = minx; i < width; i++) {
			if(flag){
				flag =false;
				break;
			}
			for (int j = miny; j < height; j++) {
				count++; 
				int pixel_temp = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				if((rgb[0] != (pixel_temp & 0xff0000) >> 16)
						||(rgb[1] != (pixel_temp & 0xff00) >> 8)
						||(rgb[2] != (pixel_temp & 0xff))){
					x1=i;
					flag = true;
					break;
				}
			}
		}
		for (int i = width-1; i > minx; i--) {
			if(flag){
				flag =false;
				break;
			}
			for (int j = miny; j < height; j++) {
				int pixel_temp = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				count++;
				if((rgb[0] != (pixel_temp & 0xff0000) >> 16)
						||(rgb[1] != (pixel_temp & 0xff00) >> 8)
						||(rgb[2] != (pixel_temp & 0xff))){
					x2=i;
					flag = true;
					break;
				}
			}
		}
		for (int j = miny; j < height; j++) {
			if(flag){
				flag =false;
				break;
			}
			for (int i = x2; i > x1; i--) {
				count++; 
				int pixel_temp = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				if((rgb[0] != (pixel_temp & 0xff0000) >> 16)
						||(rgb[1] != (pixel_temp & 0xff00) >> 8)
						||(rgb[2] != (pixel_temp & 0xff))){
					y2=j;
					flag = true;
					break;
				}
			}
		}
		for (int j = height-1; j > miny; j--) {
			if(flag){
				flag =false;
				break;
			}
			for (int i = x2; i > x1; i--) {
				count++; 
				int pixel_temp = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				if((rgb[0] != (pixel_temp & 0xff0000) >> 16)
						||(rgb[1] != (pixel_temp & 0xff00) >> 8)
						||(rgb[2] != (pixel_temp & 0xff))){
					y1=j;
					flag = true;
					break;
				}
			}
		}
		System.out.println(width*height);
		System.out.println("总共计算"+count+"次");
		int [] result ={x1,y1,x2,y2};
		return result;
	}

	public static void cut(String srcpath, String subpath, int x, int y, int height, int width) throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件
			is = new FileInputStream(srcpath);

			/**
			 * 
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
			 * 
			 * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
			 * 
			 * (例如 "jpeg" 或 "tiff")等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");

			ImageReader reader = it.next();

			// 获取图片流
			iis = ImageIO.createImageInputStream(is);

			/**
			 * 
			 * <p>
			 * iis:读取源。true:只向前搜索
			 * </p>
			 * .将它标记为 ‘只向前搜索’。
			 * 
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/**
			 * 
			 * <p>
			 * 描述如何对流进行解码的类
			 * <p>
			 * .用于指定如何在输入时从 Java Image I/O
			 * 
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
			 * 
			 * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回
			 * 
			 * ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/**
			 * 
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 
			 * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);

			/**
			 * 
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
			 * 
			 * 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			ImageIO.write(bi, "jpg", new File(subpath));
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}
	}
	
	 public static void main(String[] args) throws Exception {
		 	int a[] =getImagePixel("d:\\1.jpg"); 
		 	int x=a[0];
		 	int y=a[3];
		 	int w = a[2] - a[0];
		 	int h = a[1] - a[3];
		 	System.out.println("目标图片"+w*h);
	        cut("d:\\1.jpg","d:\\2.jpg",x,y,h,w);  
	    } 
}
