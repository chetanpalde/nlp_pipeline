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
public class HeapDumpIcon implements
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
g.transform(new AffineTransform(8.333333015441895f, 0.0f, 0.0f, 8.333333015441895f, -0.0f, -0.0f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,48.00000183105475,48.00000183105475)));
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
origAlpha = origAlpha * 0.41237f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(0.024896999821066856f, 0.0f, 0.0f, 0.02315800078213215f, 46.64400100708008f, 37.57600021362305f));
// _0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(302.8599853515625, 366.6499938964844), new Point2D.Double(302.8599853515625, 609.510009765625), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(0, 0, 0, 0),new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, -1892.199951171875f, -872.8900146484375f));
shape = new Rectangle2D.Double(-1559.300048828125, -150.6999969482422, 1339.5999755859375, 478.3599853515625);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0);
g.setClip(clip__0_0_0_0);
float alpha__0_0_0_1 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_1 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7100219726562, 486.6499938964844), 117.14f, new Point2D.Double(605.7100219726562, 486.6499938964844), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, -1891.5999755859375f, -872.8900146484375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-219.62, -150.68);
((GeneralPath)shape).lineTo(-219.62, 327.65);
((GeneralPath)shape).curveTo(-76.75, 328.55045, 125.78, 220.48, 125.78, 88.45);
((GeneralPath)shape).curveTo(125.78, -43.58, -33.660004, -150.68001, -219.62, -150.68001);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_1;
g.setTransform(defaultTransform__0_0_0_1);
g.setClip(clip__0_0_0_1);
float alpha__0_0_0_2 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_2 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7100219726562, 486.6499938964844), 117.14f, new Point2D.Double(605.7100219726562, 486.6499938964844), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, 112.76000213623047f, -872.8900146484375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-1559.3, -150.68);
((GeneralPath)shape).lineTo(-1559.3, 327.65);
((GeneralPath)shape).curveTo(-1702.17, 328.55045, -1904.7001, 220.48, -1904.7001, 88.45);
((GeneralPath)shape).curveTo(-1904.7001, -43.58, -1745.26, -150.68001, -1559.3, -150.68001);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_2;
g.setTransform(defaultTransform__0_0_0_2);
g.setClip(clip__0_0_0_2);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
float alpha__0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_1 = g.getTransform();
g.transform(new AffineTransform(0.017754999920725822f, 0.0f, 0.0f, 0.016516000032424927f, 33.41699981689453f, 39.28900146484375f));
// _0_0_1 is CompositeGraphicsNode
float alpha__0_0_1_0 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_0 = g.getClip();
AffineTransform defaultTransform__0_0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(302.8599853515625, 366.6499938964844), new Point2D.Double(302.8599853515625, 609.510009765625), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(0, 0, 0, 0),new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, -1892.199951171875f, -872.8900146484375f));
shape = new Rectangle2D.Double(-1559.300048828125, -150.6999969482422, 1339.5999755859375, 478.3599853515625);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_0;
g.setTransform(defaultTransform__0_0_1_0);
g.setClip(clip__0_0_1_0);
float alpha__0_0_1_1 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_1 = g.getClip();
AffineTransform defaultTransform__0_0_1_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_1 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7100219726562, 486.6499938964844), 117.14f, new Point2D.Double(605.7100219726562, 486.6499938964844), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, -1891.5999755859375f, -872.8900146484375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-219.62, -150.68);
((GeneralPath)shape).lineTo(-219.62, 327.65);
((GeneralPath)shape).curveTo(-76.75, 328.55045, 125.78, 220.48, 125.78, 88.45);
((GeneralPath)shape).curveTo(125.78, -43.58, -33.660004, -150.68001, -219.62, -150.68001);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_1;
g.setTransform(defaultTransform__0_0_1_1);
g.setClip(clip__0_0_1_1);
float alpha__0_0_1_2 = origAlpha;
origAlpha = origAlpha * 0.40206f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1_2 = g.getClip();
AffineTransform defaultTransform__0_0_1_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1_2 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7100219726562, 486.6499938964844), 117.14f, new Point2D.Double(605.7100219726562, 486.6499938964844), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-2.774399995803833f, 0.0f, 0.0f, 1.9696999788284302f, 112.76000213623047f, -872.8900146484375f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-1559.3, -150.68);
((GeneralPath)shape).lineTo(-1559.3, 327.65);
((GeneralPath)shape).curveTo(-1702.17, 328.55045, -1904.7001, 220.48, -1904.7001, 88.45);
((GeneralPath)shape).curveTo(-1904.7001, -43.58, -1745.26, -150.68001, -1559.3, -150.68001);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_1_2;
g.setTransform(defaultTransform__0_0_1_2);
g.setClip(clip__0_0_1_2);
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
paint = new LinearGradientPaint(new Point2D.Double(20.047000885009766, 22.746999740600586), new Point2D.Double(39.00299835205078, 49.79399871826172), new float[] {0.0f,1.0f}, new Color[] {new Color(120, 134, 0, 255),new Color(80, 89, 0, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 6.0f, -4.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(1.501, 13.63);
((GeneralPath)shape).lineTo(1.50491, 36.35);
((GeneralPath)shape).curveTo(1.50491, 36.42888, 1.593712, 36.50021, 1.69169, 36.50021);
((GeneralPath)shape).lineTo(4.50329, 36.51584);
((GeneralPath)shape).lineTo(4.49938, 41.36914);
((GeneralPath)shape).curveTo(4.49938, 41.44041, 4.521777, 41.49548, 4.62366, 41.49591);
((GeneralPath)shape).lineTo(31.379662, 41.48811);
((GeneralPath)shape).curveTo(31.472082, 41.48441, 31.503942, 41.42437, 31.503942, 41.36134);
((GeneralPath)shape).lineTo(31.507843, 36.49244);
((GeneralPath)shape).lineTo(46.389843, 36.50024);
((GeneralPath)shape).curveTo(46.483593, 36.50024, 46.505882, 36.369972, 46.502403, 36.28753);
((GeneralPath)shape).lineTo(46.502403, 13.630529);
((GeneralPath)shape).curveTo(46.5063, 13.548499, 46.456894, 13.508539, 46.374214, 13.503759);
((GeneralPath)shape).lineTo(1.6252136, 13.503759);
((GeneralPath)shape).curveTo(1.5467666, 13.5080595, 1.4931237, 13.55631, 1.5009336, 13.630529);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(58, 62, 0, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(1.501, 13.63);
((GeneralPath)shape).lineTo(1.50491, 36.35);
((GeneralPath)shape).curveTo(1.50491, 36.42888, 1.593712, 36.50021, 1.69169, 36.50021);
((GeneralPath)shape).lineTo(4.50329, 36.51584);
((GeneralPath)shape).lineTo(4.49938, 41.36914);
((GeneralPath)shape).curveTo(4.49938, 41.44041, 4.521777, 41.49548, 4.62366, 41.49591);
((GeneralPath)shape).lineTo(31.379662, 41.48811);
((GeneralPath)shape).curveTo(31.472082, 41.48441, 31.503942, 41.42437, 31.503942, 41.36134);
((GeneralPath)shape).lineTo(31.507843, 36.49244);
((GeneralPath)shape).lineTo(46.389843, 36.50024);
((GeneralPath)shape).curveTo(46.483593, 36.50024, 46.505882, 36.369972, 46.502403, 36.28753);
((GeneralPath)shape).lineTo(46.502403, 13.630529);
((GeneralPath)shape).curveTo(46.5063, 13.548499, 46.456894, 13.508539, 46.374214, 13.503759);
((GeneralPath)shape).lineTo(1.6252136, 13.503759);
((GeneralPath)shape).curveTo(1.5467666, 13.5080595, 1.4931237, 13.55631, 1.5009336, 13.630529);
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
// _0_0_3 is CompositeGraphicsNode
float alpha__0_0_3_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3_0 = g.getClip();
AffineTransform defaultTransform__0_0_3_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_3_0 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(7.4419, 38.961);
((GeneralPath)shape).lineTo(7.4967456, 33.180298);
((GeneralPath)shape).lineTo(9.397745, 30.513798);
((GeneralPath)shape).lineTo(38.625748, 30.513798);
((GeneralPath)shape).lineTo(40.573746, 28.392498);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_3_0;
g.setTransform(defaultTransform__0_0_3_0);
g.setClip(clip__0_0_3_0);
float alpha__0_0_3_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3_1 = g.getClip();
AffineTransform defaultTransform__0_0_3_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_3_1 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(13.5, 38.961);
((GeneralPath)shape).lineTo(13.55485, 33.180298);
((GeneralPath)shape).lineTo(15.45585, 30.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_3_1;
g.setTransform(defaultTransform__0_0_3_1);
g.setClip(clip__0_0_3_1);
float alpha__0_0_3_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3_2 = g.getClip();
AffineTransform defaultTransform__0_0_3_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_3_2 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(10.5, 38.961);
((GeneralPath)shape).lineTo(10.55485, 33.180298);
((GeneralPath)shape).lineTo(12.45585, 30.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_3_2;
g.setTransform(defaultTransform__0_0_3_2);
g.setClip(clip__0_0_3_2);
origAlpha = alpha__0_0_3;
g.setTransform(defaultTransform__0_0_3);
g.setClip(clip__0_0_3);
float alpha__0_0_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4 = g.getClip();
AffineTransform defaultTransform__0_0_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, -4.0f));
// _0_0_4 is CompositeGraphicsNode
float alpha__0_0_4_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_0 = g.getClip();
AffineTransform defaultTransform__0_0_4_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_0 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(16.5, 42.961);
((GeneralPath)shape).lineTo(16.55485, 37.180298);
((GeneralPath)shape).lineTo(18.455849, 34.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_0;
g.setTransform(defaultTransform__0_0_4_0);
g.setClip(clip__0_0_4_0);
float alpha__0_0_4_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_1 = g.getClip();
AffineTransform defaultTransform__0_0_4_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_1 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(19.5, 42.961);
((GeneralPath)shape).lineTo(19.55485, 37.180298);
((GeneralPath)shape).lineTo(21.455849, 34.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_1;
g.setTransform(defaultTransform__0_0_4_1);
g.setClip(clip__0_0_4_1);
float alpha__0_0_4_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_2 = g.getClip();
AffineTransform defaultTransform__0_0_4_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_2 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(22.5, 42.961);
((GeneralPath)shape).lineTo(22.55485, 37.180298);
((GeneralPath)shape).lineTo(24.455849, 34.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_2;
g.setTransform(defaultTransform__0_0_4_2);
g.setClip(clip__0_0_4_2);
float alpha__0_0_4_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_3 = g.getClip();
AffineTransform defaultTransform__0_0_4_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_3 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(25.544, 42.961);
((GeneralPath)shape).lineTo(25.599, 37.18);
((GeneralPath)shape).lineTo(27.5, 34.514);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_3;
g.setTransform(defaultTransform__0_0_4_3);
g.setClip(clip__0_0_4_3);
float alpha__0_0_4_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_4 = g.getClip();
AffineTransform defaultTransform__0_0_4_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_4 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28.5, 42.961);
((GeneralPath)shape).lineTo(28.55485, 37.180298);
((GeneralPath)shape).lineTo(30.455849, 34.513798);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_4;
g.setTransform(defaultTransform__0_0_4_4);
g.setClip(clip__0_0_4_4);
float alpha__0_0_4_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_5 = g.getClip();
AffineTransform defaultTransform__0_0_4_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_5 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(36.555, 34.719);
((GeneralPath)shape).lineTo(38.5443, 32.494503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_5;
g.setTransform(defaultTransform__0_0_4_5);
g.setClip(clip__0_0_4_5);
float alpha__0_0_4_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_6 = g.getClip();
AffineTransform defaultTransform__0_0_4_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_6 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28.555, 34.719);
((GeneralPath)shape).lineTo(30.5443, 32.494503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_6;
g.setTransform(defaultTransform__0_0_4_6);
g.setClip(clip__0_0_4_6);
float alpha__0_0_4_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_7 = g.getClip();
AffineTransform defaultTransform__0_0_4_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_7 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(26.555, 34.719);
((GeneralPath)shape).lineTo(28.5443, 32.494503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_7;
g.setTransform(defaultTransform__0_0_4_7);
g.setClip(clip__0_0_4_7);
float alpha__0_0_4_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_8 = g.getClip();
AffineTransform defaultTransform__0_0_4_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_8 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(18.555, 34.719);
((GeneralPath)shape).lineTo(20.5443, 32.494503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_8;
g.setTransform(defaultTransform__0_0_4_8);
g.setClip(clip__0_0_4_8);
float alpha__0_0_4_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_9 = g.getClip();
AffineTransform defaultTransform__0_0_4_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_9 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(16.555, 34.719);
((GeneralPath)shape).lineTo(18.5443, 32.494503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_9;
g.setTransform(defaultTransform__0_0_4_9);
g.setClip(clip__0_0_4_9);
float alpha__0_0_4_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_10 = g.getClip();
AffineTransform defaultTransform__0_0_4_10 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_10 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(12.356, 34.719);
((GeneralPath)shape).lineTo(10.544, 32.495003);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_10;
g.setTransform(defaultTransform__0_0_4_10);
g.setClip(clip__0_0_4_10);
float alpha__0_0_4_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4_11 = g.getClip();
AffineTransform defaultTransform__0_0_4_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4_11 is ShapeNode
paint = new Color(152, 163, 50, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(10.356, 34.719);
((GeneralPath)shape).lineTo(8.5442, 32.495003);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4_11;
g.setTransform(defaultTransform__0_0_4_11);
g.setClip(clip__0_0_4_11);
origAlpha = alpha__0_0_4;
g.setTransform(defaultTransform__0_0_4);
g.setClip(clip__0_0_4);
float alpha__0_0_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5 = g.getClip();
AffineTransform defaultTransform__0_0_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, -4.0f));
// _0_0_5 is CompositeGraphicsNode
float alpha__0_0_5_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_0 = g.getClip();
AffineTransform defaultTransform__0_0_5_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, -5.906199932098389f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(6.9922, 39.594);
((GeneralPath)shape).curveTo(7.79187, 39.594, 7.98439, 40.22325, 7.98439, 40.50025);
((GeneralPath)shape).lineTo(7.98439, 44.92005);
((GeneralPath)shape).lineTo(5.9999895, 44.92005);
((GeneralPath)shape).lineTo(5.9999895, 40.500053);
((GeneralPath)shape).curveTo(5.9999895, 40.223053, 6.2081294, 39.593803, 6.9921794, 39.593803);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_0;
g.setTransform(defaultTransform__0_0_5_0);
g.setClip(clip__0_0_5_0);
float alpha__0_0_5_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_1 = g.getClip();
AffineTransform defaultTransform__0_0_5_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, -2.8905999660491943f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(10.008, 39.594);
((GeneralPath)shape).curveTo(10.807, 39.594, 11.0, 40.223003, 11.0, 40.5);
((GeneralPath)shape).lineTo(11.0, 44.9198);
((GeneralPath)shape).lineTo(9.0156, 44.9198);
((GeneralPath)shape).lineTo(9.0156, 40.5);
((GeneralPath)shape).curveTo(9.0156, 40.223, 9.223741, 39.59375, 10.007791, 39.59375);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_1;
g.setTransform(defaultTransform__0_0_5_1);
g.setClip(clip__0_0_5_1);
float alpha__0_0_5_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_2 = g.getClip();
AffineTransform defaultTransform__0_0_5_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 0.09375f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(12.992, 39.594);
((GeneralPath)shape).curveTo(13.79167, 39.594, 13.98419, 40.22325, 13.98419, 40.50025);
((GeneralPath)shape).lineTo(13.98419, 44.92005);
((GeneralPath)shape).lineTo(11.99979, 44.92005);
((GeneralPath)shape).lineTo(11.99979, 40.500053);
((GeneralPath)shape).curveTo(11.99979, 40.223053, 12.207931, 39.593803, 12.991981, 39.593803);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_2;
g.setTransform(defaultTransform__0_0_5_2);
g.setClip(clip__0_0_5_2);
float alpha__0_0_5_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_3 = g.getClip();
AffineTransform defaultTransform__0_0_5_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 3.0938000679016113f, -0.01359499990940094f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15.992, 39.58);
((GeneralPath)shape).curveTo(16.79167, 39.58, 16.98419, 40.20925, 16.98419, 40.48625);
((GeneralPath)shape).lineTo(16.98419, 44.90605);
((GeneralPath)shape).lineTo(14.99979, 44.90605);
((GeneralPath)shape).lineTo(14.99979, 40.48625);
((GeneralPath)shape).curveTo(14.99979, 40.20925, 15.207931, 39.58, 15.991981, 39.58);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_3;
g.setTransform(defaultTransform__0_0_5_3);
g.setClip(clip__0_0_5_3);
float alpha__0_0_5_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_4 = g.getClip();
AffineTransform defaultTransform__0_0_5_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 6.093800067901611f, -0.01359499990940094f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(18.992, 39.58);
((GeneralPath)shape).curveTo(19.79167, 39.58, 19.98419, 40.20925, 19.98419, 40.48625);
((GeneralPath)shape).lineTo(19.98419, 44.90605);
((GeneralPath)shape).lineTo(17.99979, 44.90605);
((GeneralPath)shape).lineTo(17.99979, 40.48625);
((GeneralPath)shape).curveTo(17.99979, 40.20925, 18.20793, 39.58, 18.99198, 39.58);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_4;
g.setTransform(defaultTransform__0_0_5_4);
g.setClip(clip__0_0_5_4);
float alpha__0_0_5_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_5 = g.getClip();
AffineTransform defaultTransform__0_0_5_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 9.109399795532227f, -0.01359499990940094f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(22.008, 39.58);
((GeneralPath)shape).curveTo(22.807669, 39.58, 23.000189, 40.20925, 23.000189, 40.48625);
((GeneralPath)shape).lineTo(23.000189, 44.90605);
((GeneralPath)shape).lineTo(21.015789, 44.90605);
((GeneralPath)shape).lineTo(21.015789, 40.48625);
((GeneralPath)shape).curveTo(21.015789, 40.20925, 21.223928, 39.58, 22.007978, 39.58);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_5;
g.setTransform(defaultTransform__0_0_5_5);
g.setClip(clip__0_0_5_5);
float alpha__0_0_5_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_6 = g.getClip();
AffineTransform defaultTransform__0_0_5_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_6 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 12.109000205993652f, -0.01359499990940094f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(25.008, 39.58);
((GeneralPath)shape).curveTo(25.807669, 39.58, 26.000189, 40.20925, 26.000189, 40.48625);
((GeneralPath)shape).lineTo(26.000189, 44.90605);
((GeneralPath)shape).lineTo(24.015789, 44.90605);
((GeneralPath)shape).lineTo(24.015789, 40.48625);
((GeneralPath)shape).curveTo(24.015789, 40.20925, 24.223928, 39.58, 25.007978, 39.58);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_6;
g.setTransform(defaultTransform__0_0_5_6);
g.setClip(clip__0_0_5_6);
float alpha__0_0_5_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5_7 = g.getClip();
AffineTransform defaultTransform__0_0_5_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(6.2210001945495605, 42.89099884033203), new Point2D.Double(6.23769998550415, 39.266998291015625), new float[] {0.0f,1.0f}, new Color[] {new Color(235, 255, 57, 255),new Color(235, 255, 57, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.9844000339508057f, 0.0f, 0.0f, 1.0f, 15.109000205993652f, -0.01359499990940094f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28.008, 39.58);
((GeneralPath)shape).curveTo(28.807669, 39.58, 29.000189, 40.20925, 29.000189, 40.48625);
((GeneralPath)shape).lineTo(29.000189, 44.90605);
((GeneralPath)shape).lineTo(27.015789, 44.90605);
((GeneralPath)shape).lineTo(27.015789, 40.48625);
((GeneralPath)shape).curveTo(27.015789, 40.20925, 27.223928, 39.58, 28.007978, 39.58);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_5_7;
g.setTransform(defaultTransform__0_0_5_7);
g.setClip(clip__0_0_5_7);
origAlpha = alpha__0_0_5;
g.setTransform(defaultTransform__0_0_5);
g.setClip(clip__0_0_5);
float alpha__0_0_6 = origAlpha;
origAlpha = origAlpha * 0.23864f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6 = g.getClip();
AffineTransform defaultTransform__0_0_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6 is ShapeNode
paint = new Color(255, 255, 255, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(2.5012, 35.502);
((GeneralPath)shape).lineTo(5.4993, 35.50194);
((GeneralPath)shape).lineTo(5.510349, 40.485043);
((GeneralPath)shape).lineTo(30.511349, 40.482643);
((GeneralPath)shape).lineTo(30.50845, 35.495342);
((GeneralPath)shape).lineTo(45.49845, 35.498043);
((GeneralPath)shape).curveTo(45.502453, 35.497635, 45.50215, 14.504044, 45.50215, 14.504044);
((GeneralPath)shape).lineTo(2.5051498, 14.501843);
((GeneralPath)shape).curveTo(2.5091898, 22.840443, 2.5016499, 35.501842, 2.5016499, 35.501842);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6;
g.setTransform(defaultTransform__0_0_6);
g.setClip(clip__0_0_6);
float alpha__0_0_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7 = g.getClip();
AffineTransform defaultTransform__0_0_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, -4.939300060272217f));
// _0_0_7 is CompositeGraphicsNode
float alpha__0_0_7_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_0 = g.getClip();
AffineTransform defaultTransform__0_0_7_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_0 is ShapeNode
paint = new Color(85, 87, 83, 255);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.fill(shape);
paint = new Color(46, 52, 54, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_7_0;
g.setTransform(defaultTransform__0_0_7_0);
g.setClip(clip__0_0_7_0);
float alpha__0_0_7_1 = origAlpha;
origAlpha = origAlpha * 0.17045f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_1 = g.getClip();
AffineTransform defaultTransform__0_0_7_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.96500015258789, 20.166000366210938), new Point2D.Double(18.968000411987305, 31.347000122070312), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.2703999876976013f, 0.0f, 0.0f, 1.5633000135421753f, -43.86800003051758f, -61.926998138427734f));
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new Rectangle2D.Double(-41.58300018310547, -31.5, 4.083399772644043, 8.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_7_1;
g.setTransform(defaultTransform__0_0_7_1);
g.setClip(clip__0_0_7_1);
float alpha__0_0_7_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2 = g.getClip();
AffineTransform defaultTransform__0_0_7_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_7_2 is CompositeGraphicsNode
float alpha__0_0_7_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_0;
g.setTransform(defaultTransform__0_0_7_2_0);
g.setClip(clip__0_0_7_2_0);
float alpha__0_0_7_2_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_1 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_1;
g.setTransform(defaultTransform__0_0_7_2_1);
g.setClip(clip__0_0_7_2_1);
float alpha__0_0_7_2_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_2 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 21.5), new Point2D.Double(27.0, 21.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_2;
g.setTransform(defaultTransform__0_0_7_2_2);
g.setClip(clip__0_0_7_2_2);
float alpha__0_0_7_2_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_3 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_3;
g.setTransform(defaultTransform__0_0_7_2_3);
g.setClip(clip__0_0_7_2_3);
float alpha__0_0_7_2_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_4 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_4 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_7_2_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 21.0f));
shape = new Rectangle2D.Double(20.92799949645996, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_4;
g.setTransform(defaultTransform__0_0_7_2_4);
g.setClip(clip__0_0_7_2_4);
float alpha__0_0_7_2_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_5 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_5 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_7_2_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 19.0f));
shape = new Rectangle2D.Double(20.92799949645996, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_5;
g.setTransform(defaultTransform__0_0_7_2_5);
g.setClip(clip__0_0_7_2_5);
float alpha__0_0_7_2_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_6 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_6 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_7_2_6 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 11.5), new Point2D.Double(4.712699890136719, 11.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 21.0f));
shape = new Rectangle2D.Double(32.0, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_6;
g.setTransform(defaultTransform__0_0_7_2_6);
g.setClip(clip__0_0_7_2_6);
float alpha__0_0_7_2_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_7 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_7 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_7_2_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 13.5), new Point2D.Double(4.712699890136719, 13.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 19.0f));
shape = new Rectangle2D.Double(32.0, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_7;
g.setTransform(defaultTransform__0_0_7_2_7);
g.setClip(clip__0_0_7_2_7);
float alpha__0_0_7_2_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_8 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_8 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_8 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -50.0f));
shape = new Rectangle2D.Double(-44.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_8;
g.setTransform(defaultTransform__0_0_7_2_8);
g.setClip(clip__0_0_7_2_8);
float alpha__0_0_7_2_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_9 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_9 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -50.0f));
shape = new Rectangle2D.Double(35.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_9;
g.setTransform(defaultTransform__0_0_7_2_9);
g.setClip(clip__0_0_7_2_9);
float alpha__0_0_7_2_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_10 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_10 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_10 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -48.0f));
shape = new Rectangle2D.Double(-44.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_10;
g.setTransform(defaultTransform__0_0_7_2_10);
g.setClip(clip__0_0_7_2_10);
float alpha__0_0_7_2_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7_2_11 = g.getClip();
AffineTransform defaultTransform__0_0_7_2_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_7_2_11 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -48.0f));
shape = new Rectangle2D.Double(35.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_7_2_11;
g.setTransform(defaultTransform__0_0_7_2_11);
g.setClip(clip__0_0_7_2_11);
origAlpha = alpha__0_0_7_2;
g.setTransform(defaultTransform__0_0_7_2);
g.setClip(clip__0_0_7_2);
origAlpha = alpha__0_0_7;
g.setTransform(defaultTransform__0_0_7);
g.setClip(clip__0_0_7);
float alpha__0_0_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8 = g.getClip();
AffineTransform defaultTransform__0_0_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -10.0f, -4.939300060272217f));
// _0_0_8 is CompositeGraphicsNode
float alpha__0_0_8_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_0 = g.getClip();
AffineTransform defaultTransform__0_0_8_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_0 is ShapeNode
paint = new Color(85, 87, 83, 255);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.fill(shape);
paint = new Color(46, 52, 54, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_8_0;
g.setTransform(defaultTransform__0_0_8_0);
g.setClip(clip__0_0_8_0);
float alpha__0_0_8_1 = origAlpha;
origAlpha = origAlpha * 0.17045f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_1 = g.getClip();
AffineTransform defaultTransform__0_0_8_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.96500015258789, 20.166000366210938), new Point2D.Double(18.968000411987305, 31.347000122070312), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.2703999876976013f, 0.0f, 0.0f, 1.5633000135421753f, -43.86800003051758f, -61.926998138427734f));
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new Rectangle2D.Double(-41.58300018310547, -31.5, 4.083399772644043, 8.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_8_1;
g.setTransform(defaultTransform__0_0_8_1);
g.setClip(clip__0_0_8_1);
float alpha__0_0_8_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2 = g.getClip();
AffineTransform defaultTransform__0_0_8_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_8_2 is CompositeGraphicsNode
float alpha__0_0_8_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_0;
g.setTransform(defaultTransform__0_0_8_2_0);
g.setClip(clip__0_0_8_2_0);
float alpha__0_0_8_2_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_1 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_1;
g.setTransform(defaultTransform__0_0_8_2_1);
g.setClip(clip__0_0_8_2_1);
float alpha__0_0_8_2_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_2 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 21.5), new Point2D.Double(27.0, 21.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_2;
g.setTransform(defaultTransform__0_0_8_2_2);
g.setClip(clip__0_0_8_2_2);
float alpha__0_0_8_2_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_3 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_3;
g.setTransform(defaultTransform__0_0_8_2_3);
g.setClip(clip__0_0_8_2_3);
float alpha__0_0_8_2_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_4 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_4 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_8_2_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 21.0f));
shape = new Rectangle2D.Double(20.92799949645996, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_4;
g.setTransform(defaultTransform__0_0_8_2_4);
g.setClip(clip__0_0_8_2_4);
float alpha__0_0_8_2_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_5 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_5 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_8_2_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 19.0f));
shape = new Rectangle2D.Double(20.92799949645996, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_5;
g.setTransform(defaultTransform__0_0_8_2_5);
g.setClip(clip__0_0_8_2_5);
float alpha__0_0_8_2_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_6 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_6 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_8_2_6 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 11.5), new Point2D.Double(4.712699890136719, 11.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 21.0f));
shape = new Rectangle2D.Double(32.0, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_6;
g.setTransform(defaultTransform__0_0_8_2_6);
g.setClip(clip__0_0_8_2_6);
float alpha__0_0_8_2_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_7 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_7 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_8_2_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 13.5), new Point2D.Double(4.712699890136719, 13.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 19.0f));
shape = new Rectangle2D.Double(32.0, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_7;
g.setTransform(defaultTransform__0_0_8_2_7);
g.setClip(clip__0_0_8_2_7);
float alpha__0_0_8_2_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_8 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_8 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_8 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -50.0f));
shape = new Rectangle2D.Double(-44.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_8;
g.setTransform(defaultTransform__0_0_8_2_8);
g.setClip(clip__0_0_8_2_8);
float alpha__0_0_8_2_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_9 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_9 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -50.0f));
shape = new Rectangle2D.Double(35.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_9;
g.setTransform(defaultTransform__0_0_8_2_9);
g.setClip(clip__0_0_8_2_9);
float alpha__0_0_8_2_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_10 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_10 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_10 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -48.0f));
shape = new Rectangle2D.Double(-44.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_10;
g.setTransform(defaultTransform__0_0_8_2_10);
g.setClip(clip__0_0_8_2_10);
float alpha__0_0_8_2_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8_2_11 = g.getClip();
AffineTransform defaultTransform__0_0_8_2_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_8_2_11 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -48.0f));
shape = new Rectangle2D.Double(35.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_8_2_11;
g.setTransform(defaultTransform__0_0_8_2_11);
g.setClip(clip__0_0_8_2_11);
origAlpha = alpha__0_0_8_2;
g.setTransform(defaultTransform__0_0_8_2);
g.setClip(clip__0_0_8_2);
origAlpha = alpha__0_0_8;
g.setTransform(defaultTransform__0_0_8);
g.setClip(clip__0_0_8);
float alpha__0_0_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9 = g.getClip();
AffineTransform defaultTransform__0_0_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -20.0f, -4.939300060272217f));
// _0_0_9 is CompositeGraphicsNode
float alpha__0_0_9_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_0 = g.getClip();
AffineTransform defaultTransform__0_0_9_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_0 is ShapeNode
paint = new Color(85, 87, 83, 255);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.fill(shape);
paint = new Color(46, 52, 54, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_9_0;
g.setTransform(defaultTransform__0_0_9_0);
g.setClip(clip__0_0_9_0);
float alpha__0_0_9_1 = origAlpha;
origAlpha = origAlpha * 0.17045f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_1 = g.getClip();
AffineTransform defaultTransform__0_0_9_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.96500015258789, 20.166000366210938), new Point2D.Double(18.968000411987305, 31.347000122070312), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.2703999876976013f, 0.0f, 0.0f, 1.5633000135421753f, -43.86800003051758f, -61.926998138427734f));
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new Rectangle2D.Double(-41.58300018310547, -31.5, 4.083399772644043, 8.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_9_1;
g.setTransform(defaultTransform__0_0_9_1);
g.setClip(clip__0_0_9_1);
float alpha__0_0_9_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2 = g.getClip();
AffineTransform defaultTransform__0_0_9_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_9_2 is CompositeGraphicsNode
float alpha__0_0_9_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_0;
g.setTransform(defaultTransform__0_0_9_2_0);
g.setClip(clip__0_0_9_2_0);
float alpha__0_0_9_2_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_1 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_1;
g.setTransform(defaultTransform__0_0_9_2_1);
g.setClip(clip__0_0_9_2_1);
float alpha__0_0_9_2_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_2 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 21.5), new Point2D.Double(27.0, 21.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_2;
g.setTransform(defaultTransform__0_0_9_2_2);
g.setClip(clip__0_0_9_2_2);
float alpha__0_0_9_2_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_3 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_3;
g.setTransform(defaultTransform__0_0_9_2_3);
g.setClip(clip__0_0_9_2_3);
float alpha__0_0_9_2_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_4 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_4 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_9_2_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 21.0f));
shape = new Rectangle2D.Double(20.92799949645996, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_4;
g.setTransform(defaultTransform__0_0_9_2_4);
g.setClip(clip__0_0_9_2_4);
float alpha__0_0_9_2_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_5 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_5 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_9_2_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 19.0f));
shape = new Rectangle2D.Double(20.92799949645996, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_5;
g.setTransform(defaultTransform__0_0_9_2_5);
g.setClip(clip__0_0_9_2_5);
float alpha__0_0_9_2_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_6 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_6 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_9_2_6 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 11.5), new Point2D.Double(4.712699890136719, 11.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 21.0f));
shape = new Rectangle2D.Double(32.0, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_6;
g.setTransform(defaultTransform__0_0_9_2_6);
g.setClip(clip__0_0_9_2_6);
float alpha__0_0_9_2_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_7 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_7 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_9_2_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 13.5), new Point2D.Double(4.712699890136719, 13.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 19.0f));
shape = new Rectangle2D.Double(32.0, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_7;
g.setTransform(defaultTransform__0_0_9_2_7);
g.setClip(clip__0_0_9_2_7);
float alpha__0_0_9_2_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_8 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_8 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_8 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -50.0f));
shape = new Rectangle2D.Double(-44.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_8;
g.setTransform(defaultTransform__0_0_9_2_8);
g.setClip(clip__0_0_9_2_8);
float alpha__0_0_9_2_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_9 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_9 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -50.0f));
shape = new Rectangle2D.Double(35.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_9;
g.setTransform(defaultTransform__0_0_9_2_9);
g.setClip(clip__0_0_9_2_9);
float alpha__0_0_9_2_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_10 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_10 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_10 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -48.0f));
shape = new Rectangle2D.Double(-44.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_10;
g.setTransform(defaultTransform__0_0_9_2_10);
g.setClip(clip__0_0_9_2_10);
float alpha__0_0_9_2_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9_2_11 = g.getClip();
AffineTransform defaultTransform__0_0_9_2_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_9_2_11 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -48.0f));
shape = new Rectangle2D.Double(35.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9_2_11;
g.setTransform(defaultTransform__0_0_9_2_11);
g.setClip(clip__0_0_9_2_11);
origAlpha = alpha__0_0_9_2;
g.setTransform(defaultTransform__0_0_9_2);
g.setClip(clip__0_0_9_2);
origAlpha = alpha__0_0_9;
g.setTransform(defaultTransform__0_0_9);
g.setClip(clip__0_0_9);
float alpha__0_0_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10 = g.getClip();
AffineTransform defaultTransform__0_0_10 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -30.0f, -4.939300060272217f));
// _0_0_10 is CompositeGraphicsNode
float alpha__0_0_10_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_0 = g.getClip();
AffineTransform defaultTransform__0_0_10_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_0 is ShapeNode
paint = new Color(85, 87, 83, 255);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.fill(shape);
paint = new Color(46, 52, 54, 255);
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(-42.52799987792969, -32.5, 6.0278000831604, 10.0, 1.8838800191879272, 1.8838800191879272);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_10_0;
g.setTransform(defaultTransform__0_0_10_0);
g.setClip(clip__0_0_10_0);
float alpha__0_0_10_1 = origAlpha;
origAlpha = origAlpha * 0.17045f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_1 = g.getClip();
AffineTransform defaultTransform__0_0_10_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(16.96500015258789, 20.166000366210938), new Point2D.Double(18.968000411987305, 31.347000122070312), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.2703999876976013f, 0.0f, 0.0f, 1.5633000135421753f, -43.86800003051758f, -61.926998138427734f));
stroke = new BasicStroke(1.0f,0,0,4.0f,null,0.0f);
shape = new Rectangle2D.Double(-41.58300018310547, -31.5, 4.083399772644043, 8.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_10_1;
g.setTransform(defaultTransform__0_0_10_1);
g.setClip(clip__0_0_10_1);
float alpha__0_0_10_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2 = g.getClip();
AffineTransform defaultTransform__0_0_10_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_10_2 is CompositeGraphicsNode
float alpha__0_0_10_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_0 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_0 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_0;
g.setTransform(defaultTransform__0_0_10_2_0);
g.setClip(clip__0_0_10_2_0);
float alpha__0_0_10_2_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_1 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_1 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -52.0f));
shape = new Rectangle2D.Double(-44.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_1;
g.setTransform(defaultTransform__0_0_10_2_1);
g.setClip(clip__0_0_10_2_1);
float alpha__0_0_10_2_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_2 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 21.5), new Point2D.Double(27.0, 21.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -31.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_2;
g.setTransform(defaultTransform__0_0_10_2_2);
g.setClip(clip__0_0_10_2_2);
float alpha__0_0_10_2_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_3 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_3 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -52.0f));
shape = new Rectangle2D.Double(35.0, -29.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_3;
g.setTransform(defaultTransform__0_0_10_2_3);
g.setClip(clip__0_0_10_2_3);
float alpha__0_0_10_2_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_4 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_4 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_10_2_4 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 21.0f));
shape = new Rectangle2D.Double(20.92799949645996, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_4;
g.setTransform(defaultTransform__0_0_10_2_4);
g.setClip(clip__0_0_10_2_4);
float alpha__0_0_10_2_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_5 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_5 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_10_2_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 17.594999313354492f, 19.0f));
shape = new Rectangle2D.Double(20.92799949645996, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_5;
g.setTransform(defaultTransform__0_0_10_2_5);
g.setClip(clip__0_0_10_2_5);
float alpha__0_0_10_2_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_6 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_6 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_10_2_6 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 11.5), new Point2D.Double(4.712699890136719, 11.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 21.0f));
shape = new Rectangle2D.Double(32.0, 40.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_6;
g.setTransform(defaultTransform__0_0_10_2_6);
g.setClip(clip__0_0_10_2_6);
float alpha__0_0_10_2_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_7 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_7 = g.getTransform();
g.transform(new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f));
// _0_0_10_2_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(7.116099834442139, 13.5), new Point2D.Double(4.712699890136719, 13.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(176, 176, 176, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 28.66699981689453f, 19.0f));
shape = new Rectangle2D.Double(32.0, 38.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_7;
g.setTransform(defaultTransform__0_0_10_2_7);
g.setClip(clip__0_0_10_2_7);
float alpha__0_0_10_2_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_8 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_8 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_8 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -50.0f));
shape = new Rectangle2D.Double(-44.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_8;
g.setTransform(defaultTransform__0_0_10_2_8);
g.setClip(clip__0_0_10_2_8);
float alpha__0_0_10_2_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_9 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_9 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -50.0f));
shape = new Rectangle2D.Double(35.0, -27.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_9;
g.setTransform(defaultTransform__0_0_10_2_9);
g.setClip(clip__0_0_10_2_9);
float alpha__0_0_10_2_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_10 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_10 = g.getTransform();
g.transform(new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_10 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(8.0, 19.5), new Point2D.Double(4.328400135040283, 19.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, -47.33300018310547f, -48.0f));
shape = new Rectangle2D.Double(-44.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_10;
g.setTransform(defaultTransform__0_0_10_2_10);
g.setClip(clip__0_0_10_2_10);
float alpha__0_0_10_2_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10_2_11 = g.getClip();
AffineTransform defaultTransform__0_0_10_2_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f));
// _0_0_10_2_11 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(24.0, 23.5), new Point2D.Double(27.0, 23.5), new float[] {0.0f,0.62069f,1.0f}, new Color[] {new Color(116, 116, 116, 255),new Color(255, 255, 255, 255),new Color(63, 63, 63, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.666670024394989f, 0.0f, 0.0f, 1.0f, 19.0f, -48.0f));
shape = new Rectangle2D.Double(35.0, -25.0, 2.0, 1.0);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10_2_11;
g.setTransform(defaultTransform__0_0_10_2_11);
g.setClip(clip__0_0_10_2_11);
origAlpha = alpha__0_0_10_2;
g.setTransform(defaultTransform__0_0_10_2);
g.setClip(clip__0_0_10_2);
origAlpha = alpha__0_0_10;
g.setTransform(defaultTransform__0_0_10);
g.setClip(clip__0_0_10);
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
        return 109;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 400;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 400;
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
	public HeapDumpIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public HeapDumpIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public HeapDumpIcon(int width, int height) {
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

