package wobniar7.kirifudatest;

import java.util.Arrays;
import java.util.Random;

public class Chart {
	Combination combi;
	Random      rand;

	//ギミック以外の色の総数
	private static int COLORNUM = 4;
	//ギミックで、同じ数字の総数（現在たぶんmax3）
	private static int GIMMICKNUM = 1;

	//同時出しの最大数(=myhandの横列数)
	private static final int MAXIMUM = 12;
	//うさぎ札が出せうる位置を示すマーカー
	private static final int UFLAG = 100;

	int[]     bu    = new int[4];
	int[][]   myhand = new int[COLORNUM + GIMMICKNUM + 1][MAXIMUM];
	int[][]   str   = new int[200][20];

	public Chart() {
		combi   = new Combination();
		rand    = new Random();
	}

	//組み合わせ
	//他とは独立なので、もういじらない
	class Combination {
		//constructor
		public Combination() {
		}

		public int[][] append(int[][] arr1, int[][] arr2){
			int[][] res = new int[(arr1.length + arr2.length)][arr1.length];
			System.arraycopy(arr1, 0, res,           0, arr1.length);
			System.arraycopy(arr2, 0, res, arr1.length, arr2.length);
			return res;
		}

		public int[][] merge(int h, int[][] tl) {
			int m = tl.length, i, j;
			if (m != 0) {
				int n = tl[0].length;
				int[][] res= new int[m][n+1];
				for (i = 0; i < m; i++) {
					res[i][0] = h;
					for (j = 1; j < n+1; j++)
						res[i][j] = tl[i][j-1];
				}
				return res;
			}
			else {
				int[][] res = new int[1][1];
				res[0][0] = h;
				return res;
			}
		}
		public int[][] nCr(int[] li,int n, int r) {
			if (n == 0 || r == 0 || li.length == 0) {
				int[][] res = new int[0][0];
				return res;
			}
			else {
				if (n == r) {
					int[][] res = new int[1][1];
					res[0] = li;
					return res;
				}
				else {
					int[] tail = new int[(li.length - 1)];
					System.arraycopy(li, 1, tail, 0, li.length - 1);
					return append(merge(li[0], nCr(tail, (n-1), (r-1))), nCr(tail, (n-1), r));
				}
			}
		}
	}

