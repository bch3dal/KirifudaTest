package wobniar7.kirifudatest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class TrumpView extends SurfaceView
	implements OnClickListener, SurfaceHolder.Callback,Runnable {

	SurfaceHolder holder;
	Thread thread;

	Field field;
	GestureDetector gesDetect;

	//背景画像の準備
	Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.rasha_g);
	Matrix matrix_bg   = new Matrix();
	final int bgWidth = bg.getWidth();
	final int bgHeight = bg.getHeight();

	Bitmap winBack = BitmapFactory.decodeResource(getResources(), R.drawable.win);
	Bitmap loseBack = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
	Matrix matrix_logo   = new Matrix();
	final int logoWidth = winBack.getWidth();
	final int logoHeight = winBack.getHeight();

	//カードを配列にしたものを受け取る
	TypedArray cardImages = getResources().obtainTypedArray(R.array.cards);
	TypedArray countImages = getResources().obtainTypedArray(R.array.count);


	public TrumpView(Context context) {
		super(context);
		init(context);
	}

	public TrumpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("deprecation")
	public void init(Context context) {
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		final float screenWidth, screenHeight;
		if (Build.VERSION.SDK_INT >= 14) {
			Point size = new Point();
			disp.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
		} else {
			screenWidth = disp.getWidth();
			screenHeight = disp.getHeight();
		}

		//ゲームフィールドを生成
		field = new Field(screenWidth, screenHeight, cardImages, countImages);

		matrix_bg.postScale(screenWidth / bgWidth, screenHeight / bgHeight);
		bg = Bitmap.createBitmap(bg, 0, 0, bgWidth, bgHeight, matrix_bg, true);

		matrix_logo.postScale((float)(screenWidth * 0.7) / logoWidth, (float)(screenHeight * 0.35) / logoHeight);
		winBack = Bitmap.createBitmap(winBack, 0, 0, logoWidth, logoHeight, matrix_logo, true);
		loseBack = Bitmap.createBitmap(loseBack, 0, 0, loseBack.getWidth(), loseBack.getHeight(), matrix_logo, true);

        holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(getWidth(), getHeight());
	}


	public void whenTouchS(int x, int y) {
		int i, j;
		Card card;
		for (i = 3; i >= 0; i--) {
			for (j = 0; j < 4; j++) {
				//pile of player
				card = field.pileP[i][j];
				if (card.isMe(x, y)) {
					if (field.top[3] == Field.STATE_PLAYER || field.top[3] == Field.STATE_PAUSE) {
						if (field.top[4] == Field.STATE_ANKOKU) break;
						switch (card.getState()) {
						case Card.MAYUSE:
							card.setState(Card.SELECTED);
							break;
						case Card.SELECTED:
							card.setState(Card.MAYUSE);
							break;
						case Card.BLACKBACK:
							if(i == 3 || field.pileP[i+1][j].getState() == Card.USED)
								card.setState(Card.MAYUSE);
						default:
						}
					}
					return;
				}
				card = field.pileC[i][j];
				if (card.isMe(x, y)) {
					return;
				}
				card = field.yabu[i];
				if (card.isMe(x, y)) {
					if (field.top[3] == Field.STATE_PAUSE && field.top[4] == Field.STATE_ANKOKU) {
						switch (card.getState()) {
						case Card.BLACKBACK:
							card.setState(Card.SELECTED_B);
							break;
						case Card.SELECTED_B:
							card.setState(Card.BLACKBACK);
							break;
						default:
						}
					}
					return;
				}
			}
		}

		for (i = field.phandP - 1; i >= 0; i--) {
			card = field.handP[i];
			if (card.isMe(x, y)) {
				if (field.top[3] == Field.STATE_PLAYER || field.top[3] == Field.STATE_PAUSE) {
					if (field.top[4] == Field.STATE_ANKOKU) break;
					switch (card.getState()) {
					case Card.MAYUSE:
						card.setState(Card.SELECTED);
						break;
					case Card.SELECTED:
						card.setState(Card.MAYUSE);
					default:
					}
					return;
				}
			}
		}
	}

	public void whenTouchD(int x, int y) {
		int i, j, c, n;
		Card card;
		for (i = 3; i >= 0; i--) {
			for (j = 0; j < 4; j++) {
				card = field.pileP[i][j];
				if (card.isMe(x, y)) {
					c = card.getColor();
					n = card.getNum();
					//card.setState(SELECTED);
					Log.d("whenTouchD", "I'm " + c + ", " + n + ".");
					return;
				}
				card = field.pileC[i][j];
				if (card.isMe(x, y)) {
					return;
				}
				card = field.yabu[i];
				if (card.isMe(x, y)) {
					return;
				}
			}
		}
		for (i = field.phandP - 1; i >= 0; i--) {
			card = field.handP[i];
			if (card.isMe(x, y)) {
				c = card.getColor();
				n = card.getNum();
				//card.setState(SELECTED);
				Log.d("whenTouchD", "I'm " + c + ", " + n + ".");
				return;
			}
		}
	}

	public Card detect(int x, int y) {
		int i, j;
		Card card;
		for (i = 3; i >= 0; i--) {
			for (j = 0; j < 4; j++) {
				card = field.pileP[i][j];
				if (card.isMe(x, y)) {
					return card;
				}
				card = field.pileC[i][j];
				if (card.isMe(x, y)) {
					return card;
				}
				card = field.yabu[i];
				if (card.isMe(x, y)) {
					return card;
				}
			}
		}
		for (i = field.phandP - 1; i >= 0; i--) {
			card = field.handP[i];
			if (card.isMe(x, y)) {
				return card;
			}
		}
		return null;
	}

	//Attack button
	public int battack() {
		return field.Attack();
	}

	public boolean bPass() {
		return field.Pass();
	}

	public void bHelp() {

	}

	public Field getField() {
		return field;
	}



	@Override
	public void run() {
		Canvas canvas;
		double count = 0;
		while(thread != null){
			count = (count+1) % 128;
			if (count == 0) {
				Log.d("topinfo", "" + field.top[0] + " " + field.top[1] + " " + field.top[2] + " " + field.top[3] + " " + field.phandC + " " + field.pTrash);
			}
			field.WinLose();
			if (field.waitCP && field.top[3] == Field.STATE_COMPUTER) {
				field.waitCP = !field.waitCP;
				raisecp();
			}
			canvas = holder.lockCanvas();
			if (canvas != null) {
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				Paint paintA = new Paint();
				paintA.setAntiAlias(true);
				paintA.setAlpha((int)(255 * Math.abs(Math.cos(Math.PI * 2 * count / 128))));
				canvas.drawBitmap(bg,0,0,paint);
				canvas.drawBitmap(field.countbg,10,10,paint);
				int k = field.phandC;

				if (k >= 10) {
					canvas.drawBitmap(field.counter[1],10,50,paint);
					canvas.drawBitmap(field.counter[k % 10],50,50,paint);
				}
				else
					canvas.drawBitmap(field.counter[k % 10],40,50,paint);
				for (int i = 0; i < 4; i++) {
					cardDraw(canvas, field.pileP[i], paint, paintA, false);
					cardDraw(canvas, field.pileC[i], paint, paintA, true);
				}
				cardDraw(canvas, field.yabu, paint, paintA, false);
				cardDraw(canvas, field.handP, paint, paintA, false);
				cardDraw(canvas, field.trash, paint, paintA, false);
				if (field.top[3] == Field.STATE_WIN) {
					canvas.drawBitmap(winBack,100,400,paint);
					canvas.drawBitmap(loseBack,100,50,paint);
				}
				else if (field.top[3] == Field.STATE_LOSE) {
					canvas.drawBitmap(winBack,100,50,paint);
					canvas.drawBitmap(loseBack,100,400,paint);
				}
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	//draw a card
	public void cardDraw(Canvas cv, Card[] c, Paint p, Paint pa, boolean u) {
		for (Card list : c){
			if (list != null) {
				switch (list.getState()) {
				case Card.BLACKBACK:
					if (!u)
						cv.drawBitmap(field.back, list.getStartX(), list.getStartY(),p);
					else
						cv.drawBitmap(field.backR, list.getStartX(), list.getStartY(),p);
					break;
				case Card.MAYUSE:
				case Card.CANTUSE:
					cv.drawBitmap(list.getImage(),list.getStartX(),list.getStartY(),p);
					break;
				case Card.SELECTED:
					cv.drawBitmap(list.getImage(),list.getStartX(),list.getStartY(),p);
					cv.drawBitmap(field.select,list.getStartX(),list.getStartY(),pa);
					break;
				case Card.SELECTED_B:
					cv.drawBitmap(field.back,list.getStartX(),list.getStartY(),p);
					cv.drawBitmap(field.select,list.getStartX(),list.getStartY(),p);
					break;
				default:
				}
			}
		}
	}


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO 自動生成されたメソッド・スタブ

	}

    public void raisecp() {
    	Thread thread = new Thread(new Comth(field));
    	thread.start();
    }

}
