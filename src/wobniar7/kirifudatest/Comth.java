package wobniar7.kirifudatest;

import java.util.Random;

//コンピュータ思考クラス
class Comth implements Runnable {

	Chart chart;
	Random  rand;
	private Card c[][]   = new Card[4][4];
	private Card hc[];    //= new Card[10];
	private int handc;
//	private Card yabu[]  = new Card[4];
	private int top[]    = new int[5];
//	private Card trash[] = new Card[56];
	private int str[][];   //戦略

	public Field field;

  // Constructor
	public Comth(Field f) {
		chart      = new Chart();
		rand       = new Random();    //乱数の初期化
		field      = f;
		c          = f.pileC;
		hc         = f.handC;
		handc      = f.phandC;
//		this.yabu  = f.yabu;
		this.top   = f.top;
//		this.trash = f.trash;
	}

	//for computer
	public void run() {
		long    nowTime, drawTime;
		nowTime  = System.currentTimeMillis();
		drawTime = nowTime + 200;
		while(true) {
			nowTime = System.currentTimeMillis();
			if (drawTime < nowTime) break;
		}
		int i, j, s, rou;
		int[] beftop = new int[5];       //一つ前の札組情報
		//store topinfo
		System.arraycopy(top,0,beftop,0,5);

		if (top[3] != Field.STATE_COMPUTER) {
			return;
		}
		if (top[4]==-1) {//パス確定パターン
			field.Pass();
		}
		else {
			str = chart.strategy(c, hc, handc, field);
			rou=0;
			//各手について、評価していく
			for(s = 0; s < str.length; s++) {
				if (str[s][0] <= top[0] + 2) {    //場の数字に近い=高評価
					str[s][19] += 5;
					if (str[s][0] == top[0] + 1)  //場の数字に直近=さらに高評価
						str[s][19] += 10;
				}
				i=0;
				for(j=3;j<19;j++) {
					if(str[s][j]<50&&str[s][j]>-1) { //場札が出せる=高評価
						i+=3;
						if(str[s][j]>=10)     //場札がめくれる=高評価
							i+=7;
					}
					str[s][19] += i;
				}
				if (str[s][1] >= 3) {           //場を切る可能性が高い=高評価
					str[s][19] += 8;
					if (str[s][2] != -1)        //階段は切られにくい=さらに高評価
						str[s][19] += 8;
				}
				if (str[s][0] == 13) {          //うさぎ単品
					//望月がなければ高評価
					str[s][19] -= 10;//望月があれば温存コースで
				}
				if (str[s][0] == 0) {          //暗黒単品
					str[s][19] += 10000;//かなりの確率でコレ
				}
				//望月や霞も考慮=高評価
				//階段、複数=中盤からは高評価
				//弱いカード単独打ち出し=序盤に高評価
				//複数の役にからむカードがある=低評価
				//有利な時=利のあるギミックを単独で利用しようとする
				//不利な時=場をかき乱すギミックを単独で利用しようとする
				//弱い札が1枚残るような手=低評価
				if (str[s][19] <= 0)
					str[s][19] = 1;
				rou += str[s][19];  //抽選の準備
			}
			if (top[0] != -1 || top[1] != 0 || top[2] != -1) {
				rou += 20;              //あえてのパスの選択肢
			}
			i = 1 + rand.nextInt(rou);  //抽選
			for (s = 0; s < str.length && i > 0; s++) {
				if(str[s][19] > 0){
					if(i <= str[s][19]) break;
					else i -= str[s][19];
				}
			}
			if(s == str.length) {
				field.Pass();
				/*
				if (!field.Pass()) {
					field.waitCP = true;
				}
				*/
			}
			else     {    //出せる
				field.update(str[s], beftop);
			}
		}
	}
}
