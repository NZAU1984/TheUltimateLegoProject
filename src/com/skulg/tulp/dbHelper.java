package com.skulg.tulp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ca.umontreal.iro.theultimatelegoproject.SearchResultSet;
import ca.umontreal.iro.theultimatelegoproject.SetInfo;

public class dbHelper extends SQLiteOpenHelper
{
	SQLiteDatabase db;

	SQLiteDatabase writableDb;

	static final int VERSION = 21;

	// Filename
	static final String DATABASE_FILENAME = "Tulp.db";

	// Tables Names
	static final String LEGOSETS_TABLE_NAME = "LegoSets";
	static final String BUILDING_INSTRUCTIONS_TABLE_NAME = "BuildingInstructions";
	static final String STEP_GROUP_TABLE_NAME = "StepGroup";
	static final String INSTRUCTION_IMAGES_TABLE_NAME = "InstructionsImages";
	static final String STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME = "StepGroupInstructionsLink";
	static final String STEP_GROUP_IMAGES_LINK_TABLE_NAME = "StepGroupImagesLink";
	static final String IMAGES_TABLE_NAME = "Images";
	static final String IMPORT_TABLE_NAME	= "ImportTable";

	// Shared Columns
	static final String KEY_ID = "_id";
	static final String KEY_CREATED_AT = "created_at";

	// LEGOSETS COLUMNS NAMES
	static final String LEGOSETS_DESCRIPTION_COLUMN = "description";
	static final String LEGOSETS_BOX_NUMBER_COLUMN = "boxNo";
	static final String LEGOSETS_IMAGE_URL_COLUMN = "imageUrl";
	static final String LEGOSETS_NAME_COLUMN = "name";
	static final String LEGOSETS_LEGO_MODEL_NAME_COLUMN = "legoModelName";
	static final String LEGOSETS_PIECES_COLUMN = "pieces";
	static final String LEGOSETS_PRICE_COLUMN = "price";
	static final String LEGOSETS_RELEASED_COLUMN = "released";
	static final String LEGOSETS_FAVORITE_COLUMN = "favorite";
	static final String LEGOSETS_SEEN_COLUMN = "seen";
	static final String LEGOSETS_BUILDING_INSTRUCTIONS_ID	= "buildingInstructionsId";

	// BUILDING INSTRUCTIONS COLUMN NAME

	static final String BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN = "description";
	static final String BUILDING_INSTRUCTIONS_NAME_COLUMN = "name";
	static final String BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN = "shortcutPicture";

	// STEP GROUP COLUMN NAME
	static final String STEP_GROUP_NAME_COLUMN = "name";

	// INSTRUCTION IMAGES COLUMN NAME
	static final String INSTRUCTION_IMAGES_URL_COLUMN = "imageUrl";

	// STEP GROUP INSTRUCTIONS LINK COLUMN NAME
	static final String STEP_GROUP_INSTRUCTIONS_STEP_GROUP_ID_COLUMN = "stepGroupId";
	static final String STEP_GROUP_INSTRUCTIONS_INSTRUCTIONS_ID_COLUMN = "instructionsId";

	// STEP GROUP IMAGES LINK COLUMN NAME
	static final String STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN = "stepGroupId";
	static final String STEP_GROUP_IMAGES_IMAGE_ID_COLUMN = "imageId";

	// IMAGES COLUMN NAME
	static final String IMAGE_URL_COLUMN = "url";
	static final String IMAGE_INSTRUCTIONSID_COLUMN = "instructionsId";

	// IMPORT COLUMN NAME
	static final String IMPORT_BUILDING_INSTRUCTIONS_ID	= "buildingInstructionsId";

	Context context;

