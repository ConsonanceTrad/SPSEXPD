/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd;

import pd.messages.Messages;
import render.utils.Bundle;
import render.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * SPS 0.9.9 礼物商店的永久强化解锁。
 *
 * <p>每项解锁以 S金（{@link SPDSettings#sCoin()}）购买，购买结果存档于 {@link #GIFTUNLOCKS_FILE}，
 * 跨存档永久生效。开局时由 {@code HeroClass.initGift} 按已购项目发放属性与初始物品。
 *
 * <p>与 0.9.9 的差异（用户裁决 2026-09-30）：本次只接入礼物商店消费端，
 * {@code TRIBE_BUILD}/{@code TRIBE_BUILD_TWO}（S金炼金/锻造）与依赖未迁移物品的
 * {@code DEF_ROBOT}（壁垒无人机信标）、{@code OVERSEAS_ARTITEM}（跳舞人偶）不上架售卖，
 * 避免出现购买后无效果的假内容；其余项目照 0.9.9 数值生效。
 *
 * <p>0.9.9 中 {@code OVERSEAS_UNKNOW}（文案“初始携带5S金”）的发放逻辑是死代码，
 * 本次按其文案接线为开局 +5 S金。
 */
public class GiftUnlocks {

	public static final String GIFTUNLOCKS_FILE = "giftunlocks.dat";
	private static final String UNLOCKED = "giftunlocks";

	private static HashSet<GiftUnlock> global = null;
	private static boolean saveNeeded = false;

	public enum GiftUnlock {
		//                badges 图集帧, S金售价
		START           (67,  0),
		DEF_BASE        (57,  0),
		DEF_HT          (12,  5),
		DEF_SEED        (21, 10),
		DEF_ROBOT       (68, 15),
		DEF_TECH_MECH   (68, 10),
		TOWER_BASE      (11,  0),
		TOWER_MIG       (19, 10),
		TOWER_WAND      (19, 15),
		TOWER_EXP       (19,  5),
		PALACE_BASE     (23,  0),
		PALACE_GOLD     ( 4, 10),
		PALACE_RING     ( 4, 10),
		PALACE_CROWN    ( 4, 10),
		RUINS_BASE      (38,  0),
		RUINS_DEX       (73, 10),
		RUINS_HT        (12,  5),
		RUINS_SEED      (21, 10),
		HORN_BASE       (37,  0),
		HORN_ACU        (72,  5),
		HORN_HT         (12,  5),
		HORN_WEAPON     (72, 15),
		HORN_ARMOR      (73, 15),
		TRIBE_BASE      (74,  0),
		TRIBE_EXP       (48, 10),
		TRIBE_HT        (12,  5),
		TRIBE_BUILD     (12, 10),
		TRIBE_BUILD_TWO (12, 20),
		HOMELESS_BASE   (59,  0),
		HOMELESS_LUCKY  (28, 10),
		HOMELESS_HT     (12,  5),
		HOMELESS_PLANT  (52, 15),
		OVERSEAS_BASE   (58,  0),
		OVERSEAS_UNKNOW (22,  5),
		OVERSEAS_ROCKET (19, 15),
		OVERSEAS_ARTITEM(39,  5);

		public final int image;
		public final int price;

		GiftUnlock( int image, int price ) {
			this.image = image;
			this.price = price;
		}

		public String title() {
			return Messages.get( this, name() + ".title" );
		}

		public String desc() {
			return Messages.get( this, name() + ".desc" );
		}
	}

	public static void loadGlobal() {
		if (global == null) {
			try {
				Bundle bundle = FileUtils.bundleFromFile( GIFTUNLOCKS_FILE );
				global = restore( bundle );
			} catch (Exception e) {
				//档案缺失、损坏或文件系统不可用时按“无解锁”继续（新开局、headless 测试等）
				global = new HashSet<>();
			}
		}
	}

	public static void saveGlobal() {
		saveGlobal( false );
	}

	public static void saveGlobal( boolean force ) {
		if (saveNeeded || force) {
			loadGlobal();
			Bundle bundle = new Bundle();
			store( bundle, global );
			try {
				FileUtils.bundleToFile( GIFTUNLOCKS_FILE, bundle );
				saveNeeded = false;
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException( e );
			}
		}
	}

	private static HashSet<GiftUnlock> restore( Bundle bundle ) {
		HashSet<GiftUnlock> result = new HashSet<>();
		for (String name : bundle.getStringArray( UNLOCKED )) {
			try {
				result.add( GiftUnlock.valueOf( name ) );
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException( e );
			}
		}
		return result;
	}

	private static void store( Bundle bundle, HashSet<GiftUnlock> set ) {
		String[] names = new String[set.size()];
		int i = 0;
		for (GiftUnlock unlock : set) {
			names[i++] = unlock.name();
		}
		bundle.put( UNLOCKED, names );
	}

	public static boolean isUnlocked( GiftUnlock unlock ) {
		loadGlobal();
		return global.contains( unlock );
	}

	/** 购买一项解锁；幂等，已购项目不会重复入账。 */
	public static void buyOneGift( GiftUnlock unlock ) {
		loadGlobal();
		if (unlock == null || global.contains( unlock )) return;
		global.add( unlock );
		saveNeeded = true;
	}

	/** 已购项目（枚举顺序）。 */
	public static List<GiftUnlock> owned() {
		loadGlobal();
		List<GiftUnlock> result = new ArrayList<>( global );
		Collections.sort( result );
		return result;
	}

	/**
	 * 商店上架列表：按 0.9.9 的前置树过滤（未购 START 不上架各分支 *_BASE，
	 * 未购 *_BASE 不上架分支子项），并隐藏本次不接入的四项（见类注释）。
	 */
	public static List<GiftUnlock> filtered() {
		loadGlobal();
		List<GiftUnlock> result = new ArrayList<>();
		for (GiftUnlock unlock : GiftUnlock.values()) {
			if (!listed( unlock )) continue;
			result.add( unlock );
		}
		if (!global.contains( GiftUnlock.START )) {
			result.remove( GiftUnlock.DEF_BASE );
			result.remove( GiftUnlock.TOWER_BASE );
			result.remove( GiftUnlock.PALACE_BASE );
			result.remove( GiftUnlock.RUINS_BASE );
			result.remove( GiftUnlock.HORN_BASE );
			result.remove( GiftUnlock.TRIBE_BASE );
			result.remove( GiftUnlock.HOMELESS_BASE );
			result.remove( GiftUnlock.OVERSEAS_BASE );
		}
		if (!global.contains( GiftUnlock.DEF_BASE )) {
			result.remove( GiftUnlock.DEF_HT );
			result.remove( GiftUnlock.DEF_SEED );
			result.remove( GiftUnlock.DEF_ROBOT );
			result.remove( GiftUnlock.DEF_TECH_MECH );
		}
		if (!global.contains( GiftUnlock.TOWER_BASE )) {
			result.remove( GiftUnlock.TOWER_MIG );
			result.remove( GiftUnlock.TOWER_WAND );
			result.remove( GiftUnlock.TOWER_EXP );
		}
		if (!global.contains( GiftUnlock.PALACE_BASE )) {
			result.remove( GiftUnlock.PALACE_GOLD );
			result.remove( GiftUnlock.PALACE_RING );
			result.remove( GiftUnlock.PALACE_CROWN );
		}
		if (!global.contains( GiftUnlock.RUINS_BASE )) {
			result.remove( GiftUnlock.RUINS_DEX );
			result.remove( GiftUnlock.RUINS_HT );
			result.remove( GiftUnlock.RUINS_SEED );
		}
		if (!global.contains( GiftUnlock.HORN_BASE )) {
			result.remove( GiftUnlock.HORN_ACU );
			result.remove( GiftUnlock.HORN_HT );
			result.remove( GiftUnlock.HORN_WEAPON );
			result.remove( GiftUnlock.HORN_ARMOR );
		}
		if (!global.contains( GiftUnlock.TRIBE_BASE )) {
			result.remove( GiftUnlock.TRIBE_EXP );
			result.remove( GiftUnlock.TRIBE_HT );
		}
		if (!global.contains( GiftUnlock.HOMELESS_BASE )) {
			result.remove( GiftUnlock.HOMELESS_LUCKY );
			result.remove( GiftUnlock.HOMELESS_HT );
			result.remove( GiftUnlock.HOMELESS_PLANT );
		}
		if (!global.contains( GiftUnlock.OVERSEAS_BASE )) {
			result.remove( GiftUnlock.OVERSEAS_UNKNOW );
			result.remove( GiftUnlock.OVERSEAS_ROCKET );
			result.remove( GiftUnlock.OVERSEAS_ARTITEM );
		}
		return result;
	}

	//本次范围外的两项不进入商店：S金炼金/锻造消费端未接入，上架会出现购买后无效果的假内容。
	//DEF_ROBOT（壁垒无人机信标）与 OVERSEAS_ARTITEM（跳舞人偶）已迁移（用户裁决 2026-09-30）。
	private static boolean listed( GiftUnlock unlock ) {
		switch (unlock) {
			case TRIBE_BUILD:
			case TRIBE_BUILD_TWO:
				return false;
			default:
				return true;
		}
	}

	// ------------------------------------------------------------------
	// 开局奖励计数（对应 0.9.9 GiftUnlocks 的 *GiftisUsed() 系列方法）
	// ------------------------------------------------------------------

	/** 0.9.9 HTGiftisUsed：每项 +2 生命上限。 */
	public static int htGiftBonus() {
		loadGlobal();
		int bonus = 0;
		if (global.contains( GiftUnlock.DEF_HT )) bonus += 2;
		if (global.contains( GiftUnlock.RUINS_HT )) bonus += 2;
		if (global.contains( GiftUnlock.HORN_HT )) bonus += 2;
		if (global.contains( GiftUnlock.TRIBE_HT )) bonus += 2;
		if (global.contains( GiftUnlock.HOMELESS_HT )) bonus += 2;
		return bonus;
	}

	/** 0.9.9 ACUGiftisUsed：初始命中 +1。 */
	public static int hitGiftBonus() {
		return isUnlocked( GiftUnlock.HORN_ACU ) ? 1 : 0;
	}

	/** 0.9.9 DEXGiftisUsed：初始闪避 +1。 */
	public static int evadeGiftBonus() {
		return isUnlocked( GiftUnlock.RUINS_DEX ) ? 1 : 0;
	}

	/** 0.9.9 MIGGiftisUsed：初始法强 +1。 */
	public static int magicGiftBonus() {
		return isUnlocked( GiftUnlock.TOWER_MIG ) ? 1 : 0;
	}

	/** 0.9.9 LUCKYGiftisUsed：初始幸运 +1。 */
	public static int luckyGiftBonus() {
		return isUnlocked( GiftUnlock.HOMELESS_LUCKY ) ? 1 : 0;
	}

	/** 0.9.9 EXPGiftisUsed：每项 +5 初始经验。 */
	public static int expGiftBonus() {
		loadGlobal();
		int bonus = 0;
		if (global.contains( GiftUnlock.TOWER_EXP )) bonus += 5;
		if (global.contains( GiftUnlock.TRIBE_EXP )) bonus += 5;
		return bonus;
	}

	/** 0.9.9 GOLDGiftisUsed：初始金币 +100。 */
	public static int goldGiftBonus() {
		return isUnlocked( GiftUnlock.PALACE_GOLD ) ? 100 : 0;
	}

	/** 0.9.9 SGOLDGiftisUsed（0.9.9 未接线，此处按文案接线）：开局 +5 S金。 */
	public static int sCoinGiftBonus() {
		return isUnlocked( GiftUnlock.OVERSEAS_UNKNOW ) ? 5 : 0;
	}

	/** 0.9.9 SEEDGiftisUsed：每项初始种子 +1。 */
	public static int seedGiftCount() {
		loadGlobal();
		int count = 0;
		if (global.contains( GiftUnlock.DEF_SEED )) count++;
		if (global.contains( GiftUnlock.RUINS_SEED )) count++;
		return count;
	}

	/** 0.9.9 PLANTGiftisUsed：初始携带花盆。 */
	public static int plantGiftCount() {
		return isUnlocked( GiftUnlock.HOMELESS_PLANT ) ? 1 : 0;
	}

	/** 0.9.9 WEAPONGiftisUsed：初始携带随机 +1 鉴定武器。 */
	public static int weaponGiftCount() {
		return isUnlocked( GiftUnlock.HORN_WEAPON ) ? 1 : 0;
	}

	/** 0.9.9 ROBOTGiftisUsed：初始携带壁垒支援用无人机。 */
	public static int robotGiftCount() {
		return isUnlocked( GiftUnlock.DEF_ROBOT ) ? 1 : 0;
	}

	/** 0.9.9 ARTITEMGiftisUsed：初始携带跳舞人偶。 */
	public static int artItemGiftCount() {
		return isUnlocked( GiftUnlock.OVERSEAS_ARTITEM ) ? 1 : 0;
	}

	/** 0.9.9 ARMORGiftisUsed：初始携带随机 +1 鉴定护甲。 */
	public static int armorGiftCount() {
		return isUnlocked( GiftUnlock.HORN_ARMOR ) ? 1 : 0;
	}

	/** 0.9.9 ROCKETGiftisUsed：初始携带火箭。 */
	public static int rocketGiftCount() {
		return isUnlocked( GiftUnlock.OVERSEAS_ROCKET ) ? 1 : 0;
	}

	/** 0.9.9 RINGGiftisUsed：初始携带随机负等级戒指。 */
	public static int ringGiftCount() {
		return isUnlocked( GiftUnlock.PALACE_RING ) ? 1 : 0;
	}

	/** 0.9.9 ARTIFACTGiftisUsed：初始携带诺姆林王冠。 */
	public static int artifactGiftCount() {
		return isUnlocked( GiftUnlock.PALACE_CROWN ) ? 1 : 0;
	}

	/** 0.9.9 WANDGiftisUsed：初始携带测试法杖。 */
	public static int wandGiftCount() {
		return isUnlocked( GiftUnlock.TOWER_WAND ) ? 1 : 0;
	}

	/** 0.9.9 UpGradeGiftisUsed：每项初始携带一张升级卷轴。 */
	public static int upgradeGiftCount() {
		return isUnlocked( GiftUnlock.DEF_TECH_MECH ) ? 1 : 0;
	}

	/** 测试与坏档恢复用：清空内存状态，下次访问重新读档。 */
	public static void resetForTesting() {
		global = null;
		saveNeeded = false;
	}
}
