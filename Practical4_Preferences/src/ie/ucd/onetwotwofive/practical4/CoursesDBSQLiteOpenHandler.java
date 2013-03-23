package ie.ucd.onetwotwofive.practical4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CoursesDBSQLiteOpenHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "course_info.db";
    private static final int DATABASE_VERSION = 1;
    private static final String NOTES_TABLE_NAME = "courses";
    public static final String COURSE_NAME_COLUMN = "COURSE_NAME";
    public static final String COURSE_CREDITS_COLUMN = "COURSE_CREDITS";
    public static final String COURSE_DESCRIPTION_COLUMN = "COURSE_DESCRIPTION";
    
	static final String[][] COURSES = new String[][] { 	
		{"COMP41150 Mobile Application Development 2013", "5","This course teaches Android Mobile Applictaion development"},
		{"ACM40570  Mathematical Methods", "15","This course focuses on Maths methods"},
		{"COMP30120* Machine Learning", "20", "This course focuses on machine learnign rather than AI"},
		{"COMP30150 Information Systems II", "10", "This course looks at Information Systems"},
		{"COMP30220 Distributed Systems 2012-2013","5","This is a distributed systems course"},
		{"COMP30230 Connectionist Computing","15","This course use connectionist methods and practices..."},
		{"COMP30250 Parallel and Cluster Computing 2012-2013", "35", "This course looks at and implements parallel and sluster computing"}, 
		{"COMP30260 Artificial Intelligence for Games and Puzzles", "10","This course looks at AI for games etc"},
		{"COMP30270 Computer Graphics II", "15", "This course focuses on Computer Graphics"},
		{"COMP40010 Performance of Computer Systems", "50","This looks at computer systems perfomrance"}};

	CoursesDBSQLiteOpenHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
    public void onCreate(SQLiteDatabase db) {
		//Create the table
        db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " ("
                + COURSE_NAME_COLUMN + " TEXT PRIMARY KEY,"
                + COURSE_CREDITS_COLUMN + " INTEGER,"
                + COURSE_DESCRIPTION_COLUMN + " TEXT"
                + ");");
        
        //Add the initial data
        for (int i =0; i < COURSES.length; i++) {
        	db.execSQL("INSERT INTO courses (" + COURSE_NAME_COLUMN + " , " + COURSE_CREDITS_COLUMN + " , " +  COURSE_DESCRIPTION_COLUMN + ") " +
        				"VALUES (\""+ COURSES[i][0] + "\", " + COURSES[i][1] + " , \"" + COURSES[i][2] + "\"  )");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("CourseDatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }
}
