package ie.ucd.onetwotwofive.practical4;

import java.util.HashMap;
import ie.ucd.onetwotwofive.practical4.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CourseListActivity extends ListActivity {
	
	private HashMap<String, CompCourse> courseHashMap = new HashMap<String, CompCourse>();
	
	public final static String COURSE_NAME = "ie.ucd.12259095.COURSE_NAME";
	public final static String COURSE_CREDITS = "ie.ucd.12259095.COURSE_CREDITS";
	public final static String COURSE_DESC = "ie.ucd.12259095.COURSE_DESC";
	public final static String PLAY_MEDIA = "ie.ucd.12259095.PLAY_MEDIA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get the CourseData from the SQLLite database
		CoursesDBSQLiteOpenHandler courseDBOpenHelper = new CoursesDBSQLiteOpenHandler(this);
		SQLiteDatabase coursesDB = courseDBOpenHelper.getReadableDatabase();
		Cursor courseList = coursesDB.rawQuery("select * from courses", null);
		
		//Initialise the HashMap with the course data
		CompCourse coursesSet[] = new CompCourse[courseList.getCount()];
		int count=0;
		for (courseList.moveToFirst(); !courseList
				.isAfterLast(); courseList.moveToNext()) {
				coursesSet[count] = new CompCourse(courseList.getString(courseList.getColumnIndex(CoursesDBSQLiteOpenHandler.COURSE_NAME_COLUMN)),
												courseList.getInt(courseList.getColumnIndex(CoursesDBSQLiteOpenHandler.COURSE_CREDITS_COLUMN)),
												courseList.getString(courseList.getColumnIndex(CoursesDBSQLiteOpenHandler.COURSE_DESCRIPTION_COLUMN)));
			count++;
		}
		courseList.close();
		coursesDB.close();
		
		//Create Course short names and initialise HashMap
		final String[] courseShortNames = new String[10];
		for(int i=0; i<10; i++ ) {
			courseShortNames[i] = coursesSet[i].name.substring(0, 10);
			courseHashMap.put(courseShortNames[i], coursesSet[i]);
		}
		
		//Set preferences default
		PreferenceManager.setDefaultValues(this, R.xml.practical4_preferences, false);
		
		//Set the content view
		setContentView(R.layout.course_list_layout);

		//Create and display the listview
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.course_list_view_element, courseShortNames));
		ListView courseListView = (ListView) getListView();
		courseListView.setAdapter(new ArrayAdapter<String>(this, R.layout.course_list_view_element, courseShortNames));
		courseListView.setTextFilterEnabled(true);
		
		//Add a click listener to the list view
		courseListView.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
					                Intent intent = new Intent(v.getContext(), CourseDetailsActivity.class);
					                String courseShortNameString = (((TextView) v).getText()).toString();
					                CompCourse thisCourse = (CompCourse) courseHashMap.get(courseShortNameString);
					                intent.putExtra(CourseListActivity.COURSE_NAME, thisCourse.name);
					                intent.putExtra(CourseListActivity.COURSE_CREDITS, thisCourse.credits);
					                intent.putExtra(CourseListActivity.COURSE_DESC, thisCourse.description);
					                if (courseShortNameString.equalsIgnoreCase(courseShortNames[2])) {
					                	intent.putExtra(CourseListActivity.PLAY_MEDIA, true);
					                }
					                startActivity(intent);
						}
		});
		
        //Add the preferences button listener
        final Button preferencesButton = (Button) findViewById(R.id.preferencesButton);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CoursesPreferencesActivity.class);
                startActivity(intent);
            }
        });
		
	}
}