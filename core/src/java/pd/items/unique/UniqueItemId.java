/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique;

import java.util.Locale;

/**
 * SPSEXPD: 特殊物品的「编号」。
 *
 * <p>每个常量代表一件特殊物品，携带三段语义：</p>
 * <ul>
 *   <li>{@code family} —— 所属族（= 职业）；同一族的物品是同一条演化线；</li>
 *   <li>{@code tier}   —— 档位：0 = 开局初始，1 = 一档强化（天狗面具），2 = 二次强化（矮人国王皇冠）；</li>
 *   <li>{@code branch} —— 路线：0 = 初始档，1/2 = 面具强化时玩家二选一的方向（皇冠沿选定路线继续）。</li>
 * </ul>
 *
 * <p>{@code grantCount} 表示该路线一次发放的件数（如女猎的「灵能回旋镖×2」）。
 * {@code zhName} 只对「占位物品」有意义；已有独立类的物品传 {@code null}，其名字由各自的类提供。</p>
 */
public enum UniqueItemId {

	// ---------- 战士：纹章族（破损纹章 + 纹章碎片） ----------
	WARRIOR_SEAL_BASE_A     ( UniqueFamily.WARRIOR, 0, 0, 1, null ),
	WARRIOR_SEAL_BASE_B     ( UniqueFamily.WARRIOR, 0, 0, 1, null ),
	WARRIOR_SEAL_T1_1_FULL  ( UniqueFamily.WARRIOR, 1, 1, 1, null ),
	WARRIOR_SEAL_T1_2_BLOOD ( UniqueFamily.WARRIOR, 1, 2, 1, "鲜血宝珠" ),
	WARRIOR_SEAL_T1_2_SOUL  ( UniqueFamily.WARRIOR, 1, 2, 1, "英魂宝珠" ),
	WARRIOR_SEAL_T2_1_GILDED( UniqueFamily.WARRIOR, 2, 1, 1, "镀金纹章" ),
	WARRIOR_SEAL_T2_2_BLOOD ( UniqueFamily.WARRIOR, 2, 2, 1, "赤血宝珠" ),
	WARRIOR_SEAL_T2_2_GLORY ( UniqueFamily.WARRIOR, 2, 2, 1, "荣耀宝珠" ),

	// ---------- 法师：法杖族（老法杖 + 魔弹法杖） ----------
	MAGE_STAFF_BASE_A       ( UniqueFamily.MAGE, 0, 0, 1, null ),
	MAGE_STAFF_BASE_B       ( UniqueFamily.MAGE, 0, 0, 1, null ),
	MAGE_STAFF_T1_1_CORE    ( UniqueFamily.MAGE, 1, 1, 1, "奥法轴芯" ),
	MAGE_STAFF_T1_2_MITHRIL ( UniqueFamily.MAGE, 1, 2, 1, "秘银手杖" ),
	MAGE_STAFF_T1_2_WATCH   ( UniqueFamily.MAGE, 1, 2, 1, "精致怀表" ),
	MAGE_STAFF_T2_1_TOME    ( UniqueFamily.MAGE, 2, 1, 1, "奥法古籍" ),
	MAGE_STAFF_T2_2_ADAMANT ( UniqueFamily.MAGE, 2, 2, 1, "精金手杖" ),
	MAGE_STAFF_T2_2_REVERSE ( UniqueFamily.MAGE, 2, 2, 1, "反转怀表" ),

	// ---------- 盗贼：斗篷族（暗影斗篷 + 白银之手） ----------
	ROGUE_CLOAK_BASE_A      ( UniqueFamily.ROGUE, 0, 0, 1, null ),
	ROGUE_CLOAK_BASE_B      ( UniqueFamily.ROGUE, 0, 0, 1, null ),
	ROGUE_CLOAK_T1_1_HAND   ( UniqueFamily.ROGUE, 1, 1, 1, "大盗之手" ),
	ROGUE_CLOAK_T1_2_NIGHT  ( UniqueFamily.ROGUE, 1, 2, 1, "暗夜手环" ),
	ROGUE_CLOAK_T1_2_AGENT  ( UniqueFamily.ROGUE, 1, 2, 1, "特工手枪" ),
	ROGUE_CLOAK_T2_1_HAND   ( UniqueFamily.ROGUE, 2, 1, 1, "神偷之手" ),
	ROGUE_CLOAK_T2_2_NIGHT  ( UniqueFamily.ROGUE, 2, 2, 1, "永夜手镯" ),
	ROGUE_CLOAK_T2_2_SILENT ( UniqueFamily.ROGUE, 2, 2, 1, "无声手枪" ),

