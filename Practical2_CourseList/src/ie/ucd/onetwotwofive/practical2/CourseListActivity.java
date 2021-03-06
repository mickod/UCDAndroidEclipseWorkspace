package ie.ucd.onetwotwofive.practical2;

import java.util.HashMap;

import ie.ucd.onetwotwofive.practical2.R;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CourseListActivity extends ListActivity {
	
	private HashMap<String, CompCourse> courseHashMap = new HashMap<String, CompCourse>();
	
	public final static String COURSE_NAME = "ie.ucd.12259095.COURSE_NAME";
	public final static String COURSE_CREDITS = "ie.ucd.12259095.COURSE_CREDITS";
	public final static String COURSE_DESC = "ie.ucd.12259095.COURSE_DESC";
	public final static String PLAY_MEDIA = "ie.ucd.12259095.PLAY_MEDIA";
	
	static final String[] COURSES = new String[] { 	"COMP41150 Mobile Application Development 2013", 
													"ACM40570  Mathematical Methods", 
													"COMP30120* Machine Learning", 
													"COMP30150 Information Systems II", 
													"COMP30220 Distributed Systems 2012-2013",
													"COMP30230 Connectionist Computing", 
													"COMP30250 Parallel and Cluster Computing 2012-2013", 
													"COMP30260 Artificial Intelligence for Games and Puzzles",
													"COMP30270 Computer Graphics II", 
													"COMP40010 Performance of Computer Systems" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Initialise the HashMap with the course data
		CompCourse coursesSet[] = new CompCourse[10];
		coursesSet[0] = new CompCourse(COURSES[0],5,"This course teaches Android Mobile Applictaion development");
		coursesSet[1] = new CompCourse(COURSES[1],15,"This course focuses on Maths methods");
		coursesSet[2] = new CompCourse(COURSES[2],25,"This course focuses on machine learnign rather than AI");
		coursesSet[3] = new CompCourse(COURSES[3],10,"This course looks at Information Systems");
		coursesSet[4] = new CompCourse(COURSES[4],5,"This is a distributed systems course");
		coursesSet[5] = new CompCourse(COURSES[5],15,"This course use connectionist methods and practices...");
		coursesSet[6] = new CompCourse(COURSES[6],35,"This course looks at and implements parallel and sluster computing");
		coursesSet[7] = new CompCourse(COURSES[7],10,"This course looks at AI for games etc");
		coursesSet[8] = new CompCourse(COURSES[8],15,"This course focuses on Computer Graphics");
		coursesSet[9] = new CompCourse(COURSES[9],50,"This looks at computer systems perfomrance");
		
		//Create Course short names and initialise HashMap
		final String[] courseShortNames = new String[10];
		for(int i=0; i<10; i++ ) {
			courseShortNames[i] = COURSES[i].substring(0, 10);
			courseHashMap.put(courseShortNames[i], coursesSet[i]);
		}
	
		//Create and display the listview
		setListAdapter(new ArrayAdapter<String>(this, R.layout.course_list_view_element, courseShortNames));
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		//Add a click listener to the list view
		listView.setOnItemClickListener(new OnItemClickListener() {
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
	

	}
}