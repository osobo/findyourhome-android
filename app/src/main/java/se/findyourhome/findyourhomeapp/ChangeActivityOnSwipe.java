package se.findyourhome.findyourhomeapp;


import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;

public class ChangeActivityOnSwipe implements View.OnTouchListener {

    private static final float DEFAULT_MIN_MOVE_DIST = 75;

    private boolean swipeOngoing;

    private float xStart;
    private float yStart;

    private float minMoveDist;

    private SwipeDirection dir;

    // Intent to start new activity.
    private Intent intent;
    // Context to start new activity from.
    private Context ctx;

    public ChangeActivityOnSwipe(SwipeDirection dir, Context ctx, Intent intent, float minMoveDist) {
        this.swipeOngoing = false;
        this.minMoveDist = minMoveDist;
        this.dir = dir;
        this.ctx = ctx;
        this.intent = intent;
    }

    public ChangeActivityOnSwipe(SwipeDirection dir, Context ctx, Intent intent) {
        this(dir, ctx, intent, DEFAULT_MIN_MOVE_DIST);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xStart = event.getX();
                yStart = event.getY();
                swipeOngoing = true;
                break;

            case MotionEvent.ACTION_UP:
                if(swipeOngoing) {
                    float x = event.getX();
                    float y = event.getY();
                    float xDiff = Math.abs(xStart-x);
                    float yDiff = Math.abs(yStart-y);

                    // Only trigger if the swipe moved far enough horizontally and at least twice
                    // as much horizontally as vertically.
                    if(xDiff >= minMoveDist && xDiff >= 2*yDiff) {
                        onSideSwipe(xStart, x);
                    }
                }
                break;
        }

        return false;
    }

    private void onSideSwipe(float startX, float endX) {
        SwipeDirection dirSwiped = SwipeDirection.calcDir(startX, endX);
        if(dirSwiped == this.dir) {
            ctx.startActivity(intent);
        }
    }

    public static enum SwipeDirection {
        LEFT, RIGHT;

        public static SwipeDirection calcDir(float startX, float endX) {
            if(startX >= endX) {
                return LEFT;
            } else {
                return RIGHT;
            }
        }
    }

}