	// ---------- 女猎手：弓族（回旋镖 + 灵能弓） ----------
	HUNTRESS_BOW_BASE_A     ( UniqueFamily.HUNTRESS, 0, 0, 1, null ),
	HUNTRESS_BOW_BASE_B     ( UniqueFamily.HUNTRESS, 0, 0, 1, null ),
	HUNTRESS_BOW_T1_1_DART  ( UniqueFamily.HUNTRESS, 1, 1, 1, "灵能袖箭" ),
	HUNTRESS_BOW_T1_2_BOOM  ( UniqueFamily.HUNTRESS, 1, 2, 2, "灵能回旋镖" ),
	HUNTRESS_BOW_T2_1_DART  ( UniqueFamily.HUNTRESS, 2, 1, 1, "灵能袖箭·改" ),
	HUNTRESS_BOW_T2_2_BOOM  ( UniqueFamily.HUNTRESS, 2, 2, 2, "虚空回旋镖" ),

	// ---------- 决斗家：护手族（护手盾 + 护手绑带） ----------
	DUELIST_SHIELD_BASE_A   ( UniqueFamily.DUELIST, 0, 0, 1, null ),
	DUELIST_SHIELD_BASE_B   ( UniqueFamily.DUELIST, 0, 0, 1, null ),
	DUELIST_SHIELD_T1_1_RAPIER ( UniqueFamily.DUELIST, 1, 1, 1, "蓝钢刺剑" ),
	DUELIST_SHIELD_T1_2_ROUND  ( UniqueFamily.DUELIST, 1, 2, 1, "神木圆盾" ),
	DUELIST_SHIELD_T1_2_RUNE   ( UniqueFamily.DUELIST, 1, 2, 1, "符文绑带" ),
	DUELIST_SHIELD_T2_1_NULL   ( UniqueFamily.DUELIST, 2, 1, 1, "零素刺剑" ),
	DUELIST_SHIELD_T2_2_CAPTAIN( UniqueFamily.DUELIST, 2, 2, 1, "队长之盾" ),
	DUELIST_SHIELD_T2_2_STEEL  ( UniqueFamily.DUELIST, 2, 2, 1, "柔钢绑带" ),

	// ---------- 演员：铁铲族（铁铲 + 扩音筒） ----------
	PERFORMER_SHOVEL_BASE_A ( UniqueFamily.PERFORMER, 0, 0, 1, null ),
	PERFORMER_SHOVEL_BASE_B ( UniqueFamily.PERFORMER, 0, 0, 1, null ),
	PERFORMER_SHOVEL_T1_1_BASS  ( UniqueFamily.PERFORMER, 1, 1, 1, "魔音贝斯" ),
	PERFORMER_SHOVEL_T1_2_DRILL ( UniqueFamily.PERFORMER, 1, 2, 1, "电钻" ),
	PERFORMER_SHOVEL_T1_2_INFRA ( UniqueFamily.PERFORMER, 1, 2, 1, "次声波发生器" ),
	PERFORMER_SHOVEL_T2_1_HARM  ( UniqueFamily.PERFORMER, 2, 1, 1, "闪电口琴" ),
	PERFORMER_SHOVEL_T2_2_HOLY  ( UniqueFamily.PERFORMER, 2, 2, 1, "神圣钻头" ),
	PERFORMER_SHOVEL_T2_2_PULSE ( UniqueFamily.PERFORMER, 2, 2, 1, "脉冲单元" ),

