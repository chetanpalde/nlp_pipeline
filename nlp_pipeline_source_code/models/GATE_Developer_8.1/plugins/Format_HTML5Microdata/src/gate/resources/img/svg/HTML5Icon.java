package gate.resources.img.svg;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class has been automatically generated using <a
 * href="http://englishjavadrinker.blogspot.com/search/label/SVGRoundTrip">SVGRoundTrip</a>.
 */
@SuppressWarnings("unused")
public class HTML5Icon implements
		javax.swing.Icon {
	/**
	 * Paints the transcoded SVG image on the specified graphics context. You
	 * can install a custom transformation on the graphics context to scale the
	 * image.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	public static void paint(Graphics2D g) {
        Shape shape = null;
        Paint paint = null;
        Stroke stroke = null;
        Area clip = null;
         
        float origAlpha = 1.0f;
        Composite origComposite = g.getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = 
                (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }
        
	    Shape clip_ = g.getClip();
AffineTransform defaultTransform_ = g.getTransform();
//  is CompositeGraphicsNode
float alpha__0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0 = g.getClip();
AffineTransform defaultTransform__0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -108.5f, -0.0f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(108.5,0.0,48.0,48.0)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 2.719759941101074f, -3.17070998789859E-6f));
// _0_0 is CompositeGraphicsNode
float alpha__0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_0 is ShapeNode
paint = new Color(228, 77, 38, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(702.782, 643.698);
((GeneralPath)shape).lineTo(499.696, 700.0);
((GeneralPath)shape).lineTo(297.169, 643.777);
((GeneralPath)shape).lineTo(252.037, 137.566);
((GeneralPath)shape).lineTo(747.963, 137.566);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
float alpha__0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_1 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_1 is ShapeNode
paint = new Color(241, 101, 41, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(702.713, 178.957);
((GeneralPath)shape).lineTo(500.0, 178.957);
((GeneralPath)shape).lineTo(500.0, 656.965);
((GeneralPath)shape).lineTo(664.104, 611.469);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1;
g.setTransform(defaultTransform__0_0_1);
g.setClip(clip__0_0_1);
float alpha__0_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_2 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_2 is ShapeNode
paint = new Color(235, 235, 235, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(361.066, 428.787);
((GeneralPath)shape).lineTo(500.0, 428.787);
((GeneralPath)shape).lineTo(500.0, 366.703);
((GeneralPath)shape).lineTo(417.846, 366.703);
((GeneralPath)shape).lineTo(412.172, 303.127);
((GeneralPath)shape).lineTo(500.0, 303.127);
((GeneralPath)shape).lineTo(500.0, 241.042);
((GeneralPath)shape).lineTo(499.785, 241.042);
((GeneralPath)shape).lineTo(344.321, 241.042);
((GeneralPath)shape).lineTo(345.807, 257.698);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_2;
g.setTransform(defaultTransform__0_0_2);
g.setClip(clip__0_0_2);
float alpha__0_0_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3 = g.getClip();
AffineTransform defaultTransform__0_0_3 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_3 is ShapeNode
paint = new Color(235, 235, 235, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(500.0, 527.943);
((GeneralPath)shape).lineTo(499.728, 528.016);
((GeneralPath)shape).lineTo(430.584, 509.346);
((GeneralPath)shape).lineTo(426.164, 459.83);
((GeneralPath)shape).lineTo(392.565, 459.83);
((GeneralPath)shape).lineTo(363.84, 459.83);
((GeneralPath)shape).lineTo(372.539, 557.313);
((GeneralPath)shape).lineTo(499.714, 592.617);
((GeneralPath)shape).lineTo(500.0, 592.538);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_3;
g.setTransform(defaultTransform__0_0_3);
g.setClip(clip__0_0_3);
float alpha__0_0_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4 = g.getClip();
AffineTransform defaultTransform__0_0_4 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_4 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(627.996, 546.823);
((GeneralPath)shape).lineTo(642.585, 383.374);
((GeneralPath)shape).lineTo(644.101, 366.703);
((GeneralPath)shape).lineTo(627.37, 366.703);
((GeneralPath)shape).lineTo(499.785, 366.703);
((GeneralPath)shape).lineTo(499.785, 428.787);
((GeneralPath)shape).lineTo(576.235, 428.787);
((GeneralPath)shape).lineTo(569.029, 509.306);
((GeneralPath)shape).lineTo(499.785, 527.995);
((GeneralPath)shape).lineTo(499.785, 592.587);
((GeneralPath)shape).lineTo(627.063, 557.313);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_4;
g.setTransform(defaultTransform__0_0_4);
g.setClip(clip__0_0_4);
float alpha__0_0_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5 = g.getClip();
AffineTransform defaultTransform__0_0_5 = g.getTransform();
g.transform(new AffineTransform(0.08178737014532089f, 0.0f, 0.0f, 0.08178737014532089f, 88.88655853271484f, -10.2511568069458f));
// _0_0_5 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(653.823, 257.698);
((GeneralPath)shape).lineTo(655.309, 241.042);
((GeneralPath)shape).lineTo(499.785, 241.042);
((GeneralPath)shape).lineTo(499.785, 279.602);
((GeneralPath)shape).lineTo(499.785, 302.976);
((GeneralPath)shape).lineTo(499.785, 303.127);
((GeneralPath)shape).lineTo(649.55, 303.127);
((GeneralPath)shape).lineTo(649.55, 303.127);
((GeneralPath)shape).lineTo(649.749, 303.127);
((GeneralPath)shape).lineTo(650.995, 289.172);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5;
g.setTransform(defaultTransform__0_0_5);
g.setClip(clip__0_0_5);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
origAlpha = alpha__0;
g.setTransform(defaultTransform__0);
g.setClip(clip__0);
g.setTransform(defaultTransform_);
g.setClip(clip_);

	}
	
	public Image getImage() {
		BufferedImage image =
            new BufferedImage(getIconWidth(), getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = image.createGraphics();
    	paintIcon(null, g, 0, 0);
    	g.dispose();
    	return image;
	}

    /**
     * Returns the X of the bounding box of the original SVG image.
     * 
     * @return The X of the bounding box of the original SVG image.
     */
    public static int getOrigX() {
        return 4;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 2;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 48;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 48;
	}

	/**
	 * The current width of this resizable icon.
	 */
	int width;

	/**
	 * The current height of this resizable icon.
	 */
	int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public HTML5Icon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public HTML5Icon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public HTML5Icon(int width, int height) {
	this.width = width;
	this.height = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
    @Override
	public int getIconHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
    @Override
	public int getIconWidth() {
		return width;
	}

	public void setDimension(Dimension newDimension) {
		this.width = newDimension.width;
		this.height = newDimension.height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
    @Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(x, y);
						
		Area clip = new Area(new Rectangle(0, 0, this.width, this.height));		
		if (g2d.getClip() != null) clip.intersect(new Area(g2d.getClip()));		
		g2d.setClip(clip);

		double coef1 = (double) this.width / (double) getOrigWidth();
		double coef2 = (double) this.height / (double) getOrigHeight();
		double coef = Math.min(coef1, coef2);
		g2d.scale(coef, coef);
		paint(g2d);
		g2d.dispose();
	}
}

