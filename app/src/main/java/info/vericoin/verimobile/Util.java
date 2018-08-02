package info.vericoin.verimobile;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;

import java.text.DateFormat;

public class Util {

    public static int getConfidenceResource(TransactionConfidence.ConfidenceType confidenceType) {
        if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
            return R.drawable.transaction_building;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.PENDING)) {
            return R.drawable.transaction_pending;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.DEAD)) {
            return R.drawable.transaction_dead;
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.IN_CONFLICT)) {
            return R.drawable.transaction_conflict;
        } else{
            return R.drawable.transaction_unknown;
        }
    }

    public static String getConfidenceString(TransactionConfidence.ConfidenceType confidenceType){
        if (confidenceType.equals(TransactionConfidence.ConfidenceType.BUILDING)) {
            return "Building";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.PENDING)) {
            return "Pending";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.DEAD)) {
            return "Dead";
        } else if (confidenceType.equals(TransactionConfidence.ConfidenceType.IN_CONFLICT)) {
            return "In Conflict";
        } else{
            return "Unknown";
        }
    }

    public static String getDateString(Transaction tx){
        DateFormat format = DateFormat.getDateInstance();
        return  format.format(tx.getUpdateTime());
    }

    public static String getDateTimeString(Transaction tx){
        DateFormat format = DateFormat.getDateTimeInstance();
        return  format.format(tx.getUpdateTime());
    }
}
