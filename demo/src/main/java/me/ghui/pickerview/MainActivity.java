package me.ghui.pickerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ghui.library.DisplayManager;
import me.ghui.library.PickerView;

public class MainActivity extends AppCompatActivity implements PickerView.PickerListener {
	private PickerView pickerView;
	private TextView detailPickedTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		detailPickedTv = (TextView) findViewById(R.id.detail);
		test();
	}

	private void test() {
		List<String> items = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			items.add(i + "");
		}

		View view = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
		pickerView = (PickerView) view.findViewById(R.id.picker);
		pickerView.setSelections(items);
		DisplayManager.getInstance().setLayout(view);
		pickerView.setPickChangeListener(this);
	}

	public void display(View view) {
		DisplayManager.getInstance().show(view);
	}

	public void dismiss(View view) {
		DisplayManager.getInstance().dismiss();
	}

	@Override
	public void onPicking(int index) {
		Log.w("pickerView", "onPicking :" + index);
	}

	@Override
	public void onPicked(int index) {
		Log.e("pickerView", "onPicked:" + index);
		detailPickedTv.setText("picked:" + index);
	}
}
