package info.vericoin.verimobile;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;

import java.text.DateFormat;
import java.util.Date;

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

    public static String getDateString(Date date){
        if(date == null){
            return "Unknown";
        }else {
            DateFormat format = DateFormat.getDateInstance();
            return format.format(date);
        }
    }

    public static String getDateTimeString(Date date){
        if(date == null){
            return "Unknown";
        }else {
            DateFormat format = DateFormat.getDateTimeInstance();
            return format.format(date);
        }
    }
}