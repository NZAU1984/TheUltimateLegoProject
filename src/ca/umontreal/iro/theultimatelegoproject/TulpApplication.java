package ca.umontreal.iro.theultimatelegoproject;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TulpApplication extends Application
{
	private ImageLoader			imageLoader;
	private DisplayImageOptions	imageLoaderOptions;
	@Override
	public void onCreate()
	{
		super.onCreate();
		Tools.shortToast(getApplicationContext(), "TulpApplication.onCreate()");
	}

	private void initImageLoader()
	{
		imageLoader	= ImageLoader.getInstance();

		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
	}

	private void initImageLoaderOptions()
	{
		imageLoaderOptions	= new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.showImageOnFail(R.drawable.image_error)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	}

	public ImageLoader getImageLoader()
	{
		if(null == imageLoader)
		{
			initImageLoader();
		}

		return imageLoader;
	}

	public DisplayImageOptions getImageLoaderOptions()
	{
		if(null == imageLoaderOptions)
		{
			initImageLoaderOptions();
		}

		return imageLoaderOptions;
	}

	public String[] getBuildingInstructionImages(String setId)
	{
		return new String[]
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
	}
}
