package com.zumodegrapa.modernartui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements ColorBlockView.BlockGroup {

	/**
	 * Log tag for this component
	 */
	private static final String TAG = "ModernUI-MainActivity";

	/**
	 * The blocks registered by this context
	 */
	private List<ColorBlockView> blocks = new ArrayList<>();

	/**
	 * This Activity Dialog Builder
	 */
	private Builder mDialogBuilder;

	/**
	 * Last tracked status of the drag control
	 */
	private float mStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Set up the drag interaction.
		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Log.i(TAG, "User dragged on the seekbar " + progress);
				mStatus = progress;
				
				// Update all color blocks
				for (ColorBlockView block : blocks) {
					block.updateColor();
				}
			}
		});

		// Initialize the dialog builder for this activity.
		mDialogBuilder = new AlertDialog.Builder(this).setMessage(getString(R.string.visit_moma_dialog)).setCancelable(false)
				.setNegativeButton(getString(R.string.not_now), null)
				.setPositiveButton(getString(R.string.visit_moma_button), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "Clicked on visit moma.");
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moma_url))));
					}
				});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			// Not Very sure about this, but a warning is telling me to call
			// view#performclick()
			return view.performClick();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu from resource
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_item_more_info) {
			Log.i(TAG, "More Informarion selected");

			mDialogBuilder.create().show();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void registerBlock(ColorBlockView view) {
		// Just add it to the list.
		blocks.add(view);
	}

	@Override
	public List<ColorBlockView> getBlocks() {
		// Just return the list.
		return blocks;
	}

	@Override
	public Context getContext() {
		// MainActivity is both BlockGroup and Context
		return this;
	}
	
	@Override
	public float getTransformation() {
		return mStatus / 100;
	}
}
