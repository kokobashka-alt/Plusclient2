package wtf.onetap.obf;

import java.util.ArrayList;
import java.util.Vector;

public class b implements n {
   private long a;
   private int[] b;
   private n c;
   private long d;
   private long[] j;
   private static long[] k = new long[64];
   public static int[] e;
   private static ArrayList f;
   private static Vector m;
   private static int i;
   private static Object o;
   private static int h = 51;
   private static int l = 128;
   private static int n = 17;

   public static Object g() {
      return o;
   }

   public static n a(long longValue, long longValue2, Object object) {
      wtf.onetap.obf.i.a(longValue > 0L);
      n nn = d(longValue);
      n nn2 = d(longValue2);
      n nn3 = wtf.onetap.obf.i.a(nn, nn2);
      if (object != null) {
         m.add(object);
      }

      return nn3;
   }

   public static n c(long longValue) {
      int value = (int)a(longValue, 51, 63, e, k);
      if (value < i) {
         return (n)f.get(value);
      }

      if (f.size() % 128 == 0) {
         e = (int[])e.clone();
      }

      b bb = new b(longValue);
      f.add(bb);
      return bb;
   }

   private static n d(long longValue) {
      return new b(longValue);
   }

   public static void a(i ii) {
      i = f.size();
      c();
      ii.d();
   }

   public static void b(i ii) {
      c();
      int[] intArray = e = new int[64];
      intArray[0] = -22;
      intArray[1] = -47;
      intArray[2] = -12;
      intArray[3] = -1;
      intArray[4] = 1;
      intArray[5] = -58;
      intArray[6] = -40;
      intArray[7] = -42;
      intArray[8] = -16;
      intArray[9] = -21;
      intArray[10] = -47;
      intArray[11] = -9;
      intArray[12] = -42;
      intArray[13] = -24;
      intArray[14] = 12;
      intArray[15] = -43;
      intArray[16] = -20;
      intArray[17] = -34;
      intArray[18] = -23;
      intArray[19] = -4;
      intArray[20] = 9;
      intArray[21] = -26;
      intArray[22] = 22;
      intArray[23] = 4;
      intArray[24] = 16;
      intArray[25] = -30;
      intArray[26] = -12;
      intArray[27] = -17;
      intArray[28] = -33;
      intArray[29] = -33;
      intArray[30] = 21;
      intArray[31] = -21;
      intArray[32] = -13;
      intArray[33] = -20;
      intArray[34] = -9;
      intArray[35] = -21;
      intArray[36] = 20;
      intArray[37] = 24;
      intArray[38] = 12;
      intArray[39] = -20;
      intArray[40] = -10;
      intArray[41] = 23;
      intArray[42] = -18;
      intArray[43] = 9;
      intArray[44] = 17;
      intArray[45] = 13;
      intArray[46] = 40;
      intArray[47] = 26;
      intArray[48] = 47;
      intArray[49] = 42;
      intArray[50] = 10;
      intArray[51] = 34;
      intArray[52] = 21;
      intArray[53] = 20;
      intArray[54] = 42;
      intArray[55] = 30;
      intArray[56] = 21;
      intArray[57] = 47;
      intArray[58] = 43;
      intArray[59] = 20;
      intArray[60] = 18;
      intArray[61] = 33;
      intArray[62] = 33;
      intArray[63] = 58;
      ii.c();
   }

   private b(long longValue) {
      this.a = longValue;
      this.b = e;
      this.j = k;
   }

   @Override
   public long a(long longValue) {
      long longValue2 = this.a(8, 55);
      long longValue3 = this.a ^ longValue ^ this.d;
      this.a = longValue3;
      if (this.c != null) {
         this.c.a(longValue);
      }

      return longValue2;
   }

   @Override
   public void b(long longValue) {
      this.d = longValue;
   }

   @Override
   public void a(n nn) {
      if (this != nn) {
         if (this.c == null) {
            this.c = nn;
         } else {
            this.c.a(nn);
         }
      }
   }

