package ca.umontreal.iro.theultimatelegoproject;

import java.util.ArrayList;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.skulg.tulp.dbHelper;

public class SearchResultActivity extends Activity
{
	private TulpApplication tulpApplication;
	private Boolean isFav = false;
	protected ImageLoader imageLoader;
	private String[] imagesURL;
	private int nbResults;
	private dbHelper dbHelper;
	private ArrayList<SearchResultSet> searchResultsArrayList;

	DisplayImageOptions options;

	GridView listView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		tulpApplication = (TulpApplication) getApplication();
		imageLoader = tulpApplication.getImageLoader();

		options = tulpApplication.getImageLoaderOptions();
		listView = (GridView) findViewById(R.id.gridview_lego_sets);

		listView.setAdapter(new ImageAdapter());
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				launchSetInfoActivity(searchResultsArrayList.get(position).id);
			}
		});

		dbHelper = new dbHelper(getApplicationContext());

		Intent intent = getIntent();

		isFav = intent.getBooleanExtra("favorites", false);

		if (isFav)
		{
			searchResultsArrayList	= dbHelper.getFavoritesLegoSetsArrayList();
		}
		else
		{
			searchResultsArrayList	= dbHelper.getSearchLegoSetsArrayList(
				intent.getStringExtra("keyword"),
				intent.getStringExtra("price_from"),
				intent.getStringExtra("price_to"),
				intent.getStringExtra("year_from"),
				intent.getStringExtra("year_to"),
				intent.getStringExtra("pieces_from"),
				intent.getStringExtra("pieces_to")
			);
		}

		if ((null == searchResultsArrayList) || (0 == (nbResults = searchResultsArrayList.size())))
		{
			if (isFav)
			{
				Tools.longToast(getApplicationContext(), getResources().getString(R.string.search_results_no_favorites));
			} else
			{
				Tools.longToast(getApplicationContext(), getResources().getString(R.string.search_results_no_results));
			}

			return;
		}
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

	private void launchSetInfoActivity(String setId)
	{
		Intent launchSetInfoActivity = new Intent(this, SetInfoActivity.class);

		launchSetInfoActivity.putExtra("set_id", setId);

		startActivity(launchSetInfoActivity);
	}

	public class ImageAdapter extends BaseAdapter
	{
		private RelativeLayout.LayoutParams rl;

		@Override
		public int getCount()
		{
			return nbResults;
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

			View view	= convertView;

			SearchResultSet currentSet	= searchResultsArrayList.get(position);

			if (view == null)
			{
				view = getLayoutInflater().inflate(R.layout.layout_search_result_lego_set, parent, false);

				view.setLayoutParams(new AbsListView.LayoutParams(listView.getColumnWidth(), listView.getColumnWidth()));

				holder	= new ViewHolder();

				assert view != null;

				holder.imageView	= (ImageView) view.findViewById(R.id.imageview_image);
				holder.progressBar	= (ProgressBar) view.findViewById(R.id.progressbar_spinner);
				holder.favoriteImageView	= (ImageView) view.findViewById(R.id.imageview_favorite);
				holder.wrapper				= (RelativeLayout) view.findViewById(R.id.relativelayout_wrapper);

				view.setTag(holder);

				if (null == rl)
				{
					rl	= new RelativeLayout.LayoutParams(listView.getColumnWidth() / 5, listView.getColumnWidth() / 5);

					rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				}

				holder.favoriteImageView.setLayoutParams(rl);
			}
			else
			{
				holder = (ViewHolder) view.getTag();
			}

			if(!currentSet.favorite)
			{
				holder.favoriteImageView.setImageResource(R.drawable.image_empty);
			}
			else
			{
				holder.favoriteImageView.setImageResource(R.drawable.image_star);
			}

			if(currentSet.seen)
			{
				holder.wrapper.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_seen));
			}
			else
			{
				holder.wrapper.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_defaut));
			}

			imageLoader.displayImage(currentSet.imageUrl, holder.imageView, options, new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					holder.progressBar.setProgress(0);
					holder.progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
					holder.progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
				{
					holder.progressBar.setVisibility(View.GONE);
				}
			}, new ImageLoadingProgressListener()
			{
				@Override
				public void onProgressUpdate(String imageUri, View view, int current, int total)
				{
					holder.progressBar.setProgress(Math.round((100.0f * current) / total));
				}
			});

			return view;
		}

		class ViewHolder
		{
			ImageView imageView;
			ProgressBar progressBar;
			ImageView favoriteImageView;
			RelativeLayout wrapper;
		}
	}
}
