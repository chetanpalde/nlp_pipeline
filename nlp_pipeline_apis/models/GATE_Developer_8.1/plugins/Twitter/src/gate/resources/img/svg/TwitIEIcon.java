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
public class TwitIEIcon implements
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
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,2000.0,1625.3599853515625)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0 is ShapeNode
paint = new Color(0, 172, 237, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(1999.9999, 192.4);
((GeneralPath)shape).curveTo(1926.4199, 225.04, 1847.3298, 247.09, 1764.3398, 257.01);
((GeneralPath)shape).curveTo(1849.0398, 206.23001, 1914.1099, 125.82001, 1944.7499, 30.000015);
((GeneralPath)shape).curveTo(1865.4598, 77.030014, 1777.6499, 111.17001, 1684.1799, 129.57);
((GeneralPath)shape).curveTo(1609.3398, 49.82, 1502.7, 0.0, 1384.6799, 0.0);
((GeneralPath)shape).curveTo(1158.08, 0.0, 974.3519, 183.71, 974.3519, 410.31);
((GeneralPath)shape).curveTo(974.3519, 442.47, 977.9799, 473.79, 984.9769, 503.82);
((GeneralPath)shape).curveTo(643.96094, 486.71002, 341.60895, 323.35, 139.23792, 75.100006);
((GeneralPath)shape).curveTo(103.91391, 135.70001, 83.67961, 206.19, 83.67961, 281.39);
((GeneralPath)shape).curveTo(83.67961, 423.75, 156.11691, 549.34, 266.2229, 622.92004);
((GeneralPath)shape).curveTo(198.9609, 620.79004, 135.6879, 602.33, 80.371, 571.60004);
((GeneralPath)shape).curveTo(80.332, 573.31006, 80.332, 575.02, 80.332, 576.76);
((GeneralPath)shape).curveTo(80.332, 775.563, 221.773, 941.395, 409.477, 979.10205);
((GeneralPath)shape).curveTo(375.051, 988.47705, 338.801, 993.4971, 301.379, 993.4971);
((GeneralPath)shape).curveTo(274.938, 993.4971, 249.234, 990.91907, 224.176, 986.13306);
((GeneralPath)shape).curveTo(276.391, 1149.1411, 427.926, 1267.782, 607.48, 1271.0791);
((GeneralPath)shape).curveTo(467.05096, 1381.1411, 290.12897, 1446.7391, 97.88278, 1446.7391);
((GeneralPath)shape).curveTo(64.76168, 1446.7391, 32.09768, 1444.7902, -1.5258789E-5, 1441.0011);
((GeneralPath)shape).curveTo(181.58598, 1557.4187, 397.26996, 1625.3601, 628.988, 1625.3601);
((GeneralPath)shape).curveTo(1383.72, 1625.3601, 1796.45, 1000.12213, 1796.45, 457.89014);
((GeneralPath)shape).curveTo(1796.45, 440.10013, 1796.0399, 422.41013, 1795.25, 404.81012);
((GeneralPath)shape).curveTo(1875.4299, 346.95013, 1944.9899, 274.69012, 1999.9999, 192.40012);
g.setPaint(paint);
g.fill(shape);
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
        return 0;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 0;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 2000;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 1625;
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
	public TwitIEIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public TwitIEIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public TwitIEIcon(int width, int height) {
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

