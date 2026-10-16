/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.BrokenSeal;
import pd.items.Item;
import pd.items.SealComplete;
import pd.items.SealShard;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.EndlessAmmoBag;
import pd.items.equipment.artifacts.ScrollEarring;
import pd.items.equipment.wands.SilverHand;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.missiles.throwing.Boomerang;
import pd.items.misc.FaithSign;
import pd.items.misc.Futon;
import pd.items.misc.GauntletShield;
import pd.items.misc.GunOfSoldier;
import pd.items.misc.Shovel;
import pd.items.unique.items.BraceletArmor;
import pd.items.unique.items.ClogArmor;
import pd.items.unique.items.Megaphone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * SPSEXPD: 特殊物品机制的门面 —— 判定、整族转换、发放。
 *
 * <p>「换成哪一件」由 {@link UniqueItemId} 的 (family, tier, branch) 直接推导：
 * 一档强化 = 同族 tier 1 的某个 branch；二次强化 = 同族同 branch 的 tier 2。
 * 本类还维护 {@code 编号 → 物品构造器} 的注册表：未显式登记的一律落到
 * {@link PlaceholderUniqueItem}。</p>
 */
public final class UniqueItems {

	private UniqueItems() { }

	private static final Map<UniqueItemId, Supplier<Item>> REGISTRY = new HashMap<>();

