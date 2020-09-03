package o1310.rx1310.app.optiram;

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

public class MainActivity extends Activity {

	TextView mResultText;
	Timer killTask;
	String mUserReport = null;
	int mAmountOfApps = 0;
	int mKillTimer;
	ArrayList<String> mProcessesList;

	@Override
    protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);

        setContentView(R.layout.activity_main);

        mResultText = findViewById(R.id.resultText);

        try {
            cleanUp();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
		
    }

	void cleanUp() {

		try {

            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
			PackageManager packageManager = getPackageManager();

            activityManager.getMemoryInfo(memoryInfo);
            activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

			int wastedMemSize = (int) ((memoryInfo.availMem / 1024) / 1024);

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
            
			killTask = new Timer();
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

}
