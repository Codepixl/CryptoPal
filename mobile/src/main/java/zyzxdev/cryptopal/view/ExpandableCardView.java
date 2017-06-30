package zyzxdev.cryptopal.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by aaron on 6/29/2017.
 */

public class ExpandableCardView extends CardView{
	public boolean collapsed = true;

	public ExpandableCardView(Context context){
		super(context);
	}

	public ExpandableCardView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public ExpandableCardView(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}

	public int expand() {
		collapsed = false;

		final int initialHeight = getHeight();

		measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int targetHeight = getMeasuredHeight();

		final int distanceToExpand = targetHeight - initialHeight;

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1){
					// Do this after expanded
				}

				getLayoutParams().height = (int) (initialHeight + (distanceToExpand * interpolatedTime));
				requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((long) Math.min(distanceToExpand*2, 500));
		startAnimation(a);

		return Math.min(distanceToExpand*2, 500);
	}

	public int collapse(int collapsedHeight) {
		collapsed = true;

		final int initialHeight = getMeasuredHeight();

		final int distanceToCollapse = (int) (initialHeight - collapsedHeight);

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1){
					// Do this after collapsed
				}

				getLayoutParams().height = (int) (initialHeight - (distanceToCollapse * interpolatedTime));
				requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((long) Math.min(distanceToCollapse*2, 500));
		startAnimation(a);

		return Math.min(distanceToCollapse*2, 500);
	}
}
