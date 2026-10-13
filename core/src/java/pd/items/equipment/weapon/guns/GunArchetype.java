package pd.items.equipment.weapon.guns;

/**
 * SPSEXPD: 枪械「类型基准」——每把具体枪械选择一个基准，并可覆写其中任意参数，
 * 以此实现「类型基准 + 个体特色」。
 *
 * 字段含义见 GunWeapon：弹匣容量 magazine、单次击发弹药数 shotsPerFire、
 * 零后坐缓冲的力量阈值 perfectSTR、精准射击范围 range、装填耗时 reloadTime、
 * 枪声传播半径 noise、充能型每回合自然回充 regenPerTurn、弹道穿透 pierce（0 = 命中即止）。
 *
 * 装填一律消耗一个「通用弹匣」把弹匣填满，因此没有逐颗/整匣之分。
 */
public enum GunArchetype {

	/** 手枪。 */
	PISTOL (1, 7,  1, 12, 4,  1f, 4, 0f,   0),
	/** 冲锋枪：单次击发 4 发，每发各自独立判定。 */
	SMG    (2, 20, 4, 16, 4,  2f, 5, 0f,   0),
	/** 霰弹枪。 */
	SHOTGUN(3, 4,  1, 18, 2,  1f, 7, 0f,   0),
	/** 狙击枪：弹道穿透，命中敌人后继续飞至撞墙。 */
	SNIPER (4, 5,  1, 20, 12, 3f, 8, 0f,   Integer.MAX_VALUE),
	/** 电磁手枪等充能型：未开火时每回合自然回充 0.5 发。 */
	CHARGE (1, 3,  1, 10, 6,  2f, 5, 0.5f, 0),
	/** 自定义基准（玩具枪等），参数由子类覆写。 */
	CUSTOM (1, 1,  1, 12, 3,  1f, 3, 0f,   0),
	/** 投石索：0 阶单发，数值由子类覆写。 */
	SLING  (0, 1,  1, 10, 4,  1f, 2, 0f,   0);

	public final int tier;
	public final int magazine;
	public final int shotsPerFire;
	public final int perfectSTR;
	public final int range;
	public final float reloadTime;
	public final int noise;
	public final float regenPerTurn;
	public final int pierce;

	GunArchetype(int tier, int magazine, int shotsPerFire, int perfectSTR, int range,
	             float reloadTime, int noise, float regenPerTurn, int pierce) {
		this.tier = tier;
		this.magazine = magazine;
		this.shotsPerFire = shotsPerFire;
		this.perfectSTR = perfectSTR;
		this.range = range;
		this.reloadTime = reloadTime;
		this.noise = noise;
		this.regenPerTurn = regenPerTurn;
		this.pierce = pierce;
	}
}
