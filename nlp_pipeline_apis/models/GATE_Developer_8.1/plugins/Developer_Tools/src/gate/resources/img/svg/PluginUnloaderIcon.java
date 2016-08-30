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
public class PluginUnloaderIcon implements
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
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,48.0,48.0)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0 is CompositeGraphicsNode
float alpha__0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(0.9386181831359863f, 0.0f, -0.15052364766597748f, -0.5858481526374817f, 4.744360446929932f, 63.74424743652344f));
// _0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 0.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_0_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.9, 39.7);
((GeneralPath)shape).curveTo(8.0, 39.7, 5.7, 38.3, 4.8, 36.6);
((GeneralPath)shape).lineTo(3.0, 33.1);
((GeneralPath)shape).curveTo(2.1, 31.4, 2.5, 28.9, 3.9, 27.5);
((GeneralPath)shape).lineTo(4.7000003, 26.7);
((GeneralPath)shape).curveTo(6.1000004, 25.300001, 8.8, 24.2, 10.700001, 24.2);
((GeneralPath)shape).lineTo(39.7, 24.2);
((GeneralPath)shape).curveTo(41.600002, 24.2, 44.4, 25.2, 45.9, 26.400002);
((GeneralPath)shape).lineTo(47.600002, 27.800001);
((GeneralPath)shape).curveTo(49.100002, 29.000002, 49.7, 31.500002, 49.000004, 33.2);
((GeneralPath)shape).lineTo(47.700005, 36.3);
((GeneralPath)shape).curveTo(47.000004, 38.1, 44.800003, 39.5, 42.800003, 39.5);
((GeneralPath)shape).lineTo(9.9, 39.7);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0_0_0_0);
g.setClip(clip__0_0_0_0_0_0_0);
origAlpha = alpha__0_0_0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0_0_0);
g.setClip(clip__0_0_0_0_0_0);
float alpha__0_0_0_0_0_1 = origAlpha;
origAlpha = origAlpha * 0.01360001f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_1 is CompositeGraphicsNode
float alpha__0_0_0_0_0_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_1_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_1_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(5.0, 27.5);
((GeneralPath)shape).curveTo(6.4, 26.1, 9.0, 25.0, 11.0, 25.0);
((GeneralPath)shape).lineTo(39.8, 25.0);
((GeneralPath)shape).curveTo(41.7, 25.0, 44.5, 26.0, 46.0, 27.3);
((GeneralPath)shape).lineTo(47.0, 28.199999);
((GeneralPath)shape).curveTo(48.5, 29.4, 49.1, 31.9, 48.3, 33.699997);
((GeneralPath)shape).lineTo(47.3, 36.199997);
((GeneralPath)shape).curveTo(46.6, 37.999996, 44.399998, 39.399998, 42.399998, 39.399998);
((GeneralPath)shape).lineTo(10.3, 39.5);
((GeneralPath)shape).curveTo(8.4, 39.5, 6.1, 38.1, 5.2, 36.4);
((GeneralPath)shape).lineTo(3.7, 33.6);
((GeneralPath)shape).curveTo(2.8, 31.9, 3.2, 29.3, 4.5, 28.0);
((GeneralPath)shape).lineTo(5.0, 27.5);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_1_0;
g.setTransform(defaultTransform__0_0_0_0_0_1_0);
g.setClip(clip__0_0_0_0_0_1_0);
origAlpha = alpha__0_0_0_0_0_1;
g.setTransform(defaultTransform__0_0_0_0_0_1);
g.setClip(clip__0_0_0_0_0_1);
float alpha__0_0_0_0_0_2 = origAlpha;
origAlpha = origAlpha * 0.02729995f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_2 is CompositeGraphicsNode
float alpha__0_0_0_0_0_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_2_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_2_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(5.4, 28.3);
((GeneralPath)shape).curveTo(6.7, 26.9, 9.3, 25.8, 11.200001, 25.8);
((GeneralPath)shape).lineTo(39.800003, 25.8);
((GeneralPath)shape).curveTo(41.700005, 25.8, 44.500004, 26.8, 45.9, 28.099998);
((GeneralPath)shape).lineTo(46.4, 28.499998);
((GeneralPath)shape).curveTo(47.800003, 29.799997, 48.4, 32.3, 47.7, 34.1);
((GeneralPath)shape).lineTo(47.0, 35.9);
((GeneralPath)shape).curveTo(46.3, 37.7, 44.1, 39.100002, 42.2, 39.100002);
((GeneralPath)shape).lineTo(11.0, 39.2);
((GeneralPath)shape).curveTo(9.1, 39.2, 6.8, 37.8, 5.9, 36.1);
((GeneralPath)shape).lineTo(4.7, 34.0);
((GeneralPath)shape).curveTo(3.8, 32.3, 4.1, 29.7, 5.4, 28.4);
((GeneralPath)shape).lineTo(5.4, 28.3);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_2_0;
g.setTransform(defaultTransform__0_0_0_0_0_2_0);
g.setClip(clip__0_0_0_0_0_2_0);
origAlpha = alpha__0_0_0_0_0_2;
g.setTransform(defaultTransform__0_0_0_0_0_2);
g.setClip(clip__0_0_0_0_0_2);
float alpha__0_0_0_0_0_3 = origAlpha;
origAlpha = origAlpha * 0.04090002f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_3 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_3 is CompositeGraphicsNode
float alpha__0_0_0_0_0_3_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_3_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_3_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_3_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(5.9, 28.9);
((GeneralPath)shape).curveTo(7.0, 27.6, 9.5, 26.6, 11.4, 26.6);
((GeneralPath)shape).lineTo(39.8, 26.6);
((GeneralPath)shape).curveTo(41.7, 26.6, 44.399998, 27.7, 45.8, 29.0);
((GeneralPath)shape).curveTo(47.2, 30.3, 47.7, 32.8, 46.899998, 34.6);
((GeneralPath)shape).lineTo(46.399998, 35.699997);
((GeneralPath)shape).curveTo(45.699997, 37.499996, 43.499996, 38.899998, 41.6, 38.999996);
((GeneralPath)shape).lineTo(11.3, 39.0);
((GeneralPath)shape).curveTo(9.4, 39.0, 7.1, 37.6, 6.2, 35.9);
((GeneralPath)shape).lineTo(5.4, 34.4);
((GeneralPath)shape).curveTo(4.5, 32.7, 4.7, 30.2, 5.8, 28.9);
((GeneralPath)shape).lineTo(5.9, 28.9);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_3_0;
g.setTransform(defaultTransform__0_0_0_0_0_3_0);
g.setClip(clip__0_0_0_0_0_3_0);
origAlpha = alpha__0_0_0_0_0_3;
g.setTransform(defaultTransform__0_0_0_0_0_3);
g.setClip(clip__0_0_0_0_0_3);
float alpha__0_0_0_0_0_4 = origAlpha;
origAlpha = origAlpha * 0.05449997f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_4 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_4 is CompositeGraphicsNode
float alpha__0_0_0_0_0_4_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_4_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_4_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_4_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(6.5, 29.5);
((GeneralPath)shape).curveTo(7.5, 28.3, 9.8, 27.4, 11.7, 27.4);
((GeneralPath)shape).lineTo(39.8, 27.4);
((GeneralPath)shape).curveTo(41.7, 27.4, 44.3, 28.4, 45.399998, 29.6);
((GeneralPath)shape).curveTo(46.499996, 30.800001, 46.899998, 33.2, 46.199997, 35.0);
((GeneralPath)shape).lineTo(46.0, 35.4);
((GeneralPath)shape).curveTo(45.3, 37.2, 43.1, 38.600002, 41.2, 38.7);
((GeneralPath)shape).lineTo(11.7, 38.8);
((GeneralPath)shape).curveTo(9.8, 38.8, 7.5, 37.4, 6.6, 35.7);
((GeneralPath)shape).lineTo(6.2, 34.9);
((GeneralPath)shape).curveTo(5.3, 33.2, 5.4, 30.8, 6.4, 29.6);
((GeneralPath)shape).lineTo(6.5, 29.5);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_4_0;
g.setTransform(defaultTransform__0_0_0_0_0_4_0);
g.setClip(clip__0_0_0_0_0_4_0);
origAlpha = alpha__0_0_0_0_0_4;
g.setTransform(defaultTransform__0_0_0_0_0_4);
g.setClip(clip__0_0_0_0_0_4);
float alpha__0_0_0_0_0_5 = origAlpha;
origAlpha = origAlpha * 0.06820003f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_5 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_5 is CompositeGraphicsNode
float alpha__0_0_0_0_0_5_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_5_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_5_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_5_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(7.0, 30.1);
((GeneralPath)shape).curveTo(7.8, 29.0, 10.0, 28.1, 11.9, 28.1);
((GeneralPath)shape).lineTo(39.8, 28.1);
((GeneralPath)shape).curveTo(41.7, 28.1, 44.1, 29.0, 45.1, 30.1);
((GeneralPath)shape).curveTo(46.1, 31.2, 46.3, 33.5, 45.6, 35.2);
((GeneralPath)shape).curveTo(44.899998, 36.9, 42.699997, 38.3, 40.8, 38.3);
((GeneralPath)shape).lineTo(12.2, 38.4);
((GeneralPath)shape).curveTo(10.3, 38.4, 8.0, 37.0, 7.1, 35.3);
((GeneralPath)shape).lineTo(7.0, 35.1);
((GeneralPath)shape).curveTo(6.1, 33.4, 6.1, 31.1, 6.9, 30.0);
((GeneralPath)shape).lineTo(7.0, 30.1);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_5_0;
g.setTransform(defaultTransform__0_0_0_0_0_5_0);
g.setClip(clip__0_0_0_0_0_5_0);
origAlpha = alpha__0_0_0_0_0_5;
g.setTransform(defaultTransform__0_0_0_0_0_5);
g.setClip(clip__0_0_0_0_0_5);
float alpha__0_0_0_0_0_6 = origAlpha;
origAlpha = origAlpha * 0.08179997f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_6 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_6 is CompositeGraphicsNode
float alpha__0_0_0_0_0_6_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_6_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_6_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_6_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(7.5, 30.7);
((GeneralPath)shape).curveTo(8.1, 29.7, 10.2, 28.900002, 12.2, 28.900002);
((GeneralPath)shape).lineTo(39.9, 28.900002);
((GeneralPath)shape).curveTo(41.800003, 28.900002, 44.0, 29.7, 44.800003, 30.7);
((GeneralPath)shape).curveTo(45.600002, 31.7, 45.700005, 33.8, 45.100002, 35.3);
((GeneralPath)shape).curveTo(44.500004, 36.8, 42.4, 38.1, 40.500004, 38.1);
((GeneralPath)shape).lineTo(12.8, 38.2);
((GeneralPath)shape).curveTo(10.9, 38.2, 8.7, 36.9, 7.9, 35.3);
((GeneralPath)shape).curveTo(7.1, 33.7, 7.0, 31.6, 7.6, 30.6);
((GeneralPath)shape).lineTo(7.5, 30.7);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_6_0;
g.setTransform(defaultTransform__0_0_0_0_0_6_0);
g.setClip(clip__0_0_0_0_0_6_0);
origAlpha = alpha__0_0_0_0_0_6;
g.setTransform(defaultTransform__0_0_0_0_0_6);
g.setClip(clip__0_0_0_0_0_6);
float alpha__0_0_0_0_0_7 = origAlpha;
origAlpha = origAlpha * 0.09549997f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_7 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_7 is CompositeGraphicsNode
float alpha__0_0_0_0_0_7_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_7_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_7_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_7_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(8.1, 31.3);
((GeneralPath)shape).curveTo(8.6, 30.4, 10.5, 29.699999, 12.5, 29.699999);
((GeneralPath)shape).lineTo(40.0, 29.699999);
((GeneralPath)shape).curveTo(41.9, 29.699999, 44.0, 30.4, 44.6, 31.3);
((GeneralPath)shape).curveTo(45.199997, 32.2, 45.199997, 34.1, 44.699997, 35.399998);
((GeneralPath)shape).curveTo(44.199997, 36.699997, 42.1, 37.899998, 40.199997, 37.899998);
((GeneralPath)shape).lineTo(13.2, 38.0);
((GeneralPath)shape).curveTo(11.3, 38.0, 9.1, 36.8, 8.5, 35.4);
((GeneralPath)shape).curveTo(7.8, 34.0, 7.7, 32.1, 8.1, 31.2);
((GeneralPath)shape).lineTo(8.1, 31.300001);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_7_0;
g.setTransform(defaultTransform__0_0_0_0_0_7_0);
g.setClip(clip__0_0_0_0_0_7_0);
origAlpha = alpha__0_0_0_0_0_7;
g.setTransform(defaultTransform__0_0_0_0_0_7);
g.setClip(clip__0_0_0_0_0_7);
float alpha__0_0_0_0_0_8 = origAlpha;
origAlpha = origAlpha * 0.10909998f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_8 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_8 is CompositeGraphicsNode
float alpha__0_0_0_0_0_8_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_8_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_8_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_8_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(8.6, 31.9);
((GeneralPath)shape).curveTo(8.900001, 31.1, 10.700001, 30.5, 12.700001, 30.5);
((GeneralPath)shape).lineTo(39.9, 30.5);
((GeneralPath)shape).curveTo(41.800003, 30.5, 43.7, 31.2, 44.100002, 32.0);
((GeneralPath)shape).curveTo(44.500004, 32.8, 44.4, 34.4, 44.000004, 35.6);
((GeneralPath)shape).curveTo(43.600002, 36.8, 41.600002, 37.699997, 39.600002, 37.699997);
((GeneralPath)shape).lineTo(13.600002, 37.799995);
((GeneralPath)shape).curveTo(11.700003, 37.799995, 9.600002, 36.799995, 9.100002, 35.599995);
((GeneralPath)shape).curveTo(8.5, 34.4, 8.3, 32.7, 8.6, 32.0);
((GeneralPath)shape).lineTo(8.6, 31.9);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_8_0;
g.setTransform(defaultTransform__0_0_0_0_0_8_0);
g.setClip(clip__0_0_0_0_0_8_0);
origAlpha = alpha__0_0_0_0_0_8;
g.setTransform(defaultTransform__0_0_0_0_0_8);
g.setClip(clip__0_0_0_0_0_8);
float alpha__0_0_0_0_0_9 = origAlpha;
origAlpha = origAlpha * 0.12269999f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_9 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_9 is CompositeGraphicsNode
float alpha__0_0_0_0_0_9_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_9_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_9_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_9_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.2, 32.5);
((GeneralPath)shape).curveTo(9.3, 31.8, 11.0, 31.3, 13.0, 31.3);
((GeneralPath)shape).lineTo(40.0, 31.3);
((GeneralPath)shape).curveTo(41.9, 31.3, 43.7, 31.9, 43.9, 32.6);
((GeneralPath)shape).curveTo(44.100002, 33.3, 43.9, 34.699997, 43.600002, 35.699997);
((GeneralPath)shape).curveTo(43.2, 36.699997, 41.300003, 37.499996, 39.4, 37.499996);
((GeneralPath)shape).lineTo(14.2, 37.6);
((GeneralPath)shape).curveTo(12.3, 37.6, 10.3, 36.7, 9.8, 35.7);
((GeneralPath)shape).curveTo(9.3, 34.6, 9.1, 33.2, 9.2, 32.6);
((GeneralPath)shape).lineTo(9.2, 32.5);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_9_0;
g.setTransform(defaultTransform__0_0_0_0_0_9_0);
g.setClip(clip__0_0_0_0_0_9_0);
origAlpha = alpha__0_0_0_0_0_9;
g.setTransform(defaultTransform__0_0_0_0_0_9);
g.setClip(clip__0_0_0_0_0_9);
float alpha__0_0_0_0_0_10 = origAlpha;
origAlpha = origAlpha * 0.13639998f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_10 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_10 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_10 is CompositeGraphicsNode
float alpha__0_0_0_0_0_10_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_10_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_10_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_10_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.7, 33.1);
((GeneralPath)shape).curveTo(9.7, 32.5, 11.3, 32.1, 13.2, 32.1);
((GeneralPath)shape).lineTo(40.0, 32.1);
((GeneralPath)shape).curveTo(41.9, 32.1, 43.5, 32.6, 43.5, 33.199997);
((GeneralPath)shape).curveTo(43.5, 33.799995, 43.3, 34.999996, 42.9, 35.799995);
((GeneralPath)shape).curveTo(42.5, 36.599995, 40.800003, 37.299995, 38.800003, 37.299995);
((GeneralPath)shape).lineTo(14.500004, 37.299995);
((GeneralPath)shape).curveTo(12.600004, 37.299995, 10.700004, 36.599995, 10.300004, 35.699997);
((GeneralPath)shape).curveTo(9.9, 34.8, 9.6, 33.6, 9.6, 33.1);
((GeneralPath)shape).lineTo(9.700001, 33.1);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_10_0;
g.setTransform(defaultTransform__0_0_0_0_0_10_0);
g.setClip(clip__0_0_0_0_0_10_0);
origAlpha = alpha__0_0_0_0_0_10;
g.setTransform(defaultTransform__0_0_0_0_0_10);
g.setClip(clip__0_0_0_0_0_10);
float alpha__0_0_0_0_0_11 = origAlpha;
origAlpha = origAlpha * 0.15f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_11 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_11 is CompositeGraphicsNode
float alpha__0_0_0_0_0_11_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0_0_11_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0_0_11_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0_0_11_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15.0, 37.0);
((GeneralPath)shape).curveTo(13.1, 37.0, 11.1, 36.1, 10.7, 34.9);
((GeneralPath)shape).curveTo(10.3, 33.7, 11.5, 32.800003, 13.4, 32.800003);
((GeneralPath)shape).lineTo(40.0, 32.800003);
((GeneralPath)shape).curveTo(41.9, 32.800003, 43.2, 33.700005, 42.7, 34.9);
((GeneralPath)shape).curveTo(42.3, 36.0, 40.4, 37.0, 38.5, 37.0);
((GeneralPath)shape).lineTo(15.0, 37.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0_0_11_0;
g.setTransform(defaultTransform__0_0_0_0_0_11_0);
g.setClip(clip__0_0_0_0_0_11_0);
origAlpha = alpha__0_0_0_0_0_11;
g.setTransform(defaultTransform__0_0_0_0_0_11);
g.setClip(clip__0_0_0_0_0_11);
origAlpha = alpha__0_0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0_0);
g.setClip(clip__0_0_0_0_0);
origAlpha = alpha__0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0);
g.setClip(clip__0_0_0_0);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
float alpha__0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_1 = g.getTransform();
g.transform(new AffineTransform(0.911237359046936f, 0.0f, 0.0f, 0.911237359046936f, 2.4870986938476562f, 4.750350475311279f));
// _0_0_1 is CompositeGraphicsNode
float alpha__0_0_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_0 = g.getClip();
AffineTransform defaultTransform__0_0_1_0 = g.getTransform();
g.transform(new AffineTransform(0.9970909953117371f, 0.0f, 0.0f, 1.011160969734192f, 0.1752689927816391f, -0.49782899022102356f));
// _0_0_1_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(10.86144733428955, 33.13920211791992), new Point2D.Double(30.58730697631836, 37.720802307128906), new float[] {0.0f,1.0f}, new Color[] {new Color(181, 192, 81, 255),new Color(133, 142, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.1756349802017212f, 0.0f, 0.0f, 0.8506039977073669f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(3.836697, 16.810238);
((GeneralPath)shape).curveTo(3.792608, 16.775177, 5.5686603, 42.034206, 5.569166, 42.041862);
((GeneralPath)shape).curveTo(5.7357802, 44.564407, 7.1344333, 45.486355, 8.699929, 45.49011);
((GeneralPath)shape).curveTo(8.75587, 45.49024, 38.06941, 45.486908, 38.693935, 45.484207);
((GeneralPath)shape).curveTo(41.3303, 45.473118, 41.976185, 43.86767, 42.14961, 42.11355);
((GeneralPath)shape).curveTo(42.163517, 42.078968, 43.93866, 16.844797, 43.952568, 16.810211);
((GeneralPath)shape).curveTo(30.580612, 16.810211, 17.208654, 16.810211, 3.8366928, 16.810211);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new LinearGradientPaint(new Point2D.Double(19.347122192382812, 23.11408233642578), new Point2D.Double(19.672924041748047, 52.62547302246094), new float[] {0.0f,1.0f}, new Color[] {new Color(97, 108, 8, 255),new Color(73, 81, 6, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.1756349802017212f, 0.0f, 0.0f, 0.8506039977073669f, 0.0f, 0.0f));
stroke = new BasicStroke(0.9959154f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(3.836697, 16.810238);
((GeneralPath)shape).curveTo(3.792608, 16.775177, 5.5686603, 42.034206, 5.569166, 42.041862);
((GeneralPath)shape).curveTo(5.7357802, 44.564407, 7.1344333, 45.486355, 8.699929, 45.49011);
((GeneralPath)shape).curveTo(8.75587, 45.49024, 38.06941, 45.486908, 38.693935, 45.484207);
((GeneralPath)shape).curveTo(41.3303, 45.473118, 41.976185, 43.86767, 42.14961, 42.11355);
((GeneralPath)shape).curveTo(42.163517, 42.078968, 43.93866, 16.844797, 43.952568, 16.810211);
((GeneralPath)shape).curveTo(30.580612, 16.810211, 17.208654, 16.810211, 3.8366928, 16.810211);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_1_0;
g.setTransform(defaultTransform__0_0_1_0);
g.setClip(clip__0_0_1_0);
float alpha__0_0_1_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_1 = g.getClip();
AffineTransform defaultTransform__0_0_1_1 = g.getTransform();
g.transform(new AffineTransform(1.0072660446166992f, 0.0f, 0.0f, 1.0107239484786987f, -0.2521679997444153f, -0.11054900288581848f));
// _0_0_1_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.373860836029053, 27.37662124633789), new Point2D.Double(7.529111862182617, 69.46050262451172), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(3.495016098022461f, 0.0f, 0.0f, 0.3443230092525482f, -2.9720869064331055f, -0.03408148139715195f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(43.18838, 18.930946);
((GeneralPath)shape).lineTo(7.1998596, 18.90779);
((GeneralPath)shape).curveTo(34.314156, 19.538355, 39.641346, 22.51723, 42.946377, 22.365025);
((GeneralPath)shape).lineTo(43.188385, 18.930946);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_1;
g.setTransform(defaultTransform__0_0_1_1);
g.setClip(clip__0_0_1_1);
float alpha__0_0_1_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2 = g.getClip();
AffineTransform defaultTransform__0_0_1_2 = g.getTransform();
g.transform(new AffineTransform(1.4021790027618408f, 0.0f, 0.0f, 1.4389439821243286f, -66.03800964355469f, -8.126385688781738f));
// _0_0_1_2 is CompositeGraphicsNode
float alpha__0_0_1_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_0 is ShapeNode
paint = new Color(255, 255, 255, 124);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64.13115, 25.325447);
((GeneralPath)shape).lineTo(67.92814, 25.615145);
((GeneralPath)shape).lineTo(69.3964, 22.040586);
((GeneralPath)shape).lineTo(67.659134, 23.261576);
((GeneralPath)shape).curveTo(67.659134, 23.261576, 66.91422, 21.52417, 66.37882, 21.3845);
((GeneralPath)shape).curveTo(65.84342, 21.244831, 63.55913, 21.291391, 63.55913, 21.291391);
((GeneralPath)shape).curveTo(64.2857, 21.471079, 64.850365, 22.719519, 64.85905, 22.73368);
((GeneralPath)shape).curveTo(65.08572, 23.103537, 65.72022, 24.348064, 65.72022, 24.348064);
((GeneralPath)shape).lineTo(64.13115, 25.325455);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_0;
g.setTransform(defaultTransform__0_0_1_2_0);
g.setClip(clip__0_0_1_2_0);
float alpha__0_0_1_2_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_1 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_1 is ShapeNode
paint = new Color(255, 255, 255, 79);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64.60089, 22.852762);
((GeneralPath)shape).lineTo(63.34402, 25.250435);
((GeneralPath)shape).lineTo(60.76012, 23.620949);
((GeneralPath)shape).curveTo(60.76012, 23.620949, 61.93182, 21.409504, 62.855175, 21.409504);
((GeneralPath)shape).curveTo(63.786343, 21.409504, 64.20245, 22.212137, 64.60089, 22.852762);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_1;
g.setTransform(defaultTransform__0_0_1_2_1);
g.setClip(clip__0_0_1_2_1);
float alpha__0_0_1_2_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_2 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_2 is ShapeNode
paint = new Color(255, 255, 255, 124);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(66.45097, 28.545542);
((GeneralPath)shape).lineTo(64.002945, 31.47128);
((GeneralPath)shape).lineTo(66.33907, 34.48806);
((GeneralPath)shape).lineTo(66.382286, 32.58794);
((GeneralPath)shape).curveTo(66.382286, 32.58794, 68.10305, 32.75033, 68.497986, 32.362793);
((GeneralPath)shape).curveTo(68.89292, 31.975252, 70.535934, 30.021275, 70.535934, 30.021275);
((GeneralPath)shape).curveTo(70.00844, 30.552244, 68.64727, 30.39515, 68.63066, 30.395319);
((GeneralPath)shape).curveTo(68.19689, 30.39972, 66.4801, 30.35966, 66.4801, 30.35966);
((GeneralPath)shape).lineTo(66.45097, 28.545547);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_2;
g.setTransform(defaultTransform__0_0_1_2_2);
g.setClip(clip__0_0_1_2_2);
float alpha__0_0_1_2_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_3 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_3 is ShapeNode
paint = new Color(255, 255, 255, 79);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(68.74841, 30.061356);
((GeneralPath)shape).lineTo(67.33735, 27.751062);
((GeneralPath)shape).lineTo(70.062996, 26.371708);
((GeneralPath)shape).curveTo(70.062996, 26.371708, 71.35806, 28.513239, 70.8836, 29.305367);
((GeneralPath)shape).curveTo(70.40511, 30.104195, 69.50273, 30.048733, 68.74841, 30.061356);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_3;
g.setTransform(defaultTransform__0_0_1_2_3);
g.setClip(clip__0_0_1_2_3);
float alpha__0_0_1_2_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_4 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_4 is ShapeNode
paint = new Color(255, 255, 255, 124);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(62.170193, 28.37565);
((GeneralPath)shape).lineTo(60.961697, 25.192932);
((GeneralPath)shape).lineTo(57.437756, 25.441973);
((GeneralPath)shape).lineTo(58.884373, 26.495779);
((GeneralPath)shape).curveTo(58.884373, 26.495779, 57.885334, 27.906212, 58.02424, 28.441814);
((GeneralPath)shape).curveTo(58.16314, 28.977417, 58.94678, 31.19641, 58.94678, 31.19641);
((GeneralPath)shape).curveTo(58.749683, 30.474375, 59.56477, 29.37297, 59.572906, 29.35849);
((GeneralPath)shape).curveTo(59.785446, 28.980337, 60.676476, 27.512333, 60.676476, 27.512333);
((GeneralPath)shape).lineTo(62.170193, 28.375652);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_4;
g.setTransform(defaultTransform__0_0_1_2_4);
g.setClip(clip__0_0_1_2_4);
float alpha__0_0_1_2_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2_5 = g.getClip();
AffineTransform defaultTransform__0_0_1_2_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2_5 is ShapeNode
paint = new Color(255, 255, 255, 79);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(59.752773, 29.661146);
((GeneralPath)shape).lineTo(62.45898, 29.590477);
((GeneralPath)shape).lineTo(62.294994, 32.640865);
((GeneralPath)shape).curveTo(62.294994, 32.640865, 59.79291, 32.695175, 59.343014, 31.88884);
((GeneralPath)shape).curveTo(58.88931, 31.075684, 59.387474, 30.321226, 59.752773, 29.661144);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2_5;
g.setTransform(defaultTransform__0_0_1_2_5);
g.setClip(clip__0_0_1_2_5);
origAlpha = alpha__0_0_1_2;
g.setTransform(defaultTransform__0_0_1_2);
g.setClip(clip__0_0_1_2);
float alpha__0_0_1_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_3 = g.getClip();
AffineTransform defaultTransform__0_0_1_3 = g.getTransform();
g.transform(new AffineTransform(0.9970909953117371f, 0.0f, 0.0f, 1.011160969734192f, 0.253928005695343f, -0.6548129916191101f));
// _0_0_1_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(17.573945999145508, 25.335416793823242), new Point2D.Double(25.312450408935547, 48.805084228515625), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 26),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.1756360530853271f, 0.0f, 0.0f, 0.8506039977073669f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(41.62473, 23.651808);
((GeneralPath)shape).curveTo(36.29936, 25.986217, 29.004202, 28.320992, 23.056217, 28.370558);
((GeneralPath)shape).curveTo(16.516232, 28.425058, 11.697548, 31.531025, 6.81629, 33.914753);
((GeneralPath)shape).curveTo(7.010967, 36.79497, 7.225681, 38.805946, 7.4908457, 42.03152);
((GeneralPath)shape).curveTo(7.623113, 43.640472, 8.12877, 43.602253, 10.431216, 43.602253);
((GeneralPath)shape).curveTo(19.542667, 43.602253, 31.496632, 43.615192, 38.6888, 43.615192);
((GeneralPath)shape).curveTo(40.449337, 43.615192, 40.248375, 42.36346, 40.34348, 41.16475);
((GeneralPath)shape).curveTo(40.803875, 35.361893, 41.217728, 29.476254, 41.62473, 23.651806);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_3;
g.setTransform(defaultTransform__0_0_1_3);
g.setClip(clip__0_0_1_3);
float alpha__0_0_1_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_4 = g.getClip();
AffineTransform defaultTransform__0_0_1_4 = g.getTransform();
g.transform(new AffineTransform(0.9970909953117371f, 0.0f, 0.0f, 1.011160969734192f, 0.21997599303722382f, -0.49782899022102356f));
// _0_0_1_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(19.97749137878418, 38.962703704833984), new Point2D.Double(19.857769012451172, 23.600778579711914), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 73),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.21566903591156f, 0.0f, 0.0f, 0.82259202003479f, 0.0f, 0.0f));
stroke = new BasicStroke(0.9959154f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(5.209861, 19.402822);
((GeneralPath)shape).curveTo(5.158455, 19.40269, 6.049054, 32.078423, 6.756625, 40.968384);
((GeneralPath)shape).curveTo(6.9391613, 43.250374, 7.3101516, 44.179153, 8.747779, 44.179153);
((GeneralPath)shape).curveTo(20.59908, 44.179153, 37.44189, 44.264305, 38.015404, 44.262222);
((GeneralPath)shape).curveTo(40.789978, 44.25217, 40.738052, 43.236187, 40.967842, 41.077515);
((GeneralPath)shape).curveTo(41.05149, 40.29168, 42.504814, 19.49121, 42.49095, 19.49121);
((GeneralPath)shape).curveTo(32.56099, 19.49121, 17.478659, 19.43444, 5.209858, 19.40282);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_1_4;
g.setTransform(defaultTransform__0_0_1_4);
g.setClip(clip__0_0_1_4);
origAlpha = alpha__0_0_1;
g.setTransform(defaultTransform__0_0_1);
g.setClip(clip__0_0_1);
float alpha__0_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(-4.637519836425781, 104.38751983642578), new Point2D.Double(-4.523921012878418, 110.61377716064453), new float[] {0.0f,1.0f}, new Color[] {new Color(239, 243, 244, 255),new Color(147, 149, 150, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.3635447025299072f, 0.0f, 0.0f, 0.4461809992790222f, 30.447938919067383f, -27.71922492980957f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.014503, 8.913575);
((GeneralPath)shape).curveTo(8.042429, 8.915795, 7.228879, 8.938279, 6.785745, 9.863841);
((GeneralPath)shape).curveTo(6.708725, 10.024712, 4.361019, 16.55378, 4.248148, 16.802773);
((GeneralPath)shape).curveTo(3.281864, 18.934366, 4.172978, 21.619501, 6.0393248, 21.601524);
((GeneralPath)shape).curveTo(6.394259, 21.598225, 42.816044, 21.618174, 43.38083, 21.601524);
((GeneralPath)shape).curveTo(44.969563, 21.555515, 45.17287, 18.446154, 44.48893, 17.047398);
((GeneralPath)shape).curveTo(44.450176, 16.968157, 41.27594, 9.774863, 41.194984, 9.647454);
((GeneralPath)shape).curveTo(40.82054, 9.078439, 39.98201, 8.829062, 39.35914, 8.852419);
((GeneralPath)shape).curveTo(39.23644, 8.857139, 9.13594, 8.913299, 9.014503, 8.913575);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(77, 85, 6, 255);
stroke = new BasicStroke(0.91123736f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.014503, 8.913575);
((GeneralPath)shape).curveTo(8.042429, 8.915795, 7.228879, 8.938279, 6.785745, 9.863841);
((GeneralPath)shape).curveTo(6.708725, 10.024712, 4.361019, 16.55378, 4.248148, 16.802773);
((GeneralPath)shape).curveTo(3.281864, 18.934366, 4.172978, 21.619501, 6.0393248, 21.601524);
((GeneralPath)shape).curveTo(6.394259, 21.598225, 42.816044, 21.618174, 43.38083, 21.601524);
((GeneralPath)shape).curveTo(44.969563, 21.555515, 45.17287, 18.446154, 44.48893, 17.047398);
((GeneralPath)shape).curveTo(44.450176, 16.968157, 41.27594, 9.774863, 41.194984, 9.647454);
((GeneralPath)shape).curveTo(40.82054, 9.078439, 39.98201, 8.829062, 39.35914, 8.852419);
((GeneralPath)shape).curveTo(39.23644, 8.857139, 9.13594, 8.913299, 9.014503, 8.913575);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_2;
g.setTransform(defaultTransform__0_0_2);
g.setClip(clip__0_0_2);
float alpha__0_0_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3 = g.getClip();
AffineTransform defaultTransform__0_0_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(2.932560443878174, 21.16963768005371), new Point2D.Double(19.73766326904297, 21.16963768005371), new float[] {0.0f,1.0f}, new Color[] {new Color(87, 89, 85, 255),new Color(124, 126, 121, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.1762282848358154f, 0.0f, 0.0f, 0.4742067754268646f, -0.19649842381477356f, 4.347457408905029f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(9.439382, 10.48358);
((GeneralPath)shape).curveTo(8.536867, 10.47166, 8.478097, 10.436029, 8.274938, 11.08832);
((GeneralPath)shape).curveTo(8.22804, 11.238894, 6.3385587, 17.167019, 6.264495, 17.397062);
((GeneralPath)shape).curveTo(6.0126157, 18.179392, 6.368796, 18.385857, 7.242902, 18.369953);
((GeneralPath)shape).curveTo(7.5525513, 18.364353, 41.46476, 18.369953, 41.957756, 18.369953);
((GeneralPath)shape).curveTo(42.65762, 18.369953, 42.865223, 18.134493, 42.707134, 17.578787);
((GeneralPath)shape).curveTo(42.642895, 17.35298, 40.01248, 11.023481, 39.941845, 10.91076);
((GeneralPath)shape).curveTo(39.615135, 10.407344, 39.671795, 10.40881, 39.10115, 10.401712);
((GeneralPath)shape).curveTo(38.994022, 10.400413, 9.545328, 10.4849825, 9.439381, 10.4835825);
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
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4 is ShapeNode
paint = new Color(46, 52, 54, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(8.954633, 10.466474);
((GeneralPath)shape).curveTo(8.61479, 10.493013, 8.451331, 10.596043, 8.309087, 11.053576);
((GeneralPath)shape).curveTo(8.262189, 11.204422, 6.374784, 17.134476, 6.3007193, 17.364933);
((GeneralPath)shape).curveTo(6.04884, 18.148666, 6.3829794, 18.359367, 7.2570853, 18.343437);
((GeneralPath)shape).curveTo(7.2722254, 18.343163, 9.046978, 18.343664, 9.2149725, 18.343437);
((GeneralPath)shape).lineTo(8.954633, 10.466474);
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
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(55.20827102661133, 6.889798164367676), new Point2D.Double(60.6859016418457, 6.889798164367676), new float[] {0.0f,1.0f}, new Color[] {new Color(186, 189, 182, 255),new Color(241, 245, 236, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.7078520059585571f, 0.0f, 0.0f, 1.4605375528335571f, -0.19649842381477356f, 4.3221635818481445f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(39.149475, 10.384835);
((GeneralPath)shape).curveTo(39.562515, 10.388435, 39.683804, 10.379935, 39.947857, 10.89066);
((GeneralPath)shape).curveTo(39.983967, 10.960501, 42.581745, 17.25953, 42.67502, 17.489986);
((GeneralPath)shape).curveTo(42.992226, 18.377934, 42.38809, 18.35937, 41.429882, 18.385124);
((GeneralPath)shape).curveTo(41.247845, 18.38485, 39.094296, 18.364504, 38.882736, 18.364285);
((GeneralPath)shape).lineTo(39.14948, 10.38484);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5;
g.setTransform(defaultTransform__0_0_5);
g.setClip(clip__0_0_5);
float alpha__0_0_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6 = g.getClip();
AffineTransform defaultTransform__0_0_6 = g.getTransform();
g.transform(new AffineTransform(0.9945181608200073f, 0.0f, 0.0f, 0.9945181608200073f, -52.5611686706543f, 13.400577545166016f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(52.34590148925781,-14.093000411987305,52.28662872314453,19.10473108291626)));
g.setClip(clip);
// _0_0_6 is CompositeGraphicsNode
float alpha__0_0_6_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_0 = g.getClip();
AffineTransform defaultTransform__0_0_6_0 = g.getTransform();
g.transform(new AffineTransform(0.6257613301277161f, -0.043680861592292786f, 0.043680861592292786f, 0.6257613301277161f, 56.724266052246094f, -13.60598087310791f));
// _0_0_6_0 is CompositeGraphicsNode
float alpha__0_0_6_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_6_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6_0_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(35.39246368408203, 39.27751541137695), new Point2D.Double(14.344165802001953, 16.685270309448242), new float[] {0.0f,1.0f}, new Color[] {new Color(84, 154, 16, 255),new Color(138, 226, 52, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-0.8936172127723694f, 0.0f, 0.0f, 0.8936172127723694f, 46.13726806640625f, 2.1063826084136963f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31.40295, 6.1635723);
((GeneralPath)shape).curveTo(31.46335, 7.3352723, 29.531975, 9.249755, 29.52396, 10.328466);
((GeneralPath)shape).curveTo(29.52056, 10.786796, 29.59641, 11.970337, 29.77529, 12.382988);
((GeneralPath)shape).lineTo(37.678215, 12.382988);
((GeneralPath)shape).lineTo(37.700787, 22.245045);
((GeneralPath)shape).curveTo(41.07614, 22.186525, 41.815166, 20.866453, 43.93389, 21.394648);
((GeneralPath)shape).curveTo(45.287807, 22.840673, 45.478245, 28.458887, 41.28859, 28.483158);
((GeneralPath)shape).curveTo(39.741554, 28.513008, 38.71129, 27.58351, 37.678215, 27.561615);
((GeneralPath)shape).lineTo(37.678215, 40.994698);
((GeneralPath)shape).lineTo(27.82449, 40.962788);
((GeneralPath)shape).curveTo(27.671076, 39.547867, 29.66375, 36.71553, 29.63167, 34.88699);
((GeneralPath)shape).curveTo(29.5683, 30.70475, 20.658886, 29.280792, 20.671562, 34.88699);
((GeneralPath)shape).curveTo(20.608192, 37.51548, 22.357033, 36.77182, 22.46036, 40.9947);
((GeneralPath)shape).curveTo(22.12214, 40.9947, 15.823156, 40.9947, 12.469433, 40.9947);
((GeneralPath)shape).curveTo(12.469433, 40.9947, 12.508703, 28.872433, 12.497903, 28.554577);
((GeneralPath)shape).curveTo(12.407633, 25.852243, 9.479917, 29.261396, 7.1484437, 29.31815);
((GeneralPath)shape).curveTo(3.9746277, 29.23703, 3.1399207, 21.366337, 7.1536436, 21.268126);
((GeneralPath)shape).curveTo(9.008282, 21.368618, 10.146192, 22.544739, 12.492897, 22.47608);
((GeneralPath)shape).lineTo(12.501397, 12.382993);
((GeneralPath)shape).lineTo(23.336506, 12.382993);
((GeneralPath)shape).curveTo(23.3806, 10.95123, 21.565939, 7.989194, 21.597126, 6.2114453);
((GeneralPath)shape).curveTo(21.630297, 4.022305, 23.668034, 3.4443445, 26.184893, 3.4468172);
((GeneralPath)shape).curveTo(29.44792, 3.4499872, 31.40883, 4.267865, 31.402979, 6.1635733);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(66, 132, 4, 255);
stroke = new BasicStroke(0.8936167f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31.40295, 6.1635723);
((GeneralPath)shape).curveTo(31.46335, 7.3352723, 29.531975, 9.249755, 29.52396, 10.328466);
((GeneralPath)shape).curveTo(29.52056, 10.786796, 29.59641, 11.970337, 29.77529, 12.382988);
((GeneralPath)shape).lineTo(37.678215, 12.382988);
((GeneralPath)shape).lineTo(37.700787, 22.245045);
((GeneralPath)shape).curveTo(41.07614, 22.186525, 41.815166, 20.866453, 43.93389, 21.394648);
((GeneralPath)shape).curveTo(45.287807, 22.840673, 45.478245, 28.458887, 41.28859, 28.483158);
((GeneralPath)shape).curveTo(39.741554, 28.513008, 38.71129, 27.58351, 37.678215, 27.561615);
((GeneralPath)shape).lineTo(37.678215, 40.994698);
((GeneralPath)shape).lineTo(27.82449, 40.962788);
((GeneralPath)shape).curveTo(27.671076, 39.547867, 29.66375, 36.71553, 29.63167, 34.88699);
((GeneralPath)shape).curveTo(29.5683, 30.70475, 20.658886, 29.280792, 20.671562, 34.88699);
((GeneralPath)shape).curveTo(20.608192, 37.51548, 22.357033, 36.77182, 22.46036, 40.9947);
((GeneralPath)shape).curveTo(22.12214, 40.9947, 15.823156, 40.9947, 12.469433, 40.9947);
((GeneralPath)shape).curveTo(12.469433, 40.9947, 12.508703, 28.872433, 12.497903, 28.554577);
((GeneralPath)shape).curveTo(12.407633, 25.852243, 9.479917, 29.261396, 7.1484437, 29.31815);
((GeneralPath)shape).curveTo(3.9746277, 29.23703, 3.1399207, 21.366337, 7.1536436, 21.268126);
((GeneralPath)shape).curveTo(9.008282, 21.368618, 10.146192, 22.544739, 12.492897, 22.47608);
((GeneralPath)shape).lineTo(12.501397, 12.382993);
((GeneralPath)shape).lineTo(23.336506, 12.382993);
((GeneralPath)shape).curveTo(23.3806, 10.95123, 21.565939, 7.989194, 21.597126, 6.2114453);
((GeneralPath)shape).curveTo(21.630297, 4.022305, 23.668034, 3.4443445, 26.184893, 3.4468172);
((GeneralPath)shape).curveTo(29.44792, 3.4499872, 31.40883, 4.267865, 31.402979, 6.1635733);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6_0_0;
g.setTransform(defaultTransform__0_0_6_0_0);
g.setClip(clip__0_0_6_0_0);
float alpha__0_0_6_0_1 = origAlpha;
origAlpha = origAlpha * 0.7f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_6_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6_0_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.46140480041504, 15.329671859741211), new Point2D.Double(35.40412902832031, 43.02977752685547), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-0.8936172127723694f, 0.0f, 0.0f, 0.8936172127723694f, 46.13726806640625f, 2.1063826084136963f));
stroke = new BasicStroke(0.8936171f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30.502682, 6.15792);
((GeneralPath)shape).curveTo(30.432901, 7.4562697, 28.517963, 8.324206, 28.523886, 10.996136);
((GeneralPath)shape).curveTo(28.52398, 11.037886, 29.03464, 13.233776, 29.115795, 13.268136);
((GeneralPath)shape).lineTo(36.702206, 13.284096);
((GeneralPath)shape).lineTo(36.690117, 23.04264);
((GeneralPath)shape).curveTo(40.86615, 23.401447, 40.074345, 22.327942, 43.301483, 22.100058);
((GeneralPath)shape).curveTo(44.056362, 23.45384, 44.47935, 27.545666, 41.209206, 27.568924);
((GeneralPath)shape).curveTo(40.020084, 27.642654, 38.912888, 26.639105, 36.72685, 26.685825);
((GeneralPath)shape).lineTo(36.66264, 40.082546);
((GeneralPath)shape).curveTo(33.314754, 40.072346, 30.769432, 40.107285, 28.885332, 40.097095);
((GeneralPath)shape).curveTo(29.073425, 39.092266, 30.538803, 36.701233, 30.50806, 34.677704);
((GeneralPath)shape).curveTo(30.399466, 29.409306, 19.800608, 28.411774, 19.732965, 34.645794);
((GeneralPath)shape).curveTo(19.815855, 37.46782, 21.587463, 38.442677, 21.510948, 40.095768);
((GeneralPath)shape).curveTo(21.18684, 40.095768, 16.679934, 40.11173, 13.466125, 40.11173);
((GeneralPath)shape).curveTo(13.466125, 40.11173, 13.455034, 27.513283, 13.448275, 27.208588);
((GeneralPath)shape).curveTo(13.384335, 24.190216, 9.721873, 28.246996, 7.4876676, 28.30138);
((GeneralPath)shape).curveTo(5.0555744, 28.22364, 4.5716324, 22.373854, 7.2444115, 22.27974);
((GeneralPath)shape).curveTo(9.021675, 22.37604, 11.239811, 23.706198, 13.488613, 23.640404);
((GeneralPath)shape).lineTo(13.477353, 13.268141);
((GeneralPath)shape).lineTo(24.237494, 13.268141);
((GeneralPath)shape).curveTo(24.550562, 11.106256, 22.426434, 7.8455467, 22.45632, 6.141964);
((GeneralPath)shape).curveTo(22.42701, 4.3733134, 25.169569, 4.341993, 26.069422, 4.321795);
((GeneralPath)shape).curveTo(29.195534, 4.251627, 30.460426, 4.947684, 30.50269, 6.157922);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6_0_1;
g.setTransform(defaultTransform__0_0_6_0_1);
g.setClip(clip__0_0_6_0_1);
origAlpha = alpha__0_0_6_0;
g.setTransform(defaultTransform__0_0_6_0);
g.setClip(clip__0_0_6_0);
float alpha__0_0_6_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_1 = g.getClip();
AffineTransform defaultTransform__0_0_6_1 = g.getTransform();
g.transform(new AffineTransform(0.6163259148597717f, 0.11673741042613983f, -0.11673741042613983f, 0.6163259148597717f, 69.2288589477539f, -15.91975212097168f));
// _0_0_6_1 is CompositeGraphicsNode
float alpha__0_0_6_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_1_0 = g.getClip();
AffineTransform defaultTransform__0_0_6_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6_1_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(35.39246368408203, 39.27751541137695), new Point2D.Double(14.344165802001953, 16.685270309448242), new float[] {0.0f,1.0f}, new Color[] {new Color(84, 154, 16, 255),new Color(138, 226, 52, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-0.8936172127723694f, 0.0f, 0.0f, 0.8936172127723694f, 46.13726806640625f, 2.1063826084136963f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31.40295, 6.1635723);
((GeneralPath)shape).curveTo(31.46335, 7.3352723, 29.531975, 9.249755, 29.52396, 10.328466);
((GeneralPath)shape).curveTo(29.52056, 10.786796, 29.59641, 11.970337, 29.77529, 12.382988);
((GeneralPath)shape).lineTo(37.678215, 12.382988);
((GeneralPath)shape).lineTo(37.700787, 22.245045);
((GeneralPath)shape).curveTo(41.07614, 22.186525, 41.815166, 20.866453, 43.93389, 21.394648);
((GeneralPath)shape).curveTo(45.287807, 22.840673, 45.478245, 28.458887, 41.28859, 28.483158);
((GeneralPath)shape).curveTo(39.741554, 28.513008, 38.71129, 27.58351, 37.678215, 27.561615);
((GeneralPath)shape).lineTo(37.678215, 40.994698);
((GeneralPath)shape).lineTo(27.82449, 40.962788);
((GeneralPath)shape).curveTo(27.671076, 39.547867, 29.66375, 36.71553, 29.63167, 34.88699);
((GeneralPath)shape).curveTo(29.5683, 30.70475, 20.658886, 29.280792, 20.671562, 34.88699);
((GeneralPath)shape).curveTo(20.608192, 37.51548, 22.357033, 36.77182, 22.46036, 40.9947);
((GeneralPath)shape).curveTo(22.12214, 40.9947, 15.823156, 40.9947, 12.469433, 40.9947);
((GeneralPath)shape).curveTo(12.469433, 40.9947, 12.508703, 28.872433, 12.497903, 28.554577);
((GeneralPath)shape).curveTo(12.407633, 25.852243, 9.479917, 29.261396, 7.1484437, 29.31815);
((GeneralPath)shape).curveTo(3.9746277, 29.23703, 3.1399207, 21.366337, 7.1536436, 21.268126);
((GeneralPath)shape).curveTo(9.008282, 21.368618, 10.146192, 22.544739, 12.492897, 22.47608);
((GeneralPath)shape).lineTo(12.501397, 12.382993);
((GeneralPath)shape).lineTo(23.336506, 12.382993);
((GeneralPath)shape).curveTo(23.3806, 10.95123, 21.565939, 7.989194, 21.597126, 6.2114453);
((GeneralPath)shape).curveTo(21.630297, 4.022305, 23.668034, 3.4443445, 26.184893, 3.4468172);
((GeneralPath)shape).curveTo(29.44792, 3.4499872, 31.40883, 4.267865, 31.402979, 6.1635733);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(66, 132, 4, 255);
stroke = new BasicStroke(0.8936167f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31.40295, 6.1635723);
((GeneralPath)shape).curveTo(31.46335, 7.3352723, 29.531975, 9.249755, 29.52396, 10.328466);
((GeneralPath)shape).curveTo(29.52056, 10.786796, 29.59641, 11.970337, 29.77529, 12.382988);
((GeneralPath)shape).lineTo(37.678215, 12.382988);
((GeneralPath)shape).lineTo(37.700787, 22.245045);
((GeneralPath)shape).curveTo(41.07614, 22.186525, 41.815166, 20.866453, 43.93389, 21.394648);
((GeneralPath)shape).curveTo(45.287807, 22.840673, 45.478245, 28.458887, 41.28859, 28.483158);
((GeneralPath)shape).curveTo(39.741554, 28.513008, 38.71129, 27.58351, 37.678215, 27.561615);
((GeneralPath)shape).lineTo(37.678215, 40.994698);
((GeneralPath)shape).lineTo(27.82449, 40.962788);
((GeneralPath)shape).curveTo(27.671076, 39.547867, 29.66375, 36.71553, 29.63167, 34.88699);
((GeneralPath)shape).curveTo(29.5683, 30.70475, 20.658886, 29.280792, 20.671562, 34.88699);
((GeneralPath)shape).curveTo(20.608192, 37.51548, 22.357033, 36.77182, 22.46036, 40.9947);
((GeneralPath)shape).curveTo(22.12214, 40.9947, 15.823156, 40.9947, 12.469433, 40.9947);
((GeneralPath)shape).curveTo(12.469433, 40.9947, 12.508703, 28.872433, 12.497903, 28.554577);
((GeneralPath)shape).curveTo(12.407633, 25.852243, 9.479917, 29.261396, 7.1484437, 29.31815);
((GeneralPath)shape).curveTo(3.9746277, 29.23703, 3.1399207, 21.366337, 7.1536436, 21.268126);
((GeneralPath)shape).curveTo(9.008282, 21.368618, 10.146192, 22.544739, 12.492897, 22.47608);
((GeneralPath)shape).lineTo(12.501397, 12.382993);
((GeneralPath)shape).lineTo(23.336506, 12.382993);
((GeneralPath)shape).curveTo(23.3806, 10.95123, 21.565939, 7.989194, 21.597126, 6.2114453);
((GeneralPath)shape).curveTo(21.630297, 4.022305, 23.668034, 3.4443445, 26.184893, 3.4468172);
((GeneralPath)shape).curveTo(29.44792, 3.4499872, 31.40883, 4.267865, 31.402979, 6.1635733);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6_1_0;
g.setTransform(defaultTransform__0_0_6_1_0);
g.setClip(clip__0_0_6_1_0);
float alpha__0_0_6_1_1 = origAlpha;
origAlpha = origAlpha * 0.7f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6_1_1 = g.getClip();
AffineTransform defaultTransform__0_0_6_1_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6_1_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.46140480041504, 15.329671859741211), new Point2D.Double(35.40412902832031, 43.02977752685547), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-0.8936172127723694f, 0.0f, 0.0f, 0.8936172127723694f, 46.13726806640625f, 2.1063826084136963f));
stroke = new BasicStroke(0.8936171f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30.502682, 6.15792);
((GeneralPath)shape).curveTo(30.432901, 7.4562697, 28.517963, 8.324206, 28.523886, 10.996136);
((GeneralPath)shape).curveTo(28.52398, 11.037886, 29.03464, 13.233776, 29.115795, 13.268136);
((GeneralPath)shape).lineTo(36.702206, 13.284096);
((GeneralPath)shape).lineTo(36.690117, 23.04264);
((GeneralPath)shape).curveTo(40.86615, 23.401447, 40.074345, 22.327942, 43.301483, 22.100058);
((GeneralPath)shape).curveTo(44.056362, 23.45384, 44.47935, 27.545666, 41.209206, 27.568924);
((GeneralPath)shape).curveTo(40.020084, 27.642654, 38.912888, 26.639105, 36.72685, 26.685825);
((GeneralPath)shape).lineTo(36.66264, 40.082546);
((GeneralPath)shape).curveTo(33.314754, 40.072346, 30.769432, 40.107285, 28.885332, 40.097095);
((GeneralPath)shape).curveTo(29.073425, 39.092266, 30.538803, 36.701233, 30.50806, 34.677704);
((GeneralPath)shape).curveTo(30.399466, 29.409306, 19.800608, 28.411774, 19.732965, 34.645794);
((GeneralPath)shape).curveTo(19.815855, 37.46782, 21.587463, 38.442677, 21.510948, 40.095768);
((GeneralPath)shape).curveTo(21.18684, 40.095768, 16.679934, 40.11173, 13.466125, 40.11173);
((GeneralPath)shape).curveTo(13.466125, 40.11173, 13.455034, 27.513283, 13.448275, 27.208588);
((GeneralPath)shape).curveTo(13.384335, 24.190216, 9.721873, 28.246996, 7.4876676, 28.30138);
((GeneralPath)shape).curveTo(5.0555744, 28.22364, 4.5716324, 22.373854, 7.2444115, 22.27974);
((GeneralPath)shape).curveTo(9.021675, 22.37604, 11.239811, 23.706198, 13.488613, 23.640404);
((GeneralPath)shape).lineTo(13.477353, 13.268141);
((GeneralPath)shape).lineTo(24.237494, 13.268141);
((GeneralPath)shape).curveTo(24.550562, 11.106256, 22.426434, 7.8455467, 22.45632, 6.141964);
((GeneralPath)shape).curveTo(22.42701, 4.3733134, 25.169569, 4.341993, 26.069422, 4.321795);
((GeneralPath)shape).curveTo(29.195534, 4.251627, 30.460426, 4.947684, 30.50269, 6.157922);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6_1_1;
g.setTransform(defaultTransform__0_0_6_1_1);
g.setClip(clip__0_0_6_1_1);
origAlpha = alpha__0_0_6_1;
g.setTransform(defaultTransform__0_0_6_1);
g.setClip(clip__0_0_6_1);
origAlpha = alpha__0_0_6;
g.setTransform(defaultTransform__0_0_6);
g.setClip(clip__0_0_6);
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
        return 1;
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
	public PluginUnloaderIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public PluginUnloaderIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public PluginUnloaderIcon(int width, int height) {
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

