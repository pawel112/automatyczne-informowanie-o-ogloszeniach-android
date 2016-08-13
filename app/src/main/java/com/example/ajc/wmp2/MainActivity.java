package com.example.ajc.wmp2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


public class MainActivity extends AppCompatActivity
{
    //parser
    String website1_url       = "http://wmp.uksw.edu.pl/pl/komunikaty_dla_student%C3%B3w";
    String website1_charset   = "utf-8";
    String website1_category1 = "wmp_uksw_edu_pl";
    String website1_category2 = "Wydział Matematyczno-Przyrodniczy";

    String website2_url       = "http://e-wmp.uksw.edu.pl/mod/data/view.php?id=225";
    String website2_charset   = "utf-8";
    String website2_category1 = "e-wmp_uksw_edu_pl";
    String website2_category2 = "Wydział Matematyczno-Przyrodniczy";

    int max = 4;

    String getHtml (String url, String charset)
    {
        try
        {
            String result = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset), 8);
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                result = result + line + "\n";
            }
            is.close();
            return result;
        }
        catch (IOException e)
        {
            return e.getStackTrace().toString();
        }
    }

    void parser (Database database)
    {
        parse1 (database);
        parse2 (database);
    }

    void parse1(Database database)
    {
        String result[] = new String[50];

        String website = getHtml (website1_url, website1_charset);
        Pattern p = Pattern.compile("<h2>.{1,}</h2>");
        Matcher m = p.matcher(website);
        int count = 0;
        int count2 = 0;

        while(m.find())
        {
            count++;
            String title = m.group();
            title = title.substring (27,title.length()-9);
            result[2*count] = title;
        }

        Pattern p2 = Pattern.compile("<div class=\"field-item even\" property=\"content:encoded\">.{1,}</p>");
        Matcher m2 = p2.matcher(website);

        while(m2.find())
        {
            count2++;
            String desc = m2.group();
            desc = desc.substring (59,desc.length()-4);
            desc = desc.replace("<strong>", "");
            desc = desc.replace("</strong>", "");
            desc = desc.replace("<br />", " ");
            desc = desc.replace("<br/>", " ");
            desc = desc.replace("style=\"text-align: justify;\">", "");
            result[2*count2+1] = desc;
        }

        for (int i=1; i<count; i++)
        {
            if (i<2*max)
            {
                break;
            }
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
            String dateString = dateFormat.format(currentDate);
            String timeString = dateFormat2.format(currentDate);
            String desc = result[2*i+1];
            String title = result[2*i];

            if (database.isExistAnnouncement(title) == false)
            {
                setNotification(getResources().getString(R.string.main_activity_notifier_title), getResources().getString(R.string.main_activity_notifier_text) + " " + website1_category1 + ".\nAutor: Dziekanat.");
            }

            database.addAnnouncement (dateString, timeString, "Dziekanat", website1_category1, website1_category2, "", "", "", desc, title);;
        }

    }

    void parse2 (Database database)
    {
        String website = getHtml (website2_url, website2_charset);
        Pattern p = Pattern.compile("<h4>.{1,}\"><img");
        Matcher m = p.matcher(website);
        String temp_number;

        m.find();
        temp_number = m.group();
        temp_number = temp_number.substring (68,temp_number.length()-6);

        for (int i=0; i<max; i++)
        {
            String url = "http://e-wmp.uksw.edu.pl/mod/data/view.php?d=2&rid="+(Integer.parseInt(temp_number)-i);
            String website2 = getHtml (url, website2_charset);
            website2 = website2.replace("\n", " ");
            website2 = website2.replace(" ", " ");
            website2 = website2.replace("<br />", " ");

            Pattern p2 = Pattern.compile("<h2>.{1,}</h2>");
            Matcher m2 = p2.matcher(website2);
            Pattern p3 = Pattern.compile("<p>.{1,}<p></p>");
            Matcher m3 = p3.matcher(website2);
            Pattern p4 = Pattern.compile("course=1\">.{1,40}</a>");
            Matcher m4 = p4.matcher(website2);
            Pattern p5 = Pattern.compile("<sup>ostatnia modyfikacja – .{4,13},  [0-9]{1,2} .{2,12} [0-9]{4}, [0-9]{2}:[0-9]{2}</sup><hr />");
            Matcher m5 = p5.matcher(website2);

            m2.find();
            String title = m2.group();
            title = title.substring (4,title.length()-5);

            m3.find();
            String desc = m3.group();
            desc = desc.substring (3, desc.length()-14);

            m4.find();
            String author = m4.group();
            author = author.substring (10, author.length()-4);

            m5.find();
            String data = m5.group();

            String time = data.substring (data.length()-17, data.length()-12)+":00";
            String month = "";
            if (data.contains("styczeń"))
            {
                month = "01";
            }
            else if (data.contains("luty"))
            {
                month = "02";
            }
            else if (data.contains("marzec"))
            {
                month = "03";
            }
            else if (data.contains("kwiecień"))
            {
                month = "04";
            }
            else if (data.contains("maj"))
            {
                month = "05";
            }
            else if (data.contains("czerwiec"))
            {
                month = "06";
            }
            else if (data.contains("lipiec"))
            {
                month = "07";
            }
            else if (data.contains("sierpień"))
            {
                month = "08";
            }
            else if (data.contains("wrzesień"))
            {
                month = "09";
            }
            else if (data.contains("październik"))
            {
                month = "10";
            }
            else if (data.contains("listopad"))
            {
                month = "11";
            }
            else if (data.contains("grudzień"))
            {
                month = "12";
            }

            Pattern p6 = Pattern.compile("  [0-9]{2} ");
            Matcher m6 = p6.matcher(data);

            m6.find();

            data = data.substring(data.length()-23, data.length()-19)+"-"+month+"-"+m6.group();
            data = data.substring(0,data.length()-5)+data.substring(data.length()-3,data.length()-1);

            if (database.isExistAnnouncement(title) == false)
            {
                setNotification(getResources().getString(R.string.main_activity_notifier_title), getResources().getString(R.string.main_activity_notifier_text) + " " + website1_category1 + ".\nAutor: "+author+".");
            }

            database.addAnnouncement (data, time, author, website2_category1, website2_category2, author, "", "", desc, title);
        }
    }
    //!parser

    //check if there is an internet connection
    public void IsInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void setNotification (String title, String text)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        Intent mIntent = new Intent(this, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        mBuilder.setContentIntent(mPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random id = new Random();
        mNotificationManager.notify(id.nextInt(), mBuilder.build());
    }

    public void checkNewAnnouncement (final Database database, int numbersOfHouers)
    {
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask()
        {
            @Override
            public void run ()
            {
                parser(database);
            }
        };
        timer.schedule (hourlyTask, 0l, 1000*60*60*numbersOfHouers);
    }

    public void DatabaseShowData (Database database)
    {
        EditText id = (EditText) findViewById(R.id.id);
        EditText date = (EditText) findViewById(R.id.date);
        EditText time = (EditText) findViewById(R.id.time);
        EditText author = (EditText) findViewById(R.id.author);
        EditText text = (EditText) findViewById(R.id.text);
        EditText title = (EditText) findViewById(R.id.title);
        ScrollView table = (ScrollView) findViewById(R.id.table);

        id.setText("");
        date.setText("");
        time.setText("");
        author.setText("");
        text.setText("");

        Cursor k = database.getAnnouncements();
        while (k.moveToNext())
        {
            if (k.getInt(12) == 1)
            {
                continue;
            }

            id.setText(k.getInt(0));
            date.setText(k.getString(1));
            time.setText(k.getString(2));
            author.setText(k.getString(3));
            String category1 = k.getString(4);
            String category2 = k.getString(5);
            String category3 = k.getString(6);
            String category4 = k.getString(7);
            String category5 = k.getString(8);
            text.setText(k.getString(9));
            title.setText(k.getString(10));
            int read = k.getInt(11);


            if (read == 1)
            {
                table.setBackgroundColor(new Color().DKGRAY);
            }
            else
            {
                table.setBackgroundColor(new Color().WHITE);
            }
        }



        //database
        /*TextView tv = (TextView) findViewById(R.id.textView);
        Cursor k = database.getAnnouncements();
        tv.setText ("");
        while (k.moveToNext()) {
            int id = k.getInt(0);
            String date = k.getString(1);
            String time = k.getString(2);
            String author = k.getString(3);
            String category1 = k.getString(4);
            String category2 = k.getString(5);
            String category3 = k.getString(6);
            String category4 = k.getString(7);
            String category5 = k.getString(8);
            String description = k.getString(9);
            String title = k.getString(10);
            int read = k.getInt(11);

            int synchronization = k.getInt(2);
            assert tv != null;
            tv.setText(tv.getText() + "\n" + date + " " + time + " " + author + " " + category1 + " " + category2 + " " + category3 + " " + category4 + " " + category5 + " " + description + " " + title);

            if (read == 1)
            {
                tv.setText(tv.getText() + " " + "true");
            }
            else
            {
                tv.setText(tv.getText() + " " + "false");
            }
        }*/
        //database
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //database
        final Database database = new Database(this);
        DatabaseShowData (database);
        //database

        //

        checkNewAnnouncement (database, 1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IsInternetConnection();
                    Snackbar.make(view, getText(R.string.main_activity_reload_news), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    //parser
                    parser(database);
                    DatabaseShowData (database);
                    //!parser
                } catch (Exception e) {
                    Snackbar.make(view, getText(R.string.main_activity_no_internet), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.about_settings) {
            Intent intent = new Intent(this, AboutSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
}
