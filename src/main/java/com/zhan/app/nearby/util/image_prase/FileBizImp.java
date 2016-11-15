package com.zhan.app.nearby.util.image_prase;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FileBizImp {
	public static void cutImage(String oldImage, String newImage, Integer nw) throws IOException {
		AffineTransform transform = new AffineTransform();
		BufferedImage bis = ImageIO.read(new File(oldImage));
		int w = bis.getWidth();
		int h = bis.getHeight();
		int nh = (nw * h) / w;
		double sx = (double) nw / w;
		double sy = (double) nh / h;
		transform.setToScale(sx, sy);
		AffineTransformOp ato = new AffineTransformOp(transform, null);
		BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
		ato.filter(bis, bid);
		ImageIO.write(bid, "jpeg", new File(newImage));
		ImageUtils.cutPNG(new FileInputStream(oldImage), new FileOutputStream(newImage), 0, 0, nw, nw);
	}

	public static void main(String[] args) {
		long time=System.currentTimeMillis();
		int wh=160;
		String bigPath="C:/Users/zah/Desktop/6c9e8024-5233-4eaa-b0c3-4e17a5dc4bc7.jpg";
		String smallMy="C:/Users/zah/Desktop/thumb/my_6c9e8024-5233-4eaa-b0c3-4e17a5dc4bc7.jpg";
		String smallazh="C:/Users/zah/Desktop/thumb/azhou_6c9e8024-5233-4eaa-b0c3-4e17a5dc4bc7.jpg";
		try {
			cutImage(bigPath,smallazh,wh);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		try {
//			ImageSaveUtils.pressImageByWidth(bigPath,wh,smallMy);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.err.println(System.currentTimeMillis()-time);
	}
}
