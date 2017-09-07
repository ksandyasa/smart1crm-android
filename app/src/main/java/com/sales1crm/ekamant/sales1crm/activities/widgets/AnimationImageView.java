package com.sales1crm.ekamant.sales1crm.activities.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sales1crm.ekamant.sales1crm.R;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class AnimationImageView extends ImageView {

    public AnimationImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimationImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.loading_anim);
        final AnimationDrawable frameAnimation = (AnimationDrawable) getBackground();
        post(new Runnable(){
            public void run(){
                frameAnimation.start();
            }
        });
    }

}