	//to calculate strategy from card set
	public int[][] strategy(Card[][] pile, Card[] hand, int pHand, Field f){
		int GOSHIKI, rev;
		if (f.tendon)
			GOSHIKI = 7;
		else
			GOSHIKI = 4;

		int i, j, k, l, pStr = 0;
		int jk = 0, c, n, w;
		//initialize
		for (i = 0; i < myhand.length - 1; i++) {
			for (j = 0; j < MAXIMUM; j++)
				myhand[i][j] = -1;
		}
		for (i = 0; i< str.length; i++) {
			for (j = 0; j < str[0].length; j++)
				str[i][j] = -1;
		}
		for (i = 0; i < bu.length; i++)
			bu[i] = -1;//0:black, 1:rabbit, 2:rabbit, 3:gimick5_1

		//detect cards on board
		for (i = 0; i < pile[0].length; i++) {
			//ignore when all cards of this column are used
			if (!pile[0][i].isUsed()) {
				j = pile.length - 1;
				while (pile[j][i].isUsed() && j > 0) j--;
				c = pile[j][i].getColor();
				k = 10 * j + i;
				switch(n = pile[j][i].getNum()) {
				case 0:
					bu[0] = k;
					break;
				case 13:
					jk++;
					bu[jk] = k;
					break;
				default:
					if (f.tendon)
						rev = Math.abs(MAXIMUM - n);
					else
						rev = n - 1;
					myhand[c-1][rev] = k;
					if(c == Card.COLOR_GIMMICK && n == 5) { //Goshikifuda
						for(w = 0; w < COLORNUM; w++)
							if(myhand[w][GOSHIKI] == -1) myhand[w][GOSHIKI]= k;
						bu[3] = k;
					}
					myhand[COLORNUM + GIMMICKNUM][rev]++;
				}
				str[pStr][0]  = n;
				str[pStr][1]  = 1;
				str[pStr][2]  = c;
				str[pStr][3]  = k;
				str[pStr][19] = 0;
				pStr++;
			}
		}
		//detect cards on hand
		for(i = 0; i < pHand; i++) {
			if (!hand[i].isUsed()) {
				c = hand[i].getColor();
				k = 50 + i;
				switch(n = hand[i].getNum()) {
				case 0:
					bu[0] = k;
					break;
				case 13:
					jk++;
					bu[jk] = k;
					break;
				default:
					if (f.tendon)
						rev = Math.abs(MAXIMUM - n);
					else
						rev = n - 1;
					myhand[c-1][rev] = k;
					if(c == Card.COLOR_GIMMICK && n == 5) {
						for(w = 0; w < COLORNUM; w++)
							if(myhand[w][GOSHIKI]==-1) myhand[w][GOSHIKI] = k;
						bu[3] = k;
					}
					myhand[COLORNUM + GIMMICKNUM][rev]++;
				}
				str[pStr][0]  = n;
				str[pStr][1]  = 1;
				str[pStr][2]  = c;
				str[pStr][3]  = k;
				str[pStr][19] = 0;
				pStr++;
			}
		}

		w = MAXIMUM - 1;
		if (jk >= 1){         //usagi-fuda x1
			for (i = 0; i < COLORNUM; i++) {
				for (j = 0; j < w; j++) {
					if (myhand[i][w - j - 1] >= 0 && myhand[i][w - j] == -1)
						myhand[i][w - j] = UFLAG;
				}
				if(myhand[i][w - 1] >= 0 && myhand[i][w] >= 0) {
					j = w - 2;
					while (myhand[i][j] >= 0 && j > 0) j--;
					if(j > 0 || myhand[i][0] < 0)
						myhand[i][j] = UFLAG;
				}
			}
		}
		if (jk == 2){         //usagi-fuda x2
			for (i = 0; i < COLORNUM; i++) {
				for (j = 0; j < w; j++) {
					if (myhand[i][w - j - 1] >= 0 && myhand[i][w - j] == -1)
						myhand[i][w - j] = UFLAG;
				}
				if(myhand[i][w - 1] >= 0 && myhand[i][w] >= 0) {
					j = w - 2;
					while (myhand[i][j] >= 0 && j > 0) j--;
					if(j > 0 || myhand[i][0] < 0)
						myhand[i][j] = UFLAG;
				}
			}
			//うさぎ２枚の手
			str[pStr][0]  = 13;
			str[pStr][1]  = 2;
			str[pStr][2]  = Card.COLOR_DISABLE;
			str[pStr][3]  = bu[1];
			str[pStr][4]  = bu[2];
			str[pStr][19] = 0;
			pStr++;
		}

		for (c = 2; c <= MAXIMUM; c++) {
			for (i = 0; i < MAXIMUM; i++) {
				if (myhand[COLORNUM + GIMMICKNUM][i] != 0 && myhand[COLORNUM + GIMMICKNUM][i] + jk >= c) {
					int[] ls = new int[MAXIMUM];
					w = 0;
					for(j = 0; j < COLORNUM + GIMMICKNUM; j++){
						if (myhand[j][i] > -1 && myhand[j][i] != UFLAG && myhand[j][i] != bu[3]) {
							ls[w] = myhand[j][i];
							w++;
						}
					}
					switch(jk) {
					case 2:
						ls[w] = bu[2];
						w++;
					case 1:
						ls[w] = bu[1];
						w++;
					default:
					}
					if (i == GOSHIKI && bu[3] > -1) {
						ls[w] = bu[3];
						w++;
					}
					if(w >= c){
						int[] res = new int[w];
						System.arraycopy(ls, 0, res, 0, w);
						int[][] pat = combi.nCr(res, w, c);
						for(j=0; j<pat.length; j++) {
							Arrays.sort(pat[j]);
							//うさぎ２枚の手は外す
							if (c != 2 || Arrays.binarySearch(pat[j], bu[1]) < 0 || Arrays.binarySearch(pat[j], bu[2]) < 0) {
								if (f.tendon)
									str[pStr][0] = Math.abs(MAXIMUM - i);
								else
									str[pStr][0] = i + 1;
								str[pStr][1] =   c;
								str[pStr][2] = Card.COLOR_DISABLE;
								System.arraycopy(pat[j], 0, str[pStr], 3, c);
								str[pStr][19] =  0;
								pStr++;
							}
							//3枚、階段もついでに登録
							//11+うさぎ２枚はどうするのか?
							if (c == 3 && Arrays.binarySearch(pat[j], bu[1]) >= 0 && Arrays.binarySearch(pat[j], bu[2]) >= 0) {
								if (pat[j][0] != bu[1] && pat[j][0] != bu[2])
									n = pat[j][0];
								else if (pat[j][1] != bu[1] && pat[j][1] != bu[2])
									n = pat[j][1];
								else
									n = pat[j][2];
								if (n < 50) {				//場札の場合
									str[pStr][2] = pile[n / 10][n % 10].getColor();
									w            = pile[n / 10][n % 10].getNum();
								}
								else {						//手札の場合
									str[pStr][2] = hand[n - 50].getColor();
									w            = hand[n - 50].getNum();
								}
								//端っこ処理(通常)
								if (!f.tendon && w >= MAXIMUM-1)
									str[pStr][0] = MAXIMUM - 2;
								else
									str[pStr][0] = i+1;
								//端っこ処理(てんどん)
								if (f.tendon && w <= MAXIMUM-10)
									str[pStr][0] = MAXIMUM - 9;
								else
									str[pStr][0] = w;
								str[pStr][1] = 3;
								System.arraycopy(pat[j], 0, str[pStr], 3, c);
								str[pStr][19] = 0;
								pStr++;
							}
						}
					}
				}

				//sequences
				if (c >= 3 && (i < MAXIMUM - c + 1)) {
					for (j = 0; j < COLORNUM; j++) {
						k = 0;
						boolean ob5 = false;	//5を含んでおり、使える五色札を使ってない
						int restJK = 0;			//各札組に使われなかったジョーカー
						int or5 = -1;			//本来の5
						for (n = i; n < i + c; n++) {
							if (myhand[j][n] == -1) break;
							else if (myhand[j][n] == UFLAG) k++;
							if (n == GOSHIKI && myhand[j][GOSHIKI] != bu[3] && bu[3] != -1) {
								ob5 = true;
								or5 = myhand[j][GOSHIKI];
							}
							if (k > jk) break;
							if (n == (i + c -1)) {         //reset condition
								restJK = jk - k;
								if (f.tendon)
									str[pStr][0] = Math.abs(MAXIMUM-i);
								else
									str[pStr][0] = i+1;
								str[pStr][1] = c;
								str[pStr][2] = j+1;
								int[] qu = new int[c];
								for (l = 0; l < c; l++) {
									if (myhand[j][n - l] != UFLAG)
										qu[l] = myhand[j][n-l];
									else {
										qu[l] = bu[k];
										k--;
									}
								}

								if (restJK > 0 || ob5) {								//ジョーカーが余っている or 五色札が使えるのに余っている
									int[][] pat;
									int r = qu[c - 1];									//最弱の札の番号を控えておく（うさぎならちぇっくいらない）
									k = 0;
									Arrays.sort(qu);

									if (ob5)
										k = 1;
									int[] newqu = new int[c + restJK + k];				//元の組に、余ったジョーカーを足す（五色札も）
									System.arraycopy(qu, 0, newqu, 0, c);
									if (restJK == 2) {
										newqu[c] = bu[1];
										newqu[c+1] = bu[2];
									}
									else if (restJK == 1) {
										if(Arrays.binarySearch(qu, bu[1]) >= 0)		//1枚目が使われている場合
											newqu[c] = bu[2];
										else
											newqu[c] = bu[1];
									}
									if (ob5)
										newqu[c+restJK] = bu[3];
									pat = combi.nCr(newqu, newqu.length, c);			//combinationで、全パターンを求める。

									for (l = 0; l < pat.length; l++) {				//求めたパターンのうち、
										Arrays.sort(pat[l]);
										if ((Arrays.binarySearch(pat[l], r) >= 0 && r != bu[1] && r != bu[2])) {		//最弱の札を含んでいる

										/*
										if ((Arrays.binarySearch(pat[l], r) >= 0 && r != bu[1] && r != bu[2] && r != bu[3]) ||
												(r == bu[3] && Arrays.binarySearch(pat[l], bu[3]) >= 0)) {					//最弱の札を含んでいる
										*/
											if (!ob5 || (Arrays.binarySearch(pat[l], or5) < 0 || Arrays.binarySearch(pat[l], bu[3]) < 0)) {		//かつ本来の5と五色札がかぶっていない
												if (c != 3 || (Arrays.binarySearch(pat[l], bu[1]) < 0 || Arrays.binarySearch(pat[l], bu[2]) < 0)) {//3枚でうさぎ2枚じゃない
													System.arraycopy(pat[l], 0, str[pStr], 3, c);
													str[pStr][19] = 0;
													pStr++;
													if (f.tendon)
														str[pStr][0] = Math.abs(MAXIMUM-i);
													else
														str[pStr][0] = i+1;
													str[pStr][1] = c;
													str[pStr][2] = j+1;
												}
											}
										}
									}
									str[pStr][0] = -1;
									str[pStr][1] = -1;
									str[pStr][2] = -1;
								}
								else {
									Arrays.sort(qu);
									System.arraycopy(qu, 0, str[pStr], 3, c);
									str[pStr][19] = 0;
									pStr++;
								}
							}
						}
					}
				}
			}
		}
		j = 0;
		//得られた解について、間引きをする
		for (i = 0; i < pStr; i++) {
			if(f.top[0] > -1){  //数字のチェック
				if ((((str[i][0] >= f.top[0] && str[i][0] != 13) || str[i][0] == 0) && f.tendon) || ((str[i][0] <= f.top[0]) && !f.tendon))
					str[i][19] = -1;
				if(f.top[4] == Field.STATE_SOLOUSAGI && str[i][0] == 4 && str[i][1] == 1 && str[i][2] == Card.COLOR_GIMMICK)
					str[i][19] = 0; //うさ単時、望月札を再評価
			}
			if(f.top[1] > 0){  //枚数のチェック
				if(f.top[1] != str[i][1])
					str[i][19] = -1;//枚数が正しくない
				else if((f.top[2] > -1 && str[i][2] == -1)||(f.top[2] == -1 && str[i][2]>-1))
					str[i][19] = -1;//階段エラー
			}
			//ほか、ギミックカードによる縛りを追加
			if (f.top[4] == Field.STATE_HASAMI) {       //8切り
				if (str[i][0] != 9 || str[i][1]!=1 || str[i][2] != Card.COLOR_GIMMICK)
					str[i][19] = -1;//霞札以外は弾く
			}
			if(f.top[0] == 9 &&f.top[1] == 1 && f.top[2] == Card.COLOR_GIMMICK && str[i][0] == 8 && str[i][1] == 1 && str[i][2] != Card.COLOR_GIMMICK && f.tendon)
				str[i][19] = -1; //てんどん時、霞札が出ていたらはさみ札は出せない
			if (str[i][19] != -1) j++;
		}
		int[][] ans = new int[j][20];
		j = 0;
		for (i = 0; i < pStr; i++) {
			if (str[i][19] != -1) {
				System.arraycopy(str[i], 0, ans[j], 0, 20);
				ans[j][19] = 45 + rand.nextInt(11);
				j++;
			}
		}
		return ans;
	}

