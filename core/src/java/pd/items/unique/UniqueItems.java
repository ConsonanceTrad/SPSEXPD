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
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_BASE_B, pd.items.unique.items.Metronome::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_BASE_A, GunOfSoldier::new );
		REGISTRY.put( UniqueItemId.SOLDIER_GUN_BASE_B, EndlessAmmoBag::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_BASE_A, Futon::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_BASE_B, FaithSign::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_BASE_A, pd.items.unique.items.OrganicRecycler::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_BASE_B, pd.items.unique.items.CyberwareKit::new );

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
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_1_SANDEVISTAN, pd.items.unique.items.Sandevistan::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_2_SALVAGER, pd.items.unique.items.Salvager::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T1_2_KIT, pd.items.unique.items.EnhancedCyberwareKit::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_1_SANDEVISTAN_MK2, pd.items.unique.items.SandevistanMk2::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_2_RECOMBINER, pd.items.unique.items.Recombiner::new );
		REGISTRY.put( UniqueItemId.ASCETIC_EARRING_T2_2_NANO, pd.items.unique.items.NanoCyberware::new );

		//SPSEXPD: 决斗族六件
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T1_1_RAPIER, pd.items.unique.items.BlueSteelRapier::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T2_1_NULL, pd.items.unique.items.NullRapier::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T1_2_ROUND, pd.items.unique.items.RoundShieldUnique::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T1_2_RUNE, pd.items.unique.items.RuneBanding::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T2_2_CAPTAIN, pd.items.unique.items.CaptainShield::new );
		REGISTRY.put( UniqueItemId.DUELIST_SHIELD_T2_2_STEEL, pd.items.unique.items.SteelBanding::new );

		//SPSEXPD: 演员族六件
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T1_1_BASS, pd.items.unique.items.MagicBass::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T2_1_HARM, pd.items.unique.items.StormHarmonica::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T1_2_DRILL, pd.items.unique.items.PowerDrill::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T1_2_INFRA, pd.items.unique.items.InfrasonicGen::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T2_2_HOLY, pd.items.unique.items.HolyDrill::new );
		REGISTRY.put( UniqueItemId.PERFORMER_SHOVEL_T2_2_PULSE, pd.items.unique.items.PulseUnit::new );

		//SPSEXPD: 信徒族六件
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T1_1_SPOON, pd.items.unique.items.SilverSpoon::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T2_1_SUPPER, pd.items.unique.items.SupperSpoon::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T1_2_CROSS, pd.items.unique.items.Cross::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T1_2_STAKER, pd.items.unique.items.Staker::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T2_2_THORN, pd.items.unique.items.ThornCrown::new );
		REGISTRY.put( UniqueItemId.FOLLOWER_CUSHION_T2_2_MARTYR, pd.items.unique.items.Martyr::new );

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

	/** SPSEXPD: 某条路线的特色描述（同一组共用一行，用于选角界面的专精页）。 */
	public static String routeDesc( UniqueFamily family, int tier, int branch ) {
		if (family == null) return "";
		return pd.messages.Messages.get( UniqueItems.class,
				"route_" + family.name().toLowerCase() + "_" + tier + "_" + branch );
	}

	//SPSEXPD: inline Chinese text —— 9 族 × 一/二档 × 两条路线
	static {
		pd.messages.InlineText.of( UniqueItems.class )
			.t("route_warrior_1_1", "把纹章留在护甲上：单一路线，但它从一开始就是完整的。")
			.t("route_warrior_1_2", "以宝珠承载鲜血与英魂，把战士的战意存下来、一次放出去。")
			.t("route_warrior_2_1", "镀金纹章把纹章的力量推到极致，牺牲的是换装自由。")
			.t("route_warrior_2_2", "赤血与荣耀两条宝珠分支，分别偏向搏命与统帅。")
			.t("route_mage_1_1", "奥法轴芯：把法杖吞进核心，用一条法术打天下。")
			.t("route_mage_1_2", "秘银手杖与精致怀表：不靠施法，靠装备本身供能与计时。")
			.t("route_mage_2_1", "奥法古籍把轴芯的容量推到九种法术，真正成为一本行走的法术书。")
			.t("route_mage_2_2", "精金手杖与反转怀表：全杖供能，以及把时间倒回去一次。")
			.t("route_rogue_1_1", "大盗之手：潜行、准备刺杀，顺手从别人身上摸走东西。")
			.t("route_rogue_1_2", "暗夜手环与特工手枪：一边藏进夜里，一边在远处解决麻烦。")
			.t("route_rogue_2_1", "神偷之手在潜行偷取之外，还能对空地瞬移。")
			.t("route_rogue_2_2", "永夜手镯与无声手枪：更长的隐匿，以及几乎不惊动任何人的一枪。")
			.t("route_huntress_1_1", "灵能袖箭：把几次射击提前压进袖口，出手时不占回合。")
			.t("route_huntress_1_2", "灵能回旋镖：扔出去，走到落点附近就算接住，立刻再扔。")
			.t("route_huntress_2_1", "袖箭·改把整个弹匣都能预装填。")
			.t("route_huntress_2_2", "虚空回旋镖的弹道穿墙而过，接住判定依旧宽容。")
			.t("route_duelist_1_1", "蓝钢刺剑：从八个方位挑开破绽，攒满当场斩杀。")
			.t("route_duelist_1_2", "神木圆盾与符文绑带：先手吸收伤害，并给自己指定刻印。")
			.t("route_duelist_2_1", "零素刺剑只要六个方位就能挑满破绽。")
			.t("route_duelist_2_2", "队长之盾把吸收的伤害原样送回，柔钢绑带提供更高的防护。")
			.t("route_performer_1_1", "魔音贝斯：阶位更高、命中溅射，还保留节拍。")
			.t("route_performer_1_2", "电钻与次声波发生器：一个磨墙，一个把人定在原地。")
			.t("route_performer_2_1", "闪电竖琴在溅射之外再射出一道闪电，踩满节拍还有玻璃保护。")
			.t("route_performer_2_2", "神圣钻头与脉冲单元：钻得更快，也打得更狠。")
			.t("route_soldier_1_1", "亚卡箭：把箭当作可控的飞行道具来用。")
			.t("route_soldier_1_2", "电磁手炮与弹药箱：靠充能和弹药撑起持续火力。")
			.t("route_soldier_2_1", "注射哨箭把药剂与射击绑在一起。")
			.t("route_soldier_2_2", "MK-87 与炼金反应堆：把炼金能量直接灌进枪里。")
			.t("route_follower_1_1", "银汤匙：充能无限，但每一次使用都在累积疲劳。")
			.t("route_follower_1_2", "十字架与尖钉发射器：压低有害魔力，同时专打恶魔。")
			.t("route_follower_2_1", "圣餐银匙的疲劳上限高得多，可以连用更久。")
			.t("route_follower_2_2", "荆棘冠冕替你洗净诅咒，殉道者一次打出四颗弹丸。")
			.t("route_ascetic_1_1", "斯安威斯坦：短暂让世界停住，只有你还在动。")
			.t("route_ascetic_1_2", "回收机与强化义体工具箱：把击杀和食物变成材料，再造成义体。")
			.t("route_ascetic_2_1", "二代斯安威斯坦的充能上限翻倍，子弹时间也更长。")
			.t("route_ascetic_2_2", "重组机能用材料做食物，纳米义体可以拟态成任何一种义体。");
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

		//SPSEXPD: 法杖系的「灌注」转移 —— 新轴芯继承来源灌注的法杖 / 旧轴芯已收录的法术
		pd.items.unique.items.ArcaneCore newCore = null;
		for (Item it : granted) {
			if (it instanceof pd.items.unique.items.ArcaneCore) {
				newCore = (pd.items.unique.items.ArcaneCore) it;
				break;
			}
		}
		if (newCore != null) {
			for (Item it : consumed) {
				if (it instanceof pd.items.equipment.weapon.melee.MagesStaff) {
					newCore.inheritInfusion(it);
					break;
				}
			}
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
