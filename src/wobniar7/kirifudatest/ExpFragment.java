package wobniar7.kirifudatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class ExpFragment extends DialogFragment {

	private  Card c = null;
	private int color = 0;
	private int number = 0;
//	private int gimcat = 0;

	private int l;
	private String title;
	private String message;

	public ExpFragment() {
	}

	public void initialize(Card c, int level, String t, String msg) {
		if (c != null) {
			this.c = c;
			color = c.getColor();
			number = c.getNum();
//			gimcat = c.getGim();
			l = 0;
			title = null;
			message = null;
		}
		else {
			l = level;
			title = t;
			message = msg;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Resources r = getResources();
		TypedArray images = r.obtainTypedArray(R.array.cards);

		//カードの説明を出す
		if(c != null) {
			switch (c.getState()) {
			case Card.BLACKBACK:
				builder.setTitle(r.getText(R.string.h_ura))
				.setMessage(r.getText(R.string.h_ura_exp))
				.setIcon(images.getDrawable(0));
				break;
			case Card.MAYUSE:
			case Card.CANTUSE:
			case Card.SELECTED:
				if (number == 13) {
					builder.setTitle(r.getText(R.string.h_gimmic_usa))
					.setMessage(r.getText(R.string.h_gimmic_usa_exp));
					if (color == 1)
						builder.setIcon(images.getDrawable(55));
					else
						builder.setIcon(images.getDrawable(56));
				}
				else if (number == 0) {
					builder.setTitle(r.getText(R.string.h_gimmic_black))
					.setMessage(r.getText(R.string.h_gimmic_black_exp))
					.setIcon(images.getDrawable(0));
				}
				else if (color == 5) {
					switch (number) {
					case 4:
						builder.setTitle(r.getText(R.string.h_gimmic_4))
						.setMessage(r.getText(R.string.h_gimmic_4_exp))
						.setIcon(images.getDrawable(50));
						break;
					case 5:
						builder.setTitle(r.getText(R.string.h_gimmic_5_1))
						.setMessage(r.getText(R.string.h_gimmic_5_1_exp))
						.setIcon(images.getDrawable(51));
						break;
					case 6:
						builder.setTitle(r.getText(R.string.h_gimmic_6_1))
						.setMessage(r.getText(R.string.h_gimmic_6_1_exp))
						.setIcon(images.getDrawable(52));
						break;
					case 7:
						builder.setTitle(r.getText(R.string.h_gimmic_7_1))
						.setMessage(r.getText(R.string.h_gimmic_7_1_exp))
						.setIcon(images.getDrawable(0));
						break;
					case 8:
						builder.setTitle(r.getText(R.string.h_gimmic_8_1))
						.setMessage(r.getText(R.string.h_gimmic_8_1_exp))
						.setIcon(images.getDrawable(53));
						break;
					case 9:
					default:
						builder.setTitle(r.getText(R.string.h_gimmic_9))
						.setMessage(r.getText(R.string.h_gimmic_9_exp))
						.setIcon(images.getDrawable(54));
					}
				}
				else {
					switch (color) {
					case 1:
						builder.setTitle(r.getText(R.string.h_red) + "" + number + r.getText(R.string.h_footer));
						break;
					case 2:
						builder.setTitle(r.getText(R.string.h_yellow) + "" + number + r.getText(R.string.h_footer));
						break;
					case 3:
						builder.setTitle(r.getText(R.string.h_green) + "" + number + r.getText(R.string.h_footer));
						break;
					case 4:
					default:
						builder.setTitle(r.getText(R.string.h_violet) + "" + number + r.getText(R.string.h_footer));
					}
					if (number == 1)
						builder.setMessage(r.getText(R.string.h_n1_exp));
					else if (number == 8)
						builder.setMessage(r.getText(R.string.h_n8_exp));
					else if (number == 12)
						builder.setMessage(r.getText(R.string.h_n12_exp));
					else
						builder.setMessage(r.getText(R.string.h_normal_exp));
					builder.setIcon(images.getDrawable(color * 12 + number - 11));
				}
			default:
			}
			builder.setPositiveButton("了解", null);
		}
		else {
			switch (l) {
			default:
				//builder.setIcon(images.getDrawable(54));
			}
			if (title != null)
				builder.setTitle(title);
			else
				builder.setTitle(R.string.i_empty);
			if (message != null)
				builder.setMessage(message);
			else
				builder.setMessage(R.string.i_msg_empty);
			builder.setPositiveButton("了解", null);
		}
		return builder.create();
	}
}