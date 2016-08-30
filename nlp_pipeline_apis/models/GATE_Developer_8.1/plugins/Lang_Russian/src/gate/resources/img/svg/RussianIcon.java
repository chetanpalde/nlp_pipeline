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
public class RussianIcon implements
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
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -0.0f, -0.0f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,32.0,32.0)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0 is TextNode of 'Ж'
shape = new GeneralPath();
((GeneralPath)shape).moveTo(21.443872, 5.766054);
((GeneralPath)shape).lineTo(21.443872, 6.3267064);
((GeneralPath)shape).quadTo(19.578428, 6.347094, 19.048357, 6.8414874);
((GeneralPath)shape).quadTo(18.518286, 7.3358808, 18.518286, 8.885321);
((GeneralPath)shape).lineTo(18.518286, 15.195209);
((GeneralPath)shape).quadTo(20.037144, 15.154433, 20.5927, 14.970947);
((GeneralPath)shape).quadTo(21.148256, 14.787461, 21.647745, 14.073904);
((GeneralPath)shape).quadTo(22.147236, 13.360346, 22.605951, 11.525483);
((GeneralPath)shape).quadTo(23.197186, 9.201324, 23.757837, 8.039245);
((GeneralPath)shape).quadTo(24.318491, 6.8771653, 25.30218, 6.1992855);
((GeneralPath)shape).quadTo(26.28587, 5.5214057, 27.774147, 5.5214057);
((GeneralPath)shape).quadTo(29.252232, 5.5214057, 30.133986, 6.3521905);
((GeneralPath)shape).quadTo(31.015738, 7.182976, 31.015738, 8.396024);
((GeneralPath)shape).quadTo(31.015738, 9.313455, 30.455086, 9.884301);
((GeneralPath)shape).quadTo(29.894434, 10.455147, 28.966808, 10.455147);
((GeneralPath)shape).quadTo(27.203302, 10.455147, 27.070784, 8.457186);
((GeneralPath)shape).quadTo(26.989235, 7.4480114, 26.306257, 7.4480114);
((GeneralPath)shape).quadTo(25.827154, 7.4480114, 25.31747, 8.018857);
((GeneralPath)shape).quadTo(24.807787, 8.589704, 24.114616, 11.005606);
((GeneralPath)shape).quadTo(23.523382, 13.074923, 23.14112, 14.073904);
((GeneralPath)shape).quadTo(22.758858, 15.072885, 21.8822, 15.5621805);
((GeneralPath)shape).quadTo(23.034086, 15.929153, 23.650805, 16.398064);
((GeneralPath)shape).quadTo(24.26752, 16.866972, 24.680367, 17.453108);
((GeneralPath)shape).quadTo(25.09321, 18.039246, 26.428581, 20.51631);
((GeneralPath)shape).lineTo(28.202282, 23.778288);
((GeneralPath)shape).quadTo(28.997389, 25.22579, 29.563139, 25.577473);
((GeneralPath)shape).quadTo(30.128887, 25.929153, 31.138062, 25.929153);
((GeneralPath)shape).lineTo(31.138062, 26.5);
((GeneralPath)shape).lineTo(24.685463, 26.5);
((GeneralPath)shape).lineTo(20.658958, 18.334862);
((GeneralPath)shape).quadTo(20.077919, 17.152395, 19.767012, 16.907747);
((GeneralPath)shape).quadTo(19.456104, 16.6631, 18.518286, 16.622324);
((GeneralPath)shape).lineTo(18.518286, 23.319572);
((GeneralPath)shape).quadTo(18.518286, 24.848623, 18.778225, 25.154434);
((GeneralPath)shape).quadTo(19.038164, 25.460245, 19.476492, 25.694698);
((GeneralPath)shape).quadTo(19.91482, 25.929153, 21.443872, 25.929153);
((GeneralPath)shape).lineTo(21.443872, 26.5);
((GeneralPath)shape).lineTo(10.842444, 26.5);
((GeneralPath)shape).lineTo(10.842444, 25.929153);
((GeneralPath)shape).quadTo(12.055492, 25.929153, 12.641629, 25.745668);
((GeneralPath)shape).quadTo(13.227766, 25.562181, 13.487704, 25.118757);
((GeneralPath)shape).quadTo(13.747643, 24.675331, 13.747643, 23.421509);
((GeneralPath)shape).lineTo(13.747643, 16.622324);
((GeneralPath)shape).quadTo(12.911761, 16.632517, 12.580467, 16.846584);
((GeneralPath)shape).quadTo(12.249172, 17.060652, 11.729295, 18.100407);
((GeneralPath)shape).lineTo(7.5906606, 26.5);
((GeneralPath)shape).lineTo(1.1380608, 26.5);
((GeneralPath)shape).lineTo(1.1380608, 25.929153);
((GeneralPath)shape).quadTo(2.5753698, 25.929153, 3.1309254, 25.266565);
((GeneralPath)shape).quadTo(3.686481, 24.603975, 5.8475413, 20.51631);
((GeneralPath)shape).quadTo(7.356206, 17.662079, 8.105441, 16.85678);
((GeneralPath)shape).quadTo(8.854677, 16.051477, 10.383729, 15.5621805);
((GeneralPath)shape).quadTo(9.52746, 15.072885, 9.145197, 14.079);
((GeneralPath)shape).quadTo(8.762934, 13.085116, 8.1717005, 11.005606);
((GeneralPath)shape).quadTo(7.4683366, 8.589704, 6.9535556, 8.018857);
((GeneralPath)shape).quadTo(6.4387746, 7.4480114, 5.9698653, 7.4480114);
((GeneralPath)shape).quadTo(5.2970824, 7.4480114, 5.215533, 8.457186);
((GeneralPath)shape).quadTo(5.062628, 10.455147, 3.3195083, 10.455147);
((GeneralPath)shape).quadTo(2.3918836, 10.455147, 1.8261342, 9.884301);
((GeneralPath)shape).quadTo(1.260385, 9.313455, 1.260385, 8.375636);
((GeneralPath)shape).quadTo(1.260385, 7.244138, 2.1166542, 6.3827715);
((GeneralPath)shape).quadTo(2.9729233, 5.5214057, 4.5019755, 5.5214057);
((GeneralPath)shape).quadTo(6.01064, 5.5214057, 6.9841366, 6.2094793);
((GeneralPath)shape).quadTo(7.957633, 6.8975525, 8.523382, 8.059632);
((GeneralPath)shape).quadTo(9.089131, 9.221712, 9.670172, 11.525483);
((GeneralPath)shape).quadTo(10.312373, 14.022935, 11.097286, 14.609072);
((GeneralPath)shape).quadTo(11.8822, 15.195209, 13.747643, 15.195209);
((GeneralPath)shape).lineTo(13.747643, 8.885321);
((GeneralPath)shape).quadTo(13.747643, 7.325687, 13.166604, 6.8261967);
((GeneralPath)shape).quadTo(12.585564, 6.3267064, 10.842444, 6.3267064);
((GeneralPath)shape).lineTo(10.842444, 5.766054);
((GeneralPath)shape).lineTo(21.443872, 5.766054);
((GeneralPath)shape).closePath();
paint = new Color(0, 0, 0, 255);
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
        return 2;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 6;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 32;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 32;
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
	public RussianIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public RussianIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public RussianIcon(int width, int height) {
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