	//to calculate strategy from card set(during tendon)
/*
	public int[][] strategyR(Card[][] pile, Card[] hand, int pHand, Field f){
		int i, j, k, l, pStr = 0;
		int jk = 0, c, n, w;
		//initialize
		for (i = 0; i < myhand.length - 1; i++) {
			for (j = 0; j < MAXIMUM; j++)
				myhand[i][j] = -1;
		}
		for (i = 0; i< str.length; i++) {
			for (j = 0; j < str[0].length; j++)
				str[i][j] = -1;
		}
		for (i = 0; i < bu.length; i++)
			bu[i] = -1;//0:black, 1:rabbit, 2:rabbit, 3:gimick5_1

		//detect cards on board
		for (i = 0; i < pile[0].length; i++) {
			//ignore when all cards of this column are used
			if (!pile[0][i].isUsed()) {
				j = pile.length - 1;
				while (pile[j][i].isUsed() && j > 0) j--;
				c = pile[j][i].getColor();
				k = 10 * j + i;
				switch(n = pile[j][i].getNum()) {
				case 0:
					bu[0] = k;
					break;
				case 13:
					jk++;
					bu[jk] = k;
					break;
				default:
					myhand[c-1][Math.abs(MAXIMUM - n)] = k;												//TODO ここが変わる
					if(c == Card.COLOR_GIMMICK && n == 5) { //Goshikifuda
						for(w = 0; w < COLORNUM; w++)
							if(myhand[w][7] == -1) myhand[w][7]= k;										//TODO ここが変わる
						bu[3] = k;
					}
					myhand[COLORNUM + GIMMICKNUM][Math.abs(MAXIMUM - n)]++;								//TODO ここが変わる
				}
				str[pStr][0]  = n;
				str[pStr][1]  = 1;
				str[pStr][2]  = c;
				str[pStr][3]  = k;
				str[pStr][19] = 0;
				pStr++;
			}
		}

		//detect cards on hand
		for(i = 0; i < pHand; i++) {
			if (!hand[i].isUsed()) {
				c = hand[i].getColor();
				k = 50 + i;
				switch(n = hand[i].getNum()) {
				case 0:
					bu[0] = k;
					break;
				case 13:
					jk++;
					bu[jk] = k;
					break;
				default:
					myhand[c-1][Math.abs(MAXIMUM - n)] = k;												//TODO ここが変わる
					if(c == Card.COLOR_GIMMICK && n == 5) {
						for(w = 0; w < COLORNUM; w++)
							if(myhand[w][7]==-1) myhand[w][7] = k;										//TODO ここが変わる
						bu[3] = k;
					}
					myhand[COLORNUM + GIMMICKNUM][Math.abs(MAXIMUM - n)]++;								//TODO ここが変わる
				}
				str[pStr][0]  = n;
				str[pStr][1]  = 1;
				str[pStr][2]  = c;
				str[pStr][3]  = k;
				str[pStr][19] = 0;
				pStr++;
			}
		}

		w = MAXIMUM - 1;
		if (jk >= 1){         //usagi-fuda x1
			for (i = 0; i < COLORNUM; i++) {
				for (j = 0; j < w; j++) {
					if (myhand[i][w - j - 1] >= 0 && myhand[i][w - j] == -1)
						myhand[i][w - j] = UFLAG;
				}
				if(myhand[i][w - 1] >= 0 && myhand[i][w] >= 0) {
					j = w - 2;
					while (myhand[i][j] >= 0 && j > 0) j--;
					if(j > 0 || myhand[i][0] < 0)
						myhand[i][j] = UFLAG;
				}
			}
		}
		if (jk == 2){         //usagi-fuda x2
			for (i = 0; i < COLORNUM; i++) {
				for (j = 0; j < w; j++) {
					if (myhand[i][w - j - 1] >= 0 && myhand[i][w - j] == -1)
						myhand[i][w - j] = UFLAG;
				}
				if(myhand[i][w - 1] >= 0 && myhand[i][w] >= 0) {
					j = w - 2;
					while (myhand[i][j] >= 0 && j > 0) j--;
					if(j > 0 || myhand[i][0] < 0)
						myhand[i][j] = UFLAG;
				}
			}
			//うさぎ２枚の手
			str[pStr][0]  = 13;
			str[pStr][1]  = 2;
			str[pStr][2]  = Card.COLOR_DISABLE;
			str[pStr][3]  = bu[1];
			str[pStr][4]  = bu[2];
			str[pStr][19] = 0;
			pStr++;
		}

		for (c = 2; c <= MAXIMUM; c++) {
			for (i = 0; i < MAXIMUM; i++) {
				if (myhand[COLORNUM + GIMMICKNUM][i] != 0 && myhand[COLORNUM + GIMMICKNUM][i] + jk >= c) {
					int[] ls = new int[MAXIMUM];
					w = 0;
					for(j = 0; j < COLORNUM + GIMMICKNUM; j++){
						if (myhand[j][i] > -1 && myhand[j][i] != UFLAG && myhand[j][i] != bu[3]) {
							ls[w] = myhand[j][i];
							w++;
						}
					}
					switch(jk) {
					case 2:
						ls[w] = bu[2];
						w++;
					case 1:
						ls[w] = bu[1];
						w++;
					default:
					}
					if (i == 7 && bu[3] > -1) { //GOSHIKI								//TODO ここが変わる
						ls[w] = bu[3];
						w++;
					}
					if(w >= c){
						int[] res = new int[w];
						System.arraycopy(ls, 0, res, 0, w);
						int[][] pat = combi.nCr(res, w, c);
						for(j=0; j<pat.length; j++) {
							Arrays.sort(pat[j]);
							//うさぎ２枚の手は外す
							if (c != 2 || Arrays.binarySearch(pat[j], bu[1]) < 0 || Arrays.binarySearch(pat[j], bu[2]) < 0) {
								str[pStr][0] = Math.abs(MAXIMUM - i);									//TODO ここが変わる
								str[pStr][1] =   c;
								str[pStr][2] = Card.COLOR_DISABLE;
								System.arraycopy(pat[j], 0, str[pStr], 3, c);
								str[pStr][19] =  0;
								pStr++;
							}
							//3枚、階段もついでに登録
							//11+うさぎ２枚はどうするのか?
							if (c == 3 && Arrays.binarySearch(pat[j], bu[1]) >= 0 && Arrays.binarySearch(pat[j], bu[2]) >= 0) {
								if (pat[j][0] != bu[1] && pat[j][0] != bu[2])
									n = pat[j][0];
								else if (pat[j][1] != bu[1] && pat[j][1] != bu[2])
									n = pat[j][1];
								else
									n = pat[j][2];
								if (n < 50) {				//場札の場合
									str[pStr][2] = pile[n / 10][n % 10].getColor();
									w            = pile[n / 10][n % 10].getNum();
								}
								else {						//手札の場合
									str[pStr][2] = hand[n - 50].getColor();
									w            = hand[n - 50].getNum();
								}
								if (w <= MAXIMUM-10)														//TODO ここが変わる
									str[pStr][0] = MAXIMUM - 9;												//TODO ここが変わる
								else
									str[pStr][0] = w;														//TODO ここが変わる
								str[pStr][1] = 3;
								System.arraycopy(pat[j], 0, str[pStr], 3, c);
								str[pStr][19] = 0;
								pStr++;
							}
						}
					}
				}

				//sequences
				if (c >= 3 && (i < MAXIMUM - c + 1)) {
					for (j = 0; j < COLORNUM; j++) {
						k = 0;
						boolean ob5 = false;	//5を含んでおり、使える五色札を使ってない
						int restJK = 0;			//各札組に使われなかったジョーカー
						int or5 = -1;			//本来の5
						for (n = i; n < i + c; n++) {
							if (myhand[j][n] == -1) break;
							else if (myhand[j][n] == UFLAG) k++;
							if (n == 7 && myhand[j][7] != bu[3] && bu[3] != -1) {							//TODO ここが変わる
								ob5 = true;
								or5 = myhand[j][7];															//TODO ここが変わる
							}
							if (k > jk) break;
							if (n == (i + c -1)) {         //reset condition
								restJK = jk - k;
								str[pStr][0] = Math.abs(12-i);												//TODO ここが変わる
								str[pStr][1] = c;
								str[pStr][2] = j+1;
								int[] qu = new int[c];
								for (l = 0; l < c; l++) {
									if (myhand[j][n - l] != UFLAG)
										qu[l] = myhand[j][n-l];
									else {
										qu[l] = bu[k];
										k--;
									}
								}

								if (restJK > 0 || ob5) {								//ジョーカーが余っている or 五色札が使えるのに余っている
									int[][] pat;
									int r = qu[c - 1];									//最弱の札の番号を控えておく（うさぎならちぇっくいらない）
									k = 0;
									Arrays.sort(qu);

									if (ob5)
										k = 1;
									int[] newqu = new int[c + restJK + k];				//元の組に、余ったジョーカーを足す（五色札も）
									System.arraycopy(qu, 0, newqu, 0, c);
									if (restJK == 2) {
										newqu[c] = bu[1];
										newqu[c+1] = bu[2];
									}
									else if (restJK == 1) {
										if(Arrays.binarySearch(qu, bu[1]) >= 0)		//1枚目が使われている場合
											newqu[c] = bu[2];
										else
											newqu[c] = bu[1];
									}
									if (ob5)
										newqu[c+restJK] = bu[3];
									pat = combi.nCr(newqu, newqu.length, c);			//combinationで、全パターンを求める。

									for (l = 0; l < pat.length; l++) {				//求めたパターンのうち、
										Arrays.sort(pat[l]);
										if ((Arrays.binarySearch(pat[l], r) >= 0 && r != bu[1] && r != bu[2])) {		//最弱の札を含んでいる

										/*
										if ((Arrays.binarySearch(pat[l], r) >= 0 && r != bu[1] && r != bu[2] && r != bu[3]) ||
												(r == bu[3] && Arrays.binarySearch(pat[l], bu[3]) >= 0)) {					//最弱の札を含んでいる
										
											if (!ob5 || (Arrays.binarySearch(pat[l], or5) < 0 || Arrays.binarySearch(pat[l], bu[3]) < 0)) {		//かつ本来の5と五色札がかぶっていない
												if (c != 3 || (Arrays.binarySearch(pat[l], bu[1]) < 0 || Arrays.binarySearch(pat[l], bu[2]) < 0)) {//3枚でうさぎ2枚じゃない
													System.arraycopy(pat[l], 0, str[pStr], 3, c);
													str[pStr][19] = 0;
													pStr++;
													str[pStr][0] = Math.abs(12-i);									//TODO ここが変わる
													str[pStr][1] = c;
													str[pStr][2] = j+1;
												}
											}
										}
									}
									str[pStr][0] = -1;
									str[pStr][1] = -1;
									str[pStr][2] = -1;
								}
								else {
									Arrays.sort(qu);
									System.arraycopy(qu, 0, str[pStr], 3, c);
									str[pStr][19] = 0;
									pStr++;
								}
							}
						}
					}
				}
			}
		}
		j = 0;
		//得られた解について、間引きをする
		for (i = 0; i < pStr; i++) {
			if(f.top[0] > -1){  //数字のチェック
				if ((((str[i][0] >= f.top[0] && str[i][0] != 13) || str[i][0] == 0) && f.tendon) || ((str[i][0] <= f.top[0]) && !f.tendon))
					str[i][19] = -1;				//TODO ここ↑が変わる
				if(f.top[4] == Field.STATE_SOLOUSAGI && str[i][0] == 4 && str[i][1] == 1 && str[i][2] == Card.COLOR_GIMMICK)
					str[i][19] = 0; //うさ単時、望月札を再評価
			}
			if(f.top[1] > 0){  //枚数のチェック
				if(f.top[1] != str[i][1])
					str[i][19] = -1;//枚数が正しくない
				else if((f.top[2] > -1 && str[i][2] == -1)||(f.top[2] == -1 && str[i][2]>-1))
					str[i][19] = -1;//階段エラー
			}
			//ほか、ギミックカードによる縛りを追加
			if (f.top[4] == Field.STATE_HASAMI) {       //8切り
				if (str[i][0] != 9 || str[i][1]!=1 || str[i][2] != Card.COLOR_GIMMICK)
					str[i][19] = -1;//霞札以外は弾く
			}
			if(f.top[0] == 9 &&f.top[1] == 1 && f.top[2] == Card.COLOR_GIMMICK && str[i][0] == 8 && str[i][1] == 1 && str[i][2] != Card.COLOR_GIMMICK && f.tendon)
				str[i][19] = -1; //てんどん時、霞札が出ていたらはさみ札は出せない
			if (str[i][19] != -1) j++;
		}
		int[][] ans = new int[j][20];
		j = 0;
		for (i = 0; i < pStr; i++) {
			if (str[i][19] != -1) {
				System.arraycopy(str[i], 0, ans[j], 0, 20);
				ans[j][19] = 45 + rand.nextInt(11);
				j++;
			}
		}
		return ans;
	}
	*/
}

