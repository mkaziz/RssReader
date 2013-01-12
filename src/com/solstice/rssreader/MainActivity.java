package com.solstice.rssreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity implements AsyncTaskCaller {

    public static String XML_URL = "http://blog.solstice-mobile.com/feeds/posts/default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadRss();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void downloadRss() {

        // check internet connection before downloading, otherwise display error
        if (isNetworkConnected())
            try {
                new DownloadRssFeedTask(this).execute(new URL(XML_URL));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        else {
            new AlertDialog.Builder(this).setTitle("Argh")
                    .setMessage("We couldn't detect an internet connection!")
                    .setNeutralButton("Close", null).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            // refresh the feed
            downloadRss();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
     * android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String link = ((TextView) v.findViewById(R.id.link)).getText()
                .toString();

        // Opens browser when list item is clicked
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        this.startActivity(browserIntent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.solstice.rssreader.AsyncTaskCaller#onTaskCompleted(java.util.List)
     * Callback function used to provide the DownoadRssFeedTask class a way to
     * interact with the GUI.
     */
    @Override
    public void onTaskCompleted(List<Entry> entries) {

        ListView listView = (ListView) findViewById(android.R.id.list);

        // fill the listview with the retrieved entries
        RssArrayAdapter adapter = new RssArrayAdapter(this, entries);
        listView.setAdapter(adapter);
    }

}
