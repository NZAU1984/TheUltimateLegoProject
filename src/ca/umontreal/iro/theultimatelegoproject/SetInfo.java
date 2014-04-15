package ca.umontreal.iro.theultimatelegoproject;

import android.content.Context;

public class SetInfo
{
	public String	id;
	public String	imageURL;
	public String	description;
	public int		year;
	public String	yearAsString;
	public double	price;
	public String	priceAsString;
	public int		nbPieces;
	public String	nbPiecesAsString;

	public SetInfo(Context context, String argId, String argImageURL, String argDescription, int argYear, double argPrice, int argNbPieces)
	{
		id					= argId;
		imageURL			= argImageURL;
		description			= argDescription;
		year				= argYear;
		price				= argPrice;
		nbPieces			= argNbPieces;
		yearAsString		= String.format(context.getString(R.string.setinfo_year), year);
		priceAsString		= String.format(context.getString(R.string.setinfo_price), price);
		nbPiecesAsString	= ((0 == argNbPieces) ? context.getString(R.string.setinfo_nopieces) : context.getResources().getQuantityString(R.plurals.setinfo_nbpieces, nbPieces, nbPieces));
	}
}
