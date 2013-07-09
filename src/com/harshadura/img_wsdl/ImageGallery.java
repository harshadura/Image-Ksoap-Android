package com.harshadura.img_wsdl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class ImageGallery extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);

		Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this, ReadSDCard()));

		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
			}
		});
	}

	private List<String> ReadSDCard() {
		List<String> tFileList = new ArrayList<String>();
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString() + "/Cypad/";
		File f = new File(extStorageDirectory);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			tFileList.add(file.getPath());
		}

		return tFileList;
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> FileList;

		public ImageAdapter(Context c, List<String> fList) {
			mContext = c;
			FileList = fList;
			TypedArray a = obtainStyledAttributes(R.styleable.Theme);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Theme_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return FileList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			Bitmap bm = BitmapFactory.decodeFile(FileList.get(position)
					.toString());
			i.setImageBitmap(bm);

			i.setLayoutParams(new Gallery.LayoutParams(150, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}
	}
}