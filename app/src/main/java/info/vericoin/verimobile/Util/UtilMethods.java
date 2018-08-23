package info.vericoin.verimobile.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.common.BitMatrix;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionConfidence;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import info.vericoin.verimobile.R;

import static android.graphics.Color.BLACK;

public class UtilMethods {

    private static Context context;

    public static void setContext(Context context) {
        UtilMethods.context = context;
    }

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

    public static String mnemonicToString(List<String> mnemonics) {
        String mnemonicText = mnemonics.get(0);
        for (int i = 1; i < mnemonics.size(); i++) {
            mnemonicText = mnemonicText.concat(" " + mnemonics.get(i));
        }
        return mnemonicText;
    }

    public static List<String> stringToMnemonic(String mnemonicString) {
        return Arrays.asList(mnemonicString.split("\\s+"));
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
        int stringId;
        if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
            stringId = R.string.building_tx;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.PENDING)) {
            stringId = R.string.pending_tx;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.DEAD)) {
            stringId = R.string.dead_tx;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.IN_CONFLICT)) {
            stringId = R.string.conflict_tx;
        } else {
            stringId = R.string.unknown_tx;
        }
        return context.getString(stringId);
    }

    public static String getDateString(Date date) {
        if (date == null) {
            return context.getString(R.string.N_A);
        } else {
            DateFormat format = DateFormat.getDateInstance();
            return format.format(date);
        }
    }

    public static String getDateTimeString(Date date) {
        if (date == null) {
            return context.getString(R.string.N_A);
        } else {
            DateFormat format = DateFormat.getDateTimeInstance();
            return format.format(date);
        }
    }
}