	public dbHelper(Context context)
	{
		super(context, DATABASE_FILENAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		this.db = db;
		Log.d("TULP", "Creation of Database");
		String sql = "create table " + LEGOSETS_TABLE_NAME
				+ " ("
					+ KEY_ID							+ " integer primary key , "
					+ LEGOSETS_DESCRIPTION_COLUMN		+ " text, "
					+ LEGOSETS_BOX_NUMBER_COLUMN		+ " integer, "
					+ LEGOSETS_IMAGE_URL_COLUMN			+ " text, "
					+ LEGOSETS_NAME_COLUMN				+ " text, "
					+ LEGOSETS_LEGO_MODEL_NAME_COLUMN	+ " text, "
					+ LEGOSETS_PIECES_COLUMN			+ " integer, "
					+ LEGOSETS_PRICE_COLUMN				+ " double, "
					+ LEGOSETS_RELEASED_COLUMN			+ " integer, "
					+ LEGOSETS_BUILDING_INSTRUCTIONS_ID + " integer, "
					+ LEGOSETS_FAVORITE_COLUMN			+ " boolean, "
					+ LEGOSETS_SEEN_COLUMN				+ " boolean "
				+ ")";
		db.execSQL(sql);

		sql = "create table " + BUILDING_INSTRUCTIONS_TABLE_NAME + " (" + KEY_ID + " integer primary key , "
				+ BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN + " text," + BUILDING_INSTRUCTIONS_NAME_COLUMN + " text,"
				+ BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN + " text" + ")";
		db.execSQL(sql);

		sql = "create table " + STEP_GROUP_TABLE_NAME + " (" + KEY_ID + " integer primary key AUTOINCREMENT, "
				+ STEP_GROUP_NAME_COLUMN + " text" + ")";
		db.execSQL(sql);

		sql = "create table " + IMAGES_TABLE_NAME + " (" + KEY_ID + " integer primary key AUTOINCREMENT, "
				+ IMAGE_URL_COLUMN + " text," + IMAGE_INSTRUCTIONSID_COLUMN + " integer" + ")";
		db.execSQL(sql);

		sql = "create table " + IMPORT_TABLE_NAME + " (" + KEY_ID + " integer primary key, " + IMPORT_BUILDING_INSTRUCTIONS_ID + " integer)";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		Log.d("TULP", "Database Update");

		// Efface l'ancienne base de données
		deleteDbFile();
		// Appelle onCreate, qui recrée la base de données
		onCreate(db);
	}

	protected void deleteAllTables(SQLiteDatabase db)
	{
		db.execSQL("drop table if exists " + LEGOSETS_TABLE_NAME);
		db.execSQL("drop table if exists " + BUILDING_INSTRUCTIONS_TABLE_NAME);
		db.execSQL("drop table if exists " + INSTRUCTION_IMAGES_TABLE_NAME);
		db.execSQL("drop table if exists " + STEP_GROUP_TABLE_NAME);
		db.execSQL("drop table if exists " + STEP_GROUP_IMAGES_LINK_TABLE_NAME);
		db.execSQL("drop table if exists " + STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME);
		db.execSQL("drop table if exists " + IMAGES_TABLE_NAME);
		db.execSQL("drop table if exists " + IMPORT_TABLE_NAME);
		deleteDbFile();
	}

	protected void insertLegoSets(String description, int boxNumber, String imageUrl, String name, String modelName,
			int nbPieces, double price, int released, String buildingInstructionsId)
	{
		SQLiteDatabase myWritableDb	= getWritableDatabase();

		ContentValues val = new ContentValues();
		val.clear();
		val.put(KEY_ID, boxNumber);
		val.put(LEGOSETS_DESCRIPTION_COLUMN, description);
		val.put(LEGOSETS_BOX_NUMBER_COLUMN, boxNumber);
		val.put(LEGOSETS_IMAGE_URL_COLUMN, imageUrl);
		val.put(LEGOSETS_NAME_COLUMN, name);
		val.put(LEGOSETS_LEGO_MODEL_NAME_COLUMN, modelName);
		val.put(LEGOSETS_PIECES_COLUMN, nbPieces);
		val.put(LEGOSETS_PRICE_COLUMN, price);
		val.put(LEGOSETS_RELEASED_COLUMN, released);
		val.put(LEGOSETS_SEEN_COLUMN, false);
		val.put(LEGOSETS_FAVORITE_COLUMN, false);
		val.put(LEGOSETS_BUILDING_INSTRUCTIONS_ID, buildingInstructionsId);

		try
		{
			myWritableDb.insertWithOnConflict(LEGOSETS_TABLE_NAME, null, val, SQLiteDatabase.CONFLICT_REPLACE);
		}
		catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}

		myWritableDb.close();
	}

