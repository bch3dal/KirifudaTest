package wobniar7.kirifudatest;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Card implements Serializable {
	private final Bitmap face;

	private final int color;
	private final int number;
	private final int gimcat;
	private int state;
	private int id;

	private int x;
	private int y;

	private int cardWidth;
	private int cardHeight;

	//descrive card position
	public static final int MAYUSE = 0;
	public static final int CANTUSE = 1;
	public static final int BLACKBACK = 2;
	public static final int SELECTED  = 3;
	public static final int SELECTED_B  = 4;
	public static final int USED = -1;

	//color of card
	public static final int COLOR_DISABLE = -1;
	public static final int COLOR_BLACK = 0;
	public static final int COLOR_RED = 1;
	public static final int COLOR_YELLOW = 2;
	public static final int COLOR_GREEN = 3;
	public static final int COLOR_VIOLET = 4;
	public static final int COLOR_GIMMICK = 5;

	public Card(Bitmap f, int x, int y, int n, int state){
		face = f;
		id = n;
		if (n <= 48) {
			color  = (n - 1) / 12 + 1;
			number = (n - 1) % 12 + 1;
			gimcat = 0;
		} else {
			switch (n) {
			case 49:
				color = COLOR_GIMMICK;
				number = 4;
				gimcat = 1;
				break;
			case 50:
				color = COLOR_GIMMICK;
				number = 5;
				gimcat = 1;
				break;
			case 51:
				color = COLOR_GIMMICK;
				number = 6;
				gimcat = 1;
				break;
			case 52:
				color = COLOR_GIMMICK;
				number = 8;
				gimcat = 1;
				break;
			case 53:
				color = COLOR_GIMMICK;
				number = 9;
				gimcat = 1;
				break;
			case 54:
				color = COLOR_RED;
				number = 13;
				gimcat = 0;
				break;
			case 55:
				color = COLOR_BLACK;
				number = 13;
				gimcat = 0;
				break;
			default:
				color = COLOR_BLACK;
				number = 0;
				gimcat = 0;
			}
		}
		this.x = x;
		this.y = y;
		this.state = state;
		cardWidth = f.getWidth();
		cardHeight = f.getHeight();
	}

	public void setStart(int x, int y) {
		if (x >= 0)
			this.x = x;
		if (y >= 0)
			this.y = y;
	}

	public int getStartX(){
		return x;
	}

	public int getStartY(){
		return y;
	}

	public int getCenterX(){
		return x + cardWidth / 2;
	}

	public int getCenterY(){
		return y + cardHeight / 2;
	}

	public int getEndX(){
		return x + cardWidth;
	}

	public int getEndY(){
		return y + cardHeight;
	}

	public int getColor(){
		return color;
	}

	public int getNum(){
		return number;
	}

	public int getGim(){
		return gimcat;
	}

	public int getId(){
		return id;
	}

	public boolean isMe(int x, int y) {
		return (this.x <= x && this.x + cardWidth >= x &&
				this.y <= y && this.y + cardHeight >= y && state != USED);
	}

	public boolean isUsed() {
		return (state == USED);
	}

	public Bitmap getImage(){
		return face;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

}