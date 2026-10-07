/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.IconEntry;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.levels.Level;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;

/**
 * SPSEXPD: 投掷果实的公共基类。
 * 既是投掷武器（命中/落地都有植物效果），也是可食用物（吃下恢复饱食度并附带小效果）。
 *
 * 大小果实规则（2026 批次）：大型果实的效果数值 = 小型果实的 3 倍；
 * 作用范围小型果实 3×3（9 格）、大型果实 5×5 圆形（去掉四角，21 格）。
 * 注：果实效果暂不与对应药剂按比例同步（药剂数值后续还会平衡）。
 */
abstract class SpsFruit extends MissileWeapon {
	//SPSEXPD: 果实是投掷武器（不继承 Food），食用动作名要在这里注册，否则界面显示 TEXT_NOT_FOUND
	static {
		InlineText.of(SpsFruit.class)
			.t("ac_eat", "食用");
	}

	/** SPSEXPD: 大型果实的效果倍率。 */
	protected static final float LARGE_FACTOR = 3f;
	/** SPSEXPD: 效果范围半径——小型 1（3×3），大型 2（5×5 圆形，去掉四角）。 */
	private static final int SMALL_RADIUS = 1;
	private static final int LARGE_RADIUS = 2;

	private final int baseMin;
	private final int baseMax;

	/** SPSEXPD: 是否为该种子对应的“大型果实”。 */
	public boolean large = false;

	SpsFruit(IconEntry image, int min, int max) {
		this.image = image;
		baseMin = min;
		baseMax = max;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
		//SPSEXPD: 果实是可堆叠的消耗品，不能升级
		stackable = true;
		usesTargeting = true;
		//SPSEXPD: 食用效果为正面的果实默认「食用」，负面的默认「投掷」（由子类覆写 harmfulOnEat）
		defaultAction = harmfulOnEat() ? AC_THROW : Food.AC_EAT;
	}

	/** SPSEXPD: 该果实的食用效果是否对食用者本身有害（决定默认动作是投掷还是食用）。 */
	protected boolean harmfulOnEat() {
		return false;
	}

	protected boolean landsAt(int cell) {
		Char target = Actor.findChar(cell);
		//SPSEXPD: 只有空地才算落地；落在自己或他人身上都交给命中结算
		if (target == null) {
			parent = null;
			return true;
		}
		return false;
	}

	//SPSEXPD: 落点是施放者自己时，命中效果作用于自己（支持对脚下投掷）
	@Override
	protected void onThrow(int cell) {
		if (curUser != null && cell == curUser.pos) {
			proc(curUser, curUser, 0);
			parent = null;
			return;
		}
		super.onThrow(cell);
	}

	protected <T extends Blob> void seed(int cell, int amount, Class<T> type) {
		if (Dungeon.level != null && Dungeon.level.insideMap(cell)) {
			T blob = Blob.seed(cell, amount, type);
			//SPSEXPD: 果实产生的雾气/场至少留存 4 回合（范围与浓度初始值不变）
			if (blob != null) blob.setMinLifetime(4);
			GameScene.add(blob);
		}
	}

	/**
	 * SPSEXPD: 在落点周围播种雾/场——小型果实 3×3，大型果实 5×5 圆形（去掉四角）。
	 */
	protected <T extends Blob> void seedArea(int center, int amount, Class<T> type) {
		for (int cell : areaCells(center)) seed(cell, amount, type);
	}

	/** SPSEXPD: 中心格周围 8 格（3×3 去掉中心）。 */
	protected <T extends Blob> void seedAround(int center, int amount, Class<T> type) {
		for (int offset : PathFinder.NEIGHBOURS8) seed(center + offset, amount, type);
	}