	/*
	 * UNUSED
	public void insertBuildingInstructions(int idInstruction, String description, String imageUrl, String name)
	{
		ContentValues val = new ContentValues();
		val.clear();
		val.put(KEY_ID, idInstruction);
		val.put(BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN, description);
		val.put(BUILDING_INSTRUCTIONS_NAME_COLUMN, name);
		val.put(BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN, imageUrl);
		try
		{
			writableDb.insertOrThrow(BUILDING_INSTRUCTIONS_TABLE_NAME, null, val);
		} catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		//db.close();
	}
	*/

	/*
	 * UNUSED
	protected long insertStepGroup(String name)
	{
		long resultId = -1;
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_NAME_COLUMN, name);
		try
		{
			resultId = db.insertOrThrow(STEP_GROUP_TABLE_NAME, null, val);
		} catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
		return resultId;
	}
	*/

	/*
	 * UNUSED
	protected void insertStepGroupImageLink(int imageId, int stepGroupId)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_IMAGES_IMAGE_ID_COLUMN, imageId);
		val.put(STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN, stepGroupId);
		try
		{
			db.insertOrThrow(STEP_GROUP_IMAGES_LINK_TABLE_NAME, null, val);
		} catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}
	*/

	/*
	 * UNUSED
	protected void insertStepGroupInstructionsLink(int instructionId, int stepGroupId)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_INSTRUCTIONS_INSTRUCTIONS_ID_COLUMN, instructionId);
		val.put(STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN, stepGroupId);
		try
		{
			db.insertOrThrow(STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME, null, val);
		} catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}
	*/

	public long insertImages(String url, int instructionId)
	{
		long resultId = -1;
		SQLiteDatabase myWritableDb = getWritableDatabase();

		ContentValues val = new ContentValues();

		val.clear();
		val.put(IMAGE_URL_COLUMN, url);
		val.put(IMAGE_INSTRUCTIONSID_COLUMN, instructionId);

		try
		{
			resultId = myWritableDb.insertOrThrow(IMAGES_TABLE_NAME, null, val);
		}
		catch (SQLException e)
		{
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}

		myWritableDb.close();

		return resultId;
	}

	/*
	 * UNUSED
	public Cursor getAllLegoSets()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(LEGOSETS_TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	*/

