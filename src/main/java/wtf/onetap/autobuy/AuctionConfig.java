package wtf.onetap.autobuy;

public class AuctionConfig {
   private static final AuctionConfig AUCTION_CONFIG = new AuctionConfig();
   private boolean flag;

   private AuctionConfig() {
   }

   public static AuctionConfig getConfig() {
      return AUCTION_CONFIG;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setFlag(boolean flag2) {
      this.flag = flag2;
   }

   public void toggle() {
      this.flag = !this.flag;
   }
}