	/**
	 * SPSEXPD: 本果实的“作用范围”内的格子（含中心格）：小型果实 3×3（9 格），
	 * 大型果实 5×5 圆形（去掉四角，21 格），越界与实心格会被跳过。
	 */
	protected int[] areaCells(int center) {
		Level level = Dungeon.level;
		if (level == null || !level.insideMap(center)) return new int[]{center};
		int radius = large ? LARGE_RADIUS : SMALL_RADIUS;
		int width = level.width();
		int height = level.height();
		int cx = center % width, cy = center / width;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int dy = -radius; dy <= radius; dy++) {
			for (int dx = -radius; dx <= radius; dx++) {
				//SPSEXPD: 5×5 取圆形——四角不计入
				if (radius > 1 && Math.abs(dx) == radius && Math.abs(dy) == radius) continue;
				int x = cx + dx, y = cy + dy;
				if (x < 0 || x >= width || y < 0 || y >= height) continue;
				int cell = y * width + x;
				if (level.solid[cell]) continue;
				cells.add(cell);
			}
		}
		if (cells.isEmpty()) cells.add(center);
		int[] result = new int[cells.size()];
		for (int i = 0; i < result.length; i++) result[i] = cells.get(i);
		return result;
	}

	/** SPSEXPD: 大型果实效果更强——数值为普通果实的 3 倍。 */
	protected int scaled(int base) {
		return large ? Math.round(base * LARGE_FACTOR) : base;
	}

	/** SPSEXPD: 大型果实效果更强——时长/数值为普通果实的 3 倍。 */
	protected float scaled(float base) {
		return large ? base * LARGE_FACTOR : base;
	}

	/** SPSEXPD: 吃下果实恢复的饱食度——普通果实 15，大型果实 45。 */
	protected float eatEnergy() {
		return large ? 45f : 15f;
	}

	protected void onEat(Hero hero) {
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(Food.AC_EAT);
		return actions;
	}

	//SPSEXPD: 果实像药水一样，投掷只消耗 1 个，不参与投掷武器的磨损/损坏机制
	@Override
	protected void decrementDurability() {
		parent = null;
	}

	//SPSEXPD: 果实命中即消失——不像飞镖那样插在敌人身上（不产生中矢），也不落地
	@Override
	protected void rangedHit(Char enemy, int cell) {
		parent = null;
	}

	@Override
	public float durabilityPerUse(int level) {
		return 0;
	}

	//SPSEXPD: 果实按种类堆叠合并——不走投掷武器的 setID（“同一组”）判定
	@Override
	public boolean isSimilar(Item item) {
		return item != null && getClass() == item.getClass();
	}

	//SPSEXPD: 合并只累加数量，不做投掷武器的耐久/“每组 3 个”截断
	@Override
	public Item merge(Item other) {
		if (other != null && isSimilar(other)) {
			quantity += other.quantity();
			other.quantity(0);
		}
		return this;
	}

	@Override
	public String info() {
		//SPSEXPD: 果实不是成组投掷武器——去掉“远程投掷更精准”与“永久不损坏”这两段说明
		String info = super.info();
		info = info.replace("\n\n" + Messages.get(MissileWeapon.class, "distance"), "");
		info = info.replace("\n\n" + Messages.get(this, "unlimited_uses"), "");
		return info;
	}

	@Override
	public int defaultQuantity() {
		return 999;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(Food.AC_EAT)) {
			detach(hero.belongings.backpack);
			Catalog.countUse(getClass());

			Buff.affect(hero, Hunger.class).satisfy(eatEnergy());
			onEat(hero);
			GLog.i(Messages.get(Food.class, "eat_msg"));

			hero.sprite.operate(hero.pos);
			hero.busy();
			Sample.INSTANCE.play(Assets.Sounds.EAT);
			hero.spend(1f);

			Statistics.foodEaten++;
		}
	}

	@Override public int min(int lvl) { return scaled(baseMin); }
	@Override public int max(int lvl) { return scaled(baseMax); }
	/** SPSEXPD: 果实没有直接投掷伤害，效果全部来自命中/落地的植物能力（子类仍用 min()/max() 计算效果数值）。 */
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public int STRReq(int lvl) { return 5; }
	/** SPSEXPD: 果实不能被炼金熔化为液金。 */
	@Override public boolean canMeltIntoMetal() { return false; }
	/** SPSEXPD: 果实标记（竹背篓等只收纳果实）。 */
	@Override public boolean isFruit() { return true; }
	@Override protected boolean showStatsInfo() { return false; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return (large ? 4 : 2) * quantity(); }
}
