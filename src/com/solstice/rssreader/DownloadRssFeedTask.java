package com.solstice.rssreader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Xml;

/*
 * Code largely adapted from Google's Android documentation about XML parsing. 
 * See: http://developer.android.com/training/basics/network-ops/xml.html
 */
public class DownloadRssFeedTask extends AsyncTask<URL, Integer, List<Entry>> {

    ProgressDialog dialog;
    AsyncTaskCaller listener;

    public DownloadRssFeedTask(AsyncTaskCaller listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        // show a loading dialog box before starting to download rss
        dialog = new ProgressDialog((MainActivity) listener);
        dialog.setMessage("Loading RSS ...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(List<Entry> entries) {
        dialog.dismiss();
        listener.onTaskCompleted(entries);
    }

    @Override
    protected List<Entry> doInBackground(URL... urls) {

        String xml = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());

            // xml = getXmlString(in);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);

            // Log.d("dl", xml);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return null;
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, null, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Entry readEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        String title = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
                if (link == "")
                    continue;
            } else {
                skip(parser);
            }
        }
        return new Entry(title, link);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, null, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, "link");
        return link;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
    }
}