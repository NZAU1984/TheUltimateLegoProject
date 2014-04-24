package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class SearchResultActivity extends Activity
{
	private TulpApplication	tulpApplication;
	private Button			showSet456;
	private Button			showSet123;
	private String			sqlRequest;
	private Boolean			isFav = false;
	protected ImageLoader	imageLoader;
	private String[]		imagesURL;
	private int				imagesURLlength;

	DisplayImageOptions options;

	GridView listView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		tulpApplication	= (TulpApplication) getApplication();
		imageLoader		= tulpApplication.getImageLoader();
		imagesURL		= new String[1000];

		for (int i = 0; i < 1000; ++i)
		{
			String j = String.valueOf(i);

			if (i < 100)
			{
				j = "0" + j;
			}
			if (i < 10)
			{
				j = "0" + j;
			}

			imagesURL[i] = "http://www.cubiculus.com/images/54" + j;
		}

		imagesURLlength	= imagesURL.length;
		options			= tulpApplication.getImageLoaderOptions();
		listView		= (GridView) findViewById(R.id.gridview_lego_sets);

		listView.setAdapter(new ImageAdapter());
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				launchSetInfoActivity("123");
				// startImagePagerActivity(position);
			}
		});

		Intent intent = getIntent();

		sqlRequest = intent.getStringExtra("sql_request");

		// TEMP, the SQL request sent will tell this activity what to search, no
		// need to tell it it's showing favorites !
		isFav = intent.getBooleanExtra("favorites", false);

		initiateButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		imageLoader.stop();
		super.onBackPressed();
	}

	@Override
	public void onPause()
	{
		imageLoader.pause();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		imageLoader.resume();
		super.onResume();
	}

	@Override
	public void onDestroy()
	{
		imageLoader.stop();
		super.onDestroy();
	}

	private void initiateButtons()
	{
		showSet123 = (Button) findViewById(R.id.button_showset123);
		showSet456 = (Button) findViewById(R.id.button_showset456);

		showSet123.setVisibility(View.GONE);
		showSet456.setVisibility(View.GONE);
/*
		if (isFav)
		{
			showSet456.setVisibility(View.GONE);
		} else
		{
			showSet123.setVisibility(View.GONE);
		}

		showSet123.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSetInfoActivity("123");
			}
		});

		showSet456.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSetInfoActivity("456");
			}
		});*/
	}

	private void launchSetInfoActivity(String setId)
	{
		Intent launchSetInfoActivity = new Intent(this, SetInfoActivity.class);

		launchSetInfoActivity.putExtra("set_id", setId);

		startActivity(launchSetInfoActivity);
	}

	private void startImagePagerActivity(int position)
	{
		/*
		 * Intent intent = new Intent(this, ImagePagerActivity.class);
		 * intent.putExtra(Extra.IMAGES, imageUrls);
		 * intent.putExtra(Extra.IMAGE_POSITION, position);
		 * startActivity(intent);
		 */
	}

	public class ImageAdapter extends BaseAdapter
	{
		private RelativeLayout.LayoutParams rl;

		@Override
		public int getCount()
		{
			return imagesURLlength;
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ViewHolder holder;
			View view = convertView;
			if (view == null)
			{
				view = getLayoutInflater().inflate(R.layout.layout_search_result_lego_set, parent, false);

				view.setLayoutParams(new AbsListView.LayoutParams(listView.getColumnWidth(), listView.getColumnWidth()));

				ImageView imageViewFavorite = (ImageView) view.findViewById(R.id.imageview_favorite);

				if (null == rl)
				{
					rl = new RelativeLayout.LayoutParams(
							listView.getColumnWidth() / 5,
							listView.getColumnWidth() / 5);
					rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				}

				imageViewFavorite.setLayoutParams(rl);

				// TEMP, just to fake that some sets are favorites, others are not
				if(0 == Math.round(Math.random()))
				{
					imageViewFavorite.setImageResource(R.drawable.image_empty);
				}

				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view
						.findViewById(R.id.imageview_image);
				holder.progressBar = (ProgressBar) view
						.findViewById(R.id.progressbar_spinner);
				view.setTag(holder);
			} else
			{
				holder = (ViewHolder) view.getTag();
			}

			imageLoader.displayImage(imagesURL[position], holder.imageView,
					options, new SimpleImageLoadingListener()
					{
						@Override
						public void onLoadingStarted(String imageUri, View view)
						{
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason)
						{
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage)
						{
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener()
					{
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total)
						{
							holder.progressBar.setProgress(Math
									.round((100.0f * current) / total));
						}
					});

			return view;
		}

		class ViewHolder
		{
			ImageView imageView;
			ProgressBar progressBar;
		}
	}
	/*
	 * private Button showSet456; private Button showSet123; private String
	 * sqlRequest; private GridView gridViewLegoSets; private
	 * DisplayImageOptions options; private LayoutInflater inflater;
	 *
	 * // TEMP !! private Boolean isFav = false;
	 *
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_search_result);
	 *
	 * inflater = (LayoutInflater)
	 * getSystemService(Context.LAYOUT_INFLATER_SERVICE); options = new
	 * DisplayImageOptions.Builder() .cacheOnDisc(true)
	 * .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
	 * .showImageOnFail(R.drawable.image_error) .build();
	 *
	 * Intent intent = getIntent();
	 *
	 * sqlRequest = intent.getStringExtra("sql_request");
	 *
	 * // TEMP, the SQL request sent will tell this activity what to search, no
	 * need to tell it it's showing favorites ! isFav =
	 * intent.getBooleanExtra("favorites", false);
	 *
	 * initiateButtons();
	 *
	 * String[] zzzmThumbIds = new String[1000];
	 *
	 * for(int i = 0; i < 1000; ++i) { String j = String.valueOf(i);
	 *
	 * if(i < 100) { j = "0" + j; } if(i < 10) { j = "0" + j; }
	 *
	 * zzzmThumbIds[i] = "http://www.cubiculus.com/images/54" + j; }
	 *
	 * gridViewLegoSets = (GridView) findViewById(R.id.gridview_lego_sets);
	 * gridViewLegoSets.setAdapter(new GridViewAdapter(this, zzzmThumbIds)); }
	 *
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.search_result, menu); return true; }
	 *
	 * private void initiateButtons() { showSet123 = (Button)
	 * findViewById(R.id.button_showset123); showSet456 = (Button)
	 * findViewById(R.id.button_showset456);
	 *
	 * if(isFav) { showSet456.setVisibility(View.GONE); } else {
	 * showSet123.setVisibility(View.GONE); }
	 *
	 * showSet123.setOnClickListener(new View.OnClickListener() {
	 *
	 * @Override public void onClick(View v) { launchSetInfoActivity("123"); }
	 * });
	 *
	 * showSet456.setOnClickListener(new View.OnClickListener() {
	 *
	 * @Override public void onClick(View v) { launchSetInfoActivity("456"); }
	 * }); }
	 *
	 * private void launchSetInfoActivity(String setId) { Intent
	 * launchSetInfoActivity = new Intent(this, SetInfoActivity.class);
	 *
	 * launchSetInfoActivity.putExtra("set_id", setId);
	 *
	 * startActivity(launchSetInfoActivity); }
	 *
	 * public class GridViewAdapter extends BaseAdapter { private Context
	 * mContext; private ImageLoader imageLoader;
	 *
	 * // references to our images private String[] mThumbIds;
	 *
	 * public GridViewAdapter(Context c, String[] zzz) { mThumbIds=zzz;
	 * imageLoader = ImageLoader.getInstance();
	 * imageLoader.init(ImageLoaderConfiguration.createDefault(c)); mContext =
	 * c; }
	 *
	 * @Override public int getCount() {
	 * //Tools.shortToast(getApplicationContext(), "getCount " +
	 * mThumbIds.length); return mThumbIds.length; }
	 *
	 * @Override public Object getItem(int position) { return null; }
	 *
	 * @Override public long getItemId(int position) { return position; }
	 *
	 * // create a new ImageView for each item referenced by the Adapter
	 *
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { Tools.shortToast(getApplicationContext(), "Position is " +
	 * position + ", image URL is " + mThumbIds[position]); View page;
	 *
	 * if (convertView == null) { page =
	 * inflater.inflate(R.layout.layout_search_result_lego_set, null);
	 *
	 * // We must defined a COMMON height because GridView has a bug if cells
	 * have different heights ! page.setLayoutParams(new
	 * AbsListView.LayoutParams
	 * (gridViewLegoSets.getColumnWidth(),gridViewLegoSets.getColumnWidth()));
	 *
	 * final ImageView imgDisp = (ImageView)
	 * page.findViewById(R.id.imageview_image);
	 *
	 * final ProgressBar spinner = (ProgressBar)
	 * page.findViewById(R.id.progressbar_spinner);
	 *
	 * imageLoader.displayImage(mThumbIds[position], imgDisp, options, new
	 * SimpleImageLoadingListener() {
	 *
	 * @Override public void onLoadingStarted(String imageUri, View view) {
	 * spinner.setVisibility(View.VISIBLE); }
	 *
	 * @Override public void onLoadingFailed(String imageUri, View view,
	 * FailReason failReason) { spinner.setVisibility(View.GONE); }
	 *
	 * @Override public void onLoadingComplete(String imageUri, View view,
	 * Bitmap loadedImage) { spinner.setVisibility(View.GONE); } }); } else {
	 * //Tools.shortToast(getApplicationContext(), "page = convertView"); page =
	 * convertView; }
	 *
	 * // imageView.setImageResource(mThumbIds[position]);
	 *
	 * return page; } }
	 */

}
