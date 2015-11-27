package me.ghui.pickerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.ghui.library.PickerView;

public class MainActivity extends AppCompatActivity {
	private PickerView mPickerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPickerView = (PickerView) findViewById(R.id.picker_view);

		List<String> strings = new ArrayList<>();
		for (int i = 0; i < 157; i++) {
			strings.add(i + "");
		}
		mPickerView.setSelections(strings, 104);
		mPickerView.setPickChangeListener(new PickerView.PickerListhner() {
			@Override
			public void onChanging(int index) {
				Log.e("ghui", "onChanging: " + index);
			}

			@Override
			public void onPicked(int index) {
				Log.e("ghui", "onPicked: " + index);
			}
		});
	}
}
