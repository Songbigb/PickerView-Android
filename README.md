## PickerView for Android
Another custom PickerView for android,which is easy to use and customize.

![](http://77g5pl.com1.z0.glb.clouddn.com/imgpickerview-android.gif)

### Usage

	<me.ghui.library.PickerView android:id="@+id/picker1"
								android:layout_width="wrap_content"
								android:layout_height="wrap_content"
								android:layout_alignBottom="@id/picker2"
								android:layout_toRightOf="@id/picker0"
								android:background="@android:color/holo_orange_light"
								app:pvDisplaySize="3"
								app:pvDividerColor="@android:color/holo_green_dark"
								app:pvTextColor="@android:color/holo_blue_bright"
								app:pvTextPaddingH="20dp"
								app:pvTextPaddingV="10dp"
								app:pvTextSize="35sp"/>

###Event Callback
	
	mPickerView2.setOnPickChangeListener(new PickerView.OnPickerListener() {
			@Override
			public void onPicking(int index) {

			}

			@Override
			public void onPicked(int index) {
				
			}
		});

### Attributes
	
	<?xml version="1.0" encoding="utf-8"?>
	<resources>
		<declare-styleable name="PickerView">
			<attr name="pvTextColor" format="color|reference"/>   // color of the item text ,default is black
			<attr name="pvTextSize" format="dimension|reference"/>  //textsize of the item text ,default is 25dp
			<attr name="pvDisplaySize" format="integer|reference"/>  //display size of the view ,default is 5
			<attr name="pvTextPaddingV" format="dimension|reference"/> // text padding vertically,default is 0
			<attr name="pvTextPaddingH" format="dimension|reference"/> //text padding horizontally,default is 0
			<attr name="pvDividerColor" format="color|reference"/>  // center indicator line color,default is #FFCFCFCF
			<attr name="pvDividerScale" format="fraction"/>  // width of the indicator line,default is 80% of the pickerview
		</declare-styleable>
	</resources>

### Thanks
[WheelView-Android](https://github.com/lantouzi/WheelView-Android)

### End
`PickerView-Android` has been not finished yet,It's just a start version.Lots of ideas waitting to be achieved.  
Also glad to receive feedback from u.

