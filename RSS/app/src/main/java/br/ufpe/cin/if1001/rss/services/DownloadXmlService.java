package br.ufpe.cin.if1001.rss.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import br.ufpe.cin.if1001.rss.util.ParserRSS;

public class DownloadXmlService extends IntentService {

  SQLiteRSSHelper db;
  public static final String DOWNLOAD_COMPLETE = "br.ufpe.cin.if1001.rss.action.DOWNLOAD_COMPLETE";
  public static final String NEW_REPORT_AVAILABLE = "br.ufpe.cin.if1001.rss.NEW_REPORTS";

  public DownloadXmlService() {
    super("DownloadXmlService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    db = SQLiteRSSHelper.getInstance(getApplicationContext());
    boolean flag_problema = false;
    List<ItemRSS> items = null;
    try {
      String feed = getRssFeed(intent.getStringExtra("url"));
      items = ParserRSS.parse(feed);
      for (ItemRSS i : items) {
        Log.d("DB", "Searching for link in the database:" + i.getLink());
        ItemRSS item = db.getItemRSS(i.getLink());
        if (item == null) {
          sendBroadcast(new Intent(NEW_REPORT_AVAILABLE));
          Log.d("DB", "Found for the first time: " + i.getTitle());
          db.insertItem(i);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
      flag_problema = true;
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      flag_problema = true;
    }

    if (flag_problema) {
      Log.d("FEED", "Error loading feed.");
    } else {
      Log.d("FEED", "Feed successfully loaded.");
      LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(DOWNLOAD_COMPLETE));
    }
  }

  private String getRssFeed(String feed) throws IOException {
    InputStream in = null;
    String rssFeed = "";
    try {
      URL url = new URL(feed);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      in = conn.getInputStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      for (int count; (count = in.read(buffer)) != -1;) {
        out.write(buffer, 0, count);
      }
      byte[] response = out.toByteArray();
      rssFeed = new String(response, "UTF-8");
    } finally {
      if (in != null) {
        in.close();
      }
    }
    return rssFeed;
  }
}