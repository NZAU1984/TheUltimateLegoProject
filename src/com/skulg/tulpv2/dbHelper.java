package com.skulg.tulpv2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class dbHelper extends SQLiteOpenHelper{
	SQLiteDatabase db ;
	static final int VERSION=8;

	//Tables Names
	static final String LEGOSETS_TABLE_NAME = "LegoSets";
	static final String BUILDING_INSTRUCTIONS_TABLE_NAME = "BuildingInstructions";
	static final String STEP_GROUP_TABLE_NAME = "StepGroup";
	static final String INSTRUCTION_IMAGES_TABLE_NAME = "InstructionsImages";
	static final String STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME = "StepGroupInstructionsLink";
	static final String STEP_GROUP_IMAGES_LINK_TABLE_NAME = "StepGroupImagesLink";


	//Shared Columns
	static final String KEY_ID = "_id";
	static final String KEY_CREATED_AT = "created_at";

	//LEGOSETS COLUMNS NAMES
	static final String LEGOSETS_DESCRIPTION_COLUMN = "description";
	static final String LEGOSETS_BOX_NUMBER_COLUMN="boxNo";
	static final String LEGOSETS_IMAGE_URL_COLUMN="imageUrl";
	static final String LEGOSETS_NAME_COLUMN = "name";
	static final String LEGOSETS_LEGO_MODEL_NAME_COLUMN = "legoModelName";
	static final String LEGOSETS_PIECES_COLUMN = "pieces";
	static final String LEGOSETS_PRICE_COLUMN = "price";
	static final String LEGOSETS_RELEASED_COLUMN = "released";
	static final String LEGOSETS_FAVORITE_COLUMN = "favorite";
	static final String LEGOSETS_SEEN_COLUMN = "seen";

	//BUILDING INSTRUCTIONS COLUMN NAME

	static final String BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN = "description";
	static final String BUILDING_INSTRUCTIONS_NAME_COLUMN = "name";
	static final String BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN="shortcutPicture";

	//STEP GROUP COLUMN NAME
	static final String STEP_GROUP_NAME_COLUMN = "name";

	//INSTRUCTION IMAGES COLUMN NAME
	static final String INSTRUCTION_IMAGES_URL_COLUMN="imageUrl";

	//STEP GROUP INSTRUCTIONS LINK COLUMN NAME
	static final String  STEP_GROUP_INSTRUCTIONS_STEP_GROUP_ID_COLUMN = "stepGroupId";
	static final String  STEP_GROUP_INSTRUCTIONS_INSTRUCTIONS_ID_COLUMN = "instructionsId";

	//STEP GROUP IMAGES LINK COLUMN NAME
	static final String  STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN = "stepGroupId";
	static final String  STEP_GROUP_IMAGES_IMAGE_ID_COLUMN = "imageId";

	Context context;	


	public dbHelper(Context context) {
		super(context, "Tulp.db", null, VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db=db;
		Log.d("TULP", "Creation of Database");
		String sql = "create table " + LEGOSETS_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key , "
				+ LEGOSETS_DESCRIPTION_COLUMN + " text,"
				+ LEGOSETS_BOX_NUMBER_COLUMN + " integer,"
				+ LEGOSETS_IMAGE_URL_COLUMN + " text,"
				+ LEGOSETS_NAME_COLUMN + " text, "
				+ LEGOSETS_LEGO_MODEL_NAME_COLUMN + " text, "
				+ LEGOSETS_PIECES_COLUMN + " integer,"
				+ LEGOSETS_PRICE_COLUMN + " double," 
				+ LEGOSETS_RELEASED_COLUMN+ " integer,"
				+ LEGOSETS_FAVORITE_COLUMN+ " boolean,"
				+ LEGOSETS_SEEN_COLUMN+ " boolean"
				+")";
		db.execSQL(sql);

		sql=  "create table " + BUILDING_INSTRUCTIONS_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key , "
				+ BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN  + " text,"
				+ BUILDING_INSTRUCTIONS_NAME_COLUMN + " text,"
				+ BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN + " text"
				+")";
		db.execSQL(sql);

		sql = "create table " + STEP_GROUP_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key AUTOINCREMENT, "
				+ STEP_GROUP_NAME_COLUMN  + " text"
				+ ")";
		db.execSQL(sql);
		sql = "create table " + INSTRUCTION_IMAGES_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key AUTOINCREMENT, "
				+ INSTRUCTION_IMAGES_URL_COLUMN  + " text"
				+ ")";		
		db.execSQL(sql);
		sql = "create table " + STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key AUTOINCREMENT , "
				+ STEP_GROUP_INSTRUCTIONS_STEP_GROUP_ID_COLUMN  + " integer,"
				+ STEP_GROUP_INSTRUCTIONS_INSTRUCTIONS_ID_COLUMN  + " integer"
				+ ")";		
		db.execSQL(sql);
		sql = "create table " + STEP_GROUP_IMAGES_LINK_TABLE_NAME + " ("
				+ KEY_ID +" integer primary key AUTOINCREMENT, "
				+ STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN  + " integer,"
				+ STEP_GROUP_IMAGES_IMAGE_ID_COLUMN  + " integer"
				+ ")";		
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d("TULP", "Database Update");

		// Efface l'ancienne base de données
		deleteAllTables(db);
		// Appelle onCreate, qui recrée la base de données
		onCreate(db);
	}

	protected void deleteAllTables(SQLiteDatabase db) {
		db.execSQL("drop table if exists "+LEGOSETS_TABLE_NAME);
		db.execSQL("drop table if exists "+BUILDING_INSTRUCTIONS_TABLE_NAME);
		db.execSQL("drop table if exists "+INSTRUCTION_IMAGES_TABLE_NAME);		
		db.execSQL("drop table if exists "+STEP_GROUP_TABLE_NAME);
		db.execSQL("drop table if exists "+STEP_GROUP_IMAGES_LINK_TABLE_NAME);
		db.execSQL("drop table if exists "+STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME);
	}

	protected void insertLegoSets( String description , int boxNumber ,String imageUrl ,String name ,String modelName ,int nbPieces ,double price ,int released){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(KEY_ID, boxNumber);
		val.put(LEGOSETS_DESCRIPTION_COLUMN,description);
		val.put(LEGOSETS_BOX_NUMBER_COLUMN, boxNumber);
		val.put(LEGOSETS_IMAGE_URL_COLUMN, imageUrl);
		val.put(LEGOSETS_NAME_COLUMN, name);
		val.put(LEGOSETS_LEGO_MODEL_NAME_COLUMN, modelName);
		val.put(LEGOSETS_LEGO_MODEL_NAME_COLUMN, modelName);
		val.put(LEGOSETS_PIECES_COLUMN, nbPieces);
		val.put(LEGOSETS_PRICE_COLUMN,price);
		val.put(LEGOSETS_RELEASED_COLUMN, released);
		val.put(LEGOSETS_SEEN_COLUMN, false);
		val.put(LEGOSETS_FAVORITE_COLUMN, false);
		try {
			db.insertOrThrow(LEGOSETS_TABLE_NAME, null,val);
		} catch ( SQLException e ) {
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}

	protected void insertBuildingInstructions(int idInstruction, String description ,String imageUrl ,String name ){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(KEY_ID, idInstruction);
		val.put(BUILDING_INSTRUCTIONS_DESCRIPTION_COLUMN,description);
		val.put(BUILDING_INSTRUCTIONS_NAME_COLUMN,name);
		val.put(BUILDING_INSTRUCTIONS_SHORTCUT_IMAGE_URL_COLUMN,imageUrl);
		try {
			db.insertOrThrow(BUILDING_INSTRUCTIONS_TABLE_NAME, null,val);
		} catch ( SQLException e ) {
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}

	protected void insertStepGroup( String name ){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_NAME_COLUMN,name);
		try {
			db.insertOrThrow(STEP_GROUP_TABLE_NAME, null,val);
		} catch ( SQLException e ) {
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}
	protected void insertStepGroupImageLink( int imageId,int stepGroupId ){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_IMAGES_IMAGE_ID_COLUMN,imageId);
		val.put(STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN,stepGroupId);
		try {
			db.insertOrThrow(STEP_GROUP_IMAGES_LINK_TABLE_NAME, null,val);
		} catch ( SQLException e ) {
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}

	protected void insertStepGroupInstructionsLink( int instructionId,int stepGroupId ){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.clear();
		val.put(STEP_GROUP_INSTRUCTIONS_INSTRUCTIONS_ID_COLUMN,instructionId);
		val.put(STEP_GROUP_IMAGES_STEP_GROUP_ID_COLUMN,stepGroupId);
		try {
			db.insertOrThrow(STEP_GROUP_INSTRUCTIONS_LINK_TABLE_NAME, null,val);
		} catch ( SQLException e ) {
			Log.d("DBHelper", "Erreur BDD: " + e.getMessage());
		}
		db.close();
	}
	public Cursor getAllLegoSets() {
		//String selectQuery = "SELECT  * FROM "+LEGOSETS_TABLE_NAME+ " ;";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =db.query(LEGOSETS_TABLE_NAME, null, null, null, null, null, null);
		//Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}
	public Cursor getAllBuildingInstructions() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =db.query(BUILDING_INSTRUCTIONS_TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
}
