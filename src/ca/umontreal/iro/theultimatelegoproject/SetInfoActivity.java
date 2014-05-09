package ca.umontreal.iro.theultimatelegoproject;
// TEST
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.skulg.tulp.dbHelper;

public class SetInfoActivity extends Activity
{
	private TulpApplication		tulpApplication;
	private SetInfo				setInfo;
	private RelativeLayout		relativeLayoutImageWrapper;
	private ImageView			imageviewImage;
	private ImageView			imageviewBuildingInstructions;
	private ImageView			imageviewFavorite;
	private ProgressBar			progressbarSpinner;
	private TextView			textviewDescription;
	private TextView			textviewYear;
	private TextView			textviewPrice;
	private TextView			textviewNbPieces;
	private dbHelper			dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_info);

		tulpApplication		= (TulpApplication) getApplication();

		Intent intent	= getIntent();
		dbHelper		= new dbHelper(getApplicationContext());
		setInfo			= dbHelper.getLegoSet(intent.getStringExtra("set_id"), true);

		if(null == setInfo)
		{
			Tools.longToast(getApplicationContext(), getResources().getString(R.string.setinfo_no_such_set));

			return;
		}

		initElements();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_info, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		tulpApplication.getImageLoader().stop();
		super.onBackPressed();
	}

	@Override
	public void onPause()
	{
		tulpApplication.getImageLoader().pause();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		tulpApplication.getImageLoader().resume();
		super.onResume();
	}

	@Override
	public void onDestroy()
	{
		tulpApplication.getImageLoader().stop();
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		resizeBuildingInstructionsAndFavorite();
	}

	private void initElements()
	{
		relativeLayoutImageWrapper		= (RelativeLayout) findViewById(R.id.relativelayout_imagewrapper);
		imageviewImage					= (ImageView) findViewById(R.id.imageview_image);
		imageviewBuildingInstructions	= (ImageView) findViewById(R.id.imageview_building_instructions);
		imageviewFavorite				= (ImageView) findViewById(R.id.imageview_favorite);
		progressbarSpinner				= (ProgressBar) findViewById(R.id.progressbar_spinner);
		textviewDescription				= (TextView) findViewById(R.id.textview_description);
		textviewYear					= (TextView) findViewById(R.id.textview_year);
		textviewPrice					= (TextView) findViewById(R.id.textview_price);
		textviewNbPieces				= (TextView) findViewById(R.id.textview_nbpieces);

		textviewDescription.setText(setInfo.description);
		textviewYear.setText(setInfo.yearAsString);
		textviewPrice.setText(setInfo.priceAsString);
		textviewNbPieces.setText(setInfo.nbPiecesAsString);

		tulpApplication.getImageLoader().displayImage(setInfo.imageURL, imageviewImage, tulpApplication.getImageLoaderOptions(), new SimpleImageLoadingListener()
		{
			@Override
			public void onLoadingStarted(String imageUri, View view)
			{
				progressbarSpinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason)
			{
				progressbarSpinner.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
			{
				progressbarSpinner.setVisibility(View.GONE);
			}
		});

		imageviewBuildingInstructions.setOnClickListener(new View.OnClickListener()
		{

	        @Override
			public void onClick(View v)
	        {
	        	launchBuildingInstructionActivity();
	        }
		});

		updateFavoriteState();

		imageviewFavorite.setOnClickListener(new View.OnClickListener()
		{

	        @Override
			public void onClick(View v)
	        {
	        	setUnsetFavorite();
	        }
		});
	}

	private void resizeBuildingInstructionsAndFavorite()
	{
		int width	= ((RelativeLayout) imageviewBuildingInstructions.getParent()).getWidth() / ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 8 : 5);

		RelativeLayout.LayoutParams	buildingInstructionsLayout	= new RelativeLayout.LayoutParams(width, width);
		RelativeLayout.LayoutParams	FavoriteLayout				= new RelativeLayout.LayoutParams(width, width);

		buildingInstructionsLayout.addRule((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? RelativeLayout.CENTER_VERTICAL : RelativeLayout.ALIGN_PARENT_BOTTOM);//RelativeLayout.ALIGN_PARENT_BOTTOM);
		buildingInstructionsLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		FavoriteLayout.addRule((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? RelativeLayout.CENTER_VERTICAL : RelativeLayout.ALIGN_PARENT_TOP);
		FavoriteLayout.addRule((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);

		imageviewBuildingInstructions.setLayoutParams(buildingInstructionsLayout);
		imageviewFavorite.setLayoutParams(FavoriteLayout);
	}

	private void updateFavoriteState()
	{
		imageviewFavorite.setAlpha(setInfo.favorite ? 1.0f : 0.5f);
	}

	private void setUnsetFavorite()
	{
		setInfo.favorite	= !setInfo.favorite;

		dbHelper.setLegoSetFavorite(setInfo.id, setInfo.favorite);

		updateFavoriteState();
	}

	private void launchBuildingInstructionActivity()
	{
		Intent launchBuildingInstructionActivity = new Intent(this, BuildingInstructionsActivity.class);

		launchBuildingInstructionActivity.putExtra("building_instructions_id", setInfo.buildingInstructionsId);

		startActivity(launchBuildingInstructionActivity);
	}

}
