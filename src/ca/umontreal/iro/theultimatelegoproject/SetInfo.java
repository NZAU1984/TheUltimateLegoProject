package ca.umontreal.iro.theultimatelegoproject;

import android.content.Context;

public class SetInfo
{
	public String	id;
	public String	imageURL;
	public String	description;
	public String	year;
	public String	yearAsString;
	public String	price;
	public String	priceAsString;
	public String	nbPieces;
	public String	nbPiecesAsString;
	public Boolean	favorite;

	public SetInfo(Context context, String argId, String argImageURL, String argDescription, String argYear, String argPrice, String argNbPieces, String argFavorite)
	{
		id					= argId;
		imageURL			= argImageURL;
		description			= argDescription;
		year				= argYear;
		price				= argPrice;
		nbPieces			= argNbPieces;
		favorite			= argFavorite.equals("1");

		yearAsString		= String.format(context.getString(R.string.setinfo_year), year);

		double priceFloat	= 0;

		try
		{
			priceFloat	= Float.valueOf(price);
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		priceAsString		= String.format(context.getString(R.string.setinfo_price), priceFloat);

		int nbPiecesInt		= 0;

		try
		{
			nbPiecesInt	= Integer.valueOf(nbPieces);
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		nbPiecesAsString	= ((argNbPieces.equals("")) ? context.getString(R.string.setinfo_nopieces) : context.getResources().getQuantityString(R.plurals.setinfo_nbpieces, nbPiecesInt, nbPiecesInt));
	}
}
