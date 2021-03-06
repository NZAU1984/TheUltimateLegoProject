package ca.umontreal.iro.theultimatelegoproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class TulpApplication extends Application
{
	private ImageLoader			imageLoader;
	private DisplayImageOptions	imageLoaderOptions;
	private SharedPreferences	sharedPreferences;
	@Override
	public void onCreate()
	{
		super.onCreate();

		sharedPreferences	= getSharedPreferences(getString(R.string.preferences_filename), Context.MODE_PRIVATE);
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
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
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
				//"http://www.cubiculus.com/imaxxxges/54524?",
				//"http://upload.wikimedia.org/wikipedia/commons/8/8d/Perth_skyline_at_night.jpg"
			};
	}

	public SetInfo getSetInfo(String setId)
	{
		Boolean error	= false;

		if(error)
		{
			return null;
		}

		// SetInfo(Context argContext, String argSetId, String argImageURL, String argDescription, int argYear, float argPrice, int argNbPieces)
		return null;
	}

	public Boolean isDbCreated()
	{
		if(!sharedPreferences.contains(getString(R.string.preferences_last_db_import_date)))
		{
			return false;
		}

		return true;
	}

/*	public boolean isOnline() {
	    ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

	    if((netInfo == null) || !netInfo.isConnected() || !netInfo.isAvailable()){

	        return false;
	    }

	    Toast.makeText(getApplicationContext(), "YES Internet connection!", Toast.LENGTH_LONG).show();
	return true;
	}
	*/
}
