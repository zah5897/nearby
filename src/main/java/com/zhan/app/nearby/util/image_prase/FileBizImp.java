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
	 
}
