package com.kotov.smartnotes.utils.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * addAlarm для добавления нового будильника и передачи кода запроса.
 * <p>
 * cancelAlarm чтобы удалить тревогу, вам нужно воссоздать то же
 * намерение и передать тот же код запроса.
 * <p>
 * hasAlarm чтобы проверить, добавлен ли этот сигнал тревоги, вам необходимо воссоздать то же намерение и передать тот же код запроса.
 * <p>
 * cancelAllAlarms для удаления ВСЕХ установленных сигналов тревоги.
 */
public class AlarmUtils {
    private static final String sTagAlarms = ":alarms";

    public static void addAlarm(Context context, Intent intent, int notificationId, Calendar calendar) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Objects.requireNonNull(alarmManager).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            Objects.requireNonNull(alarmManager).set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        saveAlarmId(context, notificationId);
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Objects.requireNonNull(alarmManager).cancel(pendingIntent);
        pendingIntent.cancel();
        Toast.makeText(context, "Дзинь-дзинь! Пора кормить кота" + intent.getIntExtra("close_id", -1),
                Toast.LENGTH_LONG).show();
        removeAlarmId(context, notificationId);
    }

    public static void cancelAllAlarms(Context context, Intent intent) {
        for (int idAlarm : getAlarmIds(context)) {
            cancelAlarm(context, intent, idAlarm);
        }
    }

    public static boolean hasAlarm(Context context, Intent intent, int notificationId) {
        return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private static void saveAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        if (idsAlarms.contains(id)) {
            return;
        }

        idsAlarms.add(id);

        saveIdsInPreferences(context, idsAlarms);
    }

    private static void removeAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        for (int i = 0; i < idsAlarms.size(); i++) {
            if (idsAlarms.get(i) == id)
                idsAlarms.remove(i);
        }

        saveIdsInPreferences(context, idsAlarms);
    }

    private static List<Integer> getAlarmIds(Context context) {
        List<Integer> ids = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            JSONArray jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + sTagAlarms, "[]"));

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    private static void saveIdsInPreferences(Context context, List<Integer> lstIds) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : lstIds) {
            jsonArray.put(idAlarm);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getPackageName() + sTagAlarms, jsonArray.toString());

        editor.apply();
    }
}