package org.billthefarmer.currency;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import org.billthefarmer.currency.util.IconMaskShapes;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Screenshot tests for the adaptive icon across device configurations.
 *
 * Loads foreground (ic_coin), background, and monochrome layers individually
 * and composes them manually, simulating how different launchers render the
 * adaptive icon with various mask shapes.
 *
 * The monochrome vector is loaded separately because its path data exceeds
 * aapt2's string size limit when processed through AdaptiveIconDrawable.
 *
 * Run:  ./gradlew testDebugUnitTest --tests "*.AdaptiveIconScreenshotTest"
 * Report: build/reports/adaptive-icon/index.html
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {21, 26, 33, 35})
public class AdaptiveIconScreenshotTest {

    private static final int RENDER_SIZE = 432;
    private static final String REPORT_DIR = "build/reports/adaptive-icon";
    private static final File OUTPUT_DIR = new File(REPORT_DIR);
    private static final List<Screenshot> ALL_SCREENSHOTS = new ArrayList<>();

    private Context context;
    private Drawable foreground; // ic_coin

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        OUTPUT_DIR.mkdirs();

        foreground = loadDrawable("ic_coin");
        assertNotNull("ic_coin drawable must exist", foreground);
    }

    @AfterClass
    public static void generateReport() throws IOException {
        if (ALL_SCREENSHOTS.isEmpty()) return;
        writeHtmlReport(ALL_SCREENSHOTS);
    }

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    @Test
    public void testComposedIcon_allMaskShapes() {
        for (IconMaskShapes.MaskShape shape : IconMaskShapes.getAllShapes()) {
            Bitmap bmp = renderComposedIcon(shape.pathData, RENDER_SIZE);
            String filename = "mask_" + shape.name + ".png";
            savePng(bmp, filename);
            ALL_SCREENSHOTS.add(new Screenshot(filename, "Mask: " + shape.name,
                "Composed icon with " + shape.name + " mask"));
        }
    }

    @Test
    public void testComposedIcon_screenDensities() {
        String[] labels = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
        int[] dpi = {160, 240, 320, 480, 640};

        for (int i = 0; i < labels.length; i++) {
            int sizePx = (int) (48 * (dpi[i] / 160f));
            Bitmap bmp = renderComposedIcon(IconMaskShapes.SQUIRCLE, sizePx);
            String filename = "density_" + labels[i] + "_" + sizePx + "px.png";
            savePng(bmp, filename);
            ALL_SCREENSHOTS.add(new Screenshot(filename,
                "Density: " + labels[i] + " (" + sizePx + "px)",
                dpi[i] + "dpi, " + sizePx + "x" + sizePx + "px"));
        }
    }

    @Test
    public void testIndividualLayers() {
        // Background (white)
        Bitmap bg = Bitmap.createBitmap(RENDER_SIZE, RENDER_SIZE,
            Bitmap.Config.ARGB_8888);
        new Canvas(bg).drawColor(Color.WHITE);
        savePng(bg, "layer_background.png");
        ALL_SCREENSHOTS.add(new Screenshot("layer_background.png",
            "Layer: background", "Solid white background"));

        // Foreground (ic_coin with 25% inset, on transparent)
        Bitmap fg = renderDrawableOnCanvas(foreground, RENDER_SIZE,
            Color.TRANSPARENT, 0.25f);
        savePng(fg, "layer_foreground.png");
        ALL_SCREENSHOTS.add(new Screenshot("layer_foreground.png",
            "Layer: foreground", "ic_coin with 25% inset"));

        // Foreground without inset (raw coin)
        Bitmap raw = renderDrawableOnCanvas(foreground, RENDER_SIZE,
            Color.TRANSPARENT, 0f);
        savePng(raw, "layer_foreground_raw.png");
        ALL_SCREENSHOTS.add(new Screenshot("layer_foreground_raw.png",
            "Layer: foreground (raw)", "ic_coin without inset"));
    }

    @Test
    public void testMonochromeLayers() {
        Drawable monochrome = loadDrawable("ic_launcher_monochrome");
        if (monochrome == null) {
            System.out.println("Skipping monochrome tests: ic_launcher_monochrome " +
                "failed to load (path data may exceed aapt2 string limit)");
            return;
        }

        Bitmap mono = renderDrawableOnCanvas(monochrome, RENDER_SIZE,
            Color.parseColor("#F5F5F5"), 0f);
        savePng(mono, "layer_monochrome.png");
        ALL_SCREENSHOTS.add(new Screenshot("layer_monochrome.png",
            "Layer: monochrome", "Monochrome vector (themed icons, API 33+)"));
    }

    @Test
    public void testThemedMonochrome() {
        Drawable monochrome = loadDrawable("ic_launcher_monochrome");
        if (monochrome == null) {
            System.out.println("Skipping themed tests: monochrome drawable unavailable");
            return;
        }

        int[][] themes = {
            {0xFFE8DEF8, 0xFF1C1B1F}, // Material You light purple
            {0xFFFEF7FF, 0xFF21005E}, // Light lavender
            {0xFF1C1B1F, 0xFFE8DEF8}, // Dark purple
            {0xFFD0BCFF, 0xFF381E72}, // Deep purple
            {0xFF98D8C8, 0xFF004D40}, // Teal
            {0xFFFFDAD6, 0xFF410002}, // Red / error
            {0xFF1B1B1F, 0xFFE3E2E6}, // Dark neutral
        };
        String[] names = {
            "light_purple", "light_lavender", "dark_purple",
            "deep_purple", "teal", "red", "dark_neutral"
        };

        for (int i = 0; i < themes.length; i++) {
            Bitmap bmp = renderThemedIcon(monochrome,
                themes[i][0], themes[i][1], IconMaskShapes.SQUIRCLE, RENDER_SIZE);
            String filename = "themed_" + names[i] + ".png";
            savePng(bmp, filename);
            ALL_SCREENSHOTS.add(new Screenshot(filename,
                "Themed: " + names[i],
                String.format("bg=#%06X fg=#%06X",
                    themes[i][0] & 0xFFFFFF, themes[i][1] & 0xFFFFFF)));
        }
    }

    @Test
    public void testSafeZoneVisualization() {
        Bitmap bmp = renderComposedIcon(IconMaskShapes.SQUIRCLE, RENDER_SIZE);
        Canvas canvas = new Canvas(bmp);

        // 66/108 of the icon is the "safe zone"
        float safeFraction = 66f / 108f;
        float safeSize = RENDER_SIZE * safeFraction;
        float offset = (RENDER_SIZE - safeSize) / 2f;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3f);
        canvas.drawOval(new RectF(offset, offset,
            offset + safeSize, offset + safeSize), paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(24f);
        canvas.drawText("safe zone (66dp)", offset + 8, offset - 8, paint);

        savePng(bmp, "safe_zone.png");
        ALL_SCREENSHOTS.add(new Screenshot("safe_zone.png",
            "Safe zone visualization",
            "Red oval = 66dp safe zone. Content outside may be clipped by launchers."));
    }

    @Test
    public void testForegroundAssertions() {
        // Verify the coin drawable renders visible content
        Bitmap fg = renderDrawableOnCanvas(foreground, 192, Color.WHITE, 0f);
        boolean hasContent = false;
        outer:
        for (int x = 0; x < fg.getWidth(); x++) {
            for (int y = 0; y < fg.getHeight(); y++) {
                if (fg.getPixel(x, y) != Color.WHITE) {
                    hasContent = true;
                    break outer;
                }
            }
        }
        assertTrue("Foreground (ic_coin) should contain visible content", hasContent);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    /**
     * Compose a full adaptive icon: white background + foreground with mask.
     * The foreground gets the adaptive icon padding (18/108) plus the 25%
     * inset defined in ic_launcher.xml.
     */
    private Bitmap renderComposedIcon(String maskPathData, int size) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Path mask = IconMaskShapes.createScaledPath(maskPathData, size);
        canvas.clipPath(mask);

        // White background fills the whole masked area
        canvas.drawColor(Color.WHITE);

        // Foreground: the adaptive icon canvas is 108dp, the visible window
        // is the inner 72dp. Launchers draw the 108dp layers at full size
        // and clip with the mask. The foreground already has a 25% inset
        // defined in ic_launcher.xml, which aapt applies to the drawable.
        // Here we replicate that: draw ic_coin with combined padding.
        float insetFraction = 0.25f;
        int insetPx = (int) (size * insetFraction);
        foreground.setBounds(insetPx, insetPx, size - insetPx, size - insetPx);
        foreground.draw(canvas);

        return bmp;
    }

    private Bitmap renderDrawableOnCanvas(Drawable d, int size,
                                           int bgColor, float insetFraction) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        if (bgColor != Color.TRANSPARENT) canvas.drawColor(bgColor);

        int inset = (int) (size * insetFraction);
        d.setBounds(inset, inset, size - inset, size - inset);
        d.draw(canvas);
        return bmp;
    }

    private Bitmap renderThemedIcon(Drawable mono, int bgColor, int fgColor,
                                     String maskPathData, int size) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Path mask = IconMaskShapes.createScaledPath(maskPathData, size);
        canvas.clipPath(mask);
        canvas.drawColor(bgColor);

        Drawable copy = mono.mutate();
        copy.setTint(fgColor);
        copy.setBounds(0, 0, size, size);
        copy.draw(canvas);
        return bmp;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Drawable loadDrawable(String name) {
        int resId = context.getResources().getIdentifier(
            name, "drawable", context.getPackageName());
        if (resId == 0) return null;
        try {
            return context.getResources().getDrawable(resId, null);
        } catch (Exception e) {
            System.out.println("Warning: could not load drawable " + name +
                ": " + e.getMessage());
            return null;
        }
    }

    private static void savePng(Bitmap bmp, String filename) {
        File file = new File(OUTPUT_DIR, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + filename, e);
        }
    }

    private static void writeHtmlReport(List<Screenshot> shots) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n");
        sb.append("<title>Adaptive Icon Test Report</title>\n");
        sb.append("<style>\n");
        sb.append("*{box-sizing:border-box}\n");
        sb.append("body{font-family:system-ui,-apple-system,sans-serif;max-width:1200px;");
        sb.append("margin:0 auto;padding:2rem;background:#fafafa;color:#1a1a1a}\n");
        sb.append("h1{border-bottom:2px solid #333;padding-bottom:.5rem}\n");
        sb.append("h2{margin-top:2rem;color:#444}\n");
        sb.append(".grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));");
        sb.append("gap:1.5rem;margin:1rem 0 2rem}\n");
        sb.append(".card{background:#fff;border-radius:12px;padding:1rem;");
        sb.append("box-shadow:0 2px 8px rgba(0,0,0,.1);text-align:center}\n");
        sb.append(".card img{max-width:100%;height:auto;border-radius:8px;");
        sb.append("background:repeating-conic-gradient(#e0e0e0 0% 25%,#fff 0% 50%) 50%/16px 16px}\n");
        sb.append(".card h3{margin:.75rem 0 .25rem;font-size:.95rem}\n");
        sb.append(".card p{margin:0;font-size:.8rem;color:#666}\n");
        sb.append(".meta{color:#999;font-size:.85rem;margin-bottom:1rem}\n");
        sb.append("</style>\n</head>\n<body>\n");
        sb.append("<h1>Adaptive Icon Test Report</h1>\n");
        sb.append("<p class=\"meta\">Generated by AdaptiveIconScreenshotTest &mdash; ");
        sb.append(shots.size()).append(" screenshots</p>\n");

        sb.append("<div class=\"grid\">\n");
        for (Screenshot s : shots) {
            sb.append("<div class=\"card\">\n");
            sb.append("  <img src=\"").append(s.filename)
              .append("\" alt=\"").append(s.title).append("\">\n");
            sb.append("  <h3>").append(s.title).append("</h3>\n");
            sb.append("  <p>").append(s.description).append("</p>\n");
            sb.append("</div>\n");
        }
        sb.append("</div>\n");

        sb.append("</body>\n</html>\n");

        File report = new File(OUTPUT_DIR, "index.html");
        try (FileWriter fw = new FileWriter(report)) {
            fw.write(sb.toString());
        }
        System.out.println("\n>> Report: " + report.getAbsolutePath());
    }

    private static class Screenshot {
        final String filename, title, description;

        Screenshot(String filename, String title, String description) {
            this.filename = filename;
            this.title = title;
            this.description = description;
        }
    }
}
