package com.sales1crm.ekamant.sales1crm.activities.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.CallbackSearchBackPressed;

public class SearchEditText extends EditText {

	private CallbackSearchBackPressed callbackBack = null;
	private static final String TAG = "EditText";

	public SearchEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		// this.setOnEditorActionListener(this);
		setCustomFont(context);
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// this.setOnEditorActionListener(this);

		setCustomFont(context);
	}

	public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context);
	}

//	private void setCustomFont(Context ctx, AttributeSet attrs) {
//		TypedArray a = ctx.obtainStyledAttributes(attrs,
//				R.styleable.SearchEditText);
//		String customFont = a.getString(R.styleable.SearchEditText_editcustomFont);
//		setCustomFont(ctx, customFont);
//		a.recycle();
//	}

	public boolean setCustomFont(Context ctx) {
		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), "OpenSans-LightItalic.ttf");
		} catch (Exception e) {
			Log.e(TAG, "Could not get typeface: " + e.getMessage());
			return false;
		}

		setTypeface(tf);
		return true;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// this.setCursorVisible(false);
			if (getCallbackBack() != null) {
				callbackBack.setOnBackPressed();
			}
		}
		return super.onKeyPreIme(keyCode, event);

	}

	//
	// @Override
	// public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (actionId == EditorInfo.IME_ACTION_DONE) {
	// Log.v("TAG", "Clicke da machan");
	// hideallKeyboard();
	// setCursorVisible(false);
	// return true;
	// }
	// return false;
	// }
	//
	// private void hideallKeyboard(){
	// InputMethodManager imm = (InputMethodManager)
	// getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	// imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
	// }

	public CallbackSearchBackPressed getCallbackBack() {
		return callbackBack;
	}

	public void setCallbackBack(CallbackSearchBackPressed callbackBack) {
		this.callbackBack = callbackBack;
	}

}