	public SetInfo getLegoSet(String setId, Boolean updateSeen)
	{
		String selectColumns[]	= {LEGOSETS_BUILDING_INSTRUCTIONS_ID, LEGOSETS_IMAGE_URL_COLUMN, LEGOSETS_DESCRIPTION_COLUMN, LEGOSETS_RELEASED_COLUMN, LEGOSETS_PRICE_COLUMN, LEGOSETS_PIECES_COLUMN, LEGOSETS_FAVORITE_COLUMN};

		SQLiteDatabase myWritableDb	= getWritableDatabase();

		Cursor cursor	= null;

		SetInfo setInfo	= null;

		try
		{
			cursor = myWritableDb.query(LEGOSETS_TABLE_NAME, selectColumns, KEY_ID + "=?", new String[] {setId}, null, null, null);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			cursor	= null;
		}

		if((null != cursor) && (0 != cursor.getCount()))
		{
			cursor.moveToFirst();

			setInfo	= new SetInfo(
				context,
				setId,
				cursor.getString(cursor.getColumnIndex(LEGOSETS_BUILDING_INSTRUCTIONS_ID)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_IMAGE_URL_COLUMN)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_DESCRIPTION_COLUMN)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_RELEASED_COLUMN)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_PRICE_COLUMN)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_PIECES_COLUMN)),
				cursor.getString(cursor.getColumnIndex(LEGOSETS_FAVORITE_COLUMN))
			);

			cursor.close();
		}

		if((null != cursor) && updateSeen)
		{
			ContentValues val = new ContentValues();
			val.clear();
			val.put(LEGOSETS_SEEN_COLUMN, "1");

			try
			{
				myWritableDb.update(LEGOSETS_TABLE_NAME, val, KEY_ID + "=?", new String[] {setId});
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		myWritableDb.close();

		return setInfo;
	}

	public void setLegoSetFavorite(String setId, Boolean isFavorite)
	{
		SQLiteDatabase myWritableDb	= getWritableDatabase();

		ContentValues val = new ContentValues();
		val.clear();
		val.put(LEGOSETS_FAVORITE_COLUMN, isFavorite ? "1" : "0");

		try
		{
			myWritableDb.update(LEGOSETS_TABLE_NAME, val, KEY_ID + "=?", new String[] {setId});
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myWritableDb.close();
	}

	/*
	 * UNUSED
	public Cursor getAllBuildingInstructions()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BUILDING_INSTRUCTIONS_TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	*/

	/*
	 * UNUSED
	public Cursor getBuildingInstructionsInfo(String setId)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BUILDING_INSTRUCTIONS_TABLE_NAME, null, BUILDING_INSTRUCTIONS_NAME_COLUMN + "="
				+ setId, null, null, null, null);
		return cursor;
	}
	*/

	public String[] getBuildingInstructionsImages(String buildingInstructionsId)
	{
		SQLiteDatabase readableDb	= getReadableDatabase();

		String[] buildingInstructionsImages	= new String[] {};

		Cursor cursor	= null;

		try
		{
			cursor	= readableDb.query(IMAGES_TABLE_NAME, new String[] {IMAGE_URL_COLUMN}, IMAGE_INSTRUCTIONSID_COLUMN + "=?", new String[] {buildingInstructionsId}, null, null, KEY_ID + " ASC");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			cursor	= null;
		}

		if((null != cursor) && (0 != cursor.getCount()))
		{
			int urlIndex					= cursor.getColumnIndex(IMAGE_URL_COLUMN);
			ArrayList<String> tempArrayList	= new ArrayList<String>();

			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				tempArrayList.add(cursor.getString(urlIndex));

				cursor.moveToNext();
			}

			buildingInstructionsImages	= tempArrayList.toArray(new String[tempArrayList.size()]);

			cursor.close();
		}

		readableDb.close();

		return buildingInstructionsImages;
	}

	private Cursor searchLegoSets(String keywords, String minPrice, String maxPrice, String minYear, String maxYear,
			String minPieces, String maxPieces, Boolean favorite, Boolean returnCount)
	{
		// PRE-CONDITION : openWritableDatabase() must have been called by calling method.

		String selectColumns[] 	= {KEY_ID, LEGOSETS_IMAGE_URL_COLUMN, LEGOSETS_SEEN_COLUMN, LEGOSETS_FAVORITE_COLUMN};
		String keywordsSqlPart 	= "";
		String minPriceSqlPart 	= "";
		String maxPriceSqlPart 	= "";
		String minPiecesSqlPart = "";
		String maxPiecesSqlPart = "";
		String minYearSqlPart 	= "";
		String maxYearSqlPart 	= "";
		String favoriteSqlPart	= "";
		boolean firstPart 		= true;

		if(returnCount)
		{
			selectColumns	= new String[] {"COUNT(*)"};
		}

		if ((keywords != null) && !keywords.equals(""))
		{
			if (!firstPart)
			{
				keywordsSqlPart = keywordsSqlPart + " AND ";
			}
			keywordsSqlPart = keywordsSqlPart + LEGOSETS_DESCRIPTION_COLUMN + " like '%" + keywords + "%'";
			firstPart = false;
		}

		if ((minPrice != null) && !minPrice.equals(""))
		{
			if (!firstPart)
			{
				minPriceSqlPart = minPriceSqlPart + " AND ";
			}
			minPriceSqlPart = minPriceSqlPart + LEGOSETS_PRICE_COLUMN + ">=" + minPrice;
			firstPart = false;
		}
		if ((maxPrice != null) && !maxPrice.equals(""))
		{
			if (!firstPart)
			{
				maxPriceSqlPart = maxPriceSqlPart + " AND ";
			}
			maxPriceSqlPart = maxPriceSqlPart + LEGOSETS_PRICE_COLUMN + "<=" + maxPrice;
			firstPart = false;
		}

		if ((minPieces != null) && !minPieces.equals(""))
		{
			if (!firstPart)
			{
				minPiecesSqlPart = minPiecesSqlPart + " AND ";
			}
			minPiecesSqlPart = minPiecesSqlPart + LEGOSETS_PIECES_COLUMN + ">=" + minPieces;
			firstPart = false;
		}

		if ((maxPieces != null) && !maxPieces.equals(""))
		{
			if (!firstPart)
			{
				maxPiecesSqlPart = maxPiecesSqlPart + " AND ";
			}
			maxPiecesSqlPart = maxPiecesSqlPart + LEGOSETS_PIECES_COLUMN + "<=" + maxPieces;
			firstPart = false;
		}

		if ((minYear != null) && !minYear.equals(""))
		{
			if (!firstPart)
			{
				minYearSqlPart = minYearSqlPart + " AND ";
			}
			minYearSqlPart = minYearSqlPart + LEGOSETS_RELEASED_COLUMN + ">=" + minYear;
			firstPart = false;
		}

		if ((maxYear != null) && !maxYear.equals(""))
		{
			if (!firstPart)
			{
				maxYearSqlPart = maxYearSqlPart + " AND ";
			}
			maxYearSqlPart = maxYearSqlPart + LEGOSETS_RELEASED_COLUMN + "<=" + maxYear;
			firstPart = false;
		}

		if(favorite)
		{
			if(!firstPart)
			{
				favoriteSqlPart	= favoriteSqlPart + " AND ";
			}

			favoriteSqlPart = favoriteSqlPart + LEGOSETS_FAVORITE_COLUMN + "=1";

			firstPart	= false;
		}

		Cursor cursor	= null;

		try
		{
			cursor = writableDb.query(LEGOSETS_TABLE_NAME, selectColumns, keywordsSqlPart + minPriceSqlPart + maxPriceSqlPart
					+ minPiecesSqlPart + maxPiecesSqlPart + minYearSqlPart + maxYearSqlPart + favoriteSqlPart, null, null, null, null);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cursor;
	}

	public int getSearchLegoSetsCount(String keywords, String minPrice, String maxPrice, String minYear, String maxYear,
			String minPieces, String maxPieces)
	{
		openWritableDatabase();

		Cursor cursor	= searchLegoSets(keywords, minPrice, maxPrice, minYear, maxYear, minPieces, maxPieces, false, true);

		int count	= 0;

		if(null != cursor)
		{
			if(0 < cursor.getCount())
			{
				cursor.moveToFirst();

				count	= cursor.getInt(0);
			}

			cursor.close();
		}

		closeWritableDatabase();

		return count;
	}

	private ArrayList<SearchResultSet> getSearchLegoSetsArrayList(String keywords, String minPrice, String maxPrice, String minYear, String maxYear,
			String minPieces, String maxPieces, Boolean favorite)
	{
		openWritableDatabase();

		Cursor cursor	= searchLegoSets(keywords, minPrice, maxPrice, minYear, maxYear, minPieces, maxPieces, favorite, false);

		ArrayList<SearchResultSet> searchResults	= new ArrayList<SearchResultSet>();

		if(null != cursor)
		{
			int keyIdIndex 		= cursor.getColumnIndex(KEY_ID);
			int imageUrlIndex	= cursor.getColumnIndex(LEGOSETS_IMAGE_URL_COLUMN);
			int seenIndex 		= cursor.getColumnIndex(LEGOSETS_SEEN_COLUMN);
			int favoriteIndex	= cursor.getColumnIndex(LEGOSETS_FAVORITE_COLUMN);

			cursor.moveToFirst();

			while (!cursor.isAfterLast())
			{
				searchResults.add(new SearchResultSet(
					cursor.getString(keyIdIndex),
					cursor.getString(imageUrlIndex),
					cursor.getString(seenIndex),
					cursor.getString(favoriteIndex)
				));

				cursor.moveToNext();
			}

			cursor.close();
		}

		closeWritableDatabase();

		return searchResults;
	}

	public ArrayList<SearchResultSet> getSearchLegoSetsArrayList(String keywords, String minPrice, String maxPrice, String minYear, String maxYear,
			String minPieces, String maxPieces)
	{
		return getSearchLegoSetsArrayList(keywords, minPrice, maxPrice, minYear, maxYear, minPieces, maxPieces, false);
	}

	public ArrayList<SearchResultSet> getFavoritesLegoSetsArrayList()
	{
		return getSearchLegoSetsArrayList(null, null, null, null, null, null, null, true);
	}

	private Boolean deleteDbFile()
	{
		return context.deleteDatabase(DATABASE_FILENAME);
	}

	public Boolean insertImportSet(String setId, String buildingInstructionsId)
	{
		SQLiteDatabase myWritableDb	= getWritableDatabase();

		Boolean success	= false;

		ContentValues val = new ContentValues();

		val.clear();
		val.put(KEY_ID, setId);
		val.put(IMPORT_BUILDING_INSTRUCTIONS_ID, buildingInstructionsId);

		try
		{
			myWritableDb.insertWithOnConflict(IMPORT_TABLE_NAME, null, val, SQLiteDatabase.CONFLICT_IGNORE);

			success	= true;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myWritableDb.close();

		return success;
	}

	public String[] getAllSetsToBeImported()
	{
		SQLiteDatabase readableDb	= getReadableDatabase();

		String[] sets	= new String[] {};

		Cursor cursor = null;

		try
		{
			cursor	= readableDb.query(IMPORT_TABLE_NAME, null, null, null, null, null, null);
		}
		catch(Exception e)
		{
			cursor	= null;
		}

		if(null != cursor)
		{
			int keyIndex					= cursor.getColumnIndex(KEY_ID);
			int buildingInstructionsIdIndex	= cursor.getColumnIndex(IMPORT_BUILDING_INSTRUCTIONS_ID);

			ArrayList<String> tempArrayList	= new ArrayList<String>();

			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				tempArrayList.add(cursor.getString(keyIndex));
				tempArrayList.add(cursor.getString(buildingInstructionsIdIndex));

				cursor.moveToNext();
			}

			sets	= tempArrayList.toArray(new String[tempArrayList.size()]);

			cursor.close();
		}

		readableDb.close();

		return sets;
	}

	public void removeSetFromImportTable(int setId)
	{
		SQLiteDatabase myWritableDb	= getWritableDatabase();

		try
		{
			myWritableDb.delete(IMPORT_TABLE_NAME, KEY_ID + "=?", new String[] { String.valueOf(setId) });
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myWritableDb.close();
	}

	public int getNumberOfSetsToBeImported()
	{
		int count	= 0;

		SQLiteDatabase readableDb	= getReadableDatabase();

		Cursor cursor	= null;

		try
		{
			cursor = readableDb.rawQuery("SELECT COUNT(*) FROM " + IMPORT_TABLE_NAME, null);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			cursor	= null;
		}

		if(null != cursor)
		{
			if(0 < cursor.getCount())
			{
				cursor.moveToFirst();

				count	= cursor.getInt(0);
			}

			cursor.close();
		}

		readableDb.close();

		return count;
	}

	public Boolean buildingInstructionsExist(String buildingInstructionsId)
	{
		SQLiteDatabase readableDb	= getReadableDatabase();

		Cursor cursor	= null;

		Boolean success	= false;

		try
		{
			cursor = readableDb.query(IMAGES_TABLE_NAME, new String[] {"COUNT(*)"}, IMAGE_INSTRUCTIONSID_COLUMN + "=?", new String[] {buildingInstructionsId}, null, null, null);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			cursor	= null;

			success	= false;
		}

		if(null != cursor)
		{
			int count	= 0;

			cursor.moveToFirst();

			count	= cursor.getInt(0);

			success	= (0 == count) ? false : true;

			cursor.close();
		}

		readableDb.close();

		return success;
	}

	private void openWritableDatabase()
	{
		if(null == writableDb)
		{
			writableDb = getWritableDatabase();
		}
	}

	private void closeWritableDatabase()
	{
		if(null != writableDb)
		{
			writableDb.close();
		}
	}

	public static boolean databaseExists(Context context)
	{
		return context.getDatabasePath(DATABASE_FILENAME).exists();
	}
}