	// ---------- 星兵：手枪族（电磁手枪 + 无限弹药袋） ----------
	SOLDIER_GUN_BASE_A      ( UniqueFamily.SOLDIER, 0, 0, 1, null ),
	SOLDIER_GUN_BASE_B      ( UniqueFamily.SOLDIER, 0, 0, 1, null ),
	SOLDIER_GUN_T1_1_YAKA   ( UniqueFamily.SOLDIER, 1, 1, 1, "亚卡箭" ),
	SOLDIER_GUN_T1_2_CANNON ( UniqueFamily.SOLDIER, 1, 2, 1, "电磁手炮" ),
	SOLDIER_GUN_T1_2_AMMOBOX( UniqueFamily.SOLDIER, 1, 2, 1, "弹药箱" ),
	SOLDIER_GUN_T2_1_INJECT ( UniqueFamily.SOLDIER, 2, 1, 1, "注射哨箭" ),
	SOLDIER_GUN_T2_2_MK87   ( UniqueFamily.SOLDIER, 2, 2, 1, "MK-87" ),
	SOLDIER_GUN_T2_2_REACTOR( UniqueFamily.SOLDIER, 2, 2, 1, "炼金反应堆" ),

	// ---------- 信徒：蒲团族（蒲团 + 信标盒） ----------
	FOLLOWER_CUSHION_BASE_A ( UniqueFamily.FOLLOWER, 0, 0, 1, null ),
	FOLLOWER_CUSHION_BASE_B ( UniqueFamily.FOLLOWER, 0, 0, 1, null ),
	FOLLOWER_CUSHION_T1_1_SPOON   ( UniqueFamily.FOLLOWER, 1, 1, 1, "银汤匙" ),
	FOLLOWER_CUSHION_T1_2_CROSS   ( UniqueFamily.FOLLOWER, 1, 2, 1, "十字架" ),
	FOLLOWER_CUSHION_T1_2_STAKER  ( UniqueFamily.FOLLOWER, 1, 2, 1, "尖钉发射器" ),
	FOLLOWER_CUSHION_T2_1_SUPPER  ( UniqueFamily.FOLLOWER, 2, 1, 1, "圣餐银匙" ),
	FOLLOWER_CUSHION_T2_2_THORN   ( UniqueFamily.FOLLOWER, 2, 2, 1, "荆棘冠冕" ),
	FOLLOWER_CUSHION_T2_2_MARTYR  ( UniqueFamily.FOLLOWER, 2, 2, 1, "殉道者" ),

	// ---------- 苦修者：耳环族（经卷耳环 + 木屐） ----------
	ASCETIC_EARRING_BASE_A  ( UniqueFamily.ASCETIC, 0, 0, 1, null ),
	ASCETIC_EARRING_BASE_B  ( UniqueFamily.ASCETIC, 0, 0, 1, null ),
	ASCETIC_EARRING_T1_1_SANDEVISTAN( UniqueFamily.ASCETIC, 1, 1, 1, "斯安威斯坦" ),
	ASCETIC_EARRING_T1_2_SALVAGER( UniqueFamily.ASCETIC, 1, 2, 1, "回收机" ),
	ASCETIC_EARRING_T1_2_KIT     ( UniqueFamily.ASCETIC, 1, 2, 1, "强化义体工具箱" ),
	ASCETIC_EARRING_T2_1_SANDEVISTAN_MK2( UniqueFamily.ASCETIC, 2, 1, 1, "二代斯安威斯坦" ),
	ASCETIC_EARRING_T2_2_RECOMBINER( UniqueFamily.ASCETIC, 2, 2, 1, "重组机" ),
	ASCETIC_EARRING_T2_2_NANO     ( UniqueFamily.ASCETIC, 2, 2, 1, "纳米义体" ),
	;

	/** 所属族（= 职业演化线）。 */
	public final UniqueFamily family;
	/** 档位：0 = 初始，1 = 一档强化，2 = 二次强化。 */
	public final int tier;
	/** 路线：0 = 初始档，1/2 = 一档强化时二选一的方向。 */
	public final int branch;
	/** 该路线一次发放的件数。 */
	public final int grantCount;
	/** 占位物品的中文名（已有独立类的物品为 null）。 */
	public final String zhName;

	UniqueItemId( UniqueFamily family, int tier, int branch, int grantCount, String zhName ) {
		this.family = family;
		this.tier = tier;
		this.branch = branch;
		this.grantCount = grantCount;
		this.zhName = zhName;
	}

	/** 编号的稳定字符串形式（用作内联文本 / properties 的键片段）。 */
	public String key() {
		return name().toLowerCase( Locale.ENGLISH );
	}
}
