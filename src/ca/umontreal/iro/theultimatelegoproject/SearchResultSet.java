package ca.umontreal.iro.theultimatelegoproject;

public class SearchResultSet
{
	public String	id;
	public String	imageUrl;
	public Boolean	seen;
	public Boolean	favorite;

	public SearchResultSet(String argId, String argImageUrl, String argSeen, String argFavorite)
	{
		id			= argId;
		imageUrl	= argImageUrl;
		seen		= (argSeen.equals("1") ? true : false);
		favorite	= (argFavorite.equals("1") ? true : false);
	}
}
