package com.zumodegrapa.modernartui;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Custom View to implement the onClick handler for all elements
 * 
 * @author Francisco Salvador
 * 
 */
public class ColorBlockView extends View {

	public static final int COLOR_STATE_WHITE = 0;
	public static final int COLOR_STATE_RED = 1;
	public static final int COLOR_STATE_BLUE = 2;
	public static final int COLOR_STATE_YELLOW = 3;

	/**
	 * Log tag.
	 */
	private static final String TAG = "ModernUI-ColorBlock";

	/**
	 * We will use only one random generator across instances
	 */
	private static final Random sRandom = new Random();

	private BlockGroup mBlockGroup;
	private int mColorState = 0;

	/**
	 * We will use this to associate colors with the statuses
	 */
	private static int[] COLORS = { R.color.white_block, R.color.red_block, R.color.blue_block, R.color.yellow_block };

	/**
	 * @see #android.view.View(Context)
	 */
	public ColorBlockView(Context context) {
		this(context, null);

	}

	/**
	 * @see #android.view.View(Context, AttributeSet)
	 */
	public ColorBlockView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Safe casting
		if (context instanceof BlockGroup) {
			mBlockGroup = (BlockGroup) context;
			mBlockGroup.registerBlock(this);
		}

		/*
		 * Custom attributes.
		 * http://developer.android.com/training/custom-views/create-view.html
		 */
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorBlockView, 0, 0);

		try {
			mColorState = a.getInt(R.styleable.ColorBlockView_color_state, 0);
			setBackgroundColor(context.getResources().getColor(COLORS[mColorState]));
		} finally {
			a.recycle();
		}

		setOnClickListener(new ClickListener());
	}

	/**
	 * @return
	 */
	public int getColorState() {
		return mColorState;
	}

	/**
	 * @param mColorStatus
	 */
	public void setColorState(int mColorState) {
		this.mColorState = mColorState;

		// setBackground will handle this.
		// invalidate();
		// requestLayout();
		updateColor();
	}

	public void updateColor() {
		
		Log.d(TAG, "Updating block color");
		
		if (mBlockGroup != null) {

			int color = mBlockGroup.getContext().getResources().getColor(COLORS[mColorState]);

			float transformation = mBlockGroup.getTransformation();

			// Keep white blocks white.
			if (mColorState != COLOR_STATE_WHITE) {
				
				// Transform color
				float[] hsvColor = new float[3];
				Color.colorToHSV(color, hsvColor);
				hsvColor[0] = (hsvColor[0] + (360 *transformation)) % 360;
				color = Color.HSVToColor(hsvColor);
				
			}
			setBackgroundColor(color);
		} else {
			Log.w(TAG, "no BlockGroupContext for this block");
		}
	}

	/**
	 * OnClickListener implementation for this view.
	 */
	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {

			// NOTE: As ClickListener is a private class ColorBlockView.this ==
			// view
			if (mBlockGroup != null) {

				// If the color is not white
				if (ColorBlockView.this.getColorState() != COLOR_STATE_WHITE) {

					Log.i(TAG, "ColorBlock onClick() on a non white block");
					int blockCount = mBlockGroup.getBlocks().size();

					// We get some other random block
					ColorBlockView other = mBlockGroup.getBlocks().get(sRandom.nextInt(blockCount));

					// Random can be a troll and we do want another.
					while (ColorBlockView.this == other) {
						other = mBlockGroup.getBlocks().get(sRandom.nextInt(blockCount));
					}

					// Swap colors
					int otherState = other.getColorState();
					other.setColorState(ColorBlockView.this.getColorState());
					ColorBlockView.this.setColorState(otherState);
				} else {
					Log.i(TAG, "ColorBlock onClick() on a white block");
				}

			} else {
				Log.w(TAG, "This onClickListener is meant to be in a BlockGroup");
			}

		}

	}

	/**
	 * Interface that register this views for sending events. It is expected to
	 * be implemented by the context initializing this View.
	 */
	/**
	 * @author Francisco Salvador
	 * 
	 */
	public static interface BlockGroup {

		/**
		 * Register a ColorBlockView
		 * 
		 * @param view
		 */
		public void registerBlock(ColorBlockView view);

		/**
		 * Get the registered blocks
		 * 
		 * @return
		 */
		public List<ColorBlockView> getBlocks();

		/**
		 * @return a Context associated with this BlockGroup
		 */
		public Context getContext();

		/**
		 * @return the transformation for the colors
		 */
		public float getTransformation();
	}

}
