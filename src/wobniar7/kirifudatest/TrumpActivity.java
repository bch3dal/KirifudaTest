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
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TrumpActivity extends FragmentActivity
	implements OnClickListener {

	private GestureDetector gesDetect;
	private TrumpView view;

	private ImageButton btnOK, btnPass, btnInfo;
	private Button btnCancel, btnPause;

	private Resources r;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.game);

		btnOK = (ImageButton) findViewById(R.id.button1);
		btnPass = (ImageButton) findViewById(R.id.button2);
		btnInfo = (ImageButton) findViewById(R.id.button3);
		btnOK.setOnClickListener(this);
		btnPass.setOnClickListener(this);
		btnInfo.setOnClickListener(this);
		btnOK.setEnabled(true);
		btnPass.setEnabled(true);
		btnInfo.setEnabled(true);

		gesDetect = new GestureDetector(this, simpleOnGestureListener);
		view = (TrumpView)this.findViewById(R.id.TrumpView);


		int cbw = view.cPanelWidth(), cbh = (int)(view.cButtonHeight() * cbw / (double)view.cButtonWidth());

		//札組情報ボタンは適当に動かす
		MarginLayoutParams p = new MarginLayoutParams(cbw, cbh);
		p.setMargins(view.cPanelMarginL(), view.cPanelMarginT() - cbh, 0, 0);
		RelativeLayout.LayoutParams paramsR = new RelativeLayout.LayoutParams(p);
		btnInfo.setLayoutParams(paramsR);

		LinearLayout.LayoutParams paramsL = new LinearLayout.LayoutParams(cbw, cbh);
		btnOK.setLayoutParams(paramsL);
		btnPass.setLayoutParams(paramsL);

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
			showInfo(view.getField().top, view.getField().tendon, 0);
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

	private void showInfo(int[] top, boolean tendon, int level) {
		FragmentManager fm = getSupportFragmentManager();
		ExpFragment af = new ExpFragment();
		//top[0] - num, top[1] - cards
		String title = "現在の札組: ";
		String msg = "";

		//返せる数字
		int strong = top[0] + 1;
		String compair = r.getString(R.string.ci_ge);
		String pair = "";
		String tail = r.getString(R.string.ci_tail_set);
		if (strong > 13)
			tail = r.getString(R.string.ci_tail_unable);

		switch(top[3]) {
			case Field.STATE_PLAYER:
				msg = msg.concat("あなたの手番です。\n");
				break;
			case Field.STATE_COMPUTER:
				msg = msg.concat("コンピュータの手番です。\n");
				break;
			default:
		}

		if (tendon) {
			msg = msg.concat("!てんどん中!\n");
			strong = top[0] - 1;
			compair = r.getString(R.string.ci_le);
			if (strong < 1)
				tail = r.getString(R.string.ci_tail_unable);
		}

		if (top[1] >= 2) {
			//同じ数字
			if (top[2] == -1)
				pair = r.getString(R.string.ci_pair);
			//階段
			else if (tail != r.getString(R.string.ci_tail_unable))
				tail = r.getString(R.string.ci_tail_seq);
		}

		switch(top[1]) {
			case 0:
				title = title.concat("なし");
				msg = msg.concat("どんな札組でも出せます。");
				break;
			case 1:
				//暗黒札
				if (top[0] == 0) {
					title = title.concat("暗黒札");
				}
				//うさぎ札
				else if (top[0] == 13) {
					title = title.concat("うさぎ札(単品)");
					msg = msg.concat("望月札(ギミック4)でだけ対抗できます。");
				}
				//そのほか
				else {
					if(top[2] < 5 && top[0] == 8) {
						title = title.concat("はさみ札(8切り)");
					}
					else {
						title = title.concat(top[0] + "のスイチ(1枚)");
						msg = msg.concat(strong + compair + pair + 1 + tail);
					}
				}
				break;
			case 2:
				title = title.concat(top[0] + "のゾロ(2枚)");
				msg = msg.concat(strong + compair + pair + 2 + tail);
				break;
			case 3:
				//ゾロ目の場合
				if (top[2] == -1)
					title = title.concat(top[0] + "のアラシ(3枚)");
				//階段の場合
				else
					title = title.concat(top[0] + "の3枚階段");
				msg = msg.concat(strong + compair + pair + 3 + tail);
				break;
			case 4:
				if (top[2] == -1)
					title = title.concat(top[0] + "のてんどん(4枚)");
				//階段の場合
				else
					title = title.concat(top[0] + "の4枚階段");
				msg = msg.concat(strong + compair + pair + 4 + tail);
				break;
			default:
				if (top[2] == -1)
					title = title.concat(top[0] + "のうなどん(" + top[1] +"枚)");
				//階段の場合
				else
					title = title.concat(top[0] + "の" + top[1] + "枚階段");
				msg = msg.concat(strong + compair + pair + top[1] + tail);
		}
		switch(top[4]) {
			case Field.STATE_HASAMI:
				msg = msg.concat(r.getString(R.string.ci_tstate_hasami));
				break;
			case Field.STATE_SOLOUSAGI:
				msg = msg.concat(r.getString(R.string.ci_tstate_solousagi));
				break;
			case Field.STATE_ANKOKU:
				msg = msg.concat(r.getString(R.string.ci_tstate_ankoku));
				break;
			case Field.STATE_OOIRI:
				msg = msg.concat(r.getString(R.string.ci_tstate_ooiri));
				break;
			default:
		}
		af.initialize(null, level, title, msg);
		af.show(fm, "alert_dialog");
	}

	private void showAlert(int level, String title, String msg, Field field, int sig) {
		FragmentManager fm = getSupportFragmentManager();
		YesNoFragment ynf = new YesNoFragment();
		ynf.initialize(level, title, msg, field, sig, this);
		ynf.show(fm, "yesno_dialog");
	}

}

