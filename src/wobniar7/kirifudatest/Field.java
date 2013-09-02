package wobniar7.kirifudatest;

import java.util.Arrays;
import java.util.Random;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

public class Field {
	//カードの情報を管理するクラス（2人用）

	private Chart chart = new Chart();

	//number of cards
	public static final int CARD_TOTALNUM = 56;

	private static final Bitmap[] cardSet = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetI = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetL = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetR = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetmini = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetminiI = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetminiL = new Bitmap[CARD_TOTALNUM];
	private static final Bitmap[] cardSetminiR = new Bitmap[CARD_TOTALNUM];


	public final Bitmap[] counter = new Bitmap[10];
	public  Bitmap        countbg;

	Card[][] pileP = new Card[4][4];
	Card[][] pileC = new Card[4][4];
	Card[]   handP = new Card[12];
	Card[]   handC = new Card[12];
	Card[]   yabu  = new Card[4];
	Card[]   trash = new Card[CARD_TOTALNUM];
	int pTrash, phandP, phandC;

	//使われたカードを記録しておく
	private int[][] Cmap = new int[6][12];
	//残りカードのバランスについて
	public int[]   power = new int[2];

	public int[] top = new int[5];
	public boolean tendon = false;

	//error codes for "attack"
	public static final int Err_Atk_tooWeak = -1;
	public static final int Err_Atk_numberNotMatch = -2;
	public static final int Err_Atk_badStairs = -3;
	public static final int Err_Atk_irregalMatches = -4;
	public static final int Err_Atk_usagiSolo = -7;
	public static final int Err_Atk_hasami = -8;
	public static final int Err_Atk_noCards = -9;

	//以下、札組情報記述用
	//top[0] - num, top[1] - cards, top[2] - color

	//top[3] - state
	public static final int STATE_PLAYER   = 1;
	public static final int STATE_COMPUTER = 2;
	public static final int STATE_WIN      = 3;
	public static final int STATE_LOSE     = 4;
	public static final int STATE_PAUSE    = 5;

	//top[4] - temporal state
	public static final int STATE_FORCECUT  = -1;
	public static final int STATE_NORMAL    =  0;
	public static final int STATE_HASAMI    =  1;
	public static final int STATE_SOLOUSAGI =  2;
	public static final int STATE_ANKOKU    =  3;
	public static final int STATE_OOIRI     =  4;
	public static final int STATE_ASKTENDON =  5;

	//コンピュータを実行させるためのフラグ
	public boolean waitCP = false;

    Random rnd = new Random();
	public Bitmap back, backR;
	public Bitmap select;


	private final int srcWidth, srcHeight;
	public final int dstWidth, dstHeight;
	private final float windowWidth, windowHeight;
	private final int myHandY;
	public final int[] trashArea = new int[4];

	int plaPileX, plaPileY, comPileX, comPileY;

	//横方向の大きさ（何枚並べるのか）
	private final float cardNumW = 5.7f;
	private final float cardNumH = 5.7f;

