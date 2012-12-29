package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.cse.trafficdirection.main.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidListFilesActivity extends ListActivity {

	private String m_path = null;
	private List<String> fileList = new ArrayList<String>();
	private List<String> fileShowList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_layout);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			m_path = extras.getString("PATH");
		}

		File path = null;

		if (m_path != null)
			path = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + m_path);
		else
			path = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());

		ListDir(path);

	}

	void ListDir(File f) {
		File[] files = f.listFiles();
		fileList.clear();
		fileShowList.clear();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith("gpx")) {
					fileList.add(file.getPath());
					String fileName = file.getName();
					String temp = fileName.substring(13, 15) + " : "
							+ fileName.substring(15, 17) + "  "
							+ fileName.subSequence(11, 13) + "/"
							+ fileName.subSequence(9, 11) + "/"
							+ fileName.subSequence(5, 9);
					// fileShowList.add(file.getName());
					fileShowList.add(temp);
				}
			}
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, fileShowList);
		setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView parent, View v, int position,
			long id) {
		SparseBooleanArray chosen = parent.getCheckedItemPositions();

		for (int i = 0; i < chosen.size(); i++) {
			if (chosen.valueAt(i)) {
				Intent it = new Intent();
				// File f = new File(fileList.get(chosen.keyAt(i)));

				// f.delete();
				try {
					FileWriter fstream = new FileWriter(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ MainActivity.PATH_TO_TMP_FILE);
					BufferedWriter out = new BufferedWriter(fstream);

					out.write("1");
					out.newLine();
					out.write(fileList.get(chosen.keyAt(i)));
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// startActivity(it);
				setResult(RESULT_OK, it);
				finish();
				break;
			}
		}
	}
}