   @Override
   public int hashCode() {
      return (int)this.a(8);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return object instanceof b ? this.a(56) == ((b)object).a(56) : false;
      }
   }

   @Override
   public boolean b(n nn) {
      if (this == nn) {
         return true;
      } else {
         return nn instanceof b ? this.a(56, 63) - ((b)nn).a(56, 63) <= 0L : true;
      }
   }

   @Override
   public int[] b_IArr() {
      return this.b;
   }

   private long a(int value) {
      return this.a(0, value - 1);
   }

   private long a(int value, int value2) {
      return a(this.a, value, value2, this.b, this.j);
   }

   private static long a(long longValue, int value, int value2, int[] intArray, long[] longArray) {
      long longValue2 = 0L;
      int value3 = intArray.length;

      for (int i2 = 0; i2 < value3; i2++) {
         long longValue3 = longValue & k[i2];
         long longValue4 = (long)intArray[i2];
         if (longValue3 != 0L) {
            if (longValue4 > 0) {
               longValue3 >>>= longValue4;
            } else if (longValue4 < 0) {
               longValue3 <<= ~longValue4 + 1;
            }

            longValue2 |= longValue3;
         }
      }

      byte byteValue = 64;
      long longValue5 = longValue2;
      long longValue6 = (long)(byteValue - 1 - value2);
      if (longValue6 > 0) {
         longValue5 <<= longValue6;
      }

      long longValue7 = (long)(value + byteValue - 1 - value2);
      if (longValue7 > 0) {
         longValue5 >>>= longValue7;
      }

      return longValue5;
   }

   private static void c() {
      byte byteValue = 0;
      a(0, f.size() - 1, f, new ArrayList(f), byteValue);
   }

   private static void a(int value, int value2, ArrayList list, ArrayList list2, int value3) {
      if (value < value2) {
         int value4 = value + (value2 - value) / 2;
         if (++value3 < 17) {
            a(value, value4, list, list2, value3);
            a(value4 + 1, value2, list, list2, value3);
         }

         a(value, value4, value2, list, list2);
      }
   }

   private static void a(int value, int value2, int value3, ArrayList list, ArrayList list2) {
      int value4 = value;
      int value5 = value2 + 1;

      for (int i2 = value; i2 <= value3; i2++) {
         list2.set(i2, list.get(i2));
      }

      while (value4 <= value2 && value5 <= value3) {
         n nn;
         if (((n)list2.get(value4)).b((n)list2.get(value5))) {
            nn = (n)list2.get(value4++);
         } else {
            nn = (n)list2.get(value5++);
         }

         list.set(value, nn);
         value++;
      }

      while (value4 <= value2) {
         list.set(value, list2.get(value4));
         value++;
         value4++;
      }
   }

   static {
      long longValue = 1L;

      for (int i2 = 0; i2 < 64; i2++) {
         k[i2] = longValue;
         longValue <<= 1;
      }

      o = new Object();
      m = new Vector();
      f = new ArrayList();
      a0();
      i = f.size();
      c();
   }

   private static void a0() {
      e = new int[]{
         -12,
         -15,
         -28,
         -25,
         -49,
         -55,
         -35,
         -32,
         -37,
         -22,
         -9,
         -16,
         12,
         -21,
         -23,
         -20,
         15,
         -35,
         -32,
         9,
         -20,
         -25,
         -35,
         -1,
         1,
         -38,
         -32,
         16,
         25,
         -7,
         28,
         22,
         -15,
         -21,
         21,
         20,
         7,
         23,
         -13,
         32,
         20,
         35,
         -1,
         1,
         -4,
         37,
         25,
         15,
         4,
         -12,
         32,
         13,
         35,
         49,
         21,
         -4,
         -6,
         35,
         32,
         4,
         55,
         12,
         6,
         38
      };
      f.add(new b(3582798413376459337L));
      f.add(new b(-4229271836222821185L));
      f.add(new b(-9063738935011735521L));
      f.add(new b(1653090766937137728L));
      f.add(new b(226616754573577503L));
      f.add(new b(8360471286658484870L));
      f.add(new b(-6620337606433723597L));
      f.add(new b(2394831510978368307L));
      f.add(new b(-4344794150454602753L));
      f.add(new b(-5515845905621876910L));
      f.add(new b(2552870637877645193L));
      f.add(new b(5061869041341489885L));
      f.add(new b(2840087113913081345L));
      f.add(new b(479339010059240041L));
      f.add(new b(6232050453930203857L));
      f.add(new b(-8926146208284529973L));
      f.add(new b(-7677856960549906433L));
      f.add(new b(-8728326523189797705L));
      f.add(new b(8163785475712171919L));
      f.add(new b(4928553551592722205L));
      f.add(new b(4001924448726020428L));
      f.add(new b(2759858718093277011L));
      f.add(new b(4903377448598483857L));
      f.add(new b(-7638673803425334565L));
      f.add(new b(3632498458264014080L));
      f.add(new b(-8969827223940089377L));
      f.add(new b(349250325092655823L));
      f.add(new b(-581820172156860543L));
      f.add(new b(3804216434135395023L));
      f.add(new b(-1753884422545519854L));
      f.add(new b(374539684526225204L));
      f.add(new b(-9204373985258290371L));
      f.add(new b(2409871973786062419L));
      f.add(new b(-8975608354724958053L));
      f.add(new b(194014352686945308L));
      f.add(new b(3851428862452022951L));
      f.add(new b(-3090367644056828787L));
      f.add(new b(-8780067230655703352L));
      f.add(new b(-3385380073508081580L));
      f.add(new b(5889263973523115439L));
      f.add(new b(-6565523891383805545L));
      f.add(new b(-8741703655192024580L));
      f.add(new b(-3056557551904495940L));
      f.add(new b(-1988619988719655231L));
      f.add(new b(6073146222275885260L));
      f.add(new b(-5634779234490149663L));
      f.add(new b(-678338965358540277L));
      f.add(new b(-5189814762074554199L));
      f.add(new b(2362629190131594145L));
      f.add(new b(-8969827223977838113L));
      f.add(new b(8411895669444927022L));
      f.add(new b(-5625479178601770102L));
      f.add(new b(-3201731037569090395L));
      f.add(new b(-5200816008396368672L));
      f.add(new b(8109406763261692496L));
      f.add(new b(-2942441498499630903L));
      f.add(new b(1494972521276997241L));
      f.add(new b(1721791658022383863L));
      f.add(new b(-3204384481206967449L));
      f.add(new b(8320745581443141937L));
      f.add(new b(-1893910771899099359L));
      f.add(new b(-4148510451987229054L));
      f.add(new b(312117425316055989L));
      f.add(new b(2364195619335591394L));
      f.add(new b(8138273596287022515L));
      f.add(new b(354554304821587205L));
      f.add(new b(3688894307359049816L));
      f.add(new b(3638847041552983450L));
      f.add(new b(360217595115689669L));
      f.add(new b(-650088863347577629L));
      f.add(new b(7104052121538248111L));
      f.add(new b(4980158144067594014L));
      f.add(new b(7174786137898480490L));
      f.add(new b(-976599128154642154L));
      f.add(new b(2321386169021024820L));
      f.add(new b(-6541200274498719605L));
      f.add(new b(6096511824884979243L));
      f.add(new b(-5204400058403629634L));
      f.add(new b(-9143056578131622014L));
      f.add(new b(-4248576778507904943L));
      f.add(new b(8320259701442151003L));
      f.add(new b(2504295056979925349L));
      f.add(new b(-5353672166199305082L));
      f.add(new b(7233523478245963759L));
      f.add(new b(-3418088279907758664L));
      f.add(new b(8262432886198328730L));
      f.add(new b(7043761735556754115L));
      f.add(new b(6069004997967337928L));
      f.add(new b(7011247400730526213L));
      f.add(new b(8249456531531191217L));
      f.add(new b(-1149729553686317439L));
      f.add(new b(-6403749042666705838L));
      f.add(new b(7149179609184660981L));
      f.add(new b(5154817311801829880L));
      f.add(new b(1293389414808704476L));
      f.add(new b(548999235725212217L));
      f.add(new b(-2255244176311535179L));
      f.add(new b(3711303761411780119L));
      f.add(new b(4611740931547673732L));
      f.add(new b(-2175444858375303988L));
      f.add(new b(6132206192904733111L));
      f.add(new b(2501466297855801783L));
      f.add(new b(7000642829046022448L));
      f.add(new b(-6414340726025561042L));
      f.add(new b(15955166943143646L));
      f.add(new b(6183153063242612004L));
      f.add(new b(-5290307932089230172L));
      f.add(new b(-4527536124918306283L));
      f.add(new b(-9027815488429683771L));
      f.add(new b(-6496544330315311828L));
      f.add(new b(6007394538551386009L));
      f.add(new b(7298274474924374462L));
      f.add(new b(384146367449715586L));
      f.add(new b(3471126781513670337L));
      f.add(new b(-7950664459035982129L));
      f.add(new b(-2157985444495767078L));
      f.add(new b(-9029055530204056755L));
      f.add(new b(-4374081101521883053L));
      f.add(new b(-9141393712450107168L));
      f.add(new b(2599995702514365015L));
      f.add(new b(8540193914481490901L));
      f.add(new b(3839439071090287172L));
      f.add(new b(5891809736529983660L));
      f.add(new b(6078753988778020035L));
      f.add(new b(-6567214367845355293L));
      f.add(new b(2508245219556778232L));
      f.add(new b(8159897347862466817L));
      f.add(new b(1380139505979529400L));
      f.add(new b(4859546107482620543L));
      f.add(new b(-590360780075601615L));
      f.add(new b(-7753320703964505109L));
      f.add(new b(-7662719455391679189L));
      f.add(new b(2508091390867278619L));
      f.add(new b(-2070753610121081690L));
      f.add(new b(1195928719381025984L));
      f.add(new b(5967578239183208513L));
      f.add(new b(-5190019676948285479L));
      f.add(new b(7203395747294357808L));
      f.add(new b(-7982593204901765301L));
      f.add(new b(-1830114251775580932L));
      f.add(new b(1476154459655935361L));
      f.add(new b(4940042513586390249L));
      f.add(new b(4977420291963244652L));
      f.add(new b(-3238052122797423977L));
      f.add(new b(-4530207927365597619L));
      f.add(new b(-5272040085710593620L));
      f.add(new b(3675026699574565343L));
      f.add(new b(1700168916526219797L));
      f.add(new b(-3344035066751411011L));
      f.add(new b(1586828866937784596L));
      f.add(new b(-4576602439649350464L));
      f.add(new b(-4480699490582971220L));
      f.add(new b(1189567652454885937L));
      f.add(new b(-721589983183016592L));
      f.add(new b(8476549234551844776L));
      f.add(new b(-4076452857982855550L));
      f.add(new b(1359468928422681935L));
      f.add(new b(-8938729905784693378L));
      e = (int[])e.clone();
      f.add(new b(-2995059619862725185L));
      f.add(new b(-4149436521596280821L));
      f.add(new b(2832704263070502688L));
      f.add(new b(6102049545426319649L));
      f.add(new b(2550012361984786724L));
      f.add(new b(-730524705641785098L));
      f.add(new b(8617216827908106052L));
      f.add(new b(-970288650573610509L));
      f.add(new b(4913619856313866036L));
      f.add(new b(8093908723960531304L));
      f.add(new b(2551535272589911237L));
      f.add(new b(-6906138368669681808L));
      f.add(new b(6115345735821708515L));
      f.add(new b(5047937563917262443L));
      f.add(new b(6972946863214703296L));
      f.add(new b(-6696668694954073480L));
      f.add(new b(5152464445532490571L));
      f.add(new b(-1090038602339520516L));
      f.add(new b(-5283727973810195362L));
      f.add(new b(8629086991666863526L));
      f.add(new b(8519273211107009684L));
      f.add(new b(482687797989954994L));
      f.add(new b(-3189995266529118656L));
      f.add(new b(1227937598971211890L));
      f.add(new b(6098942623499993249L));
      f.add(new b(3627152074610867054L));
      f.add(new b(6154028797853499289L));
      f.add(new b(-9151519342890336172L));
      f.add(new b(8452907433409167610L));
      f.add(new b(8140627009899776609L));
      f.add(new b(2750139917605290386L));
      f.add(new b(3832590133468580494L));
      f.add(new b(8287116323202129004L));
      f.add(new b(8128819951404494841L));
      f.add(new b(-914586451179026517L));
      f.add(new b(-6437189423621583515L));
      f.add(new b(5000241484052758605L));
      f.add(new b(-5656253604858936046L));
      f.add(new b(-6474635297315219228L));
      f.add(new b(-6696436605915002101L));
      f.add(new b(3615446482922573742L));
      f.add(new b(-5508365461077983781L));
      f.add(new b(-4090551863189099196L));
      f.add(new b(5019989776587164464L));
      f.add(new b(-1876948813016999636L));
      f.add(new b(5144020885267078226L));
      f.add(new b(450211567112812804L));
      f.add(new b(1396730310078052617L));
      f.add(new b(2639707246139407947L));
      f.add(new b(7491855513339032535L));
      f.add(new b(6929789420396030235L));
      f.add(new b(5162924469031182478L));
      f.add(new b(8112954721433677598L));
      f.add(new b(3719596288475365312L));
      f.add(new b(-6792529343020980991L));
      f.add(new b(1669611580796802768L));
      f.add(new b(438716982235623904L));
      f.add(new b(-8699183705916033821L));
      f.add(new b(2643869364534042586L));
      f.add(new b(-9076644270373188048L));
      f.add(new b(-5420006037059459115L));
      f.add(new b(2505977144791113386L));
      f.add(new b(-4540165418859753877L));
      f.add(new b(-8784299145540133058L));
      f.add(new b(-8976687327487429566L));
      f.add(new b(-2167055082886961923L));
      f.add(new b(-2182473951258968153L));
      f.add(new b(-3290684105915241166L));
      f.add(new b(-4382435340121304939L));
      f.add(new b(5170374595367833328L));
      f.add(new b(2767223416260162445L));
      f.add(new b(-9019188558752352258L));
      f.add(new b(-1979558259986366243L));
      f.add(new b(526029079909490160L));
      f.add(new b(1435696579220501638L));
      f.add(new b(261566971943592273L));
      f.add(new b(-4126876903453052834L));
      f.add(new b(3651870680236444107L));
      f.add(new b(8623847625894564690L));
      f.add(new b(7221004515326373390L));
      f.add(new b(-3393198599491158240L));
      f.add(new b(-2153889733971270052L));
      f.add(new b(6214305555026723209L));
      f.add(new b(8084061066581375594L));
      f.add(new b(-649613782061566298L));
      f.add(new b(-8851643229639926351L));
      f.add(new b(-2103376435159600632L));
      f.add(new b(-3129996635575631478L));
      f.add(new b(3630857212738485903L));
      f.add(new b(-1900792892673637564L));
      f.add(new b(-599856201153504991L));
      f.add(new b(-969791159192358474L));
      f.add(new b(-6626148205147558125L));
      f.add(new b(-6437511081171033912L));
      f.add(new b(-5436711031818404017L));
      f.add(new b(2383883605950286644L));
      f.add(new b(6195583382520149907L));
      f.add(new b(-957140604665306658L));
      f.add(new b(-7657869937788037968L));
      f.add(new b(-4511934023738763570L));
      f.add(new b(2403221758027204898L));
      f.add(new b(-1873921640033982726L));
      f.add(new b(4652258190972594489L));
      f.add(new b(-4459612690322907452L));
      f.add(new b(-8044579746313697771L));
      f.add(new b(353089618593382509L));
      f.add(new b(8310713915444373873L));
      f.add(new b(4993190537995529108L));
      f.add(new b(2702690433275140302L));
      f.add(new b(2863733642802198822L));
      f.add(new b(-6701946979275373730L));
      f.add(new b(-2034517015681854976L));
      f.add(new b(6322281928317344058L));
      f.add(new b(-9017183457625875636L));
      f.add(new b(-4082773484757146107L));
      f.add(new b(1190297782370242334L));
      f.add(new b(8143713770685520349L));
      f.add(new b(-5223907952498624686L));
      f.add(new b(5949958773836856607L));
      f.add(new b(-7900069523326755306L));
      f.add(new b(6066078192523487734L));
      f.add(new b(-9074564665589345191L));
      f.add(new b(-833479310782125624L));
      f.add(new b(2624382671463849464L));
      f.add(new b(-7694596446982619784L));
      f.add(new b(3692919624697195608L));
      f.add(new b(3927142510325149042L));
      f.add(new b(8306260225927253263L));
      f.add(new b(170587888779878359L));
      f.add(new b(-8691865699638549189L));
      f.add(new b(2461803940270179711L));
      f.add(new b(-7878781837193921907L));
      f.add(new b(7002245945163413536L));
      f.add(new b(-5232025099841901974L));
      f.add(new b(-8699001644608318010L));
      f.add(new b(-9216165764186300648L));
      f.add(new b(-5325380435569787968L));
      f.add(new b(-7867840229937742838L));
      f.add(new b(-654834665109473185L));
      f.add(new b(-907014212465133173L));
      f.add(new b(334828717727670979L));
      f.add(new b(-2188851469323732378L));
      f.add(new b(-5252918452884327077L));
      f.add(new b(-5760981537330515777L));
      f.add(new b(8105243134657866314L));
      f.add(new b(2814192251186596089L));
      f.add(new b(5873311265016670737L));
      f.add(new b(-2038875607253116925L));
      f.add(new b(-2214471698715680556L));
      f.add(new b(-683779757641009161L));
      f.add(new b(7288147594969081518L));
      f.add(new b(6037146036415180690L));
      f.add(new b(1315654534316596471L));
      f.add(new b(3671867762177396159L));
      f.add(new b(-7538029306919678010L));
      f.add(new b(2633362230658729459L));
      f.add(new b(-6635152591713685843L));
      f.add(new b(-8753922719636053442L));
      f.add(new b(5079887811191122900L));
      f.add(new b(-2225076945326708865L));
      f.add(new b(3799423064998463735L));
      f.add(new b(4890249868731810445L));
      f.add(new b(5962074989283819093L));
      f.add(new b(-3074256120000117088L));
      f.add(new b(-8881392290976009213L));
      f.add(new b(302478994149251871L));
      f.add(new b(-729298619402692846L));
      f.add(new b(3484090952854293896L));
      f.add(new b(6028218605856439464L));
      f.add(new b(281828723682289407L));
      f.add(new b(3990825003821343626L));
      f.add(new b(-593573983436599806L));
      f.add(new b(-8035262940096049169L));
      f.add(new b(-2953150195078728673L));
      f.add(new b(-5754870218717708430L));
      f.add(new b(2625733222826497024L));
      f.add(new b(3931288423757142022L));
      f.add(new b(8193537872463197985L));
      f.add(new b(-4541070693417622617L));
      f.add(new b(-880106791252290558L));
      f.add(new b(1506165208993160427L));
      f.add(new b(-5703881857617386328L));
      f.add(new b(1213157050128827515L));
      f.add(new b(-7530269461799070262L));
      f.add(new b(-6387821290056295782L));
      f.add(new b(6198675463108423168L));
      f.add(new b(4850976821476883122L));
      f.add(new b(-5198327498139291944L));
      f.add(new b(122690481200862698L));
      f.add(new b(-5543648440844895892L));
      f.add(new b(-7787378697293266469L));
      f.add(new b(12243299638172407L));
      f.add(new b(-9159245976251665086L));
      f.add(new b(-4384294313426228118L));
      f.add(new b(-1925237574191216351L));
      f.add(new b(-7924290029981099339L));
      f.add(new b(2549305649876081179L));
      f.add(new b(2593285138655285208L));
      f.add(new b(-940268427754826596L));
      f.add(new b(538687242643397606L));
      f.add(new b(-5493837229364834640L));
      f.add(new b(-6677364661880699318L));
      f.add(new b(1443567555911964854L));
      f.add(new b(-804538161137027923L));
      f.add(new b(-8061310824480534331L));
      f.add(new b(-4472812682435400686L));
      f.add(new b(-5501238333402487418L));
      f.add(new b(-2070715669866021912L));
      f.add(new b(4636359072931569294L));
      f.add(new b(7199909651090240331L));
      f.add(new b(-3138064689999754437L));
      f.add(new b(6238853869833889819L));
      f.add(new b(2873505132338755252L));
      f.add(new b(3572284580752839939L));
      f.add(new b(8555305346206954725L));
      f.add(new b(4763466112960482837L));
      f.add(new b(-2072215053224427584L));
      f.add(new b(5938589948636922871L));
      f.add(new b(225238377148624635L));
      f.add(new b(523170550071772641L));
      f.add(new b(-8983819908216620939L));
      f.add(new b(370456984295533275L));
      f.add(new b(-5224939602921232598L));
      f.add(new b(8271598150561369079L));
      f.add(new b(-884049381808441777L));
      f.add(new b(5982236219076921164L));
      f.add(new b(-1918679435903889412L));
      e = (int[])e.clone();
      f.add(new b(2443244517212572692L));
      f.add(new b(2316441843708393727L));
      f.add(new b(8537275765477940731L));
      f.add(new b(-5747939806652740227L));
      f.add(new b(-3235907595904192373L));
      f.add(new b(-7544676312592391311L));
      f.add(new b(7282513324148435093L));
      f.add(new b(-1765514661259273011L));
      f.add(new b(8357349309549096490L));
      f.add(new b(-9186848035738126050L));
      f.add(new b(-3027128938350421985L));
      f.add(new b(-6658766272413335717L));
      f.add(new b(7416455857994986863L));
      f.add(new b(-5354880645327535449L));
      f.add(new b(7042231323028615465L));
      f.add(new b(4782895049641750987L));
      f.add(new b(-8684740169253548742L));
      f.add(new b(1295554854987480244L));
      f.add(new b(7221890471910029258L));
      f.add(new b(-9134299255051585923L));
      f.add(new b(533242581843409615L));
      f.add(new b(-4535796300209903104L));
      f.add(new b(7121346944210379196L));
      f.add(new b(-7875957490957741710L));
      f.add(new b(2647660901331407859L));
      f.add(new b(-876445335908357158L));
      f.add(new b(3791118985075266110L));
      f.add(new b(-1954767959481182028L));
      f.add(new b(5982392296853008608L));
      f.add(new b(7456124855750014845L));
      f.add(new b(4788119383867559102L));
      f.add(new b(2408056121719245109L));
      f.add(new b(6971897501952122700L));
      f.add(new b(-7553181840907343526L));
      f.add(new b(-1122545715757014454L));
      f.add(new b(-1848053008656882221L));
      f.add(new b(-1036273975112893173L));
      f.add(new b(8458872995306495305L));
      f.add(new b(2766588394269262381L));
      f.add(new b(-5680161661864807343L));
      f.add(new b(8387556184629422643L));
      f.add(new b(-6610640199004452467L));
      f.add(new b(558856987457364059L));
      f.add(new b(177380503181211857L));
      f.add(new b(-7996888209385097349L));
      f.add(new b(-4586193909145629911L));
      f.add(new b(-2891484813894654795L));
      f.add(new b(-6675148854352885142L));
      f.add(new b(8642179924883112340L));
      f.add(new b(4617066634873857072L));
      f.add(new b(-4588496170579793400L));
      f.add(new b(-4180028785317917095L));
      f.add(new b(-7559296265340359134L));
      f.add(new b(-6749231946729314485L));
      f.add(new b(-697834034891080686L));
      f.add(new b(-4167309337492752141L));
      f.add(new b(-4589821714608358488L));
      f.add(new b(-6351220157232984125L));
      f.add(new b(4799649175212631712L));
      f.add(new b(-9123353676784383060L));
      f.add(new b(8152791542055038822L));
      f.add(new b(-7804274684946382122L));
      f.add(new b(-9097875584013817108L));
      f.add(new b(-3250889479397631905L));
      f.add(new b(-3195754572153596075L));
      f.add(new b(-5343668732745457578L));
      f.add(new b(6182548766053972794L));
      f.add(new b(-3171550658892337127L));
      f.add(new b(-6848894141167728328L));
      f.add(new b(-8001659436383340185L));
      f.add(new b(-7986271770110149442L));
      f.add(new b(-4386889457658221511L));
      f.add(new b(-4264523070933039727L));
      f.add(new b(-4059138941999074462L));
      f.add(new b(-6549807167261412043L));
      f.add(new b(455844088195791117L));
      f.add(new b(-9016613126657455911L));
      f.add(new b(-5535874609330914234L));
      f.add(new b(2853714367256482997L));
      f.add(new b(-4319554411223940719L));
      f.add(new b(4825400752921387331L));
      f.add(new b(-8798982648839581318L));
      f.add(new b(-3429371062675330361L));
      f.add(new b(8251246871007713791L));
      f.add(new b(-6900107009448423142L));
      f.add(new b(-6834101406046491427L));
      f.add(new b(-4471030041943286964L));
      f.add(new b(4960244469272413182L));
      f.add(new b(7454387139030346229L));
      f.add(new b(7007712724443458828L));
      f.add(new b(7064809479850429722L));
      f.add(new b(4791656578028102049L));
      f.add(new b(-7505905088127922513L));
      f.add(new b(-1755711361511098560L));
      f.add(new b(-5697664581072224285L));
      f.add(new b(-4531803533889768429L));
      f.add(new b(-3335224127033217191L));
      f.add(new b(-3112811752949229879L));
      f.add(new b(7452934526200025874L));
      f.add(new b(-7729212008855317182L));
      f.add(new b(1246606436046724114L));
      f.add(new b(-5741618017481075242L));
      f.add(new b(8303885155012352246L));
      f.add(new b(1648746147756534429L));
      f.add(new b(-1812238655082357582L));
      f.add(new b(7410400814988037925L));
      f.add(new b(-7954698650613758615L));
      f.add(new b(-9084556964973609494L));
      f.add(new b(-5293197489766041389L));
      f.add(new b(-8029324926439334550L));
      f.add(new b(-1070177370255498327L));
      f.add(new b(3794422940714175944L));
      f.add(new b(2684352653967941672L));
      f.add(new b(-1748574085934659159L));
      f.add(new b(5007345509865076488L));
      f.add(new b(-867052745504543323L));
      f.add(new b(6018231681365459906L));
      f.add(new b(5859008380267414526L));
      f.add(new b(5166238740476871396L));
      f.add(new b(3774405498241766940L));
      f.add(new b(-4332367071158943937L));
      f.add(new b(6325625307007228373L));
      f.add(new b(323792932420803475L));
      f.add(new b(6307636269137757777L));
      f.add(new b(1512548284794221898L));
      f.add(new b(7484232364387756035L));
      f.add(new b(-5227191637225073182L));
      f.add(new b(-4317473822300578338L));
      f.add(new b(7478963739733865903L));
      f.add(new b(5992620216353295169L));
      f.add(new b(3972268683110036516L));
      f.add(new b(-7606225531280879194L));
      f.add(new b(-966047222157007418L));
      f.add(new b(2663676149390899650L));
      f.add(new b(-9169878261651541693L));
      f.add(new b(-6562661832420023232L));
      f.add(new b(-6909894755933879157L));
      f.add(new b(4648725054034821526L));
      f.add(new b(6090043792417986941L));
      f.add(new b(1241578970402569639L));
      f.add(new b(8292617171992217156L));
      f.add(new b(-1964224711499876498L));
      f.add(new b(-6850200167959973930L));
      f.add(new b(-7629774356751187471L));
      f.add(new b(2691402081183923796L));
      f.add(new b(-7879549714983068541L));
      f.add(new b(-4081755819141782976L));
      f.add(new b(-4344794150559460385L));
      f.add(new b(-5536555537964556422L));
      f.add(new b(-8831362855943376910L));
      f.add(new b(-8791826284337203935L));
      f.add(new b(-7506802667335488486L));
      f.add(new b(1255901535265139751L));
      f.add(new b(5863619200216767667L));
      f.add(new b(-3262825790187396189L));
      f.add(new b(8286693056733962937L));
      f.add(new b(1487789609682073674L));
      f.add(new b(3486149450905700125L));
      f.add(new b(-6473752462066718776L));
      f.add(new b(-9121171794641621978L));
      f.add(new b(-914955494097029245L));
      f.add(new b(2534734775993966644L));
      f.add(new b(441465110426522206L));
      f.add(new b(-7720712424897210394L));
      f.add(new b(-6908806536167147340L));
      f.add(new b(7388743482555896606L));
      f.add(new b(7099157202270892407L));
      f.add(new b(1632877902564507970L));
      e = (int[])e.clone();
      f.add(new b(-4149016744506350514L));
      f.add(new b(-838693302075664227L));
      f.add(new b(3534643993537888852L));
      f.add(new b(-2939861408154637074L));
      f.add(new b(-7652221817369475578L));
      f.add(new b(7015649012957722387L));
      f.add(new b(2653229413140169112L));
      f.add(new b(431764082279641199L));
      f.add(new b(6933816426855853404L));
      f.add(new b(-979718283385095556L));
      f.add(new b(5999922811270379246L));
      f.add(new b(5106319688033724934L));
      f.add(new b(4806926060471823355L));
      f.add(new b(-8826921835126538869L));
      f.add(new b(2480881661184583394L));
      f.add(new b(1470114987086523463L));
      f.add(new b(-931986112398529218L));
      f.add(new b(5779694691222415691L));
      f.add(new b(5840407067205534261L));
      f.add(new b(-2242693302912914719L));
      f.add(new b(-645728824203092608L));
      f.add(new b(-7782776686979930412L));
      f.add(new b(3788061370847108134L));
      f.add(new b(-9034400445735878488L));
      f.add(new b(4887738989927279433L));
      f.add(new b(-6870618591846182945L));
      f.add(new b(-1104504440523492239L));
      f.add(new b(7254140557108284450L));
      f.add(new b(7135816989164078714L));
      f.add(new b(3889082577458611881L));
      f.add(new b(-597310709815662010L));
      f.add(new b(-1782225813103622233L));
      f.add(new b(-5622678865843268231L));
      f.add(new b(1388181039864375234L));
      f.add(new b(1645433740468836229L));
      f.add(new b(2606414823419649426L));
      f.add(new b(2457036889793306959L));
      f.add(new b(-2047758749535044811L));
      f.add(new b(-2114573952956595289L));
      f.add(new b(-8987106636229507809L));
      f.add(new b(-3453332515884020395L));
      f.add(new b(-8873905899012102926L));
      f.add(new b(7447063101519828940L));
      f.add(new b(-7810855768185757199L));
      f.add(new b(1725949839365674745L));
      f.add(new b(-7572140645230152983L));
      f.add(new b(7105988976093418925L));
      f.add(new b(-6843460709728145050L));
      f.add(new b(2769539404307219335L));
      f.add(new b(-4493818684150759139L));
      f.add(new b(531487528593056222L));
      f.add(new b(-3206155569055463318L));
      f.add(new b(5008669685267642187L));
      f.add(new b(143526317778805741L));
      f.add(new b(2415572157036281975L));
      f.add(new b(5909720462021679481L));
      f.add(new b(2874560354146023604L));
      f.add(new b(4806466004169279873L));
      f.add(new b(-3037372091187908483L));
      f.add(new b(374554381911381812L));
      f.add(new b(-6433464617308155370L));
      f.add(new b(5994929780360683579L));
      f.add(new b(-1018372300313466244L));
      f.add(new b(4845889341845064961L));
      f.add(new b(4908241034488932379L));
      f.add(new b(1370155106893835727L));
      f.add(new b(373600891908619087L));
      f.add(new b(7234932808195394198L));
      f.add(new b(-5301819867702308946L));
      f.add(new b(-4042508378196468945L));
      f.add(new b(-7920624643275495245L));
      f.add(new b(8546152035661991094L));
      f.add(new b(-5663639875666571455L));
      f.add(new b(7167770737133524015L));
      f.add(new b(8538494645252445903L));
      f.add(new b(-4479019116035755949L));
      f.add(new b(528233496320433995L));
      f.add(new b(559454846675724954L));
      f.add(new b(7446798902607005185L));
      f.add(new b(2545502255684888451L));
      f.add(new b(437090599779718007L));
      f.add(new b(540071784913051373L));
      f.add(new b(1434709518198005826L));
      f.add(new b(-8922636376803834202L));
      f.add(new b(-758036734608727599L));
      f.add(new b(5783925133267406424L));
      f.add(new b(6302993594996182844L));
      f.add(new b(-4320972384544728259L));
      f.add(new b(-3152104711950550153L));
      f.add(new b(-6471917125593062529L));
      f.add(new b(1340735026493255249L));
      f.add(new b(-3069502206373953979L));
      f.add(new b(3908472119570182809L));
      f.add(new b(-8918695724632705949L));
      f.add(new b(-7653008592952087510L));
      f.add(new b(-5622678666859777505L));
      f.add(new b(-2179889309113835026L));
      f.add(new b(-6766707086142034004L));
      f.add(new b(-791644659098266947L));
      f.add(new b(-5569584841996400201L));
      f.add(new b(1659436195374691334L));
      f.add(new b(6205294921816258076L));
      f.add(new b(4980453856107311302L));
      f.add(new b(328829217860985860L));
      f.add(new b(2481298344894566121L));
      f.add(new b(-1731434320379564011L));
      f.add(new b(-3102226446454236082L));
      f.add(new b(6001531074433776433L));
      f.add(new b(7154142787914862460L));
      f.add(new b(-4557245943229339524L));
      f.add(new b(2377934876263663244L));
      f.add(new b(8559685607452176844L));
      f.add(new b(-2247848847337211620L));
      f.add(new b(-7782095531207503096L));
      f.add(new b(-6530260842201385219L));
      f.add(new b(7349144988378543359L));
      f.add(new b(6013821412492805691L));
      f.add(new b(7145817519063827526L));
      f.add(new b(8580880284208906050L));
      f.add(new b(-5689441366905798760L));
      f.add(new b(-3321950321390336310L));
      f.add(new b(2571178188538703047L));
      f.add(new b(2433092655413238133L));
      f.add(new b(-2174559825432553249L));
      f.add(new b(4623881689883273604L));
      f.add(new b(5887632836105847793L));
      f.add(new b(-789166660816067982L));
      f.add(new b(-4604194279025050887L));
      f.add(new b(-4315828353437590636L));
      f.add(new b(-3085507173232335060L));
      f.add(new b(2433850031989099668L));
      f.add(new b(165097373693568917L));
      f.add(new b(8556735651575336691L));
      f.add(new b(-6780631454960137801L));
      f.add(new b(1435864424287816839L));
      f.add(new b(5939215290560626809L));
      f.add(new b(2769881422125096321L));
      f.add(new b(-4355412564938838078L));
      f.add(new b(-7540846956045262544L));
      f.add(new b(8114620230781290968L));
      f.add(new b(4760109184450310633L));
      f.add(new b(-6700870501937290064L));
      f.add(new b(-7645936498703524237L));
      f.add(new b(3625676132446529088L));
      f.add(new b(1692453188077569639L));
      f.add(new b(-3048903108912642906L));
      f.add(new b(6015184561411652114L));
      f.add(new b(1669058532112146710L));
      f.add(new b(201710923811144331L));
      f.add(new b(-7583610121266965390L));
      f.add(new b(8233730289644011976L));
      f.add(new b(3958020102766781547L));
      f.add(new b(-7857865625794345866L));
      f.add(new b(-4195614693143589794L));
      f.add(new b(-3092933378412181022L));
      f.add(new b(4014258572194468251L));
      f.add(new b(2374758312188928833L));
      f.add(new b(47259619400371079L));
      f.add(new b(3955366222600771822L));
      f.add(new b(6251538057771040267L));
      f.add(new b(7035890772323251069L));
      f.add(new b(8494092631735049580L));
      f.add(new b(-9018867957350530470L));
      f.add(new b(3503397358460414318L));
      f.add(new b(-4384855791807365501L));
      f.add(new b(-6430103338268521987L));
      f.add(new b(-3046889911502374707L));
      f.add(new b(-2277896542388953027L));
      f.add(new b(4757652253270781775L));
      f.add(new b(3558219730587799390L));
      e = (int[])e.clone();
      f.add(new b(-4496788484562153763L));
      f.add(new b(-5227747911922973175L));
      f.add(new b(1727688237018342359L));
      f.add(new b(-6828792696402263851L));
      f.add(new b(5897934612688897936L));
      f.add(new b(1371772241952355965L));
      f.add(new b(7379270019485398708L));
      f.add(new b(-9213498587045786446L));
      f.add(new b(3775307216143755827L));
      f.add(new b(-3302851165101866267L));
      f.add(new b(3888544446761036394L));
      f.add(new b(-1082372843669776884L));
      f.add(new b(-8664984632598316014L));
      f.add(new b(2476923337073332531L));
      f.add(new b(3926073202162691158L));
      f.add(new b(-6881501931885883077L));
      f.add(new b(5846239982026967666L));
      f.add(new b(-8749779096119324607L));
      f.add(new b(7092345503200302867L));
      f.add(new b(-1948038335255580868L));
      f.add(new b(-5625796371310669742L));
      f.add(new b(7193924841294708948L));
      f.add(new b(2793825758478122338L));
      f.add(new b(6169884117104275176L));
      f.add(new b(-4306602668959424462L));
      f.add(new b(-8038134260080033611L));
      f.add(new b(-9179101526377739725L));
      f.add(new b(-8818628227574591081L));
      f.add(new b(-6447244567351667658L));
      f.add(new b(-3451491897812686313L));
      f.add(new b(-5544020031623853877L));
      f.add(new b(-8724341406481917343L));
      f.add(new b(-3028200177738433300L));
      f.add(new b(5948418256482777047L));
      f.add(new b(107920826959805034L));
      f.add(new b(8558439383539614660L));
      f.add(new b(-799210960285337913L));
      f.add(new b(-5306847648388879870L));
      f.add(new b(5807663805086688128L));
      f.add(new b(-5324603930499836296L));
      f.add(new b(-4291148202444880098L));
      f.add(new b(3749485480061245174L));
      f.add(new b(1216827230190824377L));
      f.add(new b(7377825634068232454L));
      f.add(new b(1225857378121826707L));
      f.add(new b(-1890663751131646064L));
      f.add(new b(-1086585730488987183L));
      f.add(new b(5796909472895932293L));
      f.add(new b(-2084374805372659848L));
      f.add(new b(-5201710430514966721L));
      f.add(new b(-8842343686191877014L));
      f.add(new b(6226396594970891271L));
      f.add(new b(-4241308094394835715L));
      f.add(new b(-5418294339671104659L));
      f.add(new b(-6591056275946447295L));
      f.add(new b(-1972652117235699003L));
      f.add(new b(-3377616730897726705L));
      f.add(new b(-5509996754720355892L));
      f.add(new b(8225441736195051862L));
      f.add(new b(-4436990342599371275L));
      f.add(new b(-9108385070568037564L));
      f.add(new b(1541325046896052691L));
      f.add(new b(3766065536347969231L));
      f.add(new b(-2902465236465550824L));
      f.add(new b(-4433163076492435283L));
      f.add(new b(-5479145276205961383L));
      f.add(new b(-1966470055397870667L));
      f.add(new b(-7992995191363994530L));
      f.add(new b(7335061027975051735L));
      f.add(new b(3673599115891483968L));
      f.add(new b(89972556024070643L));
      f.add(new b(-4295746884905407626L));
      f.add(new b(2714218053878420282L));
      f.add(new b(-4042848906633641759L));
      f.add(new b(3981920924242410216L));
      f.add(new b(-3453162629310881110L));
      f.add(new b(-3362965555261071631L));
      f.add(new b(-1886573499515287844L));
      f.add(new b(-5689906170268012805L));
      f.add(new b(-1927790122304702384L));
      f.add(new b(6050472201674629761L));
      f.add(new b(-1818866231008195596L));
      f.add(new b(4927411885454950041L));
      f.add(new b(-4530306825494514702L));
      f.add(new b(-2263969985070888738L));
      f.add(new b(3879552216457201479L));
      f.add(new b(5917534575382178777L));
      f.add(new b(-6597736639350712525L));
      f.add(new b(1511948209017193144L));
      f.add(new b(5187059065746149652L));
      f.add(new b(-596747759765771674L));
      f.add(new b(8573021530157869848L));
      f.add(new b(5784840104440007712L));
      f.add(new b(-8962610372960364024L));
      f.add(new b(8619049945104326164L));
      f.add(new b(8346235069913257658L));
      f.add(new b(6034132583292386901L));
      f.add(new b(-9017220263353120860L));
      f.add(new b(-1882758217466599976L));
      f.add(new b(-7683920594348497591L));
      f.add(new b(4960248160917847493L));
      f.add(new b(-7988742168428759466L));
      f.add(new b(-4099525026001176258L));
      f.add(new b(4936870286789733853L));
      f.add(new b(-7981636083423833040L));
      f.add(new b(-3069222839778443442L));
      f.add(new b(5976431045875065464L));
      f.add(new b(4695810411489787170L));
      f.add(new b(-1984438829762305922L));
      f.add(new b(-6459060987223716451L));
      f.add(new b(245652339095491878L));
      f.add(new b(-9169778957092295379L));
      f.add(new b(-5752974643624176463L));
      f.add(new b(-6725861253616506329L));
      f.add(new b(-3147345502296892305L));
      f.add(new b(-9000481128253573032L));
      f.add(new b(-8650068578649534605L));
      f.add(new b(6218023961073415485L));
      f.add(new b(-715560680416282202L));
      f.add(new b(-4548817954953659010L));
      f.add(new b(1253264718444273569L));
      f.add(new b(-2268661698803447970L));
      f.add(new b(5905764588580883550L));
      f.add(new b(4953078137174118354L));
      f.add(new b(-7805210556227676868L));
      f.add(new b(3886128890215950869L));
      f.add(new b(-5409639743640050083L));
      f.add(new b(2762186733949894231L));
      f.add(new b(8412754792608487107L));
      f.add(new b(-912807670676571130L));
      f.add(new b(-6638264185055961474L));
      f.add(new b(-3186364270632378684L));
      f.add(new b(-6519166037343708076L));
      f.add(new b(-6823094715486451804L));
      f.add(new b(1220880076312441591L));
      f.add(new b(162319660581338937L));
      f.add(new b(-8716401639622061858L));
      f.add(new b(8234797116736262758L));
      f.add(new b(-6401291171576249401L));
      f.add(new b(-9044267721166631744L));
      f.add(new b(-2915166995818159129L));
      f.add(new b(8249278985880875243L));
      f.add(new b(1525820736572620164L));
      f.add(new b(1659873776117268721L));
      f.add(new b(-2187108343432857053L));
      f.add(new b(232190673987262405L));
      f.add(new b(-5568108558024024694L));
      f.add(new b(-5371667555758221309L));
      f.add(new b(-4185144339320490264L));
      f.add(new b(-2156995349330454696L));
      f.add(new b(-4491624723523557681L));
      f.add(new b(-3384910250373074578L));
      f.add(new b(-663723894318865386L));
      f.add(new b(-1743992677387433761L));
      f.add(new b(-7689831840844000246L));
      f.add(new b(1584118170765106625L));
      f.add(new b(8598135971407764904L));
      f.add(new b(-6523097459088387838L));
      f.add(new b(-9126904855402705356L));
      f.add(new b(6123054589587915642L));
      f.add(new b(4724421488156838210L));
      e = (int[])e.clone();
      f.add(new b(-6767592604459019280L));
      f.add(new b(-7881833499800273427L));
      f.add(new b(-7561608934404156865L));
      f.add(new b(7303065892901723871L));
      f.add(new b(2433293225810996790L));
      f.add(new b(-8714876626230916148L));
      f.add(new b(-4135351278299575743L));
      f.add(new b(4643240613118938536L));
      f.add(new b(5965020492619517036L));
      f.add(new b(7380193997983751934L));
      f.add(new b(-6853290244418192061L));
      f.add(new b(473897143214480855L));
      f.add(new b(59871759391878217L));
      f.add(new b(-8665446262975710006L));
      f.add(new b(-7912919400178371174L));
      f.add(new b(8280924656642166775L));
      f.add(new b(7090698357963156940L));
      f.add(new b(-8055277520590223571L));
      f.add(new b(1181452332787518231L));
      f.add(new b(3825186158970106449L));
      f.add(new b(-4296382982854361405L));
      f.add(new b(378790696384692158L));
      f.add(new b(7224531859144342527L));
      f.add(new b(-989400111811214341L));
      f.add(new b(-5374474039228714696L));
      f.add(new b(-1876332836984876709L));
      f.add(new b(-6439523891634498548L));
      f.add(new b(1645118154341756670L));
      f.add(new b(-8788321957565908337L));
      f.add(new b(-1938655639472920825L));
      f.add(new b(2868628524525126226L));
      f.add(new b(3549249229577232347L));
      f.add(new b(2512689723585987174L));
      f.add(new b(-2177805659716959813L));
      f.add(new b(-8721037185660175687L));
      f.add(new b(-4316514636240990583L));
      f.add(new b(6135691293912806735L));
      f.add(new b(-5678714157979824815L));
      f.add(new b(5934248294556292963L));
      f.add(new b(-712925502055824133L));
      f.add(new b(2656476380992078928L));
      f.add(new b(6093887870814003723L));
      f.add(new b(2417932703184789201L));
      f.add(new b(-1899570606793814964L));
      f.add(new b(4814817745727298543L));
      f.add(new b(-6701489755573792311L));
      f.add(new b(2437319942894203858L));
      f.add(new b(3581143672249418063L));
      f.add(new b(-8874713418960891331L));
      f.add(new b(4790316860209843919L));
      f.add(new b(-8700603568932953478L));
      f.add(new b(-6490447767959457882L));
      f.add(new b(403991967534239546L));
      f.add(new b(353962004108983799L));
      f.add(new b(3627825773018077280L));
      f.add(new b(1478270013760480562L));
      f.add(new b(-8691876636703430536L));
      f.add(new b(-2920641186018329358L));
      f.add(new b(-8700119138481304046L));
      f.add(new b(-4267284838374998983L));
      f.add(new b(6228644292875025757L));
      f.add(new b(8301759057420326644L));
      f.add(new b(-3056904581296748094L));
      f.add(new b(-600013476876815817L));
      f.add(new b(4699532878207966467L));
      f.add(new b(7460440286293257114L));
      f.add(new b(5999610219737835719L));
      f.add(new b(-8805308993982836577L));
      f.add(new b(5012825324867066402L));
      f.add(new b(-8709806009474417898L));
      f.add(new b(7074871789942913667L));
      f.add(new b(4970027479497011172L));
      f.add(new b(1580473831601880596L));
      f.add(new b(-3371885105188505528L));
      f.add(new b(-6753533811815752550L));
      f.add(new b(-5432118097636613007L));
      f.add(new b(4016682588799627533L));
      f.add(new b(4908194801769233511L));
      f.add(new b(-6538971782084054690L));
      f.add(new b(-5291781793567474221L));
      f.add(new b(-3178732043156902859L));
      f.add(new b(525787427045098327L));
      f.add(new b(6174398077441085956L));
      f.add(new b(4785682488752973998L));
      f.add(new b(-7761452936247452414L));
      f.add(new b(-990547000997621522L));
      f.add(new b(1319423120102958893L));
      f.add(new b(-3173578067914331950L));
      f.add(new b(6020932923837651692L));
      f.add(new b(-1763199202714878259L));
      f.add(new b(-2202231046844931906L));
      f.add(new b(194239778279288104L));
      f.add(new b(4899338630009656057L));
      f.add(new b(-9138083904387509912L));
      f.add(new b(-8862886337902087534L));
      f.add(new b(1529244224156804555L));
      f.add(new b(7077682490170911547L));
      f.add(new b(1509024338996160602L));
      f.add(new b(2549079623991288480L));
      f.add(new b(-971009736142201048L));
      f.add(new b(-8734060285802194466L));
      f.add(new b(6976766701416266621L));
      f.add(new b(-1104812155172057820L));
      f.add(new b(-6503940837928725411L));
      f.add(new b(3609322259739660791L));
      f.add(new b(-5296897352266032056L));
      f.add(new b(-2018794063185146162L));
      f.add(new b(-792308398210173029L));
      f.add(new b(3460843245593544909L));
      f.add(new b(285204749341200016L));
      f.add(new b(-5459992328457553942L));
      f.add(new b(3799050891967709428L));
      f.add(new b(-806145682936860254L));
      f.add(new b(7218612830370602422L));
      f.add(new b(2744259088382534420L));
      f.add(new b(8288440391905299917L));
      f.add(new b(4888233112435600895L));
      f.add(new b(-696417602720342780L));
      f.add(new b(-1053678622329521190L));
      f.add(new b(4733474961956535564L));
      f.add(new b(-7642073538166765372L));
      f.add(new b(-6428708270350560983L));
      f.add(new b(-4091396596563133786L));
      f.add(new b(-758066183472441777L));
      f.add(new b(8345630987958270078L));
      f.add(new b(6148630082201599359L));
      f.add(new b(-2258477348254154749L));
      f.add(new b(-8755698761772155007L));
      f.add(new b(-3344899295384580505L));
      f.add(new b(-9189857018723154967L));
      f.add(new b(2312752259985137416L));
      f.add(new b(497831775756184356L));
      f.add(new b(-4284571844387881025L));
      e = (int[])e.clone();
      f.add(new b(187685385787897210L));
      f.add(new b(4781513664293568902L));
      f.add(new b(5908976727492572663L));
      f.add(new b(-9183104511607658892L));
      f.add(new b(-4118252825643706114L));
      f.add(new b(2722471107828637076L));
      f.add(new b(-5632448490767324322L));
      f.add(new b(-5680283349934674636L));
      f.add(new b(7431489930407589336L));
      f.add(new b(6059945263731252740L));
      f.add(new b(-5707787088803751191L));
      f.add(new b(3694282146609433627L));
      f.add(new b(-5608438769881829246L));
      f.add(new b(7048832045945404747L));
      f.add(new b(-8983542417699616288L));
      f.add(new b(-5213962607874778119L));
      f.add(new b(-6834870639071893585L));
      f.add(new b(-2067334976202034173L));
      f.add(new b(-708515079230775514L));
      f.add(new b(-1839239952134874931L));
      f.add(new b(1650629983996219479L));
      f.add(new b(-7891401124442824798L));
      f.add(new b(-3343887791919573335L));
      f.add(new b(-2230580559865464265L));
      f.add(new b(-6861717668699380555L));
      f.add(new b(6999203387280264378L));
      f.add(new b(6239056604932007740L));
      f.add(new b(-3245428360386831961L));
      f.add(new b(-7718748904239504868L));
      f.add(new b(106459096711016094L));
      f.add(new b(-766553502623000362L));
      f.add(new b(6971297637679679547L));
      f.add(new b(1273043939154897939L));
      f.add(new b(6921175850597666599L));
      f.add(new b(-5520323911211862155L));
      f.add(new b(6976948367283663826L));
      f.add(new b(-7644090487829955532L));
      f.add(new b(235904030043848758L));
      f.add(new b(-5531450798397075558L));
      f.add(new b(1529870764664683555L));
      f.add(new b(-1888655822848543423L));
      f.add(new b(4709800221065447736L));
      f.add(new b(-5477683259142286851L));
      f.add(new b(-7888640782752777716L));
      f.add(new b(-2992623516642284069L));
      f.add(new b(-7788735810433766407L));
      f.add(new b(150776145650254661L));
      f.add(new b(7267853639924782980L));
      f.add(new b(2316650672140002541L));
      f.add(new b(-2890956972192919724L));
      f.add(new b(-4340629595075281896L));
      f.add(new b(1266333593933743428L));
      f.add(new b(5004472913217273622L));
      f.add(new b(4821728999667074633L));
      f.add(new b(-5715326424200514550L));
      f.add(new b(-1148078527732967530L));
      f.add(new b(4950896357815742210L));
      f.add(new b(1508355488646189935L));
      f.add(new b(-3235729883297803153L));
      f.add(new b(5913100279158050991L));
      f.add(new b(-1772703096229150343L));
      f.add(new b(85082069376628775L));
      f.add(new b(4775019134185466740L));
      f.add(new b(-3363015737055955088L));
      f.add(new b(-9127611162498061224L));
      f.add(new b(6021038800541235159L));
      f.add(new b(6968089861854633269L));
      f.add(new b(8112915257832318339L));
      f.add(new b(-7594387898144920364L));
      f.add(new b(-3170054505465477824L));
      f.add(new b(-2257323262432605623L));
      f.add(new b(174682620211008927L));
      f.add(new b(5940020624785641937L));
      f.add(new b(-2045832608674138178L));
      e = (int[])e.clone();
      f.add(new b(6298417671929561901L));
      f.add(new b(3775299986192559842L));
      f.add(new b(-1792835688523372466L));
      f.add(new b(-7712075769624973202L));
      f.add(new b(3764527462001390695L));
      f.add(new b(3839083319143839989L));
      f.add(new b(-641337510160210030L));
      f.add(new b(6113915323175501563L));
      f.add(new b(-2144493453406788765L));
      f.add(new b(-2057332930027976472L));
      f.add(new b(-7772002316488130148L));
      f.add(new b(-6880496822839875241L));
      f.add(new b(1450255944477198284L));
      f.add(new b(-3195116128900506720L));
      f.add(new b(8218560353194502435L));
      f.add(new b(-7844170161566585291L));
      f.add(new b(-5723669466780741840L));
      f.add(new b(-6602348776953248642L));
      f.add(new b(196475056952056430L));
      f.add(new b(1237673818223851674L));
      f.add(new b(-4076763967555650366L));
      f.add(new b(6060932653831602518L));
      f.add(new b(-909945138616862950L));
      f.add(new b(47622032709091118L));
      f.add(new b(7175029561268474883L));
      f.add(new b(-6733611988713141190L));
      f.add(new b(-1028070794765636386L));
      f.add(new b(2736832202808647162L));
      f.add(new b(8255865899683166563L));
      f.add(new b(-5381623134727780278L));
      f.add(new b(2699796199812661453L));
      f.add(new b(4743829927705689326L));
      f.add(new b(6170174531602813249L));
      f.add(new b(-5408043547443330036L));
      f.add(new b(-695993733146226724L));
      f.add(new b(6265761628647328912L));
      f.add(new b(5118185584186448629L));
      f.add(new b(6960126833885546018L));
      f.add(new b(-7993710511274741890L));
      f.add(new b(106923818575143318L));
      f.add(new b(2473895006781378386L));
      f.add(new b(128603236141413289L));
      f.add(new b(-632210358707721643L));
      f.add(new b(2370929738214723801L));
      f.add(new b(7197363510161681974L));
      f.add(new b(-4400698094180886262L));
      f.add(new b(4813743737922859940L));
      f.add(new b(6173890977535238156L));
      f.add(new b(5027129450084450210L));
      f.add(new b(8502755694184494135L));
      f.add(new b(-8003091344982157108L));
      f.add(new b(7372037023825934522L));
      f.add(new b(1258133585387395804L));
      f.add(new b(-6773201765147734860L));
      f.add(new b(-2943111820478664577L));
      f.add(new b(8536360318767008955L));
      f.add(new b(-4218578409944737002L));
      f.add(new b(3898331438029585198L));
      f.add(new b(-822211967679056675L));
      f.add(new b(3537845357488857659L));
      f.add(new b(-1798309966040966882L));
      f.add(new b(-5638358235930059686L));
      f.add(new b(-3212200223672945413L));
      f.add(new b(6130458468629111573L));
      f.add(new b(-6419746116869323827L));
      f.add(new b(-6425255261216189551L));
      f.add(new b(6111271211185562959L));
      f.add(new b(-2190428062190196186L));
      f.add(new b(4723015298498372817L));
      f.add(new b(6282469884875613414L));
      f.add(new b(-3198652837221270067L));
      f.add(new b(-9182161252040981154L));
      f.add(new b(-8692347042396873445L));
      f.add(new b(1221539145387022096L));
      f.add(new b(8469303777864218861L));
      f.add(new b(7104753662633431945L));
      f.add(new b(-5517903287917214151L));
      f.add(new b(-3404534401721009685L));
      f.add(new b(6043675161748237180L));
      f.add(new b(4902380049576060679L));
      f.add(new b(-7559182525212643676L));
      f.add(new b(-8729480093059412655L));
      f.add(new b(3611965764147122265L));
      f.add(new b(-5467959050735795780L));
      f.add(new b(3473131731781188444L));
      f.add(new b(6081435465497463256L));
      f.add(new b(-5476412014107834865L));
      f.add(new b(7329114948106428889L));
      f.add(new b(-3386214596128451548L));
      f.add(new b(7375321088379403766L));
      f.add(new b(2788067214403841203L));
      f.add(new b(4626126094552551859L));
      f.add(new b(-605418146171979416L));
      f.add(new b(1352337986346853725L));
      f.add(new b(4635989539814982510L));
      f.add(new b(3571286592827842989L));
      f.add(new b(6963456295406109651L));
      f.add(new b(8442659478016510659L));
      f.add(new b(-2301701194767853447L));
      f.add(new b(-7604079324082594877L));
      f.add(new b(-604831520982002056L));
      f.add(new b(-6538372114968838576L));
      f.add(new b(2596244130883869896L));
      f.add(new b(-587674551313275249L));
      f.add(new b(-4545705876676204398L));
      f.add(new b(-714997730429306458L));
      f.add(new b(3810360037637835294L));
      f.add(new b(-4413405135368802261L));
      f.add(new b(-3319503144428391432L));
      f.add(new b(8223068159670526429L));
      f.add(new b(-2258964974245460799L));
      f.add(new b(-6650263086107269783L));
      f.add(new b(-666857224990249816L));
      f.add(new b(-9041055094940999173L));
      f.add(new b(5941192142449097791L));
      f.add(new b(-6455250630592834388L));
      f.add(new b(5866349262495063122L));
      f.add(new b(-8867360983144224744L));
      f.add(new b(8091163113598734508L));
      f.add(new b(4009074842854228050L));
      f.add(new b(422091814846952359L));
      f.add(new b(-7688784126924710819L));
      f.add(new b(-7931598648879714674L));
      f.add(new b(-706003556894043241L));
      f.add(new b(-8781525864320484357L));
      f.add(new b(-5713303890529875961L));
      f.add(new b(6169321167150853832L));
      f.add(new b(7254999841768274189L));
      f.add(new b(-1004699724735319373L));
      f.add(new b(-2886486580112866878L));
      f.add(new b(-9040337189330686274L));
      f.add(new b(449602561224974828L));
      f.add(new b(8213699148359370048L));
      f.add(new b(5129579735390151463L));
      f.add(new b(-2205250756797124138L));
      f.add(new b(-1058872089099110793L));
      f.add(new b(-9165716975201551620L));
      f.add(new b(-6719689943238420946L));
      f.add(new b(-8919314194162110823L));
      f.add(new b(-5596226924980366437L));
      f.add(new b(4921694099413379792L));
      f.add(new b(-3012155077752319040L));
      f.add(new b(-6773168527582187220L));
      f.add(new b(-8052183902051288758L));
      f.add(new b(-5520901850551518419L));
      f.add(new b(7480770222741297536L));
      f.add(new b(-7655763741513052108L));
      f.add(new b(-1976452855724194196L));
      f.add(new b(-4066838519690170581L));
      f.add(new b(-900627367374797046L));
      f.add(new b(8510075295201824178L));
      f.add(new b(2766127410764340105L));
      f.add(new b(-8703054531486897374L));
      f.add(new b(-4142907772288728176L));
      e = (int[])e.clone();
      f.add(new b(-7815911452101473649L));
      f.add(new b(346462641118801075L));
      f.add(new b(-9077466849271974585L));
      f.add(new b(5785876016892780269L));
      f.add(new b(5834710004539020687L));
      f.add(new b(5044690520212962418L));
      f.add(new b(6241069604958923001L));
      f.add(new b(7052086284910086710L));
      f.add(new b(189854864215272853L));
      f.add(new b(7133173937990195840L));
      f.add(new b(285071292190156939L));
      f.add(new b(-1112705073941706815L));
      f.add(new b(-805588185703721853L));
      f.add(new b(-4073853220351961523L));
      f.add(new b(-6651084837989564478L));
      f.add(new b(-3442219163212351499L));
      f.add(new b(-8003314446286596026L));
      f.add(new b(-3051347884324619925L));
      f.add(new b(-8843874078270366150L));
      f.add(new b(1648029130789175655L));
      f.add(new b(8584589628571783775L));
      f.add(new b(-6912947415983063586L));
      f.add(new b(3480526025914376483L));
      f.add(new b(-1905940478071530293L));
      f.add(new b(-698775706527965085L));
      f.add(new b(-1920035167924062657L));
      f.add(new b(8364566697208758359L));
      f.add(new b(8474519902158789024L));
      f.add(new b(-8708905320396243973L));
      f.add(new b(3504159520437312159L));
      f.add(new b(-785892738122286083L));
      f.add(new b(-6636753447239222377L));
      f.add(new b(6063649171616576603L));
      f.add(new b(-3172793417170484136L));
      f.add(new b(7149868752008178285L));
      f.add(new b(8433043696538376305L));
      f.add(new b(-944159204750713848L));
      f.add(new b(-6772208973199731816L));
      f.add(new b(-3222396769248625947L));
      f.add(new b(8583802742640437785L));
      f.add(new b(-8843855816585247948L));
      f.add(new b(-2892020363121998117L));
      f.add(new b(7346137542101644599L));
      f.add(new b(2737113323361384078L));
      f.add(new b(5951259045005024418L));
      f.add(new b(-8692069100587064884L));
      f.add(new b(4871933580730609140L));
      f.add(new b(7348355411203377992L));
      f.add(new b(5801173831692287232L));
      f.add(new b(1168732709881494703L));
      f.add(new b(7372037592729057074L));
      f.add(new b(1585553611245740209L));
      f.add(new b(-6769503263344268071L));
      f.add(new b(1637270037137696208L));
      f.add(new b(-3014242862799889472L));
      f.add(new b(-5694098906342652837L));
      f.add(new b(1640345764387469442L));
      f.add(new b(-1000557647729452566L));
      f.add(new b(-3238418969655333659L));
      f.add(new b(6122028929914119981L));
      f.add(new b(-5321914031139119396L));
      f.add(new b(1678167913974641890L));
      f.add(new b(6008750473730615850L));
      f.add(new b(3518794249724124424L));
      f.add(new b(-7754806594656035324L));
      f.add(new b(2676680484553361329L));
      f.add(new b(-752863780629557061L));
      f.add(new b(-5670773255068528883L));
      f.add(new b(7333343115649166008L));
      f.add(new b(-5336446850763525728L));
      f.add(new b(5919103187285466175L));
      f.add(new b(-7559526603830758698L));
      f.add(new b(-953512382054949247L));
      f.add(new b(1728800224470183157L));
      f.add(new b(2814227471662644662L));
      f.add(new b(5179912722444751764L));
      f.add(new b(-3216467350917804538L));
      f.add(new b(-5593382329688575539L));
      f.add(new b(8273487664311880029L));
      f.add(new b(5929944742823521344L));
      f.add(new b(2610685347662216096L));
      f.add(new b(8489528558678134779L));
      f.add(new b(2440806127619945851L));
      f.add(new b(-6636226435205320651L));
      f.add(new b(6244612292182060974L));
      f.add(new b(-4450723501343326329L));
      f.add(new b(-7871884797289020185L));
      f.add(new b(-7716850322265099771L));
      f.add(new b(-1760521245265556810L));
      f.add(new b(66530760986568543L));
      f.add(new b(-3414630552460820098L));
      f.add(new b(5789823882832682768L));
      f.add(new b(7454291274345517905L));
      f.add(new b(2790816601011237204L));
      f.add(new b(-2185741227575558345L));
      f.add(new b(-3001842539865021835L));
      f.add(new b(-9198404071873116811L));
      f.add(new b(5871027992055403232L));
      f.add(new b(8362286819758241796L));
      f.add(new b(386686921354153088L));
      f.add(new b(-1741872212254001156L));
      f.add(new b(-4112108296561865433L));
      f.add(new b(8521333655231067848L));
      f.add(new b(-4326338526350638550L));
      f.add(new b(-2289562993455723347L));
      f.add(new b(4977035576169211974L));
      f.add(new b(-7676140391431821703L));
      f.add(new b(-3335748016447987012L));
      f.add(new b(-3020497540633368574L));
      f.add(new b(2378953752304538266L));
      f.add(new b(2575007850877698089L));
      f.add(new b(-4077296841628487676L));
      f.add(new b(8470304661355597728L));
      f.add(new b(326357273647836194L));
      f.add(new b(-3147937531664113455L));
      f.add(new b(5109222220612246153L));
      f.add(new b(1515538727264378611L));
      f.add(new b(2614814649041173719L));
      f.add(new b(6060199991677192116L));
      f.add(new b(544604681938460872L));
      f.add(new b(-9117693688658067195L));
      f.add(new b(-3231002732450524483L));
      f.add(new b(179919457992556073L));
      f.add(new b(4792491397108163711L));
      f.add(new b(6139508993402315381L));
      f.add(new b(5068221584778665259L));
      f.add(new b(6249489326587342690L));
      f.add(new b(-6749781345383005898L));
      f.add(new b(4859473202141516689L));
      f.add(new b(-7524293875812044954L));
      f.add(new b(7352388315992496916L));
      f.add(new b(-7781941730318475741L));
      f.add(new b(7457248452947991173L));
      f.add(new b(-6808013182376001879L));
      f.add(new b(8250528765882614021L));
      f.add(new b(8341179196263829941L));
      f.add(new b(-8760097088243737594L));
      f.add(new b(2530103610277073472L));
      f.add(new b(-6878347042649582527L));
      f.add(new b(-760252631945903863L));
      f.add(new b(-3312491104557419632L));
      f.add(new b(-6392422182113820013L));
      f.add(new b(-6518543893599599981L));
      f.add(new b(293732942254868670L));
      f.add(new b(3966458601660477804L));
      f.add(new b(-6906337009305293754L));
      f.add(new b(4704130656474821178L));
      f.add(new b(-1969725322182086920L));
      f.add(new b(2870463524726850706L));
      f.add(new b(6208074917660812841L));
      f.add(new b(-6499813991265030026L));
      f.add(new b(-1908592776030310243L));
      f.add(new b(-6878008093189644155L));
      f.add(new b(403979468457782695L));
      f.add(new b(8528481066805088320L));
      f.add(new b(-4197793517579495341L));
      f.add(new b(1434484334714018975L));
      e = (int[])e.clone();
      f.add(new b(367833801398253132L));
      f.add(new b(2800919790346664433L));
      f.add(new b(-6588709360237233804L));
      f.add(new b(-2934894164961577852L));
      f.add(new b(2329426872445439718L));
      f.add(new b(5026307372188509519L));
      f.add(new b(4761332754311301382L));
      f.add(new b(3943685469299242209L));
      f.add(new b(8078232691125645381L));
      f.add(new b(1664771436694755588L));
      f.add(new b(4905707394886825642L));
      f.add(new b(-8895435304952234906L));
      f.add(new b(5182144283702665668L));
      f.add(new b(-9114979415713083919L));
      f.add(new b(-5319143650165511625L));
      f.add(new b(-7507238699834961295L));
      f.add(new b(1322731590664072892L));
      f.add(new b(-703744538693200514L));
      f.add(new b(-5610957249933748959L));
      f.add(new b(-806481704715536435L));
      f.add(new b(4762897698864129851L));
      f.add(new b(6253489808143152445L));
      f.add(new b(-4058508035894156618L));
      f.add(new b(-1822823339117593212L));
      f.add(new b(7356857954862564569L));
      f.add(new b(-1836761858071817880L));
      f.add(new b(-8067590839878788329L));
      f.add(new b(1569021051964945438L));
      f.add(new b(2812737314272282031L));
      f.add(new b(7491336164043464876L));
      f.add(new b(-5530477310436255753L));
      f.add(new b(-8950526114325554540L));
      f.add(new b(8199118724161033848L));
      f.add(new b(-3357906083246380765L));
      f.add(new b(-4426878158004418448L));
      f.add(new b(-2143793390478597476L));
      f.add(new b(5114946937047361300L));
      f.add(new b(8635348043722215802L));
      f.add(new b(-8878091787769677279L));
      f.add(new b(-3041095758028569179L));
      f.add(new b(-1959985622431399719L));
      f.add(new b(-4420244669973840215L));
      f.add(new b(-1141178060862445187L));
      f.add(new b(-2270065176328219257L));
      f.add(new b(-1004428238743211676L));
      f.add(new b(-2241200110507089868L));
      f.add(new b(-4604309179592888831L));
      f.add(new b(-5335419097667977510L));
      f.add(new b(-6351316247585788465L));
      f.add(new b(-604487868359082169L));
      f.add(new b(-7526585037987811965L));
      f.add(new b(15094187862755341L));
      f.add(new b(-7561229935397310529L));
      f.add(new b(7007361590973991984L));
      f.add(new b(-9015741378524850509L));
      f.add(new b(5786371710295296302L));
      f.add(new b(-995257572618312396L));
      f.add(new b(-3216142717869298343L));
      f.add(new b(5116834845964663058L));
      f.add(new b(1452395188384499617L));
      f.add(new b(-2044721200934274842L));
      f.add(new b(8121720562147814470L));
      f.add(new b(8231912353479129551L));
      f.add(new b(-6457108398239369104L));
      f.add(new b(-2190362010911183200L));
      f.add(new b(7072601081703544413L));
      f.add(new b(-4290890196095784176L));
      f.add(new b(1182457670079149920L));
      f.add(new b(-4470793357868808332L));
      f.add(new b(-8779326923250412180L));
      f.add(new b(-9142786255068500655L));
      f.add(new b(-1939787287160566618L));
      f.add(new b(7215234117174550972L));
      f.add(new b(2556904551729826111L));
      f.add(new b(-781037204628866999L));
      f.add(new b(-6482028609406688068L));
      f.add(new b(-9141904604767519297L));
      f.add(new b(-4297311608998904248L));
      f.add(new b(-6715164101721834356L));
      f.add(new b(1261847409822590600L));
      f.add(new b(-2283906353738233526L));
      f.add(new b(-6796294556716107721L));
      f.add(new b(-6865071679219463985L));
      e = (int[])e.clone();
      f.add(new b(-8794613720148411183L));
      f.add(new b(7286570861861856392L));
      f.add(new b(-5421010676319540099L));
      f.add(new b(312210807450569030L));
      f.add(new b(3820705951885511533L));
      f.add(new b(8555556708436644080L));
      f.add(new b(8492287229284229499L));
      f.add(new b(8261981738385416544L));
      f.add(new b(6246628383393033148L));
      f.add(new b(-3427043011961118011L));
      f.add(new b(2529924141097946933L));
      f.add(new b(6241183011804323593L));
      f.add(new b(2866077615922834524L));
      f.add(new b(2825612244155215442L));
      f.add(new b(8560018237555307022L));
      f.add(new b(3597140052229782177L));
      f.add(new b(-5433823775165360548L));
      f.add(new b(-4540543305635265218L));
      f.add(new b(6238607568960738306L));
      f.add(new b(-6503604644079087484L));
      f.add(new b(3994325395340892972L));
      f.add(new b(7026615263304065796L));
      f.add(new b(-6772162881387408252L));
      f.add(new b(-2253711957317169185L));
      f.add(new b(-8826083780947057373L));
      f.add(new b(-5764301269454304794L));
      f.add(new b(-1885330957239219686L));
      f.add(new b(-8853782588428638056L));
      f.add(new b(-1107619080329705198L));
      f.add(new b(3910724568063902692L));
      f.add(new b(6257313156482256537L));
      f.add(new b(84730588936962559L));
      f.add(new b(5860089139284461886L));
      f.add(new b(-7957681714222223522L));
      f.add(new b(401877021369904558L));
      f.add(new b(5936995721615583494L));
      f.add(new b(7323734877948879515L));
      f.add(new b(1478159615537448736L));
      f.add(new b(-727020612932429490L));
      f.add(new b(-2245420755140073990L));
      f.add(new b(557736115082976244L));
      f.add(new b(192547009977661736L));
      f.add(new b(6298497011345664804L));
      f.add(new b(-5545960412481395203L));
      f.add(new b(3561955680305187209L));
      f.add(new b(4962096942988624680L));
      f.add(new b(-8827878743048751017L));
      f.add(new b(2654049242558802928L));
      f.add(new b(303272149524197223L));
      f.add(new b(2323388360717544836L));
      f.add(new b(3993326040931395187L));
      f.add(new b(-2003645747589060223L));
      f.add(new b(1536897015873575727L));
      f.add(new b(3479330310892887828L));
      f.add(new b(4925792641097630703L));
      f.add(new b(5030769496861131732L));
      f.add(new b(-3080452185106116760L));
      f.add(new b(3755012192573489368L));
      f.add(new b(-2091333855914104995L));
      f.add(new b(-6766168288975073028L));
      f.add(new b(-3444424932473405475L));
      f.add(new b(-2915773042140257374L));
      f.add(new b(4003296294313839151L));
      f.add(new b(6098666582970207193L));
      f.add(new b(-2010173572861681939L));
      f.add(new b(7468445069326284017L));
      f.add(new b(8628593347148811504L));
      f.add(new b(2314473649870069141L));
      e = (int[])e.clone();
      f.add(new b(-5211275046652982633L));
      f.add(new b(1469631523244953155L));
      f.add(new b(-5566966711842235654L));
      f.add(new b(-3258386715249332971L));
      f.add(new b(-3376854872478686379L));
      f.add(new b(5838957911236607493L));
      f.add(new b(-9140572911840012857L));
      f.add(new b(-4212355519671952501L));
      f.add(new b(5008440378663973297L));
      f.add(new b(3476786169732592351L));
      f.add(new b(5037452843553495877L));
      f.add(new b(-9001251542418669384L));
      f.add(new b(-1066058242920533721L));
      f.add(new b(-578203092328671430L));
      f.add(new b(-2005187775196528024L));
      f.add(new b(2615414266385385873L));
      f.add(new b(-4088919846375056638L));
      f.add(new b(-5664059935975686368L));
      f.add(new b(-8868248374157669061L));
      f.add(new b(7486577998719494628L));
      f.add(new b(-8790353903138201517L));
      f.add(new b(-2095281845690093384L));
      f.add(new b(2788055557587720230L));
      f.add(new b(2332127697665294867L));
      f.add(new b(-4586768066966937772L));
      f.add(new b(2514080713414666280L));
      f.add(new b(-6746511265479057239L));
      f.add(new b(265455432819698907L));
      f.add(new b(-3045470178890094086L));
      f.add(new b(-5605342844063491185L));
      f.add(new b(-840940601530457512L));
      f.add(new b(2816084215154215171L));
      f.add(new b(5856605558574156618L));
      f.add(new b(-9181131335141377496L));
      f.add(new b(1652213617015265265L));
      f.add(new b(-1113108734629838589L));
      f.add(new b(4009349269835859605L));
      f.add(new b(3514025479740834601L));
      f.add(new b(7035839509435218348L));
      f.add(new b(1636365453945058267L));
      f.add(new b(-5518495090792474860L));
      f.add(new b(-6525111392878043310L));
      f.add(new b(-3010139165671895144L));
      f.add(new b(3700067399822876891L));
      f.add(new b(-4520402119151464978L));
      f.add(new b(193504745066766287L));
      f.add(new b(520482364419704904L));
      f.add(new b(-1883910648988769380L));
      f.add(new b(-9085583111317428971L));
      f.add(new b(3578167323398247403L));
      f.add(new b(7489590249664416543L));
      f.add(new b(3491368129859197343L));
      f.add(new b(-3081553315519365213L));
      f.add(new b(-5481961827925588582L));
      f.add(new b(-4512072876526563728L));
      f.add(new b(6147277679278972201L));
      f.add(new b(2868983005815050206L));
      f.add(new b(-1007273275041802274L));
      f.add(new b(-7677585882947606759L));
      f.add(new b(2465277961542657299L));
      f.add(new b(-2203081621554619277L));
      f.add(new b(-5412643719613005500L));
      f.add(new b(3945625175606698820L));
      f.add(new b(-4309513312755424104L));
      f.add(new b(8586879886186423555L));
      f.add(new b(4776473293374592582L));
      f.add(new b(-5469361202816120745L));
      f.add(new b(-695007527443545170L));
      f.add(new b(-7866446450588209480L));
      f.add(new b(-5594080513941734931L));
      e = (int[])e.clone();
      f.add(new b(-3154620656169935422L));
      f.add(new b(5805990518460921876L));
      f.add(new b(5058644964284808570L));
      f.add(new b(-4300605882437889760L));
      f.add(new b(-613198912462078539L));
      f.add(new b(-8770308492165647913L));
      f.add(new b(-6913488844636413716L));
      f.add(new b(4660798347419460285L));
      f.add(new b(5069962391996232123L));
      f.add(new b(4662661506627977103L));
      f.add(new b(1205041122731991851L));
      f.add(new b(-1888827821818289942L));
      f.add(new b(-7651410863211507420L));
      f.add(new b(-8039343243343308730L));
      f.add(new b(-5190354650041051298L));
      f.add(new b(-2006882401684581334L));
      f.add(new b(3566715052236510667L));
      f.add(new b(-3242279236740299420L));
      f.add(new b(2803603690421084788L));
      f.add(new b(-6809266529775316946L));
      f.add(new b(1639445337102696020L));
      f.add(new b(5944477930934255187L));
      f.add(new b(-4496720477628790408L));
      f.add(new b(-8684519667128845294L));
      f.add(new b(4910267865186814213L));
      f.add(new b(7128051422878982793L));
      f.add(new b(-8977140233255339738L));
      f.add(new b(-4405041247055589039L));
      f.add(new b(3868461199369729972L));
      f.add(new b(2312164800015833338L));
      f.add(new b(-702237624312922062L));
      f.add(new b(-6859228575981317417L));
      f.add(new b(-3259939662797203563L));
      f.add(new b(-3167829404984613474L));
      f.add(new b(-5294377758658563723L));
      f.add(new b(4016831580844469607L));
      f.add(new b(4937642025892460787L));
      f.add(new b(-5545364315164260177L));
      f.add(new b(4726041988281416639L));
      f.add(new b(-3279658630470669320L));
      f.add(new b(6004169986122039393L));
      f.add(new b(306291849220874758L));
      f.add(new b(-5368885731172699408L));
      f.add(new b(6190984895276665163L));
      f.add(new b(7115270684390253186L));
      f.add(new b(5122894281478204759L));
      f.add(new b(4635296964263321023L));
      f.add(new b(7078228005030890718L));
      f.add(new b(-9051001791719541255L));
      f.add(new b(7019695034469576853L));
      f.add(new b(-7616632260839310416L));
      f.add(new b(-5636428356435124195L));
      f.add(new b(4980314816751396500L));
      f.add(new b(-772417551426913517L));
      f.add(new b(-3453178994972218603L));
      f.add(new b(8558715308328380987L));
      f.add(new b(5111845789861939643L));
      f.add(new b(1287961085654743217L));
      f.add(new b(-980755211681150606L));
      f.add(new b(4880186740933115468L));
      f.add(new b(-4043002335883694039L));
      f.add(new b(-9159074830126698801L));
      f.add(new b(-958221859423237742L));
      f.add(new b(-7731844317622968297L));
      f.add(new b(-729789010315927609L));
      f.add(new b(-8701456668738599645L));
      f.add(new b(-4426642317219986696L));
      f.add(new b(-1970670963691417956L));
      f.add(new b(5847949101847306235L));
      f.add(new b(-6800194991004892259L));
      f.add(new b(-850635488681567098L));
      f.add(new b(6155593612502682727L));
      f.add(new b(-4447966940324937123L));
      f.add(new b(-2235219078673698781L));
      f.add(new b(7318829346202647793L));
      f.add(new b(6112282237784088200L));
      f.add(new b(-1842906280362596853L));
      f.add(new b(-5266098235896990770L));
      f.add(new b(-9222815718674421738L));
      f.add(new b(8580417906579615749L));
      f.add(new b(-4114236396830854250L));
      f.add(new b(-6386493082177611869L));
      f.add(new b(8388580856300133425L));
      f.add(new b(59451558383613178L));
      f.add(new b(3658299475908693632L));
      f.add(new b(-4086999753522927493L));
      f.add(new b(7395690972219334721L));
      f.add(new b(72324756315604372L));
      f.add(new b(-8014951791336914111L));
      f.add(new b(-4356631444422931898L));
      f.add(new b(3893422665014259964L));
      f.add(new b(1579076616826404401L));
      f.add(new b(1526897723801761354L));
      f.add(new b(-1877135681051516295L));
      f.add(new b(-875167094285353406L));
      f.add(new b(-1838272399758110745L));
      f.add(new b(6291356450738039983L));
      f.add(new b(-7894863910957243581L));
      f.add(new b(-2093016498039193767L));
      f.add(new b(-3272193923812589914L));
      f.add(new b(502943979505888582L));
      f.add(new b(2336851048249241528L));
      f.add(new b(-738861871123974708L));
      f.add(new b(-3203376464221824738L));
      f.add(new b(-1013715117051808161L));
      f.add(new b(-4044707875947592334L));
      f.add(new b(2366448023130775633L));
      f.add(new b(3538625845997004217L));
      f.add(new b(3758689349193917161L));
      f.add(new b(5139230031972559540L));
      f.add(new b(-4206017201217086313L));
      f.add(new b(-5224185204531394783L));
      f.add(new b(143064205739667119L));
      f.add(new b(294619749326848470L));
      f.add(new b(5174637109309334537L));
      f.add(new b(3628894686897257509L));
      f.add(new b(-1767189106742332658L));
      f.add(new b(3521614884729639404L));
      f.add(new b(-2000992414020327470L));
      f.add(new b(-6357105934051404485L));
      f.add(new b(-1836652669569773764L));
      f.add(new b(-6801100437637432730L));
      f.add(new b(-5326320675203081234L));
      f.add(new b(-5688729470271205892L));
      f.add(new b(-7884940544358775986L));
      f.add(new b(-1828040258795273068L));
      f.add(new b(-683133408062016132L));
      f.add(new b(-1108721891367832612L));
      f.add(new b(-6572944009017212461L));
      f.add(new b(-3322654775118060786L));
      f.add(new b(-6523060100931230713L));
      f.add(new b(3793142507413254792L));
      f.add(new b(2773448761976378245L));
      f.add(new b(-6659517626926091055L));
      f.add(new b(8573408368600447478L));
      f.add(new b(-3116364835885059247L));
      f.add(new b(2318086606316473347L));
      f.add(new b(537480731978998640L));
      e = (int[])e.clone();
      f.add(new b(6119723107557205593L));
      f.add(new b(8597329314988749831L));
      f.add(new b(3896672322626540210L));
      f.add(new b(4711917324712712871L));
      f.add(new b(-5600277749240259879L));
      f.add(new b(8337129185459208120L));
      f.add(new b(-9104543985502194356L));
      f.add(new b(-8660663141691217439L));
      f.add(new b(3798425845009672871L));
      f.add(new b(-5215628747627744510L));
      f.add(new b(-1964544019882649898L));
      f.add(new b(3521611718148699308L));
      f.add(new b(2446253797096709984L));
      f.add(new b(-1087276790224685562L));
      f.add(new b(-7892130863898344620L));
      f.add(new b(8444766521589266582L));
      f.add(new b(3867591518708691296L));
      f.add(new b(8358585515014171474L));
      f.add(new b(-1836305157017664455L));
      f.add(new b(4006577450305827531L));
      f.add(new b(-8774468387197782773L));
      f.add(new b(7097629903497357339L));
      f.add(new b(1409627525455749724L));
      f.add(new b(-3015035892985126893L));
      f.add(new b(-2947658444281480334L));
      f.add(new b(7143057676553274160L));
      f.add(new b(-1120966576047178242L));
      f.add(new b(7073250813243106934L));
      f.add(new b(-4072167323738822589L));
      f.add(new b(3574240279153591030L));
      f.add(new b(4893428646729342571L));
      f.add(new b(-1949914458836494812L));
      f.add(new b(1493556439163050479L));
      f.add(new b(4630564097094458008L));
      f.add(new b(-8832541366058608481L));
      f.add(new b(4956482398051248230L));
      f.add(new b(-6441325446491835272L));
      f.add(new b(-2955745404960205692L));
      f.add(new b(8539455407091840145L));
      f.add(new b(-1016941280240913311L));
      f.add(new b(5964884732152040305L));
      f.add(new b(3912713325744782639L));
      f.add(new b(7341211307475446253L));
      f.add(new b(7087269958750547480L));
      f.add(new b(8337188103064560174L));
      f.add(new b(-5518066177436299321L));
      f.add(new b(3119650737037743L));
      f.add(new b(-2181038824725174301L));
      f.add(new b(-8060714082135395273L));
      f.add(new b(-5252812702869946595L));
      f.add(new b(1566510439334028856L));
      f.add(new b(4797204001604813031L));
      f.add(new b(2464888627858322005L));
      f.add(new b(-6353155792441386385L));
      f.add(new b(6200530005313516790L));
      f.add(new b(-2014150370263795665L));
      f.add(new b(-5570999628788886270L));
      f.add(new b(2871121824117206709L));
      f.add(new b(8644172612217215695L));
      f.add(new b(-2242964945965175831L));
      f.add(new b(8263097832476851618L));
      f.add(new b(-7904289956272972276L));
      f.add(new b(7323001393680900472L));
      f.add(new b(8351681072026928106L));
      f.add(new b(-5206061806878821666L));
      f.add(new b(7337792854961204176L));
      f.add(new b(-5480889411902341701L));
      f.add(new b(2402365709655220487L));
      f.add(new b(-4045024563323271516L));
      f.add(new b(-4354781203737329247L));
      f.add(new b(568867287734172248L));
      f.add(new b(-4328466525977750714L));
      f.add(new b(6115158167062595127L));
      f.add(new b(1402959159202051979L));
      f.add(new b(147981835260157044L));
      f.add(new b(7383973202874203858L));
      f.add(new b(-8811933163398909047L));
      f.add(new b(-1075835039207491023L));
      f.add(new b(5018518379590586993L));
      f.add(new b(-947473270089374821L));
      f.add(new b(268334838721535790L));
      f.add(new b(-5743647425543053901L));
      f.add(new b(-9118919462340812756L));
      f.add(new b(8621432068921083968L));
      f.add(new b(-3142904295715923155L));
      f.add(new b(2425036144501569372L));
      f.add(new b(-5271774272368048970L));
      f.add(new b(5027447156751842118L));
      f.add(new b(3595690871110261702L));
      f.add(new b(6093244548300481980L));
      f.add(new b(8290130817405449107L));
      f.add(new b(-9098383617634143933L));
      f.add(new b(297472958266735135L));
      f.add(new b(-5567542861554597568L));
      f.add(new b(2420533792738606849L));
      f.add(new b(4614593283558568300L));
      f.add(new b(-1878252813233220172L));
      f.add(new b(-5713890741281652011L));
      f.add(new b(-2121200680204113331L));
      f.add(new b(-996257495390304257L));
      f.add(new b(2849776905117814295L));
      f.add(new b(3908340496850759055L));
      f.add(new b(-7657578106555140427L));
      f.add(new b(-3225106117812799251L));
      f.add(new b(2726528112340481823L));
      f.add(new b(2346347551603770583L));
      f.add(new b(236425561399247697L));
      f.add(new b(-5620019170100575558L));
      f.add(new b(7339085072803302504L));
      f.add(new b(-8002422358265174398L));
      f.add(new b(1270328043253553388L));
      f.add(new b(-1905035376799861581L));
      f.add(new b(-2214808577672075764L));
      f.add(new b(6124238551698359953L));
      f.add(new b(6236484641857437844L));
      f.add(new b(-5511575947227782818L));
      f.add(new b(-2029326383067596828L));
      f.add(new b(7097611914939860228L));
      f.add(new b(3670010319917438024L));
      f.add(new b(-8859549200748016306L));
      f.add(new b(2860105101482743877L));
      f.add(new b(5779991884239268084L));
      f.add(new b(-5522350916635283809L));
      f.add(new b(-8988833026466102372L));
      f.add(new b(-1128125855238998078L));
      f.add(new b(5899775170254761879L));
      f.add(new b(6110256196043114798L));
      f.add(new b(-7789601166275553754L));
      f.add(new b(-6724407209052836827L));
      f.add(new b(4896593390999210351L));
      f.add(new b(-5454496145208663652L));
      f.add(new b(6946595102900967184L));
      f.add(new b(-1067152008537422596L));
      f.add(new b(1398220228665882545L));
      f.add(new b(5921717947677696957L));
      f.add(new b(-6362061313000197518L));
      f.add(new b(6235690416252137098L));
      f.add(new b(-4460290274805358825L));
      f.add(new b(8450495271391268659L));
      f.add(new b(8581261088041688323L));
      f.add(new b(8634323512396440295L));
      f.add(new b(1720287047289485892L));
      f.add(new b(-7656830285682773375L));
      f.add(new b(-654743152325295047L));
      f.add(new b(3645549698133179694L));
      f.add(new b(310287065962840247L));
      f.add(new b(1675729587902067477L));
      f.add(new b(-715568384888521024L));
      f.add(new b(8285464067052334006L));
      f.add(new b(376907088193792778L));
      f.add(new b(-679576609509952970L));
      f.add(new b(1630036573652078321L));
      f.add(new b(3771486243898919823L));
      f.add(new b(1379521836681317215L));
      f.add(new b(138224812237837333L));
      f.add(new b(556274544238132054L));
      f.add(new b(2590212131707962339L));
      f.add(new b(-7914054295664266307L));
      f.add(new b(-4536004749630400204L));
      f.add(new b(-699529581658560407L));
      f.add(new b(2462226023653041349L));
      f.add(new b(3692841112059934736L));
      f.add(new b(-1006436577844664428L));
      f.add(new b(1519815246430894171L));
      f.add(new b(8346679074856944888L));
      f.add(new b(6333835761048456239L));
      f.add(new b(-3164699547798742512L));
      f.add(new b(-2179040368767636713L));
      f.add(new b(-4351287108843516081L));
      f.add(new b(-2977517434787985771L));
      f.add(new b(-652624649000395839L));
      f.add(new b(-7647969870801395345L));
      f.add(new b(4830130233572113957L));
      f.add(new b(-6619573011296707007L));
      f.add(new b(-6851560624939067977L));
      f.add(new b(-2884278560578886228L));
      f.add(new b(-8832905906794672326L));
      f.add(new b(-4188625065735496850L));
      f.add(new b(7478732037352403787L));
      f.add(new b(7190693578158431828L));
      f.add(new b(-8681743302690945167L));
      f.add(new b(2497157564128900784L));
      f.add(new b(180305937521937831L));
      f.add(new b(-2087271572887410178L));
      f.add(new b(-8809871658723851012L));
      f.add(new b(241412938912275138L));
      f.add(new b(-3364175791753496605L));
      f.add(new b(4897916051403537946L));
      f.add(new b(-5513909041534595083L));
      f.add(new b(7106569253228617207L));
      f.add(new b(7455585394141657596L));
      f.add(new b(-7898717748861384119L));
      f.add(new b(-896324817097673176L));
      f.add(new b(6285818730877739321L));
      f.add(new b(-1840616575010519697L));
      f.add(new b(-746706925341966074L));
      f.add(new b(5016363013579518858L));
      f.add(new b(558472269209539373L));
      f.add(new b(-636990937094483895L));
      f.add(new b(8128755083744022355L));
      f.add(new b(1503399435232002011L));
      f.add(new b(308765769064346388L));
      f.add(new b(3656218428609083138L));
      f.add(new b(2380068579383201608L));
      f.add(new b(-1078433619666069374L));
      f.add(new b(8387471728293221273L));
      f.add(new b(4685991376104766940L));
      f.add(new b(6077261844126989069L));
      f.add(new b(8360121067962504201L));
      f.add(new b(-1962479779838105262L));
      f.add(new b(-9160524997579229843L));
      f.add(new b(161898533023257849L));
      f.add(new b(-771570679441907137L));
      f.add(new b(-972078646660881412L));
      f.add(new b(-6556531524502350075L));
      f.add(new b(7450341470684344530L));
      f.add(new b(-846894225108287214L));
      f.add(new b(-2105175187986272631L));
      f.add(new b(-8850853104172645341L));
      f.add(new b(403265082130202206L));
      f.add(new b(7060655116568624456L));
      f.add(new b(-2114821930431406908L));
      f.add(new b(3694922411138365548L));
      f.add(new b(-1981568524648199170L));
      f.add(new b(-4548664112252684308L));
      f.add(new b(-7890537388624873579L));
      f.add(new b(-7932613123866020754L));
      f.add(new b(-7875839089378243863L));
      f.add(new b(7127970133517670449L));
      f.add(new b(5170086211589109612L));
      f.add(new b(-6382752775688143728L));
      f.add(new b(8582805269342925694L));
      f.add(new b(8452631973415342079L));
      f.add(new b(227786199621291981L));
      f.add(new b(8473797213458798150L));
      f.add(new b(-4242138452128064813L));
      f.add(new b(-6790442366638951646L));
      f.add(new b(4898418596603140503L));
      f.add(new b(-5266485953786288452L));
      f.add(new b(7134941022581466542L));
      f.add(new b(-7967843974349076732L));
      f.add(new b(6325637121058595894L));
      f.add(new b(-1076902959229108374L));
      f.add(new b(-2292281153892242409L));
      f.add(new b(-8754880373211878568L));
      f.add(new b(1253438724212433092L));
      f.add(new b(-9039503906426713826L));
      f.add(new b(-2098920963290338686L));
      f.add(new b(8551805848227012954L));
      f.add(new b(1244855760807947919L));
      f.add(new b(-5573139109698587420L));
      f.add(new b(5854277458278487473L));
      f.add(new b(-2919021304972052464L));
      f.add(new b(-3066915717680830409L));
      f.add(new b(2869481665578341652L));
      f.add(new b(-841450359289580471L));
      e = (int[])e.clone();
      f.add(new b(1487662770479616720L));
      f.add(new b(8578117930487813241L));
      f.add(new b(-5685856604351934503L));
      f.add(new b(-5281289858816498345L));
      f.add(new b(-6751707241410286668L));
      f.add(new b(-4418920178666521678L));
      f.add(new b(5118210108447090275L));
      f.add(new b(-7977837870286205675L));
      f.add(new b(2507924614361749221L));
      f.add(new b(-7592507413508272010L));
      f.add(new b(4687375328527784604L));
      f.add(new b(-4121489652431540066L));
      f.add(new b(-7732536595025125557L));
      f.add(new b(2330809004391677397L));
      f.add(new b(-998488915787139827L));
      f.add(new b(-3202435771667166743L));
      f.add(new b(-3206604362705222427L));
      f.add(new b(-1806665694845486050L));
      f.add(new b(5886900598865531142L));
      f.add(new b(-1117764883782136547L));
      f.add(new b(-4453257055966882010L));
      f.add(new b(3541963512216189426L));
      f.add(new b(-1977229751928802029L));
      f.add(new b(-7782933398791066716L));
      f.add(new b(-9207961721021738538L));
      f.add(new b(-7763010522084181294L));
      f.add(new b(3856607072389697589L));
      f.add(new b(-6909448808919711763L));
      f.add(new b(-6363954138109837751L));
      f.add(new b(8370594742554905963L));
      f.add(new b(-5570136955416566091L));
      f.add(new b(-7673060412903882638L));
      f.add(new b(-3027971712489069028L));
      f.add(new b(1154982538269633131L));
      f.add(new b(-6443560146053658407L));
      f.add(new b(-9011320750389470738L));
      f.add(new b(2318413118332707200L));
      f.add(new b(-6476998449480496131L));
      f.add(new b(5152029366522375485L));
      f.add(new b(-4535928585653525014L));
      f.add(new b(6195275091881064312L));
      f.add(new b(3863569215510667942L));
      f.add(new b(7487841669864611202L));
      f.add(new b(2331966922800757766L));
      f.add(new b(-4356060312227157244L));
      f.add(new b(-9122316328246523066L));
      f.add(new b(-615116593552458508L));
      f.add(new b(-5645584240633492013L));
      f.add(new b(1349849987925304778L));
      f.add(new b(6204937728363881700L));
      f.add(new b(-712643258717849678L));
      f.add(new b(-6475302118330642688L));
      f.add(new b(7108560086238765551L));
      f.add(new b(-5372440115921991815L));
      f.add(new b(-2962895105669778697L));
      f.add(new b(5010680073098758979L));
      f.add(new b(-5513979972470554336L));
      f.add(new b(5984221163107983771L));
      f.add(new b(-6830039629147669210L));
      f.add(new b(5766475316184790184L));
      f.add(new b(8161664709251169546L));
      f.add(new b(-8648479158658339902L));
      f.add(new b(4930800843990916644L));
      f.add(new b(-9139482599016796097L));
      f.add(new b(-5639046332290335498L));
      f.add(new b(3649516216378192231L));
      f.add(new b(5102226409677298113L));
      f.add(new b(1627793791671639174L));
      f.add(new b(8271578921443762006L));
      f.add(new b(-2953834427918682845L));
      f.add(new b(5839507964881389853L));
      f.add(new b(-8988188590692636328L));
      f.add(new b(7345010324722447093L));
      f.add(new b(257037597263371203L));
      f.add(new b(-1749328679739888991L));
      f.add(new b(-8061882434948749946L));
      f.add(new b(6220260748509486574L));
      f.add(new b(-7896009637694898064L));
      f.add(new b(-5593481338177651105L));
      f.add(new b(-5294102775103116050L));
      f.add(new b(-7512679404932691811L));
      f.add(new b(533027527317224542L));
      f.add(new b(-720664978012160220L));
      f.add(new b(-8005959938866135911L));
      f.add(new b(-5742028178877855601L));
      f.add(new b(8634168960842102275L));
      f.add(new b(-702489224765747856L));
      f.add(new b(5115422709815995107L));
      f.add(new b(-1935898864046105295L));
      f.add(new b(6106018266837093594L));
      f.add(new b(-4464410678403398966L));
      f.add(new b(8564519811884327937L));
      f.add(new b(-7569615398008643854L));
      f.add(new b(8113511913301123929L));
      f.add(new b(13284394265693179L));
      f.add(new b(-4214426426489342687L));
      f.add(new b(6979112976994167549L));
      f.add(new b(-6513962483194362403L));
      f.add(new b(1298174531388644372L));
      f.add(new b(8103558850950434286L));
      f.add(new b(-6769119020617355496L));
      f.add(new b(-5406539018921188814L));
      f.add(new b(2717939664546562722L));
      f.add(new b(7194169074273283206L));
      f.add(new b(-787084639334289790L));
      f.add(new b(-4073692005681200316L));
      f.add(new b(7258174128270349569L));
      f.add(new b(8254711807140401752L));
      f.add(new b(-7543138259562294799L));
      f.add(new b(5875122118975681610L));
      f.add(new b(7215400265041082640L));
      f.add(new b(2576495085669365167L));
      f.add(new b(-4088917550572674438L));
      f.add(new b(-5340028028988201797L));
      f.add(new b(452056441704448210L));
      f.add(new b(4842030937988214309L));
      f.add(new b(6932270134450965062L));
      f.add(new b(565240350741666790L));
      f.add(new b(-844952032478937202L));
      f.add(new b(-4429447830055683327L));
      f.add(new b(1474694719206970501L));
      f.add(new b(2854877029793825738L));
      f.add(new b(-7999475481463355114L));
      f.add(new b(3979335937629605871L));
      f.add(new b(-4421560930939351317L));
      f.add(new b(-4549698583858955919L));
      f.add(new b(8609515359741055851L));
      f.add(new b(-9058079622073769922L));
      f.add(new b(-4432661495622373683L));
      f.add(new b(4972921213748737714L));
      f.add(new b(-7884682879912615640L));
      f.add(new b(-2084201799376565015L));
      f.add(new b(-4588894767322693829L));
      f.add(new b(-5522955135712413173L));
      f.add(new b(68932211586294841L));
      f.add(new b(2822357885855531931L));
      f.add(new b(-8986501031228384632L));
      f.add(new b(4729875749709588620L));
      f.add(new b(-4456025705591736768L));
      f.add(new b(8614641742479605355L));
      f.add(new b(7445645338857047984L));
      f.add(new b(3524673697563480621L));
      f.add(new b(3566361958269993253L));
      f.add(new b(6111203260338362205L));
      f.add(new b(-6706663518964897270L));
      f.add(new b(2832436991978457791L));
      f.add(new b(-6813234028165709450L));
      f.add(new b(357272857320213398L));
      f.add(new b(6216137054416770765L));
      f.add(new b(3459677942993555756L));
      f.add(new b(-3076076134305297913L));
      f.add(new b(-2213862438640520504L));
      f.add(new b(-7952144700885609045L));
      f.add(new b(4644119599136713820L));
      f.add(new b(1931085521544996L));
      f.add(new b(8605288627689898485L));
      f.add(new b(-2083515897572409311L));
      f.add(new b(-8709677596182599360L));
      e = (int[])e.clone();
      f.add(new b(-653205076545047825L));
      f.add(new b(5797543421679334898L));
      f.add(new b(-596743070022369192L));
      f.add(new b(-1939816268830634328L));
      f.add(new b(-5418312832211728699L));
      f.add(new b(-1006586830114833363L));
      f.add(new b(-2019234621202333079L));
      f.add(new b(-2999392616148863867L));
      f.add(new b(3670874067568683485L));
      f.add(new b(4682163872118861773L));
      f.add(new b(2528755961145499712L));
      f.add(new b(-1958073446116904369L));
      f.add(new b(-9179078764871262070L));
      f.add(new b(342375624703703030L));
      f.add(new b(1652385225036748428L));
      f.add(new b(3623817395114434182L));
      f.add(new b(-7817363573909595370L));
      f.add(new b(6223385160015434627L));
      f.add(new b(-9123128122574916681L));
      f.add(new b(-1734358140125640479L));
      f.add(new b(-6532137755598008024L));
      f.add(new b(-4230166390682668265L));
      f.add(new b(-7781609166882627034L));
      f.add(new b(-7707072001281017450L));
      f.add(new b(7137145612288201112L));
      f.add(new b(-6860831683849754583L));
      f.add(new b(3765279396914002784L));
      f.add(new b(7090324897983048070L));
      f.add(new b(30208265395099739L));
      f.add(new b(-8059499444937613168L));
      f.add(new b(-3339456942018755402L));
      f.add(new b(-4175161276382134437L));
      f.add(new b(442704737546104051L));
      f.add(new b(5889090174852383241L));
      f.add(new b(2323617978579979312L));
      f.add(new b(-6454494080400865307L));
      f.add(new b(1189004702597933617L));
      f.add(new b(-5564325616758825717L));
      f.add(new b(7272007408021246702L));
      f.add(new b(-1962548770569383407L));
      f.add(new b(-7876144614072691233L));
      f.add(new b(-3332966938189116435L));
      f.add(new b(-8872893169832515323L));
      f.add(new b(1162040482206494516L));
      f.add(new b(-4426285145636175458L));
      f.add(new b(5779190324074191981L));
      f.add(new b(-4608928546906162394L));
      f.add(new b(8139132700655232920L));
      f.add(new b(-7607887739954416629L));
      f.add(new b(-4064936704108218272L));
      f.add(new b(1181434679061928686L));
      f.add(new b(-1914364837562601454L));
      f.add(new b(540975161415069466L));
      f.add(new b(-3308844326718329295L));
      f.add(new b(1298871282378397737L));
      f.add(new b(7168508311974741534L));
      f.add(new b(-5693217259779389982L));
      f.add(new b(2512733689835690690L));
      f.add(new b(-735966763808075050L));
      f.add(new b(-2106107418417512627L));
      f.add(new b(5771331863618246957L));
      f.add(new b(244189481792193285L));
      f.add(new b(-9092777210393761422L));
      e = new int[]{
         -16,
         -26,
         -33,
         -8,
         -15,
         -21,
         -43,
         -18,
         -12,
         -44,
         -2,
         8,
         2,
         -29,
         -23,
         -48,
         16,
         -23,
         -14,
         15,
         12,
         -34,
         -26,
         -34,
         -6,
         18,
         21,
         26,
         -13,
         -32,
         6,
         -12,
         14,
         -13,
         -10,
         33,
         -15,
         23,
         -22,
         -23,
         23,
         13,
         29,
         12,
         10,
         -13,
         13,
         -7,
         26,
         43,
         -9,
         15,
         -4,
         44,
         7,
         34,
         4,
         34,
         13,
         9,
         22,
         32,
         23,
         48
      };
   }
}
