package org.billthefarmer.currency.util;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Icon mask shapes used by various Android OEMs.
 * Paths are defined in a 100x100 coordinate space and scaled at render time.
 */
public class IconMaskShapes {

    public static final String CIRCLE =
        "M50,0A50,50,0,1,1,50,100A50,50,0,1,1,50,0Z";

    // Google Pixel / AOSP default
    public static final String SQUIRCLE =
        "M50,0C10,0,0,10,0,50C0,90,10,100,50,100C90,100,100,90,100,50C100,10,90,0,50,0Z";

    // Samsung / Xiaomi
    public static final String ROUNDED_SQUARE =
        "M50,0L92,0Q100,0,100,8L100,92Q100,100,92,100L8,100Q0,100,0,92L0,8Q0,0,8,0Z";

    // Teardrop
    public static final String TEARDROP =
        "M50,0A50,50,0,0,1,100,50L100,85Q100,100,85,100L50,100A50,50,0,0,1,0,50A50,50,0,0,1,50,0Z";

    // Pebble / clover shape
    public static final String PEBBLE =
        "M50,0Q85,0,100,15L100,85Q100,100,85,100L15,100Q0,100,0,85L0,15Q0,0,50,0Z";

    public static Path createScaledPath(String pathData, int size) {
        Path path = new Path();
        PathDataParser.parse(path, pathData);
        Matrix matrix = new Matrix();
        matrix.setScale(size / 100f, size / 100f);
        path.transform(matrix);
        return path;
    }

    public static MaskShape[] getAllShapes() {
        return new MaskShape[] {
            new MaskShape("circle", CIRCLE),
            new MaskShape("squircle", SQUIRCLE),
            new MaskShape("rounded_square", ROUNDED_SQUARE),
            new MaskShape("teardrop", TEARDROP),
            new MaskShape("pebble", PEBBLE),
        };
    }

    public static class MaskShape {
        public final String name;
        public final String pathData;

        public MaskShape(String name, String pathData) {
            this.name = name;
            this.pathData = pathData;
        }
    }

    /**
     * Minimal SVG path data parser supporting M, L, A, Q, C, Z commands.
     * Android's PathParser is private API in test contexts, so we roll our own.
     */
    static class PathDataParser {
        static void parse(Path path, String data) {
            float[] coords = new float[7];
            int i = 0;
            float cx = 0, cy = 0;
            while (i < data.length()) {
                char cmd = data.charAt(i);
                if (Character.isLetter(cmd) || cmd == ',') {
                    i++;
                } else {
                    // skip whitespace
                    i++;
                    continue;
                }

                switch (cmd) {
                    case 'M':
                        i = readFloats(data, i, coords, 2);
                        path.moveTo(coords[0], coords[1]);
                        cx = coords[0]; cy = coords[1];
                        break;
                    case 'L':
                        i = readFloats(data, i, coords, 2);
                        path.lineTo(coords[0], coords[1]);
                        cx = coords[0]; cy = coords[1];
                        break;
                    case 'Q':
                        i = readFloats(data, i, coords, 4);
                        path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                        cx = coords[2]; cy = coords[3];
                        break;
                    case 'C':
                        i = readFloats(data, i, coords, 6);
                        path.cubicTo(coords[0], coords[1], coords[2], coords[3],
                                     coords[4], coords[5]);
                        cx = coords[4]; cy = coords[5];
                        break;
                    case 'A':
                        i = readFloats(data, i, coords, 7);
                        // coords: rx, ry, xrot, largeArc, sweep, x, y
                        arcTo(path, cx, cy, coords[0], coords[1],
                              coords[2], coords[3] != 0, coords[4] != 0,
                              coords[5], coords[6]);
                        cx = coords[5]; cy = coords[6];
                        break;
                    case 'Z':
                    case 'z':
                        path.close();
                        break;
                    default:
                        break;
                }
            }
        }

        private static int readFloats(String data, int pos, float[] out, int count) {
            int idx = 0;
            StringBuilder sb = new StringBuilder();
            while (pos < data.length() && idx < count) {
                char c = data.charAt(pos);
                if (c == ',' || c == ' ') {
                    if (sb.length() > 0) {
                        out[idx++] = Float.parseFloat(sb.toString());
                        sb.setLength(0);
                    }
                    pos++;
                } else if (c == '-' && sb.length() > 0) {
                    // negative sign starts a new number
                    out[idx++] = Float.parseFloat(sb.toString());
                    sb.setLength(0);
                    sb.append(c);
                    pos++;
                } else if (Character.isDigit(c) || c == '.' || c == '-') {
                    sb.append(c);
                    pos++;
                } else {
                    break;
                }
            }
            if (sb.length() > 0 && idx < count) {
                out[idx++] = Float.parseFloat(sb.toString());
            }
            return pos;
        }

        /**
         * Approximate SVG arc with cubic beziers via Android's Path.arcTo.
         */
        private static void arcTo(Path path, float cx, float cy,
                                   float rx, float ry, float xRot,
                                   boolean largeArc, boolean sweep,
                                   float x, float y) {
            // Use simple approximation: compute bounding oval and sweep angle
            double dx2 = (cx - x) / 2.0;
            double dy2 = (cy - y) / 2.0;
            double midX = (cx + x) / 2.0;
            double midY = (cy + y) / 2.0;

            double d = (dx2 * dx2) / (rx * rx) + (dy2 * dy2) / (ry * ry);
            if (d > 1) {
                rx *= Math.sqrt(d);
                ry *= Math.sqrt(d);
            }

            // For icon masks, the arcs are simple enough that we can
            // approximate with a bounding rect arc
            RectF oval = new RectF(
                (float)(midX - rx), (float)(midY - ry),
                (float)(midX + rx), (float)(midY + ry));

            float startAngle = (float) Math.toDegrees(Math.atan2(cy - midY, cx - midX));
            float endAngle = (float) Math.toDegrees(Math.atan2(y - midY, x - midX));
            float sweepAngle = endAngle - startAngle;

            if (sweep && sweepAngle < 0) sweepAngle += 360;
            if (!sweep && sweepAngle > 0) sweepAngle -= 360;

            path.arcTo(oval, startAngle, sweepAngle);
        }
    }
}
