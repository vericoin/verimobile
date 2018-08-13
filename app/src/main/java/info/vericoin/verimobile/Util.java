package info.vericoin.verimobile;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.common.BitMatrix;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionConfidence;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;

import static android.graphics.Color.BLACK;

public class Util {

    public static String hashStringSHA256(String toHash) {
        MessageDigest messageDigest = Sha256Hash.newDigest();
        messageDigest.update(toHash.getBytes());
        byte[] hash = messageDigest.digest();
        return Base58.encode(hash);
    }

    public static int getConfidenceResource(TransactionConfidence.ConfidenceType confidenceType) {
        if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
            return R.drawable.transaction_building;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.PENDING)) {
            return R.drawable.transaction_pending;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.DEAD)) {
            return R.drawable.transaction_dead;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.IN_CONFLICT)) {
            return R.drawable.transaction_conflict;
        } else {
            return R.drawable.transaction_unknown;
        }
    }

    public static Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : Color.TRANSPARENT;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String getConfidenceString(TransactionConfidence.ConfidenceType confidenceType) {
        if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
            return "Building";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.PENDING)) {
            return "Pending";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.DEAD)) {
            return "Dead";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.IN_CONFLICT)) {
            return "In Conflict";
        } else {
            return "Unknown";
        }
    }

    public static String getDateString(Date date) {
        if (date == null) {
            return "Unknown";
        } else {
            DateFormat format = DateFormat.getDateInstance();
            return format.format(date);
        }
    }

    public static String getDateTimeString(Date date) {
        if (date == null) {
            return "Unknown";
        } else {
            DateFormat format = DateFormat.getDateTimeInstance();
            return format.format(date);
        }
    }
}
