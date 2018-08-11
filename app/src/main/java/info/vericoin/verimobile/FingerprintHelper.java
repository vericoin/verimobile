package info.vericoin.verimobile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

@TargetApi(28)
public class FingerprintHelper {

    private static final String TAG = MainActivity.class.getName();

    private AppCompatActivity context;

    public FingerprintHelper(AppCompatActivity context){
        this.context = context;
    }

    public interface OnAuthListener{
        void onSuccess();
    }

    private OnAuthListener listener;

    public OnAuthListener getListener() {
        return listener;
    }

    public void setListener(OnAuthListener listener) {
        this.listener = listener;
    }

    public boolean isFingerPrintSupported(){
        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
            } else {
                // Everything is ready for fingerprint authentication
                return true;
            }
        }
        return false;
    }

    private boolean isSupportBiometricSupported() {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return true;
        }
        return false;
    }

    public void unLockUsingBiometric(){
        // Create biometricPrompt
        BiometricPrompt mBiometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle("Unlock Wallet")
                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancel button clicked");
                    }
                })
                .build();
        mBiometricPrompt.authenticate(new CancellationSignal(), context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback(){
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if(listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    public void attemptToUnlock(){
        if (isSupportBiometricSupported()) {
            unLockUsingBiometric();
        }else if(isFingerPrintSupported()){
            unLockUsingFingerprintManager();
        }
    }

    public void unLockUsingFingerprintManager(){
        FingerprintDialog dialog = new FingerprintDialog();
        dialog.show(context.getSupportFragmentManager(), "Fingerprint Dialog");
        dialog.setListener(new FingerprintDialog.OnAuthListener() {
            @Override
            public void onSuccess() {
                if(listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }
}
