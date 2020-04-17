package ru.bmixsoft.jsontest.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 12.02.2018.
 */

public class CalendarHelper {

    public static void addEventIntent(Context context)
    {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", context.getString(R.string.app_name));
        context.startActivity(intent);
    }

    public static long addEvent(Context context, String descr, long date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        return addEvent(context, descr, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

        // Add an event to the calendar of the user.
    public static long addEvent(Context context, String descr, int _year, int _month, int _day, int _hour, int _minute) {

        GregorianCalendar calDate = new GregorianCalendar(_year,_month,_day,_hour,_minute);

        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+ 15 * 60*1000); // 15 минут
            values.put(CalendarContract.Events.TITLE, context.getString(R.string.app_name));
            values.put(CalendarContract.Events.DESCRIPTION, descr);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            System.out.println(Calendar.getInstance().getTimeZone().getID());
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            long eventId = Long.parseLong(uri.getLastPathSegment());
            // Add a 30 minute, 1 hour and 1 day reminders (3 reminders)
            setReminder(cr, eventId, 30);
            setReminder(cr, eventId, 60);
            setReminder(cr, eventId, 60*24);
            try {
                Utils.msgInfo("Добавлено новое событие в календарь");
            }
            catch (Exception e)
            {

            }
            return  eventId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // routine to add reminders with the event
    public static void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // function to remove an event from the calendar using the eventId stored within the Task object.
    public static void removeEventNew(Context context, long eventId) {

        ContentResolver cr = context.getContentResolver();
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        ContentValues event = new ContentValues();
        cr.delete(eventUri, null,  null);

        try {
            Utils.msgInfo("Удалено событие из календаря");
        }
        catch (Exception e)
        {

        }

        //   Log.i(DEBUG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");
    }

    // function to remove an event from the calendar using the eventId stored within the Task object.
    public static void removeEvent(Context context, long eventId) {
        ContentResolver cr = context.getContentResolver();

        int iNumRowsDeleted = 0;
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        iNumRowsDeleted = cr.delete(eventUri, null, null);
        try {
            Utils.msgInfo("Удалено событие из календаря");
        }
        catch (Exception e)
        {

        }

     //   Log.i(DEBUG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");
    }

    public static long updateEvent(Context context,long eventId, String descr, long date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        return updateEvent(context, eventId, descr, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static int updateEvent(Context context, long _eventId, String descr, int _year, int _month, int _day, int _hour, int _minute) {
        int iNumRowsUpdated = 0;
        GregorianCalendar calDate = new GregorianCalendar(_year,_month,_day,_hour,_minute);


         ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.DESCRIPTION, descr);
        event.put("hasAlarm", 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.DTSTART, calDate.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis()+15*60*1000);// минут

       // Uri eventsUri = Uri.parse(CalendarContract.Reminders.CONTENT_URI + "events");
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, _eventId);

        iNumRowsUpdated = context.getContentResolver().update(eventUri, event, null,
                null);

        // TODO put text into strings.xml
     //   Log.i(DEBUG_TAG, "Updated " + iNumRowsUpdated + " calendar entry.");

        return iNumRowsUpdated;
    }

}
