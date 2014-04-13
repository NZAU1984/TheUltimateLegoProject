package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/*
 * TODO :
 * -> Simplify BigBuildingGalleryAdapter / ThumbnailBuildingGalleryAdapter ? Abstract class to be extended ?
 * -> ONE imageLoader object, not two !
 */
public class BuildingInstructionActivity extends Activity
{
	private String				setId;
	private DisplayImageOptions	options;
	private LayoutInflater		inflater;
	private ViewPager			viewPagerBig;
	private ViewPager			viewPagerThumbnails;
	private int					bigPosition	= 0;
	private int					thumbnailPosition	= 0;
	private ImageView[]			thumbnailImageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building_instruction);

		Intent intent	= getIntent();
		setId			= intent.getStringExtra("set_id");
		inflater		= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		options			= new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageOnFail(R.drawable.image_error)
			.build();
		viewPagerBig	= (ViewPager) findViewById(R.id.viewpager_big);
		viewPagerThumbnails	= (ViewPager) findViewById(R.id.viewpager_thumbnail);

		viewPagerBig.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				Tools.shortToast(getApplicationContext(), "Page changed : " + arg0);
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


		String[] temp = new String[]
				{
				"http://www.cubiculus.com/images/54516?",
				"http://www.cubiculus.com/images/54517?",
				"http://www.cubiculus.com/images/54518?",
				"http://www.cubiculus.com/images/54519?",
				"http://www.cubiculus.com/images/54520?",
				"http://www.cubiculus.com/images/54521?",
				"http://www.cubiculus.com/images/54522?",
				"http://www.cubiculus.com/images/54523?",
				"http://www.cubiculus.com/imaxxxges/54524?",
				"http://upload.wikimedia.org/wikipedia/commons/8/8d/Perth_skyline_at_night.jpg"
			};

		viewPagerBig.setAdapter(new BigBuildingGalleryAdapter(this, temp));
		viewPagerThumbnails.setAdapter(new ThumbnailBuildingGalleryAdapter(this, temp));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_instruction, menu);
		return true;
	}

	protected void addBorderToThumbnail(int position)
	{
		if(null == thumbnailImageViews[position])
		{
			return;
		}

		thumbnailImageViews[position].setBackgroundResource(R.drawable.background_building_instruction_gallery_thumbnail);
	}

	protected void removeBorderToThumbnail(int position)
	{
		if(null == thumbnailImageViews[position])
		{
			return;
		}

		thumbnailImageViews[position].setBackgroundColor(Color.TRANSPARENT);
	}

	protected void updateThumbnailViewPager(int position)
	{
		removeBorderToThumbnail(bigPosition + 1);
		addBorderToThumbnail(position + 1);
	}

	class BigBuildingGalleryAdapter extends PagerAdapter
	{
		private ImageLoader	imageLoader;
		private String[] urlImages;

		BigBuildingGalleryAdapter(Context context, String[] argUrlImages)
		{
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
			urlImages	= argUrlImages;
		}
		@Override
		public int getCount()
		{
			return urlImages.length;
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

			imageLoader.displayImage(urlImages[position], imgDisp, options, new SimpleImageLoadingListener()
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

		@Override public float getPageWidth(int position)
		{
			if(1 == urlImages.length)
			{
				return 1f;
			}

			if(2 == urlImages.length)
			{
				return 0.5f;
			}

			return 1f;
		}
	}

	class ThumbnailBuildingGalleryAdapter extends PagerAdapter
	{
		private ImageLoader	imageLoader;
		private String[] urlImages;

		ThumbnailBuildingGalleryAdapter(Context context, String[] argUrlImages)
		{
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
			urlImages	= argUrlImages;
			thumbnailImageViews	= new ImageView[argUrlImages.length + 1];
		}
		@Override
		public int getCount()
		{
			return urlImages.length + 1;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == ((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			//Tools.shortToast(getApplicationContext(), "instant.. " + position);
			final View page	= inflater.inflate(R.layout.layout_building_gallery_thumbnail, null);

			final ImageView imgDisp	= (ImageView) page.findViewById(R.id.imageview_image);

			thumbnailImageViews[position]	= imgDisp;

			final ProgressBar spinner	= (ProgressBar) page.findViewById(R.id.progressbar_spinner);

			updateThumbnailViewPager(bigPosition);

			final int finalPosition	= position;

			imgDisp.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Tools.shortToast(getApplicationContext(), "image click" + finalPosition);

					viewPagerBig.setCurrentItem(finalPosition - 1);
				}
			});

			if(0 == position)
			{
				imgDisp.setImageResource(R.drawable.image_empty);

				return page;
			}

			imageLoader.displayImage(urlImages[position - 1], imgDisp, options, new SimpleImageLoadingListener()
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
			//Tools.shortToast(getApplicationContext(), "destroy " + position);
			thumbnailImageViews[position]	= null;

			((ViewPager) container).removeView((View) object);
		}

		@Override public float getPageWidth(int position)
		{
			if(1 == urlImages.length)
			{
				return 1f;
			}

			if(2 == urlImages.length)
			{
				return 0.5f;
			}

			return 0.333333333333f;
		}

	}

}