	//constructor
	public Field(float w, float h, TypedArray cimg, TypedArray simg) {
		//とりあえず裏面を読んでみて、その大きさを取得
		back = ((BitmapDrawable) cimg.getDrawable(0)).getBitmap();
		backR = ((BitmapDrawable) cimg.getDrawable(0)).getBitmap();
		select = ((BitmapDrawable) cimg.getDrawable(1)).getBitmap();

		srcWidth = back.getWidth();
		srcHeight = back.getHeight();
		windowWidth = w;
		windowHeight = h;

		final float widthScale = w / srcWidth / cardNumW;
		final float heightScale = h / srcHeight / cardNumH;
		final float scale = Math.min(widthScale, heightScale);

		//変換行列（フル、フル逆、手札カウンター）
		Matrix matrix = new Matrix(), matrixR = new Matrix(), matrixC = new Matrix();
		matrix.postScale(scale, scale);
		matrixR.postScale(scale, scale);
		matrixR.postRotate(180);
		matrixC.postScale(scale / 2, scale / 2);

		//カード裏、カード裏逆、選択時カーソル
		back = Bitmap.createBitmap(back, 0, 0, srcWidth, srcHeight, matrix, true);
		backR = Bitmap.createBitmap(backR, 0, 0, srcWidth, srcHeight, matrixR, true);
		select = Bitmap.createBitmap(select, 0, 0, srcWidth, srcHeight, matrix, true);

		dstWidth = back.getWidth();
		dstHeight = back.getHeight();
		myHandY = (int)(windowHeight) - dstHeight - 10;
		plaPileX = dstWidth + 20;
		plaPileY = (int)(windowHeight) - (dstHeight * 3 + 20);
		comPileX = (int)windowWidth - (dstWidth + 10);
		comPileY = dstHeight + 10;
		trashArea[0] = plaPileX;
		trashArea[1] = comPileY + dstHeight + 10;
		trashArea[2] = (dstWidth + 10) * 2;
		trashArea[3] = plaPileY  - comPileY - dstHeight * 2 - 20;

		//コンピュータの手札を数える機構
		countbg = ((BitmapDrawable) simg.getDrawable(10)).getBitmap();
		countbg = Bitmap.createBitmap(countbg, 0, 0, srcWidth, srcHeight, matrix, true);
		for(int i = 0;i < 10; i++){
			counter[i] = ((BitmapDrawable) simg.getDrawable(i)).getBitmap();
			counter[i] = Bitmap.createBitmap(counter[i], 0, 0, counter[i].getWidth(), counter[i].getHeight(), matrixC, true);
		}

		//カードの画像情報だけ先に取得しておく
		for(int i = 0;i < CARD_TOTALNUM; i++){
			cardSet[i] = ((BitmapDrawable) cimg.getDrawable(i+2)).getBitmap();
			cardSet[i] = Bitmap.createBitmap(cardSet[i], 0, 0, srcWidth, srcHeight, matrix, true);
			cardSetI[i] = ((BitmapDrawable) cimg.getDrawable(i+2)).getBitmap();
			cardSetI[i] = Bitmap.createBitmap(cardSetI[i], 0, 0, srcWidth, srcHeight, matrixR, true);
		}

		for(int i = 0; i < trash.length; i++)
			trash[i] = new Card(back, 0, 0, 0, Card.USED);
		init();
	}

	//ゲームを始めるとき
	public void init() {
		int i, r;
		int[] deck = new int[CARD_TOTALNUM];
		for (i = 0; i < CARD_TOTALNUM; i++) {
			r = rnd.nextInt(CARD_TOTALNUM);
			while (deck[r] != 0) {
				if (r < CARD_TOTALNUM - 1)
					r++;
				else
					r = 0;
			}
			deck[r] = i + 1;
		}

		int s;
		for (i = 0; i < 4; i++) {
			//自分と相手の場札を交互に配る
			switch(i){
			case 0:
			case 2:
				s = Card.BLACKBACK;
				break;
			case 1:
				s = Card.CANTUSE;
				break;
			default:
				s = Card.MAYUSE;
			}
			for (int j = 0; j < 4; j++) {
				pileP[i][j] = new Card(cardSet[deck[i*8+j*2] - 1],
						plaPileX + (dstWidth + 10) * j, plaPileY + (int)(dstHeight * i / 3.5), deck[i*8+j*2], s);
				pileC[i][j] = new Card(cardSetI[deck[i*8+j*2+1] - 1],
						comPileX - (dstWidth + 10) * j, comPileY - (int)(dstHeight * i / 3.5), deck[i*8+j*2+1], s);
				//TODO 暗黒札とうさぎ札を記録するところがない
				//Cmap[pileP[i][j].getColor() - 1][pileP[i][j].getNum() - 1] = 10 * i + j;
				//Cmap[pileC[i][j].getColor() - 1][pileC[i][j].getNum() - 1] = 100 + 10 * i + j;
			}
			//藪に配る
			yabu[i] = new Card(cardSet[deck[i+32] - 1],
					10, (int)(windowHeight) - (dstHeight + 10) * (5 - i), deck[i+32], Card.BLACKBACK);
			//TODO 暗黒札とうさぎ札を記録するところがない
			//Cmap[yabu[i].getColor() - 1][yabu[i].getNum() - 1] = 400 + i;
		}

		//TODO 手札を10⇒12にするが、札が少なければ取り合えずくばらない
		for (i = 0; i < 10; i++) {
			//自分と相手の手札を交互に配る
			handP[i] = new Card(cardSet[deck[i*2+36] - 1],
					(int)(dstWidth * 0.35 * i) + 10, myHandY, deck[i*2+36], Card.MAYUSE);
			handC[i] = new Card(cardSetI[deck[i*2+37] - 1], -1, -1, deck[i*2+37], Card.BLACKBACK);
			//TODO 暗黒札とうさぎ札を記録するところがない
			//			Cmap[handP[i].getColor() - 1][handP[i].getNum() - 1] = 50 + i;
			//			Cmap[handC[i].getColor() - 1][handC[i].getNum() - 1] = 150 * i;
		}
		//札組情報初期化
		top[0] = -1;
		top[1] = 0;
		top[2] = -1;
		top[4] = STATE_NORMAL;
		//TODO 先手後手はランダムにもできるようにしたい
		top[3] = STATE_PLAYER;
		waitCP = false;
		tendon = false;
		for(i = 0;i < trash.length; i++)
			trash[i].setState(Card.USED);
		//各種ポインターを初期値へ
		pTrash = 0;
		phandP = 10;
		phandC = 10;
		//残カード情報の初期化
		power[0] = 186;
		power[1] = 186;
	}

