package de.igorlueckel.andropiled.helpers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 20.08.2015.
 */
public class VideoAnalyzer {

    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

    public VideoAnalyzer(int videoId, Context context) {
        final AssetFileDescriptor afd = context.getResources().openRawResourceFd(videoId);
        mediaMetadataRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
    }

    public void analyze(LedDevice ledDevice) {
        String videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String length = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        long nanoLength = TimeUnit.MILLISECONDS.toNanos(Long.parseLong(length));
        long nanoTick = TimeUnit.MILLISECONDS.toNanos(1000 / 24);
        long currentNanos = 0;
        Bitmap lastBitmap = null;
        while (currentNanos <= nanoLength) {
            Bitmap sync = mediaMetadataRetriever.getFrameAtTime(currentNanos, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//            if (lastBitmap != null && lastBitmap.sameAs(sync))
//                break;
            int color = getAverageColor(sync, ledDevice);
            Log.d("", "analyze: " + color);
            lastBitmap = sync;
            currentNanos += nanoTick;
        }
        mediaMetadataRetriever.release();
    }

    int getAverageColor(Bitmap bitmap, LedDevice ledDevice) {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
        }

        return Color.rgb(redBucket / pixelCount,
                greenBucket / pixelCount,
                blueBucket / pixelCount);
    }

    /**
     *
     * @param original
     * @param color Color.RED
     * @return
     */
    Bitmap tintBitmap(Bitmap original, int color) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColorFilter(new LightingColorFilter(color, 0));

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }
}
