package com.example.firstandroid;

import java.io.InputStream;
import java.net.URL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ImdbItemActivity extends Activity {
	
	private TextView titleText = null;
	private TextView snippetText = null;
	private ImageView thumbImage = null;
	private String link = null;
	private String thumbnail;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imdb_item);
		this.titleText = (TextView) findViewById(R.id.titleText);
		this.snippetText = (TextView) findViewById(R.id.snippetText);
		this.thumbImage = (ImageView) findViewById(R.id.imageView);
		Intent i = getIntent();
		this.titleText.setText(i.getStringExtra("title"));
		this.snippetText.setText(i.getStringExtra("snippet"));
		this.link = i.getStringExtra("link");
		this.thumbnail = i.getStringExtra("thumbnail");
		new LoadImageTask(this).execute(this.thumbnail);
	}

	public void onOpen(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.link));
		startActivity(browserIntent);
	}
	
	// The definition of our task class
	private class LoadImageTask extends AsyncTask<String, Integer, String> {
		protected ImdbItemActivity activity = null;
		private Drawable image;
		LoadImageTask(ImdbItemActivity activity)
		{
			this.activity = activity;
		}

		public Drawable LoadImageFromWebOperations(String url) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				return d;
			} catch (Exception e) {
				return null;
			}
		}

    	@Override
        protected String doInBackground(String... params) {
            String url=params[0];
           this.image = this.LoadImageFromWebOperations(url);
           publishProgress();
           return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
           super.onProgressUpdate(values);
           this.activity.thumbImage.setImageDrawable(this.image);
        }
    }
}