	//card copy
	public Card cCopy(Card src) {
		return (new Card(src.getImage(), src.getStartX(), src.getStartY(), src.getId(), src.getState()));
	}
	public Card cCopy(Card src, int s) {
		return (new Card(src.getImage(), src.getStartX(), src.getStartY(), src.getId(), s));
	}

	//refresh cards on hand
	public void hRefresh(int target) {
		int p, q = 0;
		Card[] newHand = new Card[handP.length];
		Card b = new Card(cardSet[0], 0, 0, 1, Card.USED);
		switch (target) {
		case 1:                 //player
			for (p = 0; p < handP.length; p++) {
				if (handP[p] != null) {
					if (handP[p].getState() != Card.USED) {
						newHand[q] = cCopy(handP[p], Card.MAYUSE);
						q++;
					}
				}
			}
			p = q;
			for (q = p; q<handP.length; q++)
				newHand[q] = cCopy(b, Card.USED);
			for (p = 0; p < handP.length; p++) {
				 handP[p] = cCopy(newHand[p]);
				 handP[p].setStart((int)(dstWidth * 0.35 * p) + 10, myHandY);
			}
			break;
		case 2:
			for (p = 0; p < handC.length; p++) {
				if (handC[p] != null) {
					if (handC[p].getState() != Card.USED) {
						newHand[q] = cCopy(handC[p], Card.MAYUSE);
						q++;
					}
				}
			}
			p = q;
			for (q = p; q<handC.length; q++)
				newHand[q] = cCopy(b, Card.USED);
			for (p = 0; p < handC.length; p++) {
				 handC[p] = cCopy(newHand[p]);
				 handC[p].setStart((int)(dstWidth * 0.35 * p) + 10, -1);
			}
			break;
		default:
		}
	}

