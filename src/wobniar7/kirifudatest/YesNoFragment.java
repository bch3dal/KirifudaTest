package wobniar7.kirifudatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class YesNoFragment extends DialogFragment {

	private int l;
	private String title;
	private String message;
	private Field field;
	private int sig;
	private TrumpActivity ac;

	public static final int YNFRG_NOCARDATTACK = 1;
	public static final int YNFRG_ASKTENDON    = 2;
	public static final int YNFRG_END          = 3;

	public YesNoFragment() {
	}

	public void initialize(int level, String t, String msg, Field field, int sig, TrumpActivity ac) {
		l = level;
		title = t;
		message = msg;
		this.field = field;
		this.sig = sig;
		this.ac = ac;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


		switch (sig) {
		case YNFRG_NOCARDATTACK:
			builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					field.Pass();
				}
			});
			builder.setNegativeButton(R.string.button_no, null);
			break;
		case YNFRG_ASKTENDON:
			builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					field.Tendon(true);
				}
			});
			builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					field.Tendon(false);
				}
			});
			break;
		case YNFRG_END:
			builder.setPositiveButton(R.string.button_again,  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					field.init();
				}
			});
			builder.setNegativeButton(R.string.button_quit,   new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ac.finish();
				}
			});
			break;
		default:
			builder.setPositiveButton("了解", null);
		}
		return builder.create();
	}
}