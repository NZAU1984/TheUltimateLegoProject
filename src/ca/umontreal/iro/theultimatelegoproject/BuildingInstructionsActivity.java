package ca.umontreal.iro.theultimatelegoproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.skulg.tulp.dbHelper;

public class BuildingInstructionsActivity extends android.support.v4.app.FragmentActivity implements TaskCallbacks
{
	private static final String TAG_TASK_FRAGMENT = "specific_building_instructions_fragment";
	private SpecificBuildingInstructionsFragment specificBuildingInstructionsFragment;

	private TulpApplication		tulpApplication;
	private dbHelper			dbHelper;
	private String				buildingInstructionsId;
	private ImageLoader			imageLoader;
	private DisplayImageOptions	options;
	private LayoutInflater		inflater;
	private LinearLayout		linearLayoutPicturesWrapper;
	private RelativeLayout		relativeLayoutSpinnerWrapper;
	private ViewPager			viewPagerBig;
	private ViewPager			viewPagerThumbnails;
	private String[]			imagesURL;
	private int					imagesURLlength;
	private int					bigPosition	= 0;
	private ImageView[]			thumbnailImageViews;
	private int					dummyPages;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_building_instructions);

		tulpApplication					= (TulpApplication) getApplication();
		dbHelper						= new dbHelper(getApplicationContext());
		Intent intent					= getIntent();
		buildingInstructionsId			= intent.getStringExtra("building_instructions_id");
		inflater						= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader						= tulpApplication.getImageLoader();
		options							= tulpApplication.getImageLoaderOptions();
		linearLayoutPicturesWrapper		= (LinearLayout) findViewById(R.id.linearlayout_pictures_wrapper);
		relativeLayoutSpinnerWrapper	= (RelativeLayout) findViewById(R.id.relativelayout_spinner_wrapper);
		viewPagerBig					= (ViewPager) findViewById(R.id.viewpager_big);
		viewPagerThumbnails				= (ViewPager) findViewById(R.id.viewpager_thumbnail);

		dummyPages	= (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 1;

		{
			if(dbHelper.buildingInstructionsExist(buildingInstructionsId))
			{
				initViewPagers();
			}
			else
			{
				initLoading();

				FragmentManager fragmentManager			= getSupportFragmentManager();
				specificBuildingInstructionsFragment	= (SpecificBuildingInstructionsFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

			    if(null == specificBuildingInstructionsFragment)
			    {
			    	specificBuildingInstructionsFragment	= new SpecificBuildingInstructionsFragment();

			    	Bundle arguments	= new Bundle();

			    	arguments.putString("building_instructions_id", buildingInstructionsId);

			    	specificBuildingInstructionsFragment.setArguments(arguments);

			    	fragmentManager.beginTransaction().add(specificBuildingInstructionsFragment, TAG_TASK_FRAGMENT).commit();
			    }
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_instruction, menu);
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

	private void initViewPagers()
	{
		imagesURL		= dbHelper.getBuildingInstructionsImages(buildingInstructionsId);
		imagesURLlength	= imagesURL.length;

		linearLayoutPicturesWrapper.setVisibility(View.VISIBLE);
		relativeLayoutSpinnerWrapper.setVisibility(View.GONE);

		viewPagerBig.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				viewPagerThumbnails.setCurrentItem(arg0);
				updateThumbnailViewPager(arg0);

				bigPosition	= arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub

			}
		});

		viewPagerBig.setAdapter(new BigBuildingGalleryAdapter(this));
		viewPagerThumbnails.setAdapter(new ThumbnailBuildingGalleryAdapter(this));
	}

	private void initLoading()
	{
		linearLayoutPicturesWrapper.setVisibility(View.GONE);
		relativeLayoutSpinnerWrapper.setVisibility(View.VISIBLE);
	}

	protected void addBorderToThumbnail(int position)
	{
		if(null == thumbnailImageViews[position])
		{
			return;
		}

		thumbnailImageViews[position].setBackgroundResource(R.drawable.button_defaut);
	}

	protected void removeBorderFromThumbnail(int position)
	{
		if(null == thumbnailImageViews[position])
		{
			return;
		}

		thumbnailImageViews[position].setBackgroundResource(R.drawable.border_unselected_thumbnail);
	}

	protected void updateThumbnailViewPager(int position)
	{
		removeBorderFromThumbnail(bigPosition + dummyPages);
		addBorderToThumbnail(position + dummyPages);
	}

	class BigBuildingGalleryAdapter extends PagerAdapter
	{
		BigBuildingGalleryAdapter(Context context)
		{
		}

		@Override
		public int getCount()
		{
			return imagesURLlength;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == ((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			final View page	= inflater.inflate(R.layout.layout_building_gallery_big, null);

			final ImageView imgDisp	= (ImageView) page.findViewById(R.id.imageview_image);

			final ProgressBar spinner	= (ProgressBar) page.findViewById(R.id.progressbar_spinner);

			imageLoader.displayImage(imagesURL[position], imgDisp, options, new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
				{
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) container).addView(page, 0);

			return page;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			((ViewPager) container).removeView((View) object);
		}
	}

	class ThumbnailBuildingGalleryAdapter extends PagerAdapter
	{
		ThumbnailBuildingGalleryAdapter(Context context)
		{
			thumbnailImageViews	= new ImageView[imagesURLlength + dummyPages];
		}

		@Override
		public int getCount()
		{
			return imagesURLlength + dummyPages;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == ((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			final View page					= inflater.inflate(R.layout.layout_building_gallery_thumbnail, null);
			final ImageView imgDisp			= (ImageView) page.findViewById(R.id.imageview_image);
			thumbnailImageViews[position]	= imgDisp;
			final ProgressBar spinner		= (ProgressBar) page.findViewById(R.id.progressbar_spinner);
			final int finalPosition			= position;

			updateThumbnailViewPager(bigPosition);

			imgDisp.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					viewPagerBig.setCurrentItem(finalPosition - dummyPages);
				}
			});

			if(dummyPages > position)
			{
				imgDisp.setImageResource(R.drawable.image_empty);

				return page;
			}

			imageLoader.displayImage(imagesURL[position - dummyPages], imgDisp, options, new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
				{
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) container).addView(page, 0);

			return page;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			thumbnailImageViews[position]	= null;

			((ViewPager) container).removeView((View) object);
		}

		@Override public float getPageWidth(int position)
		{
			// If orientation is portrait => each page has 1/3
			// If orientation is landscape => each page has 1/7
			return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ?  (float) 1/7 : (float) 1/3;
		}
	}

	@Override
	public void onPreExecute(String fromWho)
	{

	}

	@Override
	public void onProgressUpdate(double fraction)
	{

	}

	@Override
	public void onCancelled()
	{

	}

	@Override
	public void onPostExecute(String fromWho, Boolean success)
	{
		initViewPagers();
	}
}
