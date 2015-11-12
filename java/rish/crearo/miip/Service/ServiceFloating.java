package rish.crearo.miip.Service;

import android.animation.Animator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import rish.crearo.miip.Manager.PInfo;
import rish.crearo.miip.Manager.RetrievePackages;
import rish.crearo.miip.R;

public class ServiceFloating extends Service {

    public static int ID_NOTIFICATION = 979;

    private WindowManager windowManager;

    private View chatHead, comicDialogBox, bottomDialogBox;

    boolean mHasDoubleClicked = false;
    long lastPressTime;

    ArrayList<String> myArray;
    ArrayList<PInfo> apps;
    List listApps;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        RetrievePackages getInstalledPackages = new RetrievePackages(getApplicationContext());
        apps = getInstalledPackages.getInstalledApps(false);
        myArray = new ArrayList<>();

        for (int i = 0; i < apps.size(); ++i) {
            myArray.add(apps.get(i).appname);
        }

        listApps = new ArrayList();
        for (int i = 0; i < apps.size(); ++i) {
            listApps.add(apps.get(i));
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_window, null);

        chatHead = popupView;
        comicDialogBox = chatHead.findViewById(R.id.popup_dialog_rellay);
        bottomDialogBox = chatHead.findViewById(R.id.popup_dialog_bottom_rellay);

        setInvisible(comicDialogBox);
        setInvisible(bottomDialogBox);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        try {
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            long pressTime = System.currentTimeMillis();

                            // If double click...
                            if (pressTime - lastPressTime <= 300) {
                                createNotification();
                                stopSelf();
                                mHasDoubleClicked = true;
                            } else {     // If not double click....
                                mHasDoubleClicked = false;
                            }
                            lastPressTime = pressTime;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            pressTime = System.currentTimeMillis();
                            // If single click...
                            if (pressTime - lastPressTime <= 100) {
                                if (comicDialogBox.getVisibility() == View.VISIBLE) {
                                    animRevealExit(comicDialogBox);
                                } else {
                                    animRevealOpen(comicDialogBox);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, paramsF);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatHead.setLongClickable(true);

        chatHead.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

                setVisible(bottomDialogBox);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setInvisible(bottomDialogBox);
                    }
                }, 500);

                return false;
            }
        });

    }

    private void initiatePopupWindow(View anchor) {
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_window, null);

            windowManager.removeView(anchor);


//            ListPopupWindow popup = new ListPopupWindow(this);
//            popup.setAnchorView(anchor);
//            popup.setWidth((int) (display.getWidth() / (1.5)));
//            popup.setAdapter(new CustomAdapter(getApplicationContext(), R.layout.row, listApps));
//            popup.setOnItemClickListener(new OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View view, int position, long id3) {
//                    Intent i;
//                    PackageManager manager = getPackageManager();
//                    try {
//                        i = manager.getLaunchIntentForPackage(apps.get(position).pname.toString());
//                        if (i == null)
//                            throw new PackageManager.NameNotFoundException();
//                        i.addCategory(Intent.CATEGORY_LAUNCHER);
//                        startActivity(i);
//                    } catch (PackageManager.NameNotFoundException e) {
//
//                    }
//                }
//            });
//            popup.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), ServiceFloating.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new Notification(R.drawable.floating2, "Click to start launcher", System.currentTimeMillis());
        notification.setLatestEventInfo(getApplicationContext(), "Start launcher", "Click to start launcher", pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICATION, notification);
    }


    private void animRevealOpen(View viewRoot) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            viewRoot.clearAnimation();
            int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 5;
            int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
            int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

            final Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);

            setVisible(viewRoot);
            anim.setDuration(500);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.start();
        }
    }

    void animRevealExit(final View viewRoot) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            viewRoot.clearAnimation();
            int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 5;
            int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;

            int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

            // create the animation (the final radius is zero)
            final Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, finalRadius, 0);

            viewRoot.setVisibility(View.VISIBLE);
            anim.setDuration(500);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInvisible(viewRoot);
                }
            }, 500);
        }
    }

    private void setInvisible(View view) {
        view.setVisibility(View.GONE);
    }

    private void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void alterVisibility(View view) {
        if (view.getVisibility() == View.GONE)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}