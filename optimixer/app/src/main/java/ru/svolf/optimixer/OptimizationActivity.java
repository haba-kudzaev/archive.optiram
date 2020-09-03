package ru.svolf.optimixer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class OptimizationActivity extends Activity {
    private TextView mResultText;

    private int mAmountOfApps = 0;
    private int mKillTimer = 0;
    private String mUserReport = null;
    private ArrayList<String> mProcessesList;

    private void cleanUp() {
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            int wastedMemSize = (int) ((memoryInfo.availMem / 1024) / 1024);
            activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            PackageManager packageManager = getPackageManager();
            @SuppressLint("WrongConstant") List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(8704);
            if (Build.VERSION.SDK_INT < 22) {
                mProcessesList = new ArrayList<>();
                try {
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
                        if (runningAppProcessInfo.processName != null) {
                            mProcessesList.add(runningAppProcessInfo.processName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (ApplicationInfo applicationInfo : installedApplications) {
                if (applicationInfo.processName != null) {
                    try {
                        StringBuilder details;
                        if (Build.VERSION.SDK_INT < 22 && !applicationInfo.processName.equals(getPackageName()) && mAmountOfApps < 39 && mAmountOfApps >= 1 && mProcessesList.contains(applicationInfo.processName)) {
                            details = new StringBuilder();
                            details.append(mUserReport);
                            details.append("\n- ");
                            details.append((String) packageManager.getApplicationLabel(applicationInfo));
                            mUserReport = details.toString();
                            mAmountOfApps++;
                        }
                        if (Build.VERSION.SDK_INT >= 22 && !applicationInfo.processName.equals(getPackageName()) && mAmountOfApps < 39 && mAmountOfApps >= 1) {
                            details = new StringBuilder();
                            details.append(mUserReport);
                            details.append("\n- ");
                            details.append((String) packageManager.getApplicationLabel(applicationInfo));
                            mUserReport = details.toString();
                            mAmountOfApps++;
                        }
                        if (Build.VERSION.SDK_INT < 22 && !applicationInfo.processName.equals(getPackageName()) && mAmountOfApps == 0 && mProcessesList.contains(applicationInfo.processName)) {
                            details = new StringBuilder();
                            details.append("=======================\n- ");
                            details.append((String) packageManager.getApplicationLabel(applicationInfo));
                            mUserReport = details.toString();
                            mAmountOfApps++;
                        }
                        if (Build.VERSION.SDK_INT >= 22 && !applicationInfo.processName.equals(getPackageName()) && mAmountOfApps == 0) {
                            details = new StringBuilder();
                            details.append("=======================\n- ");
                            details.append((String) packageManager.getApplicationLabel(applicationInfo));
                            mUserReport = details.toString();
                            mAmountOfApps++;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    try {
                        if (!applicationInfo.processName.equals(getPackageName())) {
                            activityManager.killBackgroundProcesses(applicationInfo.processName);
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
            activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            int clearedMemSize = (int) ((memoryInfo.availMem / 1024) / 1024);
            int releasedMemSize = clearedMemSize - wastedMemSize;
            if (releasedMemSize < 0) {
                releasedMemSize = 0;
            }
            String valueOf = String.valueOf(releasedMemSize);
            if (mUserReport != null) {
                mUserReport = "Memory released: " +
                        valueOf +
                        "MB\n" +
                        "Available memory: " +
                        wastedMemSize +
                        " >> " +
                        clearedMemSize +
                        "MB\n\n" +
                        "Terminated apps: " +
                        mUserReport;
            }
            if (mUserReport == null) {
                mUserReport = "Memory released: " +
                        valueOf +
                        "MB\n" +
                        "Available memory: " +
                        wastedMemSize +
                        " >> " +
                        clearedMemSize +
                        "MB\n\n" +
                        "-- ended --";
            }
            mResultText.setText(Html.fromHtml(String.format(Locale.ENGLISH, "%d >> <b>%dMiB</b>\n%sMiB released", wastedMemSize, clearedMemSize, valueOf)));
            Timer killTask = new Timer();
            killTask.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (mKillTimer >= 8) {
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    mKillTimer++;
                }
            }, 0, 400);
        } catch (Exception e4) {
            Toast.makeText(this, e4.getMessage(), Toast.LENGTH_LONG).show();
            e4.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization);

        mResultText = findViewById(R.id.textStatus);

        TextView textChannel = findViewById(R.id.textChannel);
        textChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri linkUri = Uri.parse("https://t.me/VolfsChannel");
                startActivity(new Intent(Intent.ACTION_VIEW, linkUri));
            }
        });

        try {
            cleanUp();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
