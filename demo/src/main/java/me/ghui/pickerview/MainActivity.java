package me.ghui.pickerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ghui.library.DefaultDisplayManager;
import me.ghui.library.PickerView;

public class MainActivity extends AppCompatActivity implements PickerView.PickerListener, View.OnClickListener {
	private DefaultDisplayManager mDisplayManager;
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
		mDisplayManager = DefaultDisplayManager.newInstance();
		mDisplayManager.setLayout(view);
		View okBtn = view.findViewById(R.id.ok_btn);
		pickerView = (PickerView) view.findViewById(R.id.picker);
		pickerView.setPickChangeListener(this);
		pickerView.setSelections(items);
		okBtn.setOnClickListener(this);
	}

	public void display(View view) {
		mDisplayManager.show(view);
	}

	public void dismiss(View view) {
		mDisplayManager.dismiss();
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ok_btn) {
			mDisplayManager.dismiss();
		}
	}
}
