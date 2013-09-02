package wobniar7.kirifudatest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TopMenu extends Activity
	implements View.OnClickListener{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);
		Button button1 = (Button) findViewById(R.id.mButton_start);
		Button button2 = (Button) findViewById(R.id.mButton_continue);
		Button button3 = (Button) findViewById(R.id.mButton_config);
		Button button4 = (Button) findViewById(R.id.mButton_exit);

		//main.xmlのon clickプロパティでも設定可能
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
	}

	@Override
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.mButton_start :
			Intent intent = new Intent(this,TrumpActivity.class);
			startActivity(intent);
			break;
		case R.id.mButton_continue :
			//再開
			break;
		case R.id.mButton_config :
			setting();
			//ランキングページ遷移
			break;
		default :
			System.exit(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_menu, menu);
		return true;
	}

	private void setting(){
		Intent intent = new Intent(this, wobniar7.kirifudatest.Config.class);
		startActivityForResult(intent, 0);
	}

}