package ca.umontreal.iro.theultimatelegoproject;

import android.content.Context;
import android.widget.Toast;

public class Tools
{

	public static int	totalSets	= 999;
	public static int	currentSets	= 0;
	public static void shortToast(Context context, String text)
	{
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void longToast(Context context, String text)
	{
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}
}
