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
public class AlchemyIcon implements
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
origAlpha = origAlpha * 0.4f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(0.919623613357544f, 0.0f, 0.0f, 2.1521098613739014f, 2.237642288208008f, -47.980430603027344f));
// _0_0_0 is ShapeNode
paint = new Color(0, 0, 0, 255);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(44.401966, 41.570206);
((GeneralPath)shape).curveTo(44.401966, 42.485893, 35.15024, 43.228203, 23.737663, 43.228203);
((GeneralPath)shape).curveTo(12.325084, 43.228203, 3.0733604, 42.485893, 3.0733604, 41.570206);
((GeneralPath)shape).curveTo(3.0733604, 40.65452, 12.325084, 39.91221, 23.737663, 39.91221);
((GeneralPath)shape).curveTo(35.15024, 39.91221, 44.401966, 40.65452, 44.401966, 41.570206);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
float alpha__0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1 = g.getClip();
AffineTransform defaultTransform__0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1 is CompositeGraphicsNode
float alpha__0_1_0 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0 = g.getClip();
AffineTransform defaultTransform__0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(20.993087768554688, 24.09668731689453), new Point2D.Double(31.603107452392578, 23.301191329956055), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(205, 205, 205, 255),new Color(255, 255, 255, 81),new Color(214, 214, 214, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(19.228392, 5.6023855);
((GeneralPath)shape).curveTo(18.274069, 5.8523855, 17.798643, 8.906069, 19.705551, 9.1023855);
((GeneralPath)shape).lineTo(19.705551, 17.102386);
((GeneralPath)shape).lineTo(9.71609, 34.020084);
((GeneralPath)shape).curveTo(8.521776, 36.04272, 7.191811, 39.203884, 11.144566, 41.208633);
((GeneralPath)shape).curveTo(13.697754, 42.27191, 18.23556, 43.0, 24.0, 43.0);
((GeneralPath)shape).curveTo(29.76444, 43.0, 34.302246, 42.27191, 36.855434, 41.208633);
((GeneralPath)shape).curveTo(40.80819, 39.203884, 39.478226, 36.04272, 38.28391, 34.020084);
((GeneralPath)shape).lineTo(28.294447, 17.102386);
((GeneralPath)shape).lineTo(28.294447, 9.102386);
((GeneralPath)shape).curveTo(30.201359, 8.90607, 29.725931, 5.8523865, 28.771608, 5.6023865);
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_1_0;
g.setTransform(defaultTransform__0_1_0);
g.setClip(clip__0_1_0);
float alpha__0_1_1 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_1 = g.getClip();
AffineTransform defaultTransform__0_1_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_1 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(27.65882682800293, 39.50259780883789), 17.254612f, new Point2D.Double(27.65882682800293, 39.50259780883789), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(218, 218, 214, 255),new Color(228, 228, 225, 127),new Color(218, 218, 214, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(-0.3767108917236328f, -0.2571776807308197f, 0.15113793313503265f, -0.2213846892118454f, 31.386539459228516f, 52.92100524902344f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(38.314835, 38.0);
((GeneralPath)shape).curveTo(38.314835, 40.209137, 33.905865, 42.0, 24.0, 42.0);
((GeneralPath)shape).curveTo(14.000384, 42.0, 9.685165, 40.209137, 9.685165, 38.0);
((GeneralPath)shape).curveTo(9.685165, 35.79086, 16.094135, 33.999996, 24.0, 33.999996);
((GeneralPath)shape).curveTo(31.905865, 33.999996, 38.314835, 35.79086, 38.314835, 37.999996);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(255, 255, 255, 128);
stroke = new BasicStroke(1.0f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(38.314835, 38.0);
((GeneralPath)shape).curveTo(38.314835, 40.209137, 33.905865, 42.0, 24.0, 42.0);
((GeneralPath)shape).curveTo(14.000384, 42.0, 9.685165, 40.209137, 9.685165, 38.0);
((GeneralPath)shape).curveTo(9.685165, 35.79086, 16.094135, 33.999996, 24.0, 33.999996);
((GeneralPath)shape).curveTo(31.905865, 33.999996, 38.314835, 35.79086, 38.314835, 37.999996);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_1;
g.setTransform(defaultTransform__0_1_1);
g.setClip(clip__0_1_1);
origAlpha = alpha__0_1;
g.setTransform(defaultTransform__0_1);
g.setClip(clip__0_1);
float alpha__0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_2 = g.getClip();
AffineTransform defaultTransform__0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_2 is CompositeGraphicsNode
float alpha__0_2_0 = origAlpha;
origAlpha = origAlpha * 0.8f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_2_0 = g.getClip();
AffineTransform defaultTransform__0_2_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_2_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(25.367456436157227, 32.88559341430664), new Point2D.Double(33.0572395324707, 31.206214904785156), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(115, 210, 22, 255),new Color(129, 231, 30, 127),new Color(115, 210, 22, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(14.0, 27.59375);
((GeneralPath)shape).lineTo(10.71875, 33.28125);
((GeneralPath)shape).curveTo(9.069973, 36.149376, 8.6648, 37.088352, 9.125, 38.5);
((GeneralPath)shape).curveTo(9.735943, 40.374043, 12.25, 42.5, 23.5, 42.5);
((GeneralPath)shape).curveTo(32.181755, 42.5, 37.729248, 41.60591, 38.90625, 38.5625);
((GeneralPath)shape).curveTo(39.31793, 37.49801, 38.90095, 35.87768, 38.09375, 34.53125);
((GeneralPath)shape).lineTo(33.96875, 27.625);
((GeneralPath)shape).curveTo(33.84851, 27.80756, 33.720097, 27.997175, 33.53125, 28.15625);
((GeneralPath)shape).curveTo(33.088127, 28.529512, 32.42992, 28.866392, 31.59375, 29.125);
((GeneralPath)shape).curveTo(29.921413, 29.642218, 27.446182, 29.9375, 24.0, 29.9375);
((GeneralPath)shape).curveTo(20.52157, 29.9375, 18.039877, 29.642502, 16.375, 29.125);
((GeneralPath)shape).curveTo(15.542561, 28.866251, 14.906974, 28.530209, 14.46875, 28.15625);
((GeneralPath)shape).curveTo(14.26949, 27.986214, 14.123159, 27.790316, 14.0, 27.59375);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_2_0;
g.setTransform(defaultTransform__0_2_0);
g.setClip(clip__0_2_0);
float alpha__0_2_1 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_2_1 = g.getClip();
AffineTransform defaultTransform__0_2_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_2_1 is ShapeNode
paint = new Color(115, 210, 22, 170);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(24.0, 23.750957);
((GeneralPath)shape).curveTo(21.240711, 23.750957, 18.754326, 24.063398, 16.920942, 24.575703);
((GeneralPath)shape).curveTo(16.00425, 24.831854, 15.233758, 25.142496, 14.68726, 25.50354);
((GeneralPath)shape).curveTo(14.14076, 25.864584, 13.759421, 26.304804, 13.759421, 26.84375);
((GeneralPath)shape).curveTo(13.759421, 27.331453, 14.04285, 27.775639, 14.481073, 28.149595);
((GeneralPath)shape).curveTo(14.919297, 28.523554, 15.538673, 28.853048, 16.371113, 29.111797);
((GeneralPath)shape).curveTo(18.03599, 29.6293, 20.52157, 29.936543, 24.0, 29.936543);
((GeneralPath)shape).curveTo(27.446182, 29.936543, 29.922184, 29.629015, 31.594522, 29.111797);
((GeneralPath)shape).curveTo(32.43069, 28.85319, 33.0758, 28.522858, 33.518925, 28.149595);
((GeneralPath)shape).curveTo(33.962048, 27.776335, 34.240578, 27.333748, 34.240578, 26.84375);
((GeneralPath)shape).curveTo(34.240578, 26.304804, 33.859238, 25.864584, 33.31274, 25.50354);
((GeneralPath)shape).curveTo(32.76624, 25.142496, 31.99575, 24.831854, 31.079058, 24.575703);
((GeneralPath)shape).curveTo(29.245672, 24.063398, 26.759289, 23.750957, 24.0, 23.750957);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_2_1;
g.setTransform(defaultTransform__0_2_1);
g.setClip(clip__0_2_1);
float alpha__0_2_2 = origAlpha;
origAlpha = origAlpha * 0.8f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_2_2 = g.getClip();
AffineTransform defaultTransform__0_2_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_2_2 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(21.75, 27.949663162231445), 8.625035f, new Point2D.Double(21.75, 27.949663162231445), new float[] {0.0f,1.0f}, new Color[] {new Color(102, 185, 19, 255),new Color(114, 209, 22, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.4347808361053467f, -0.10869524627923965f, 0.06186575070023537f, 0.2474629431962967f, 10.5643892288208f, 22.572616577148438f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(24.0, 24.642551);
((GeneralPath)shape).curveTo(26.440725, 24.642551, 28.65343, 24.92231, 30.230274, 25.362926);
((GeneralPath)shape).curveTo(31.018696, 25.583235, 31.649954, 25.85107, 32.060417, 26.12224);
((GeneralPath)shape).curveTo(32.47088, 26.393412, 32.625034, 26.63978, 32.625034, 26.842615);
((GeneralPath)shape).curveTo(32.625034, 27.08953, 32.505566, 27.322292, 32.196705, 27.58246);
((GeneralPath)shape).curveTo(31.887842, 27.842628, 31.383255, 28.10421, 30.678074, 28.322306);
((GeneralPath)shape).curveTo(29.267715, 28.7585, 27.068209, 29.04268, 24.0, 29.04268);
((GeneralPath)shape).curveTo(20.902422, 29.04268, 18.685461, 28.758244, 17.282988, 28.322306);
((GeneralPath)shape).curveTo(16.58175, 28.104336, 16.087973, 27.842001, 15.783828, 27.58246);
((GeneralPath)shape).curveTo(15.479683, 27.32292, 15.374966, 27.091599, 15.374966, 26.842615);
((GeneralPath)shape).curveTo(15.374966, 26.63978, 15.529122, 26.393412, 15.939585, 26.12224);
((GeneralPath)shape).curveTo(16.350046, 25.851068, 16.981304, 25.583235, 17.769726, 25.362926);
((GeneralPath)shape).curveTo(19.34657, 24.92231, 21.559275, 24.642551, 24.0, 24.642551);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_2_2;
g.setTransform(defaultTransform__0_2_2);
g.setClip(clip__0_2_2);
origAlpha = alpha__0_2;
g.setTransform(defaultTransform__0_2);
g.setClip(clip__0_2);
float alpha__0_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3 = g.getClip();
AffineTransform defaultTransform__0_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3 is CompositeGraphicsNode
float alpha__0_3_0 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_0 = g.getClip();
AffineTransform defaultTransform__0_3_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_0 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(25.279067993164062, 38.94019317626953), new Point2D.Double(25.146484375, 35.05110549926758), new float[] {0.0f,1.0f}, new Color[] {new Color(255, 255, 255, 255),new Color(242, 242, 242, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(24.0, 33.53125);
((GeneralPath)shape).curveTo(20.011662, 33.53125, 16.400013, 33.978256, 13.75, 34.71875);
((GeneralPath)shape).curveTo(12.4249935, 35.088997, 11.352421, 35.54064, 10.5625, 36.0625);
((GeneralPath)shape).curveTo(9.772579, 36.58436, 9.21875, 37.220993, 9.21875, 38.0);
((GeneralPath)shape).curveTo(9.21875, 38.704937, 9.585332, 39.365723, 10.21875, 39.90625);
((GeneralPath)shape).curveTo(10.852168, 40.446777, 11.765525, 40.907246, 12.96875, 41.28125);
((GeneralPath)shape).curveTo(15.3752, 42.02926, 18.9722, 42.46875, 24.0, 42.46875);
((GeneralPath)shape).curveTo(28.98119, 42.46875, 32.582764, 42.028847, 35.0, 41.28125);
((GeneralPath)shape).curveTo(36.20862, 40.90745, 37.14075, 40.44577, 37.78125, 39.90625);
((GeneralPath)shape).curveTo(38.42175, 39.36673, 38.78125, 38.708256, 38.78125, 38.0);
((GeneralPath)shape).curveTo(38.78125, 37.220993, 38.22742, 36.58436, 37.4375, 36.0625);
((GeneralPath)shape).curveTo(36.64758, 35.54064, 35.57501, 35.088997, 34.25, 34.71875);
((GeneralPath)shape).curveTo(31.599987, 33.978256, 27.988337, 33.53125, 24.0, 33.53125);
((GeneralPath)shape).closePath();
((GeneralPath)shape).moveTo(24.0, 34.46875);
((GeneralPath)shape).curveTo(27.917526, 34.46875, 31.469063, 34.917778, 34.0, 35.625);
((GeneralPath)shape).curveTo(35.26547, 35.97861, 36.278683, 36.408504, 36.9375, 36.84375);
((GeneralPath)shape).curveTo(37.596317, 37.278996, 37.84375, 37.674435, 37.84375, 38.0);
((GeneralPath)shape).curveTo(37.84375, 38.396317, 37.651993, 38.769913, 37.15625, 39.1875);
((GeneralPath)shape).curveTo(36.660507, 39.605087, 35.85061, 40.02494, 34.71875, 40.375);
((GeneralPath)shape).curveTo(32.455032, 41.07512, 28.924677, 41.53125, 24.0, 41.53125);
((GeneralPath)shape).curveTo(19.028185, 41.53125, 15.469813, 41.074707, 13.21875, 40.375);
((GeneralPath)shape).curveTo(12.093218, 40.025146, 11.300673, 39.60408, 10.8125, 39.1875);
((GeneralPath)shape).curveTo(10.324327, 38.77092, 10.15625, 38.39963, 10.15625, 38.0);
((GeneralPath)shape).curveTo(10.15625, 37.674435, 10.403681, 37.278996, 11.0625, 36.84375);
((GeneralPath)shape).curveTo(11.721319, 36.408504, 12.734531, 35.97861, 14.0, 35.625);
((GeneralPath)shape).curveTo(16.530937, 34.917778, 20.082474, 34.46875, 24.0, 34.46875);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_3_0;
g.setTransform(defaultTransform__0_3_0);
g.setClip(clip__0_3_0);
float alpha__0_3_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_1 = g.getClip();
AffineTransform defaultTransform__0_3_1 = g.getTransform();
g.transform(new AffineTransform(0.6227447986602783f, 0.0f, 0.0f, 0.7736585140228271f, 8.92151165008545f, -0.5760962963104248f));
// _0_3_1 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(19.95998764038086, 8.250467300415039), new Point2D.Double(23.87128257751465, 8.953522682189941), new float[] {0.0f,0.5f,1.0f}, new Color[] {new Color(218, 218, 214, 255),new Color(228, 228, 225, 127),new Color(218, 218, 214, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(32.425896, 8.5);
((GeneralPath)shape).curveTo(32.425896, 9.328427, 28.748833, 10.0, 24.212948, 10.0);
((GeneralPath)shape).curveTo(19.677061, 10.0, 15.999998, 9.328427, 15.999998, 8.5);
((GeneralPath)shape).curveTo(15.999998, 7.6715727, 19.677061, 7.0, 24.212948, 7.0);
((GeneralPath)shape).curveTo(28.748833, 7.0, 32.425896, 7.6715727, 32.425896, 8.5);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = new Color(136, 138, 133, 255);
stroke = new BasicStroke(1.4406892f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(32.425896, 8.5);
((GeneralPath)shape).curveTo(32.425896, 9.328427, 28.748833, 10.0, 24.212948, 10.0);
((GeneralPath)shape).curveTo(19.677061, 10.0, 15.999998, 9.328427, 15.999998, 8.5);
((GeneralPath)shape).curveTo(15.999998, 7.6715727, 19.677061, 7.0, 24.212948, 7.0);
((GeneralPath)shape).curveTo(28.748833, 7.0, 32.425896, 7.6715727, 32.425896, 8.5);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_1;
g.setTransform(defaultTransform__0_3_1);
g.setClip(clip__0_3_1);
float alpha__0_3_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_2 = g.getClip();
AffineTransform defaultTransform__0_3_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_2 is ShapeNode
paint = new Color(0, 0, 0, 10);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(12.219689, 41.089245);
((GeneralPath)shape).curveTo(10.717088, 40.29375, 11.313709, 37.86307, 12.551147, 36.095303);
((GeneralPath)shape).curveTo(13.788585, 34.327538, 21.420315, 18.771187, 21.862255, 17.710527);
((GeneralPath)shape).lineTo(21.862255, 8.970345);
((GeneralPath)shape).lineTo(23.790337, 9.090213);
((GeneralPath)shape).lineTo(23.834526, 18.054277);
((GeneralPath)shape).curveTo(23.834526, 18.054277, 20.14679, 31.713161, 18.230099, 38.70276);
((GeneralPath)shape).curveTo(17.909786, 39.870842, 16.948467, 41.00086, 18.495264, 42.326683);
((GeneralPath)shape).curveTo(15.2248955, 41.862644, 14.407301, 41.774258, 12.219688, 41.089245);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_3_2;
g.setTransform(defaultTransform__0_3_2);
g.setClip(clip__0_3_2);
float alpha__0_3_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_3 = g.getClip();
AffineTransform defaultTransform__0_3_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_3 is ShapeNode
paint = new Color(0, 0, 0, 14);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(37.07042, 40.33388);
((GeneralPath)shape).curveTo(38.571007, 39.53509, 37.910107, 37.019318, 36.67267, 35.251553);
((GeneralPath)shape).curveTo(35.43523, 33.483788, 27.803501, 17.927437, 27.36156, 16.866777);
((GeneralPath)shape).lineTo(27.36156, 8.79847);
((GeneralPath)shape).lineTo(26.417854, 8.965213);
((GeneralPath)shape).lineTo(26.389284, 17.210527);
((GeneralPath)shape).curveTo(26.389284, 17.210527, 31.54577, 31.025661, 33.46246, 38.01526);
((GeneralPath)shape).curveTo(33.782772, 39.183342, 34.74409, 40.31336, 33.197296, 41.639183);
((GeneralPath)shape).curveTo(36.467667, 41.175144, 35.046894, 41.411037, 37.070415, 40.333885);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_3_3;
g.setTransform(defaultTransform__0_3_3);
g.setClip(clip__0_3_3);
float alpha__0_3_4 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_4 = g.getClip();
AffineTransform defaultTransform__0_3_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_4 is ShapeNode
paint = new Color(211, 215, 207, 255);
stroke = new BasicStroke(1.0f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28.294449, 9.102385);
((GeneralPath)shape).curveTo(26.7114, 9.373428, 26.238869, 9.598708, 24.004223, 9.598708);
((GeneralPath)shape).curveTo(21.769577, 9.598708, 21.049303, 9.312604, 19.689928, 9.14926);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_4;
g.setTransform(defaultTransform__0_3_4);
g.setClip(clip__0_3_4);
float alpha__0_3_5 = origAlpha;
origAlpha = origAlpha * 0.75f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_5 = g.getClip();
AffineTransform defaultTransform__0_3_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_5 is ShapeNode
paint = new RadialGradientPaint(new Point2D.Double(23.881792068481445, -0.723404049873352), 15.475195f, new Point2D.Double(23.881792068481445, -0.723404049873352), new float[] {0.0f,0.6176308f,0.7459709f,1.0f}, new Color[] {new Color(255, 255, 255, 0),new Color(255, 255, 255, 0),new Color(255, 255, 255, 255),new Color(255, 255, 255, 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(0.34978801012039185f, 0.005310730077326298f, -0.003594089997932315f, 0.1445447951555252f, 15.580211639404297f, 17.009489059448242f));
stroke = new BasicStroke(2.0f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28.018528, 17.634432);
((GeneralPath)shape).curveTo(27.085352, 18.462858, 24.77331, 18.554276, 23.943901, 18.554276);
((GeneralPath)shape).curveTo(23.001377, 18.554276, 21.65079, 18.617535, 19.869274, 17.72282);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_5;
g.setTransform(defaultTransform__0_3_5);
g.setClip(clip__0_3_5);
float alpha__0_3_6 = origAlpha;
origAlpha = origAlpha * 0.1f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_6 = g.getClip();
AffineTransform defaultTransform__0_3_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_6 is ShapeNode
paint = new Color(255, 255, 255, 255);
stroke = new BasicStroke(1.9999999f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(38.579483, 36.308937);
((GeneralPath)shape).curveTo(35.255898, 38.20572, 26.896366, 38.915028, 23.942356, 38.915028);
((GeneralPath)shape).curveTo(20.585482, 38.915028, 15.900249, 38.559868, 9.555225, 36.51131);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_6;
g.setTransform(defaultTransform__0_3_6);
g.setClip(clip__0_3_6);
float alpha__0_3_7 = origAlpha;
origAlpha = origAlpha * 0.5f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_7 = g.getClip();
AffineTransform defaultTransform__0_3_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_7 is ShapeNode
paint = new LinearGradientPaint(new Point2D.Double(25.0, 7.1875), new Point2D.Double(27.84375, 6.84375), new float[] {0.0f,0.7083067f,1.0f}, new Color[] {new Color(205, 205, 205, 255),new Color(255, 255, 255, 81),new Color(214, 214, 214, 255)}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f));
shape = new GeneralPath();
((GeneralPath)shape).moveTo(18.9375, 6.8125);
((GeneralPath)shape).curveTo(18.78125, 8.046875, 18.88659, 8.4475765, 20.1875, 8.703125);
((GeneralPath)shape).curveTo(21.493528, 8.959678, 22.359077, 9.05873, 23.937202, 9.08998);
((GeneralPath)shape).curveTo(24.926819, 9.109576, 26.739086, 8.941143, 27.772701, 8.709582);
((GeneralPath)shape).curveTo(28.576357, 8.529539, 28.81157, 8.369245, 29.005524, 8.048771);
((GeneralPath)shape).curveTo(29.304295, 7.555108, 29.174881, 7.02383, 29.098484, 6.80508);
((GeneralPath)shape).curveTo(28.09564, 7.3843155, 26.23437, 7.703125, 23.812494, 7.65625);
((GeneralPath)shape).curveTo(21.39062, 7.609375, 19.57812, 7.25, 18.9375, 6.8125);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
origAlpha = alpha__0_3_7;
g.setTransform(defaultTransform__0_3_7);
g.setClip(clip__0_3_7);
float alpha__0_3_8 = origAlpha;
origAlpha = origAlpha * 0.1f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_8 = g.getClip();
AffineTransform defaultTransform__0_3_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_8 is ShapeNode
paint = new Color(46, 52, 54, 255);
stroke = new BasicStroke(1.9999999f,1,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(38.579483, 38.34187);
((GeneralPath)shape).curveTo(35.255898, 40.23865, 26.896366, 40.859573, 23.942356, 40.859573);
((GeneralPath)shape).curveTo(20.585482, 40.859573, 15.900249, 40.592804, 9.555225, 38.544247);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_8;
g.setTransform(defaultTransform__0_3_8);
g.setClip(clip__0_3_8);
float alpha__0_3_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_3_9 = g.getClip();
AffineTransform defaultTransform__0_3_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_3_9 is ShapeNode
paint = new Color(136, 138, 133, 255);
stroke = new BasicStroke(0.9999999f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(19.228392, 5.6023855);
((GeneralPath)shape).curveTo(18.274069, 5.8523855, 17.732351, 8.640903, 19.705551, 9.1023855);
((GeneralPath)shape).lineTo(19.705551, 17.102386);
((GeneralPath)shape).lineTo(9.71609, 34.020084);
((GeneralPath)shape).curveTo(8.521776, 36.04272, 7.191811, 39.203884, 11.144566, 41.208633);
((GeneralPath)shape).curveTo(13.697754, 42.27191, 18.23556, 43.0, 24.0, 43.0);
((GeneralPath)shape).curveTo(29.76444, 43.0, 34.302246, 42.27191, 36.855434, 41.208633);
((GeneralPath)shape).curveTo(40.80819, 39.203884, 39.478226, 36.04272, 38.28391, 34.020084);
((GeneralPath)shape).lineTo(28.294447, 17.102386);
((GeneralPath)shape).lineTo(28.294447, 9.102386);
((GeneralPath)shape).curveTo(30.422329, 8.552517, 29.725931, 5.8523865, 28.771608, 5.6023865);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_3_9;
g.setTransform(defaultTransform__0_3_9);
g.setClip(clip__0_3_9);
origAlpha = alpha__0_3;
g.setTransform(defaultTransform__0_3);
g.setClip(clip__0_3);
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
        return 3;
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
	public AlchemyIcon() {
        this.width = getOrigWidth();
        this.height = getOrigHeight();
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public AlchemyIcon(Dimension size) {
	this.width = size.width;
	this.height = size.width;
	}

	public AlchemyIcon(int width, int height) {
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

