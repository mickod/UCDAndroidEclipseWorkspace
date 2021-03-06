package ie.ucd.onetwotwofive.practical3;

import java.util.HashMap;

import ie.ucd.onetwotwofive.practical3.R;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FruitListActivity extends ListActivity {
	
	public final static String FRUIT_NAME = "ie.ucd.12259095.FRUIT_NAME";
	
	static final String[] FRUITS = new String[] { 	"apple", "banana", "kiwi", "strawberry" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Create a language localised FRUITS array
		final HashMap<String, String> fruitLocalNameHashMap = new HashMap<String, String>();
		String[] languageLocalisedFruits = new String[FRUITS.length];
		for (int i=0; i< FRUITS.length; i++) {
			languageLocalisedFruits[i] = getString(getResources().getIdentifier(FRUITS[i], "string",  getPackageName()));
			fruitLocalNameHashMap.put(languageLocalisedFruits[i], FRUITS[i]);
		}
	
		//Create and display the listview
		setListAdapter(new ArrayAdapter<String>(this, R.layout.fruit_list_view_element, languageLocalisedFruits));
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		//Add a click listener to the list view
		listView.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
					                Intent intent = new Intent(v.getContext(), FruitDetailsActivity.class);
					                String languageLocalfruitName = (((TextView) v).getText()).toString();
					                String fruitName = fruitLocalNameHashMap.get(languageLocalfruitName);
					                intent.putExtra(FruitListActivity.FRUIT_NAME, fruitName);
					                startActivity(intent);
						}
		});
	

	}
}