	static {
		// 已有独立类的物品
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_BASE_A, BrokenSeal::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_BASE_B, SealShard::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T1_1_FULL, SealComplete::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_BASE_A, MagesStaff::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_BASE_B, WandOfMagicMissile::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_BASE_A, CloakOfShadows::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_BASE_B, SilverHand::new );
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_BASE_A, Boomerang::new );
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_BASE_B, SpiritBow::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_BASE_A, GauntletShield::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_BASE_B, BraceletArmor::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_BASE_A, Shovel::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_BASE_B, Megaphone::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_BASE_A, GunOfSoldier::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_BASE_B, EndlessAmmoBag::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_BASE_A, Futon::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_BASE_B, FaithSign::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_BASE_A, ScrollEarring::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_BASE_B, ClogArmor::new );

		//SPSEXPD: 战士族其余五件（一档 B 的两颗宝珠 / 二档的镀金纹章与两颗宝珠）
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T1_2_BLOOD, pd.items.unique.items.BloodOrb::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T1_2_SOUL, pd.items.unique.items.SoulOrb::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T2_1_GILDED, pd.items.GildedSeal::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T2_2_BLOOD, pd.items.unique.items.CrimsonOrb::new );
		REGISTRY.put( UniqueItemId.WARRIOR_SEAL_T2_2_GLORY, pd.items.unique.items.GloryOrb::new );

		//SPSEXPD: 星兵族六件
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T1_1_YAKA, pd.items.unique.items.YakaArrow::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T1_2_CANNON, pd.items.unique.items.ElecHandCannon::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T1_2_AMMOBOX, pd.items.unique.items.AmmoBox::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T2_1_INJECT, pd.items.unique.items.InjectionArrow::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T2_2_MK87, pd.items.unique.items.MK87::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_T2_2_REACTOR, pd.items.unique.items.AlchemyReactor::new );

		//SPSEXPD: 法师族六件
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T1_1_CORE, pd.items.unique.items.ArcaneCore::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T1_2_MITHRIL, pd.items.unique.items.MythrilStaff::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T1_2_WATCH, pd.items.unique.items.FineWatch::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T2_1_TOME, pd.items.unique.items.ArcaneTome::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T2_2_ADAMANT, pd.items.unique.items.AdamantStaff::new );
		REGISTRY.put( UniqueItemId.MAGE_STAFF_T2_2_REVERSE, pd.items.unique.items.RewindWatch::new );

		//SPSEXPD: 盗贼族六件
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T1_1_HAND, pd.items.unique.items.GreatHand::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T1_2_NIGHT, pd.items.unique.items.NightBracelet::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T1_2_AGENT, pd.items.unique.items.AgentPistol::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T2_1_HAND, pd.items.unique.items.MasterHand::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T2_2_NIGHT, pd.items.unique.items.EternalBracelet::new );
		REGISTRY.put( UniqueItemId.ROGUE_CLOAK_T2_2_SILENT, pd.items.unique.items.SilentPistol::new );

		//SPSEXPD: 女猎手族四件
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_T1_1_DART, pd.items.unique.items.PsionicDart::new );
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_T1_2_BOOM, pd.items.unique.items.PsionicBoomerang::new );
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_T2_1_DART, pd.items.unique.items.PsionicDartMk2::new );
		REGISTRY.put( UniqueItemId.HUNTRESS_BOW_T2_2_BOOM, pd.items.unique.items.VoidBoomerang::new );

		//SPSEXPD: 信徒族六件
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_1_SCROLL, pd.items.unique.items.ThousandScroll::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_1_HORN, pd.items.unique.items.BaHuiHorn::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_2_TALISMAN, pd.items.unique.items.SealingTalisman::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_2_STAFF, pd.items.unique.items.PilgrimStaff::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_2_SIXWORD, pd.items.unique.items.SixWordMantra::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_2_RING, pd.items.unique.items.NineRingStaff::new );

		//SPSEXPD: 决斗族六件
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T1_1_RAPIER, pd.items.unique.items.BlueSteelRapier::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T2_1_NULL, pd.items.unique.items.NullRapier::new );

		// 其余编号一律用占位类
		for (UniqueItemId id : UniqueItemId.values()) {
			REGISTRY.computeIfAbsent( id, k -> () -> new PlaceholderUniqueItem( k ) );
		}
	}

	// ---------- 编号 / 物品 ----------

	/** 物品的编号；非特殊物品返回 null。 */
	public static UniqueItemId idOf( Item item ) {
		return item instanceof UniqueItem ? ((UniqueItem) item).uniqueId() : null;
	}

	public static boolean isUnique( Item item ) {
		return idOf( item ) != null;
	}

	/** 该编号对应的物品（新实例）。 */
	public static Item create( UniqueItemId id ) {
		if (id == null) return null;
		Supplier<Item> s = REGISTRY.get( id );
		return s == null ? null : s.get();
	}

	public static String nameOf( UniqueItemId id ) {
		if (id == null) return "?";
		Item it = create( id );
		return it != null ? it.name() : id.name();
	}

	// ---------- 族 / 档位 / 路线 ----------

	/** SPSEXPD: 英雄所属的族（按职业判定）。 */
	public static UniqueFamily familyOf( Hero hero ) {
		return hero == null ? null : UniqueFamily.of( hero.heroClass );
	}

	/** 按族 + 档位（可指定 branch，<0 表示不限）列出编号。 */
	public static List<UniqueItemId> idsOf( UniqueFamily family, int tier, int branch ) {
		List<UniqueItemId> out = new ArrayList<>();
		for (UniqueItemId id : UniqueItemId.values()) {
			if (id.family == family && id.tier == tier && (branch < 0 || id.branch == branch)) out.add( id );
		}
		return out;
	}

	/** 某档位下存在的所有路线号（升序）。 */
	public static List<Integer> branches( UniqueFamily family, int tier ) {
		List<Integer> out = new ArrayList<>();
		for (UniqueItemId id : UniqueItemId.values()) {
			if (id.family == family && id.tier == tier && !out.contains( id.branch )) out.add( id.branch );
		}
		out.sort( Integer::compareTo );
		return out;
	}

	/** 某条路线要发放的物品名清单（用于选择框文案）。 */
	public static String routeLabel( UniqueFamily family, int tier, int branch ) {
		StringBuilder sb = new StringBuilder();
		for (UniqueItemId id : idsOf( family, tier, branch )) {
			if (sb.length() > 0) sb.append( "、" );
			sb.append( nameOf( id ) );
		}
		return sb.toString();
	}

	// ---------- 持有 / 完整性 ----------

	/** 英雄当前持有的某族某档物品（含护甲上贴附的纹章）。 */
	public static List<Item> held( Hero hero, UniqueFamily family, int tier ) {
		List<Item> out = new ArrayList<>();
		if (hero == null || hero.belongings == null || family == null) return out;

		for (Item it : hero.belongings) {
			UniqueItemId id = idOf( it );
			if (id != null && id.family == family && (tier < 0 || id.tier == tier)) out.add( it );
		}

		//SPSEXPD: 护甲上贴附的纹章不在 Belongings 的迭代范围里，单独查
		for (Armor a : new Armor[]{ hero.belongings.armor, hero.belongings.secondArmor }) {
			if (a == null) continue;
			BrokenSeal seal = a.checkSeal();
			if (seal == null) continue;
			UniqueItemId id = idOf( seal );
			if (id != null && id.family == family && (tier < 0 || id.tier == tier)) out.add( seal );
		}
		return out;
	}

	/** 缺少的编号（该档该路线应有但英雄身上没有的）。 */
	public static List<UniqueItemId> missing( Hero hero, UniqueFamily family, int tier, int branch ) {
		List<Item> have = held( hero, family, tier );
		List<UniqueItemId> miss = new ArrayList<>();
		for (UniqueItemId id : idsOf( family, tier, branch )) {
			boolean found = false;
			for (Item it : have) {
				if (idOf( it ) == id) { found = true; break; }
			}
			if (!found) miss.add( id );
		}
		return miss;
	}

	// ---------- 整族转换 ----------

	/** 消耗英雄持有的某族某档某路线的物品，返回被消耗的实例（用于升级保留）。 */
	public static List<Item> consume( Hero hero, UniqueFamily family, int tier, int branch ) {
		List<UniqueItemId> ids = idsOf( family, tier, branch );
		List<Item> out = new ArrayList<>();
		for (Item it : held( hero, family, tier )) {
			if (!ids.contains( idOf( it ) )) continue;
			removeFromHero( hero, it );
			out.add( it );
		}
		return out;
	}

	/** 发放某族某档某路线的新物品，返回发放的实例。 */
	public static List<Item> grant( Hero hero, UniqueFamily family, int tier, int branch ) {
		List<Item> out = new ArrayList<>();
		for (UniqueItemId id : idsOf( family, tier, branch )) {
			for (int i = 0; i < Math.max( 1, id.grantCount ); i++) {
				Item item = create( id );
				if (item == null) continue;
				if (!item.collect( hero.belongings.backpack ) && Dungeon.level != null) {
					Dungeon.level.drop( item, hero.pos );
				}
				out.add( item );
			}
		}
		return out;
	}

	/**
	 * SPSEXPD: 升级保留 —— 把被消耗物品的升级/刻印搬到新发放的物品上。
	 * 占位阶段只处理破损纹章一系的等级与刻印（与既有 TengusMask 行为一致）。
	 */
	public static void preserve( List<Item> consumed, List<Item> granted ) {
		BrokenSeal oldSeal = null;
		for (Item it : consumed) {
			if (it instanceof BrokenSeal) { oldSeal = (BrokenSeal) it; break; }
		}
		BrokenSeal newSeal = null;
		for (Item it : granted) {
			if (it instanceof BrokenSeal) { newSeal = (BrokenSeal) it; break; }
		}
		if (oldSeal != null && newSeal != null) {
			newSeal.level( oldSeal.level() );
			newSeal.setGlyph( oldSeal.getGlyph() );
		}
	}

	/** 从英雄的任何携带位置（装备槽 / 背包 / 护甲贴附）移除一件物品。 */
	private static void removeFromHero( Hero hero, Item it ) {
		Belongings b = hero.belongings;
		if (b.weapon == it) b.weapon = null;
		else if (b.armor == it) b.armor = null;
		else if (b.secondWep == it) b.secondWep = null;
		else if (b.secondArmor == it) b.secondArmor = null;
		else if (b.artifact == it) b.artifact = null;
		else if (b.misc == it) b.misc = null;
		else if (b.ring == it) b.ring = null;
		else if (b.accessory4 == it) b.accessory4 = null;
		else if (b.accessory5 == it) b.accessory5 = null;
		else if (b.badge == it) b.badge = null;
		else {
			//SPSEXPD: 背包或其它容器袋里的物品 —— 找到它所在的袋再移除
			for (pd.items.equipment.bags.Bag bag : b.getBags()) {
				if (bag.items.contains( it )) { it.detachAll( bag ); return; }
			}
			//护甲上贴附的纹章不在任何袋里
			if (it instanceof BrokenSeal) {
				if (b.armor != null && b.armor.checkSeal() == it) b.armor.detachSeal();
				else if (b.secondArmor != null && b.secondArmor.checkSeal() == it) b.secondArmor.detachSeal();
			}
		}
	}
}
