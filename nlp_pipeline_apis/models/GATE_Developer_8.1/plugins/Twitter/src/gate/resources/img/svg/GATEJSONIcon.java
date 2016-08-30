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
public class GATEJSONIcon implements
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
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,48.0003547668457,48.0)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(0.8500000238418579f, 0.0f, 0.0f, 0.8500000238418579f, 4.0f, -2.7999989986419678f));
// _0_0 is CompositeGraphicsNode
float alpha__0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(39.186287, 27.999989);
((GeneralPath)shape).curveTo(39.320816, 51.410988, 0.81336004, 46.822826, 0.81336004, 46.822826);
((GeneralPath)shape).lineTo(0.81336004, 9.177162);
((GeneralPath)shape).curveTo(0.81336004, 9.177162, 39.051773, 4.589025, 39.186287, 27.99999);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(0, 128, 0, 255);
stroke = new BasicStroke(1.6267201f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(39.186287, 27.999989);
((GeneralPath)shape).curveTo(39.320816, 51.410988, 0.81336004, 46.822826, 0.81336004, 46.822826);
((GeneralPath)shape).lineTo(0.81336004, 9.177162);
((GeneralPath)shape).curveTo(0.81336004, 9.177162, 39.051773, 4.589025, 39.186287, 27.99999);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
float alpha__0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1 is ShapeNode
paint = new Color(255, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30.316797, 39.760696);
((GeneralPath)shape).lineTo(15.039994, 39.760696);
((GeneralPath)shape).curveTo(11.543449, 39.760696, 8.645321, 38.599094, 6.3455925, 36.275894);
((GeneralPath)shape).curveTo(4.0693283, 33.952694, 2.9311917, 31.019365, 2.9311917, 27.475891);
((GeneralPath)shape).curveTo(2.9311917, 23.955906, 4.057592, 21.116442, 6.3103924, 18.957489);
((GeneralPath)shape).curveTo(8.586657, 16.798576, 11.496521, 15.719112, 15.039994, 15.719088);
((GeneralPath)shape).lineTo(28.627197, 15.719088);
((GeneralPath)shape).lineTo(28.627197, 19.907888);
((GeneralPath)shape).lineTo(15.039994, 19.907888);
((GeneralPath)shape).curveTo(12.740251, 19.907904, 10.839449, 20.647104, 9.337593, 22.12549);
((GeneralPath)shape).curveTo(7.8591847, 23.603907, 7.1199923, 25.504698, 7.1199923, 27.827892);
((GeneralPath)shape).curveTo(7.1199923, 30.127636, 7.8591843, 31.993229, 9.337593, 33.424694);
((GeneralPath)shape).curveTo(10.839449, 34.856167, 12.74025, 35.571896, 15.039994, 35.571896);
((GeneralPath)shape).lineTo(26.127998, 35.571896);
((GeneralPath)shape).lineTo(26.127998, 30.327095);
((GeneralPath)shape).lineTo(14.723195, 30.327095);
((GeneralPath)shape).lineTo(14.723195, 26.490294);
((GeneralPath)shape).lineTo(30.3168, 26.490294);
((GeneralPath)shape).lineTo(30.3168, 39.760696);
g.setPaint(paint);
g.fill(shape);
paint = new Color(128, 0, 0, 255);
stroke = new BasicStroke(0.8000002f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30.316797, 39.760696);
((GeneralPath)shape).lineTo(15.039994, 39.760696);
((GeneralPath)shape).curveTo(11.543449, 39.760696, 8.645321, 38.599094, 6.3455925, 36.275894);
((GeneralPath)shape).curveTo(4.0693283, 33.952694, 2.9311917, 31.019365, 2.9311917, 27.475891);
((GeneralPath)shape).curveTo(2.9311917, 23.955906, 4.057592, 21.116442, 6.3103924, 18.957489);
((GeneralPath)shape).curveTo(8.586657, 16.798576, 11.496521, 15.719112, 15.039994, 15.719088);
((GeneralPath)shape).lineTo(28.627197, 15.719088);
((GeneralPath)shape).lineTo(28.627197, 19.907888);
((GeneralPath)shape).lineTo(15.039994, 19.907888);
((GeneralPath)shape).curveTo(12.740251, 19.907904, 10.839449, 20.647104, 9.337593, 22.12549);
((GeneralPath)shape).curveTo(7.8591847, 23.603907, 7.1199923, 25.504698, 7.1199923, 27.827892);
((GeneralPath)shape).curveTo(7.1199923, 30.127636, 7.8591843, 31.993229, 9.337593, 33.424694);
((GeneralPath)shape).curveTo(10.839449, 34.856167, 12.74025, 35.571896, 15.039994, 35.571896);
((GeneralPath)shape).lineTo(26.127998, 35.571896);
((GeneralPath)shape).lineTo(26.127998, 30.327095);
((GeneralPath)shape).lineTo(14.723195, 30.327095);
((GeneralPath)shape).lineTo(14.723195, 26.490294);
((GeneralPath)shape).lineTo(30.3168, 26.490294);
((GeneralPath)shape).lineTo(30.3168, 39.760696);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_1;
g.setTransform(defaultTransform__0_0_1);
g.setClip(clip__0_0_1);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
float alpha__0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1 = g.getClip();
AffineTransform defaultTransform__0_1 = g.getTransform();
g.transform(new AffineTransform(0.13750000298023224f, 0.0f, 0.0f, 0.13750000298023224f, 23.352754592895508f, 23.226686477661133f));
// _0_1 is CompositeGraphicsNode
float alpha__0_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0 = g.getClip();
AffineTransform defaultTransform__0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(-666.1166381835938, 413.044921875), new Point2D.Double(-553.2698974609375, 525.9075927734375), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(255, 255, 255, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.9988399744033813f, 0.0f, 0.0f, 0.9986990094184875f, 689.0077514648438f, -388.84375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(79.8646, 119.09957);
((GeneralPath)shape).curveTo(115.26228, 167.35492, 149.90417, 105.63105, 149.85327, 68.51282);
((GeneralPath)shape).curveTo(149.79308, 24.62677, 105.31237, 0.09913, 79.8356, 0.09913);
((GeneralPath)shape).curveTo(38.94318, 0.09913, 0.0, 33.89521, 0.0, 80.13502);
((GeneralPath)shape).curveTo(0.0, 131.53102, 44.64038, 159.99998, 79.8356, 159.99998);
((GeneralPath)shape).curveTo(71.87113, 158.85324, 45.329403, 153.16603, 44.972683, 92.03322);
((GeneralPath)shape).curveTo(44.732815, 50.68662, 58.46025, 34.16771, 79.777954, 41.43417);
((GeneralPath)shape).curveTo(80.25539, 41.61124, 103.29187, 50.69868, 103.29187, 80.384705);
((GeneralPath)shape).curveTo(103.29187, 109.944626, 79.8646, 119.09958, 79.8646, 119.09958);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_1_0;
g.setTransform(defaultTransform__0_1_0);
g.setClip(clip__0_1_0);
float alpha__0_1_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_1 = g.getClip();
AffineTransform defaultTransform__0_1_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(-553.2697143554688, 525.9077758789062), new Point2D.Double(-666.1163940429688, 413.0451965332031), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(255, 255, 255, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.9988399744033813f, 0.0f, 0.0f, 0.9986990094184875f, 689.0077514648438f, -388.84375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(79.82327, 41.40081);
((GeneralPath)shape).curveTo(56.43322, 33.33893, 27.78025, 52.61662, 27.78025, 91.22962);
((GeneralPath)shape).curveTo(27.78025, 154.2776, 74.5008, 160.0, 80.16441, 160.0);
((GeneralPath)shape).curveTo(121.05683, 159.99998, 160.0, 126.20391, 160.0, 79.9641);
((GeneralPath)shape).curveTo(160.0, 28.5681, 115.35962, 0.09913, 80.16441, 0.09913);
((GeneralPath)shape).curveTo(89.91252, -1.25087, 132.70529, 10.649039, 132.70529, 69.135925);
((GeneralPath)shape).curveTo(132.70529, 107.2771, 100.75243, 128.0409, 79.96982, 119.169365);
((GeneralPath)shape).curveTo(79.492386, 118.992294, 56.4559, 109.904854, 56.4559, 80.218834);
((GeneralPath)shape).curveTo(56.4559, 50.658913, 79.82328, 41.400803, 79.82328, 41.400803);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_1_1;
g.setTransform(defaultTransform__0_1_1);
g.setClip(clip__0_1_1);
origAlpha = alpha__0_1;
g.setTransform(defaultTransform__0_1);
g.setClip(clip__0_1);
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
	public GATEJSONIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public GATEJSONIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public GATEJSONIcon(int width, int height) {
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

