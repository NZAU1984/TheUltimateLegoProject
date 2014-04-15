package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
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

public class SetInfoActivity extends Activity
{
	private TulpApplication				tulpApplication;
	private SetInfo						setInfo;
	private ImageView					imageviewImage;
	private ImageView					imageviewBuildingInstructions;
	private ProgressBar					progressbarSpinner;
	private TextView					textviewDescription;
	private TextView					textviewYear;
	private TextView					textviewPrice;
	private TextView					textviewNbPieces;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_info);

		tulpApplication		= (TulpApplication) getApplication();

		Intent intent	= getIntent();

		setInfo	= tulpApplication.getSetInfo(intent.getStringExtra("set_id"));

		if(null == setInfo)
		{
			Tools.shortToast(getApplicationContext(), "setInfo is null ! Error ?");

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
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		resizeBuildingInstructions();
	}

	private void initElements()
	{
		imageviewImage					= (ImageView) findViewById(R.id.imageview_image);
		imageviewBuildingInstructions	= (ImageView) findViewById(R.id.imageview_building_instructions);
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
	}

	private void resizeBuildingInstructions()
	{
		int width	= ((RelativeLayout) imageviewBuildingInstructions.getParent()).getWidth() / 5;

		RelativeLayout.LayoutParams	buildingInstructionsLayout	= new RelativeLayout.LayoutParams(width, width);

		buildingInstructionsLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		buildingInstructionsLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		imageviewBuildingInstructions.setLayoutParams(buildingInstructionsLayout);
	}

	private void launchBuildingInstructionActivity()
	{
		Intent launchBuildingInstructionActivity = new Intent(this, BuildingInstructionActivity.class);

		launchBuildingInstructionActivity.putExtra("set_id", setInfo.id);

		startActivity(launchBuildingInstructionActivity);
	}

}
