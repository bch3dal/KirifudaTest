package wobniar7.kirifudatest;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Config extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.config);
		setResult(RESULT_OK,null);
	}
}