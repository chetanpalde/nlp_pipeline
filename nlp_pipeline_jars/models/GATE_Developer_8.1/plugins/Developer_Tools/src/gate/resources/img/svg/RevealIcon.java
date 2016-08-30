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
public class RevealIcon implements
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
g.transform(new AffineTransform(0.023640267550945282f, 0.0f, 0.0f, 0.022995369508862495f, 45.02649688720703f, 39.46533203125f));
// _0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0 = origAlpha;
origAlpha = origAlpha * 0.40206185f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(302.8571472167969, 366.64788818359375), new Point2D.Double(302.8571472167969, 609.5050659179688), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(0, 0, 0, 0),new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.7743890285491943f, 0.0f, 0.0f, 1.9697060585021973f, -1892.178955078125f, -872.8853759765625f));
shape = new Rectangle2D.Double(-1559.2523193359375, -150.6968536376953, 1339.633544921875, 478.357177734375);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0);
g.setClip(clip__0_0_0_0);
float alpha__0_0_0_1 = origAlpha;
origAlpha = origAlpha * 0.40206185f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_1 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7142944335938, 486.64788818359375), 117.14286f, new Point2D.Double(605.7142944335938, 486.64788818359375), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(2.7743890285491943f, 0.0f, 0.0f, 1.9697060585021973f, -1891.633056640625f, -872.8853759765625f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-219.61876, -150.68037);
((GeneralPath)shape).curveTo(-219.61876, -150.68037, -219.61876, 327.65042, -219.61876, 327.65042);
((GeneralPath)shape).curveTo(-76.74459, 328.55087, 125.78146, 220.48074, 125.78138, 88.45424);
((GeneralPath)shape).curveTo(125.78138, -43.572304, -33.655437, -150.68036, -219.61876, -150.68037);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0_1;
g.setTransform(defaultTransform__0_0_0_1);
g.setClip(clip__0_0_0_1);
float alpha__0_0_0_2 = origAlpha;
origAlpha = origAlpha * 0.40206185f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_2 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(605.7142944335938, 486.64788818359375), 117.14286f, new Point2D.Double(605.7142944335938, 486.64788818359375), new float[] {0.0f,1.0f}, new Color[] {new Color(0, 0, 0, 255),new Color(0, 0, 0, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-2.7743890285491943f, 0.0f, 0.0f, 1.9697060585021973f, 112.76229858398438f, -872.8853759765625f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(-1559.2523, -150.68037);
((GeneralPath)shape).curveTo(-1559.2523, -150.68037, -1559.2523, 327.65042, -1559.2523, 327.65042);
((GeneralPath)shape).curveTo(-1702.1265, 328.55087, -1904.6525, 220.48074, -1904.6525, 88.45424);
((GeneralPath)shape).curveTo(-1904.6525, -43.572304, -1745.2157, -150.68036, -1559.2523, -150.68037);
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
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(25.0, 4.311681747436523), 19.996933f, new Point2D.Double(25.0, 4.311681747436523), new float[] {0.0f,0.25f,0.68f,1.0f}, new Color[] {new Color(143, 179, 217, 255),new Color(114, 159, 207, 255),new Color(52, 101, 164, 255),new Color(32, 74, 135, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.01216483861207962f, 2.585073947906494f, -3.2504982948303223f, 0.015296213328838348f, 38.710994720458984f, -60.38692092895508f));
shape = new RoundRectangle2D.Double(4.499479293823242, 4.50103759765625, 38.993865966796875, 39.00564193725586, 4.95554780960083, 4.980924129486084);
g.setPaint(paint);
g.fill(shape);
paint = new LinearGradientPaint(new Point2D.Double(20.0, 4.0), new Point2D.Double(20.0, 44.0), new float[] {0.0f,1.0f}, new Color[] {new Color(52, 101, 164, 255),new Color(32, 74, 135, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
stroke = new BasicStroke(1.0f,0,1,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(4.499479293823242, 4.50103759765625, 38.993865966796875, 39.00564193725586, 4.95554780960083, 4.980924129486084);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_1;
g.setTransform(defaultTransform__0_0_1);
g.setClip(clip__0_0_1);
float alpha__0_0_2 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_2 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(25.0, -0.05076269432902336), new Point2D.Double(25.285715103149414, 57.71428680419922), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
stroke = new BasicStroke(1.0f,0,1,4.0f,null,0.0f);
shape = new RoundRectangle2D.Double(5.5017547607421875, 5.489577293395996, 36.996883392333984, 37.007320404052734, 3.013584613800049, 2.9943172931671143);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_2;
g.setTransform(defaultTransform__0_0_2);
g.setClip(clip__0_0_2);
float alpha__0_0_3 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3 = g.getClip();
AffineTransform defaultTransform__0_0_3 = g.getTransform();
g.transform(new AffineTransform(0.19086800515651703f, 0.1612599939107895f, 0.1612599939107895f, -0.19086800515651703f, 7.2809157371521f, 24.306129455566406f));
// _0_0_3 is CompositeGraphicsNode
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
origAlpha = alpha__0_0_4;
g.setTransform(defaultTransform__0_0_4);
g.setClip(clip__0_0_4);
float alpha__0_0_5 = origAlpha;
origAlpha = origAlpha * 0.44444442f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5 = g.getClip();
AffineTransform defaultTransform__0_0_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(31.0, 12.875), new Point2D.Double(3.2591991424560547, 24.893844604492188), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.535999596118927f, 5.498996734619141f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(0.91099966, 27.748999);
((GeneralPath)shape).curveTo(28.15259, 29.47655, 10.984791, 13.750064, 32.036, 13.248998);
((GeneralPath)shape).lineTo(37.325214, 24.364037);
((GeneralPath)shape).curveTo(27.718748, 19.884726, 21.14768, 42.897034, 0.78599966, 29.373999);
((GeneralPath)shape).lineTo(0.91099966, 27.748999);
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
g.transform(new AffineTransform(0.665929913520813f, 0.0f, 0.0f, 0.665929913520813f, 11.393279075622559f, 4.907034873962402f));
// _0_0_6 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(32.5, 16.5625), 14.4375f, new Point2D.Double(32.5, 16.5625), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(46.9375, 16.5625);
((GeneralPath)shape).curveTo(46.9375, 24.536112, 40.47361, 31.0, 32.5, 31.0);
((GeneralPath)shape).curveTo(24.526388, 31.0, 18.0625, 24.536112, 18.0625, 16.5625);
((GeneralPath)shape).curveTo(18.0625, 8.588889, 24.526388, 2.125, 32.5, 2.125);
((GeneralPath)shape).curveTo(40.47361, 2.125, 46.9375, 8.588889, 46.9375, 16.5625);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_6;
g.setTransform(defaultTransform__0_0_6);
g.setClip(clip__0_0_6);
float alpha__0_0_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7 = g.getClip();
AffineTransform defaultTransform__0_0_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(39.0, 26.125), new Point2D.Double(36.375, 20.4375), new float[] {0.0f,1.0f}, new Color[] {new Color(27, 31, 32, 255),new Color(186, 189, 182, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.535999596118927f, -1.5010031461715698f));
stroke = new BasicStroke(4.0f,1,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(33.036, 14.998998);
((GeneralPath)shape).lineTo(46.036, 43.999);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_7;
g.setTransform(defaultTransform__0_0_7);
g.setClip(clip__0_0_7);
float alpha__0_0_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8 = g.getClip();
AffineTransform defaultTransform__0_0_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_8 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(33.036, 14.998998);
((GeneralPath)shape).lineTo(46.036, 43.999);
g.setPaint(paint);
g.fill(shape);
paint = new LinearGradientPaint(new Point2D.Double(42.90625, 42.21875), new Point2D.Double(44.8125, 41.40625), new float[] {0.0f,0.64444447f,1.0f}, new Color[] {new Color(46, 52, 54, 255),new Color(136, 138, 133, 255),new Color(85, 87, 83, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.535999596118927f, -1.5010031461715698f));
stroke = new BasicStroke(2.0f,1,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(33.036, 14.998998);
((GeneralPath)shape).lineTo(46.036, 43.999);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_8;
g.setTransform(defaultTransform__0_0_8);
g.setClip(clip__0_0_8);
float alpha__0_0_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9 = g.getClip();
AffineTransform defaultTransform__0_0_9 = g.getTransform();
g.transform(new AffineTransform(1.272613286972046f, 0.0f, 0.0f, 1.272613286972046f, 12.072080612182617f, -6.673644065856934f));
// _0_0_9 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15.5, 24.75);
((GeneralPath)shape).lineTo(11.728554, 24.19539);
((GeneralPath)shape).lineTo(9.451035, 27.075687);
((GeneralPath)shape).lineTo(8.813057, 23.317446);
((GeneralPath)shape).lineTo(5.3699408, 22.041456);
((GeneralPath)shape).lineTo(8.747095, 20.273342);
((GeneralPath)shape).lineTo(8.896652, 16.604443);
((GeneralPath)shape).lineTo(11.621825, 19.26993);
((GeneralPath)shape).lineTo(15.157373, 18.278416);
((GeneralPath)shape).lineTo(13.464468, 21.69389);
((GeneralPath)shape).lineTo(15.5, 24.75);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_9;
g.setTransform(defaultTransform__0_0_9);
g.setClip(clip__0_0_9);
float alpha__0_0_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10 = g.getClip();
AffineTransform defaultTransform__0_0_10 = g.getTransform();
g.transform(new AffineTransform(0.5838837027549744f, 0.5838837027549744f, -0.5838837027549744f, 0.5838837027549744f, 24.48128318786621f, 9.477374076843262f));
// _0_0_10 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15.5, 24.75);
((GeneralPath)shape).lineTo(11.728554, 24.19539);
((GeneralPath)shape).lineTo(9.451035, 27.075687);
((GeneralPath)shape).lineTo(8.813057, 23.317446);
((GeneralPath)shape).lineTo(5.3699408, 22.041456);
((GeneralPath)shape).lineTo(8.747095, 20.273342);
((GeneralPath)shape).lineTo(8.896652, 16.604443);
((GeneralPath)shape).lineTo(11.621825, 19.26993);
((GeneralPath)shape).lineTo(15.157373, 18.278416);
((GeneralPath)shape).lineTo(13.464468, 21.69389);
((GeneralPath)shape).lineTo(15.5, 24.75);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_10;
g.setTransform(defaultTransform__0_0_10);
g.setClip(clip__0_0_10);
float alpha__0_0_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_11 = g.getClip();
AffineTransform defaultTransform__0_0_11 = g.getTransform();
g.transform(new AffineTransform(0.5791025757789612f, 0.12860369682312012f, -0.12860369682312012f, 0.5791025757789612f, 5.244583606719971f, 16.59849739074707f));
// _0_0_11 is ShapeNode
paint = new Color(255, 255, 255, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15.5, 24.75);
((GeneralPath)shape).lineTo(11.728554, 24.19539);
((GeneralPath)shape).lineTo(9.451035, 27.075687);
((GeneralPath)shape).lineTo(8.813057, 23.317446);
((GeneralPath)shape).lineTo(5.3699408, 22.041456);
((GeneralPath)shape).lineTo(8.747095, 20.273342);
((GeneralPath)shape).lineTo(8.896652, 16.604443);
((GeneralPath)shape).lineTo(11.621825, 19.26993);
((GeneralPath)shape).lineTo(15.157373, 18.278416);
((GeneralPath)shape).lineTo(13.464468, 21.69389);
((GeneralPath)shape).lineTo(15.5, 24.75);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_11;
g.setTransform(defaultTransform__0_0_11);
g.setClip(clip__0_0_11);
float alpha__0_0_12 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_12 = g.getClip();
AffineTransform defaultTransform__0_0_12 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.005668648984283209f, 1.9989968538284302f));
// _0_0_12 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(31.994285583496094, 16.859249114990234), new Point2D.Double(37.7237434387207, 16.859249114990234), new float[] {0.0f,0.7888889f,1.0f}, new Color[] {new Color(238, 238, 236, 255),new Color(255, 255, 255, 255),new Color(238, 238, 236, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(32.9375, 11.9375);
((GeneralPath)shape).curveTo(32.87939, 11.943775, 32.84168, 11.954412, 32.78125, 11.96875);
((GeneralPath)shape).curveTo(32.480507, 12.044301, 32.22415, 12.283065, 32.09375, 12.5625);
((GeneralPath)shape).curveTo(31.963346, 12.841935, 31.958935, 13.12817, 32.09375, 13.40625);
((GeneralPath)shape).lineTo(35.84375, 21.75);
((GeneralPath)shape).curveTo(35.837093, 21.759354, 35.837093, 21.771896, 35.84375, 21.78125);
((GeneralPath)shape).curveTo(35.853104, 21.787907, 35.865646, 21.787907, 35.875, 21.78125);
((GeneralPath)shape).curveTo(35.884354, 21.787907, 35.896896, 21.787907, 35.90625, 21.78125);
((GeneralPath)shape).curveTo(35.912907, 21.771896, 35.912907, 21.759354, 35.90625, 21.75);
((GeneralPath)shape).curveTo(36.14071, 21.344227, 36.483208, 21.082874, 36.9375, 20.96875);
((GeneralPath)shape).curveTo(37.18631, 20.909716, 37.44822, 20.917711, 37.6875, 20.96875);
((GeneralPath)shape).curveTo(37.696854, 20.975407, 37.709396, 20.975407, 37.71875, 20.96875);
((GeneralPath)shape).curveTo(37.725407, 20.959396, 37.725407, 20.946854, 37.71875, 20.9375);
((GeneralPath)shape).lineTo(33.96875, 12.59375);
((GeneralPath)shape).curveTo(33.824844, 12.242701, 33.48375, 11.983006, 33.125, 11.9375);
((GeneralPath)shape).curveTo(33.06451, 11.929827, 32.99561, 11.931225, 32.9375, 11.9375);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_12;
g.setTransform(defaultTransform__0_0_12);
g.setClip(clip__0_0_12);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
origAlpha = alpha__0;
g.setTransform(defaultTransform__0);
g.setClip(clip__0);
g.setTransform(defaultTransform_);
g.setClip(clip_);

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
        return 5;
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
	public RevealIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public RevealIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public RevealIcon(int width, int height) {
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

