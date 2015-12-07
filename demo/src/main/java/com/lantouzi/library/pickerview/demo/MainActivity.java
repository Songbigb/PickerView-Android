package com.lantouzi.library.pickerview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lantouzi.library.pickerview.DefaultDisplayManager;
import com.lantouzi.library.pickerview.PickerView;

import java.util.ArrayList;
import java.util.List;

import me.ghui.pickerview.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private DefaultDisplayManager mDisplayManager;
	private PickerView pickerView;
	private PickerView mPickerView0;
	private PickerView mPickerView1;
	private PickerView mPickerView2;
	private TextView mPicker2DetailTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPickerView0 = (PickerView) findViewById(R.id.picker0);
		mPickerView1 = (PickerView) findViewById(R.id.picker1);
		mPickerView2 = (PickerView) findViewById(R.id.picker2);
		mPicker2DetailTv = (TextView) findViewById(R.id.picker2_picked_tv);

		List<String> items = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			items.add(i + "");
		}

		mPickerView0.setSelections(items.subList(0, 12));
		mPickerView1.setSelections(items.subList(0, 32), 0);
		mPickerView2.setSelections(items, 10);
		mPickerView2.setOnPickChangeListener(new PickerView.OnPickerListener() {
			@Override
			public void onPicking(int index) {

			}

			@Override
			public void onPicked(int index) {
				mPicker2DetailTv.setText("picked:" + index);
			}
		});
		pop();
	}

	private void pop() {
		List<String> items = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			items.add(i + "");
		}

		View view = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
		mDisplayManager = DefaultDisplayManager.newInstance();
		mDisplayManager.setLayout(view);
		View okBtn = view.findViewById(R.id.ok_btn);
		pickerView = (PickerView) view.findViewById(R.id.picker);
		pickerView.setSelections(items);
		okBtn.setOnClickListener(this);
	}

	public void popOnClick(View view) {
		mDisplayManager.show(view);
	}

	@Override
	public void onClick(View v) {
		mDisplayManager.dismiss();
	}
}