    //when attack button pressed
	public int Attack() {
		int i, j, k = 0, s;
		int[][] str;
		str = chart.strategy(pileP, handP, phandP, this);
		int[] atklist = new int[16];      //出すカードのリスト（初期化)
		int[] model   = new int[16];      //比較のためのカード列
		int[] beftop  = new int[5];       //一つ前の札組情報
		//store topinfo
		System.arraycopy(top, 0, beftop, 0, 5);

		//search checked card(pile)
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (pileP[i][j].getState() == Card.SELECTED) {
					atklist[k] = 10 * i + j;
					k++;
				}
			}
		}

		//search checked card(hand)
		for (i = 0; i < phandP; i++) {
			if (handP[i].getState() == Card.SELECTED) {
			atklist[k] = 50 + i;
			k++;
			}
		}

		//when no cards
		if (k == 0)
			return Err_Atk_noCards;

		int[] stdatklist = new int[k];
		System.arraycopy(atklist, 0, stdatklist, 0, k);
		Arrays.sort(stdatklist);
		System.arraycopy(stdatklist, 0, atklist, 0, k);
		Arrays.fill(atklist, k, 16, -1);
		for (s = 0; s < str.length; s++) {
			System.arraycopy(str[s], 3, model, 0, 16);
			if (Arrays.equals(atklist, model)) {
				break;
			}
		}
		if (s == str.length)
			return Err_Atk_irregalMatches;
		/*
		if (top[0] > -1) {  //数字のチェック
			if (top[0] >= str[s][0] && top[0] <= 12) {
				return Err_Atk_tooWeak;
			}
			//TODO てんどん時は逆
		}
		*/
		if (top[1] > 0) {  //枚数のチェック
			if (top[1] != str[s][1]) {
				return Err_Atk_numberNotMatch;
			}
			else if ((top[2] > -1 && str[s][2] == -1) || (top[2] == -1 && str[s][2] > -1)) {
				return Err_Atk_badStairs;
			}
		}

		switch (top[4]) {      //ギミック縛り
		case STATE_HASAMI:              //霞待ち
			if (str[s][0] != 9|| str[s][1] != 1 || str[s][2] != 5)
				return Err_Atk_hasami;
			break;
		case STATE_SOLOUSAGI:              //望月待ち
			if (str[s][0] != 4|| str[s][1] != 1|| str[s][2] != 5)
				return Err_Atk_usagiSolo;
			break;
		default:
		    break;
		}
		update(str[s], beftop);
		return 0;
	}


	//for completion of attack
	public void update(int[] str, int[] beftop) {
		System.arraycopy(str, 0, top, 0, 3);//札組情報書き換え
		int i = pTrash, j, p, q;

		switch(top[3]) {
		case STATE_PLAYER:
			for(j = 3; j < top[1] + 3; j++) {
				if (str[j] < 50) {   //場札の場合
					p = str[j] / 10;   //横列
					q = str[j] % 10;   //縦列
					trash[i+j-3] = cCopy(pileP[p][q], Card.CANTUSE);
					pileP[p][q].setState(Card.USED);
					if (p > 0)
						pileP[p-1][q].setState(Card.MAYUSE);
				}
				else {
					p = str[j] - 50;  //手札の場合
					trash[i+j-3] = cCopy(handP[p], Card.CANTUSE);
					handP[p].setState(Card.USED);
					phandP--;
				}
				trash[i+j-3].setStart(trashArea[0] + rnd.nextInt(trashArea[2]), trashArea[1] + rnd.nextInt(trashArea[3]));
			}
			hRefresh(1);
			pTrash = i + j - 3;

			top[4] = STATE_NORMAL;
			if (beftop[4] == STATE_SOLOUSAGI && top[0] == 4 && top[1] == 1 && top[2] == 5)
				top[4] = STATE_FORCECUT;                   //必ず切れるパターン(うさ⇒望月)
			if (top[1] == 1) {                   //単発のギミックカード
				if (top[2] != 5 && top[0] == 8) top[4] = STATE_HASAMI; //8切り
				if (top[0] == 13) top[4] = STATE_SOLOUSAGI;           //うさ単
				if (top[0] == 0) {                      //暗黒
					top[4] = STATE_ANKOKU;
					top[3] = STATE_PAUSE;
					break;
				}
				if (top[2] == 5 && top[0] == 6) {       //大入り
					top[4] = STATE_OOIRI;
					top[3] = STATE_PAUSE;
					break;
				}
			}
			//てんどんの処理
			if (top[1] == 4 && top[2] == -1) {
				for (i = 0; i < 4; i++)
					if (trash[pTrash - 1 - i].getColor() == Card.COLOR_GIMMICK) break;
				if (i == 4) {                           //てんどん起こすか聞く
					top[4] = STATE_ASKTENDON;
					top[3] = STATE_PAUSE;
					break;
				}
			}
			top[3] = STATE_COMPUTER;
			waitCP = true;
			break;

		case STATE_COMPUTER:
			for(j = 3; j < top[1] + 3; j++) {
				if (str[j] < 50) {   //場札の場合
					p = str[j] / 10;   //横列
					q = str[j] % 10;   //縦列
					trash[i+j-3] = cCopy(pileC[p][q], Card.CANTUSE);
					pileC[p][q].setState(Card.USED);
					if (p > 0)
						pileC[p-1][q].setState(Card.MAYUSE);
				}
				else {
					p = str[j] - 50;  //手札の場合
					trash[i+j-3] = cCopy(handC[p], Card.CANTUSE);
					handC[p].setState(Card.USED);
					phandC--;
				}
				trash[i+j-3].setStart(trashArea[0] + rnd.nextInt(trashArea[2]), trashArea[1] + rnd.nextInt(trashArea[3]));
			}
			hRefresh(2);
			pTrash = i + j - 3;
			top[4] = 0;
			if (top[1] == 1) { //ギミックカード
				if (top[2] != 5 && top[0] == 8) top[4] = 1; //8切り
				if (top[0] == 13) top[4] = 2;               //うさ単
				if (top[0] == 0) {                          //暗黒
					i = rnd.nextInt(4);
					handC[phandC] = cCopy(yabu[i], Card.MAYUSE);
					yabu[i].setState(Card.USED);
					phandC++;
					Pass();
					return;
				}
				//TODO 大入りの挙動
				if (top[2] == 5 && top[0] == 6) top[4] = 4;
			}
			top[3] = STATE_PLAYER;
			break;
		default:
		}
		updPower();
		return;
	}

	//update card left
	private void updPower() {
		power[0] = 1;
		power[1] = 1;
	}


	//FINAL for Ooiri-fuda (discard one)
	public boolean Ooiri() {
		int c = 0, i, j, ir = -1, jr = -1, pr = -1;
		for (i = 0; i < pileP.length; i++) {
			for (j = 0; j < pileP[0].length; j++) {
				if (pileP[i][j].getState() == Card.SELECTED) {
					ir = i;
					jr = j;
					c++;
				}
			}
		}
		for (i = 0; i < phandP; i++) {
			if (handP[i].getState() == Card.SELECTED) {
				pr = i;
				c++;
			}
		}
		if (c != 1) return false;

		if (ir >= 0){
			if (pileP[ir][jr].getNum() == 13) return false;
			trash[pTrash] = cCopy(pileP[ir][jr], Card.CANTUSE);
			pileP[ir][jr].setState(Card.USED);
			if (ir > 0)
				pileP[ir-1][jr].setState(Card.MAYUSE);
		}
		else {
			if (handP[pr].getNum() == 13) return false;
			trash[pTrash] = cCopy(handP[pr], Card.CANTUSE);
			handP[pr].setState(Card.USED);
			phandP--;
			hRefresh(1);
		}
		trash[pTrash].setStart(trashArea[0] + rnd.nextInt(trashArea[2]), trashArea[1] + rnd.nextInt(trashArea[3]));

		pTrash++;
		top[3] = STATE_COMPUTER;
		top[4] = STATE_NORMAL;
		waitCP = true;
		return true;
	}

	public boolean Ankoku() {
		int c = 0, ir = -1;
		for (int i = 0; i < yabu.length; i++) {
			if (yabu[i].getState() == Card.SELECTED_B) {
				ir = i;
				c++;
			}
		}
		if (c != 1) return false;
		handP[phandP] = cCopy(yabu[ir], Card.MAYUSE);
		yabu[ir].setState(Card.USED);
		phandP++;
		hRefresh(1);
		Pass();
		return true;
	}

	//for pass
	public boolean Pass() {
		if (top[3] == STATE_PLAYER || top[3] == STATE_COMPUTER || top[3] == STATE_PAUSE) {
			if (top[0] != -1 || top[1] != 0 || top[2] != -1) {
				top[0]=-1;
				top[1]=0;
				top[2]=-1;
				top[4] = STATE_NORMAL;
				if (top[3] == STATE_PLAYER || top[3] == STATE_PAUSE) {
					top[3]=STATE_COMPUTER;
					waitCP = true;
				}
				else
					top[3]=STATE_PLAYER;
				return true;
			}
		}
		return false;
	}

	public void Tendon(boolean en) {
		if (en)
			tendon = !tendon;
		if (top[3] == STATE_PAUSE || top[4] == STATE_ASKTENDON) {
			top[3] = STATE_COMPUTER;
			top[4] = STATE_NORMAL;
			waitCP = true;
		}
		return;
	}

	//FINAL check win or lose
	public void WinLose() {
		boolean pw = true, cw = true;
		for (int i = 0; i < pileP[0].length; i++) {
			pw = pw && (pileP[0][i].isUsed());
			cw = cw && (pileC[0][i].isUsed());
		}
		if (pw && (phandP == 0))
			top[3] = STATE_WIN;
		else if (cw && (phandC == 0))
			top[3] = STATE_LOSE;
		return;
	}
}

