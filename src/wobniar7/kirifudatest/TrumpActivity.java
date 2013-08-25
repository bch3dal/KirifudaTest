package wobniar7.kirifudatest;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TrumpActivity extends FragmentActivity
	implements OnClickListener {

	private GestureDetector gesDetect;
	private TrumpView view;

	private Button btnAttack;
	private Button btnPass;
	private Button btnHelp;

	private Resources r;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.game);

		btnAttack = (Button) findViewById(R.id.button1);
		btnPass = (Button) findViewById(R.id.button2);
		btnHelp = (Button) findViewById(R.id.button3);
		btnAttack.setOnClickListener(this);
		btnPass.setOnClickListener(this);
		btnHelp.setOnClickListener(this);
		btnAttack.setEnabled(true);
		btnPass.setEnabled(true);
		btnHelp.setEnabled(true);

		gesDetect = new GestureDetector(this, simpleOnGestureListener);
		view = (TrumpView)this.findViewById(R.id.TrumpView);
	}

	public void onClick(View v) {
		String title, msg;
		r = getResources();
		switch (v.getId()) {
		case R.id.button1:
			if (view.getField().top[3] == Field.STATE_PLAYER) {
				switch(view.battack()) {
				case Field.Err_Atk_tooWeak:
					title = r.getString(R.string.w_atk_tooWeak);
					msg = r.getString(R.string.w_atk_tooWeak_msg);
					break;
				case Field.Err_Atk_numberNotMatch:
					title = r.getString(R.string.w_atk_numberNotMatch);
					msg = r.getString(R.string.w_atk_numberNotMatch_msg);
					break;
				case Field.Err_Atk_badStairs:
					title = r.getString(R.string.w_atk_badStairs);
					msg = r.getString(R.string.w_atk_badStairs_msg);
					break;
				case Field.Err_Atk_irregalMatches:
					title = r.getString(R.string.w_atk_irregalMatches);
					msg = r.getString(R.string.w_atk_irregalMatches_msg);
					break;
				case Field.Err_Atk_usagiSolo:
					title = r.getString(R.string.w_atk_usagiSolo);
					msg = r.getString(R.string.w_atk_usagiSolo_msg);
					break;
				case Field.Err_Atk_hasami:
					title = r.getString(R.string.w_atk_hasami);
					msg = r.getString(R.string.w_atk_hasami_msg);
					break;
				case Field.Err_Atk_noCards:
					title = r.getString(R.string.w_atk_noCards);
					msg = r.getString(R.string.w_atk_noCards_msg);
					showAlert(0, title, msg, view.getField(), YesNoFragment.YNFRG_NOCARDATTACK);
					return;
				default:
					if (view.getField().top[3] == Field.STATE_PAUSE) {
						switch (view.getField().top[4]) {
						case Field.STATE_ANKOKU:
							showAlert(null, 0, r.getString(R.string.i_atk_ankoku), r.getString(R.string.i_atk_ankoku_msg));
							break;
						case Field.STATE_OOIRI:
							showAlert(null, 0, r.getString(R.string.i_atk_ooiri), r.getString(R.string.i_atk_ooiri_msg));
							break;
						case Field.STATE_ASKTENDON:
							showAlert(0, r.getString(R.string.q_atk_tendon), r.getString(R.string.q_atk_tendon_msg), view.getField(), YesNoFragment.YNFRG_ASKTENDON);
							break;
						default:
						}
					}
					return;
				}
				showAlert(null, 0, title, msg);
			}
			//when stopped for gimick
			else if (view.getField().top[3] == Field.STATE_PAUSE) {
				switch (view.getField().top[4]) {
				case Field.STATE_ANKOKU:
					if (!view.getField().Ankoku())
						showAlert(null, 0, r.getString(R.string.w_atk_ankokuE), r.getString(R.string.w_atk_ankokuE_msg));
					break;
				case Field.STATE_OOIRI:
					if (!view.getField().Ooiri())
						showAlert(null, 0, r.getString(R.string.w_atk_ooiriE), r.getString(R.string.w_atk_ooiriE_msg));
					break;
				default:
				}
			}

			break;
		case R.id.button2:
			if (!view.bPass())
				showAlert(null, 0, r.getString(R.string.w_pas_cantPass), r.getString(R.string.w_pas_cantPass_msg));
			Log.d("onClick", "Pass!");
			break;
		case R.id.button3:
			Log.d("onClick", "Help!");
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		gesDetect.onTouchEvent(e);
		return false;
	}

	private final SimpleOnGestureListener simpleOnGestureListener =
			new SimpleOnGestureListener() {
		@Override
		//evoked when doubletapped for the first time
		public boolean onDoubleTap(MotionEvent e) {
			//TODO 提出エリアに吹っ飛ばす処理
			view.whenTouchD((int)e.getX(), (int)e.getY());
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
			return super.onFling(e1, e2, vX, vY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			Card c = view.detect((int)e.getX(), (int)e.getY());
			if (c != null) {
				showAlert(c, 0, null, null);
			}
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
			//TODO カードを追従させる
			return super.onScroll(e1, e2, dX, dY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (view.getField().top[3] == Field.STATE_WIN)
				showAlert(0, r.getString(R.string.q_win), r.getString(R.string.q_win_msg), view.getField(), YesNoFragment.YNFRG_END);
			else if (view.getField().top[3] == Field.STATE_LOSE)
				showAlert(0, r.getString(R.string.q_lose), r.getString(R.string.q_lose_msg), view.getField(), YesNoFragment.YNFRG_END);
			else
				view.whenTouchS((int)e.getX(), (int)e.getY());
			return super.onSingleTapConfirmed(e);
		}

		@Override
		//do nothing
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		//do nothing
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		//do nothing
		public void onShowPress(MotionEvent e) {
			;
		}

		@Override
		//do nothing
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	};

	private void showAlert(Card c, int level, String title, String msg) {
		FragmentManager fm = getSupportFragmentManager();
		ExpFragment af = new ExpFragment();
		af.initialize(c, level, title, msg);
		af.show(fm, "alert_dialog");
	}
	private void showAlert(int level, String title, String msg, Field field, int sig) {
		FragmentManager fm = getSupportFragmentManager();
		YesNoFragment ynf = new YesNoFragment();
		ynf.initialize(level, title, msg, field, sig, this);
		ynf.show(fm, "yesno_dialog");
	}

}

