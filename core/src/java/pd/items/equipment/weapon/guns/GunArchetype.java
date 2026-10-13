package pd.items.equipment.weapon.guns;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械「类型基准」——每把具体枪械选择一个基准，并可覆写其中任意参数，
 * 以此实现「类型基准 + 个体特色」。
 *
 * SPSEXPD: 枪械不再分「阶位」。伤害与力量需求由显式基准给出（数值与原阶位公式等价）：
 * min(lvl) = minBase + lvl、max(lvl) = maxBase + maxGrowth * lvl、
 * STRReq(lvl) = strBase - (√(8lvl+1)-1)/2。
 *
 * 其余字段：弹匣容量 magazine、单次击发弹药数 shotsPerFire、零后坐缓冲的力量阈值 perfectSTR、
 * 精准射击范围 range、装填耗时 reloadTime、枪声传播半径 noise、充能型每回合自然回充 regenPerTurn、
 * 弹道穿透 pierce（0 = 命中即止）。装填一律消耗一个「通用弹匣」把弹匣填满。
 */
public enum GunArchetype {

	/** 手枪。 */
	PISTOL (7,  1, 12, 4,  1f, 4, 0f,   0,                  3, 8,  2, 10),
	/** 冲锋枪：单次击发 4 发，每发各自独立判定。 */
	SMG    (20, 4, 16, 4,  2f, 5, 0f,   0,                  4, 10, 3, 12),
	/** 霰弹枪。 */
	SHOTGUN(4,  1, 18, 2,  1f, 7, 0f,   0,                  5, 14, 3, 14),
	/** 狙击枪：弹道穿透，命中敌人后继续飞至撞墙。 */
	SNIPER (5,  1, 20, 12, 3f, 8, 0f,   Integer.MAX_VALUE,  6, 20, 4, 16),
	/** 电磁手枪等充能型：未开火时每回合自然回充 0.5 发。 */
	CHARGE (3,  1, 10, 6,  2f, 5, 0.5f, 0,                  3, 8,  2, 10),
	/** 自定义基准（玩具枪等），参数由子类覆写。 */
	CUSTOM (1,  1, 12, 3,  1f, 3, 0f,   0,                  3, 8,  2, 10);


	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunArchetype.class)
			.t("pistol", "手枪")
			.t("smg", "冲锋枪")
			.t("shotgun", "霰弹枪")
			.t("sniper", "狙击枪")
			.t("charge", "特殊枪械")
			.t("custom", "特殊枪械");
	}

	public final int magazine;
	public final int shotsPerFire;
	public final int perfectSTR;
	public final int range;
	public final float reloadTime;
	public final int noise;
	public final float regenPerTurn;
	public final int pierce;
	/** SPSEXPD: 伤害/力量基准（取代原先的阶位公式）。 */
	public final int minBase;
	public final int maxBase;
	public final int maxGrowth;
	public final int strBase;

	GunArchetype(int magazine, int shotsPerFire, int perfectSTR, int range,
	             float reloadTime, int noise, float regenPerTurn, int pierce,
	             int minBase, int maxBase, int maxGrowth, int strBase) {
		this.magazine = magazine;
		this.shotsPerFire = shotsPerFire;
		this.perfectSTR = perfectSTR;
		this.range = range;
		this.reloadTime = reloadTime;
		this.noise = noise;
		this.regenPerTurn = regenPerTurn;
		this.pierce = pierce;
		this.minBase = minBase;
		this.maxBase = maxBase;
		this.maxGrowth = maxGrowth;
		this.strBase = strBase;
	}

	/** SPSEXPD: 信息显示用的类型名（内联中文，英文见 properties）。 */
	public String displayName() {
		return pd.messages.Messages.get(GunArchetype.class, name().toLowerCase());
	}
}
