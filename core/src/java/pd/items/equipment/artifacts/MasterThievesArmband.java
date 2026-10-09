/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon behavior restored from SPS-PD 0.9.8.
 */

package pd.items.equipment.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.wands.DamageWand;
import pd.levels.Terrain;
import pd.levels.features.HighGrass;
import pd.levels.features.OldHighGrass;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.utils.GLog;
import pd.windows.WndLifeTradeItem;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 魔术之手法杖——由「神偷袖章」神器改造而成，现在是一根普通法杖（不再是神器）。
 * 所有能力都通过「释放」施法完成（不再有独立的「魔术之手」动作）：
 * <ul>
 *   <li>命中生物：造成伤害，并按价值概率偷走一件东西（与偷商品同一套算法，越贵越难）；</li>
 *   <li>落点是普通商店货品：按标价概率偷取，失手会惊动商店老板（不散落金币）；</li>
 *   <li>落点是秘密商店货品：同样概率偷取，失手则以货价一半的永久生命上限为代价；</li>
 *   <li>落点是普通掉落物：直接取来一件；</li>
 *   <li>落点是宝箱怪：当场把它惊醒并打它一记；</li>
 *   <li>落点是未上锁的宝箱 / 坟墓 / 遗骸 / 藏宝地：隔空打开它；</li>
 *   <li>落点是草丛或植物：像走过去踩踏一样触发掉。</li>
 * </ul>
 * 法术弹道用 {@link Ballistica#PROJECTILE}，与雷霆法杖一样落在指定点后停止，不再继续飞行。
 *
 * <p>类名与包名保持不变以尽量减少旧存档加载失败面，但基类已从 Artifact 改为 DamageWand，
 * 因此旧档中仍装在神器槽上的那一件无法按原类型还原（用户已知悉并接受）。</p>
 */
public class MasterThievesArmband extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MasterThievesArmband.class)
			.t("name", "魔术之手法杖")
			.t("staff_name", "魔术之手魔杖")
			.t("stolen", "魔术之手顺手从%1$s身上摸走了%2$s。")
			.t("stolen_stone", "魔术之手没从%1$s身上摸到什么，只抓到一块石头。")
			.t("steal_fail", "魔术之手没从%1$s身上摸到东西——它护得太紧了。")
			.t("pick_ground", "魔术之手取来了%1$s。")
			.t("open_from_afar", "魔术之手打开了%1$s。")
			.t("steal_goods_ok", "魔术之手从货架上顺走了%1$s。")
			.t("steal_goods_fail", "魔术之手失手了，商店老板发现了你。")
			.t("steal_life_ok", "魔术之手从密店的货架上顺走了%1$s。")
			.t("steal_life_fail", "魔术之手失手了，你被抽走了%1$d点永久生命。")
			.t("steal_life_none", "魔术之手失手了；你的永久生命已所剩无几。")
			.t("desc", "一根缠着紫色天鹅绒的细杖，杖顶嵌着一只小小的银手。它只认「释放」一件事：指尖摸到活物就伤人取物，摸到货架就按价钱掂量着偷，摸到地上的东西就顺手搬走。")
			.t("stats_desc", "释放时造成_%1$d~%2$d点伤害_，并按价值概率从命中的敌人或 NPC 身上偷走一件东西（与偷商品同一套算法，越贵越难偷，失手不消耗机会）。落点是商店货品时会按标价概率偷取：被抓到会惊动商店老板（货品越贵越难偷）；若是秘密商店，失手还要付出货价一半的永久生命。落点是普通掉落物则直接取来，落点是未上锁的宝箱、坟墓、遗骸或藏宝地则隔空打开，落点是草丛或植物则会像踩踏一样把它们处理掉，落点是宝箱怪则会当场惊醒并打它一下。")
			.t("bmage_desc", "当_战斗法师_以魔术之手魔杖近战攻击目标时，这根魔杖同样会恢复充能。")
			.t("discover_hint", "可在法杖池中找到。");
	}

	//SPSEXPD: 偷窃价位随等级指数上涨——0 级时等价 100 金币，45 级时 7000 金币
	//（即 45 级时标价 10000 以内的商品都能有 70% 以上成功率），中间为纯指数插值。
	public static final float STEAL_VALUE_BASE = 100f;
	public static final float STEAL_VALUE_AT_MAX_LEVEL = 7000f;
	public static final int STEAL_CURVE_LEVEL = 45;

	{
		//SPSEXPD: 沿用原有占位法杖图标；默认动作与瞄准由 Wand 基类设定（释放）
		image = EquipmentEquipWeaponBasicWeaponDict.OLD_STAFF;
		//SPSEXPD: 与雷霆法杖一致，弹道落在指定点后停止，不再继续飞行
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 5 + 3 * lvl; }

	@Override public int initialCharges() { return 3; }

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage(damageRoll(), this);
			//SPSEXPD: 命中敌人或 NPC 时顺手偷窃
			if (target instanceof Mob) stealFrom((Mob) target, target);
			return;
		}

		//SPSEXPD: 落点没有生物时先处理地面交互，都没有才踩踏草丛/植物
		int cell = bolt.collisionPos;
		Heap heap = Dungeon.level != null ? Dungeon.level.heaps.get(cell) : null;

		if (heap != null) {
			if (heap.type == Heap.Type.FOR_SALE) {
				tryStealGoods(heap);
				return;
			}
			if (heap.type == Heap.Type.FOR_LIFE) {
				tryStealLifeGoods(heap);
				return;
			}
			if (heap.type == Heap.Type.MIMIC || heap.type == Heap.Type.G_MIMIC) {
				awakenMimic(heap);
				return;
			}
			if (isRemoteOpenable(heap)) {
				openFromAfar(heap);
				return;
			}
			if (!heap.isEmpty()) {
				grabGroundItem(heap);
				return;
			}
		}

		trampleCell(cell);
	}

	/** SPSEXPD: 魔术之手能远程打开的容器——未上锁的宝箱、坟墓、遗骸与藏宝地（上锁/水晶宝箱除外）。 */
	public static boolean isRemoteOpenable(Heap heap) {
		if (heap == null) return false;
		switch (heap.type) {
			case CHEST:
			case TOMB:
			case SKELETON:
			case REMAINS:
			case E_DUST:
				return true;
			default:
				return false;
		}
	}

	/** SPSEXPD: 隔空开容器——诅咒、幽灵与掉血等后果照旧落在释放者身上（与走过去开一样）。 */
	protected void openFromAfar(Heap heap) {
		Hero owner = ownerOf();
		if (owner == null) return;

		GLog.i(Messages.get(this, "open_from_afar", heap.title()));
		heap.open(owner);
		updateQuickslot();
	}

	/**
	 * SPSEXPD: 隔空摸到宝箱怪——先按开箱流程把它惊醒（含「这是一个宝箱怪！」提示），
	 * 再当场给它一记法术伤害。不顺手偷：宝箱怪的库存要打死它才会掉。
	 */
	protected void awakenMimic(Heap heap) {
		if (Dungeon.level == null || heap == null) return;

		int cell = heap.pos;
		openFromAfar(heap);

		Mob mimic = Dungeon.level.mobs() != null ? Dungeon.level.mobs().findMob(cell) : null;
		if (mimic == null) return;

		wandProc(mimic, chargesPerCast());
		mimic.damage(damageRoll(), this);
		Sample.INSTANCE.play(Assets.Sounds.HIT);
		if (mimic.sprite != null) mimic.sprite.flash();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//与其它 SPS 法杖一致：战斗法师近战联动为空
	}

	/** SPSEXPD: 当前等级能稳稳偷到的商品标价上限（随等级指数上涨）。 */
	public float stealValueCap() {
		double growth = STEAL_VALUE_AT_MAX_LEVEL / (double) STEAL_VALUE_BASE;
		return STEAL_VALUE_BASE * (float) Math.pow(growth, level() / (double) STEAL_CURVE_LEVEL);
	}

	/** SPSEXPD: 偷窃成功率——越贵越难，充能消耗固定 1 点（由「释放」本身扣除）。 */
	public float stealChance(Item item) {
		if (item == null) return 0f;
		//免费店（freeAndNoRestock）标价为 0，取 1 兜底避免除零
		int price = Math.max(1, Shopkeeper.sellPrice(item));
		return Math.min(1f, stealValueCap() / price);
	}

	/** SPSEXPD: 偷普通商店的货品；失手会惊动商店老板，但不会散落金币。 */
	protected void tryStealGoods(Heap heap) {
		Hero owner = ownerOf();
		Item goods = heap.peek();
		if (owner == null || goods == null) return;

		if (Random.Float() < stealChance(goods)) {
			takeFromHeap(heap, owner, "steal_goods_ok");
		} else {
			GLog.w(Messages.get(this, "steal_goods_fail"));
			alertShopkeepers();
		}
	}

	/** SPSEXPD: 偷秘密商店的货品；失手要付出「货价一半」的永久生命上限。 */
	protected void tryStealLifeGoods(Heap heap) {
		Hero owner = ownerOf();
		Item goods = heap.peek();
		if (owner == null || goods == null) return;

		if (Random.Float() < stealChance(goods)) {
			takeFromHeap(heap, owner, "steal_life_ok");
		} else {
			int cost = Math.max(1, WndLifeTradeItem.price() / 2);
			if (owner.spendPermanentHT(cost)) {
				GLog.w(Messages.get(this, "steal_life_fail", cost));
			} else {
				//永久生命已不足以支付代价：保底不扣，只提示失手
				GLog.w(Messages.get(this, "steal_life_none"));
			}
		}
	}

	/** SPSEXPD: 落点是普通掉落物（含金币堆）时直接取来一件。 */
	protected void grabGroundItem(Heap heap) {
		Hero owner = ownerOf();
		if (owner == null) return;

		Item picked = heap.pickUp();
		if (picked == null) return;

		deliver(picked, owner);
		GLog.i(Messages.get(this, "pick_ground", picked.name()));
		onStolen(owner);
	}

	private void takeFromHeap(Heap heap, Hero owner, String messageKey) {
		Item stolen = heap.pickUp();
		if (stolen == null) return;

		deliver(stolen, owner);
		GLog.i(Messages.get(this, messageKey, stolen.name()));
		onStolen(owner);
	}

	/** SPSEXPD: 交给英雄；背包放不下就掉在脚下（与旧版魔术之手一致）。 */
	private void deliver(Item item, Hero owner) {
		if (!item.doPickUp(owner) && Dungeon.level != null) {
			Dungeon.level.drop(item, owner.pos);
		}
	}

	private void onStolen(Hero owner) {
		//SPSEXPD: 取物成功的音效已按用户要求取消（保留取物动作动画与快捷栏刷新）
		if (owner.sprite != null) owner.sprite.operate(owner.pos);
		updateQuickslot();
	}

	/** SPSEXPD: 惊动本层的商店老板（秘密商店店主是 TownNpc，不参与）。 */
	private void alertShopkeepers() {
		if (Dungeon.level == null || Dungeon.level.mobs() == null) return;
		//SPSEXPD: 老板被惊动会召唤守卫（往 mobs 里加人），必须先取快照再迭代，
		//否则触发 ConcurrentModificationException 闪退（已在实机复现）
		for (Mob mob : Dungeon.level.mobs().snapshot()) {
			if (mob instanceof Shopkeeper) ((Shopkeeper) mob).noticeTheft();
		}
	}

	/**
	 * SPSEXPD: 落点没有物品时踩踏草丛与植物——与角色走过去踩踏同一套效果，
	 * 但不触发陷阱/井/门（那属于「按格子」而非「踩踏」）。
	 */
	protected void trampleCell(int cell) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;

		switch (Dungeon.level.map[cell]) {
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
				HighGrass.trample(Dungeon.level, cell);
				return;
			case Terrain.OLD_HIGH_GRASS:
				OldHighGrass.trample(Dungeon.level, cell, Actor.findChar(cell));
				return;
			default:
				break;
		}

		Plant plant = Dungeon.level.plants.get(cell);
		if (plant != null) {
			//隔空踩踏：以释放者作为触发者，让植物效果作用在英雄身上
			plant.trigger(ownerOf());
		}
	}

	private Hero ownerOf() {
		return curUser instanceof Hero ? (Hero) curUser : Dungeon.hero;
	}

	/**
	 * SPSEXPD: 从被命中的目标身上摸走一件东西——与偷商品用同一套概率（越贵越难偷），
	 * 失手不消耗目标身上的机会（还能再花 1 点充能重试）。
	 */
	protected void stealFrom(Mob mob, Char target) {
		Hero owner = ownerOf();
		if (owner == null) return;

		//先看能得到什么（此时还不改 firstItem 标记），再按它的价值掷概率
		Item loot = mob.firstItem ? mob.SupercreateLoot() : null;

		if (loot == null) {
			//目标身上已经没有可偷的东西：沿用旧版规则给一块石头
			GLog.i(Messages.get(this, "stolen_stone", Messages.get(target, "name")));
			deliver(new StoneOre(), owner);
			return;
		}

		if (Random.Float() >= stealChance(loot)) {
			GLog.w(Messages.get(this, "steal_fail", Messages.get(target, "name")));
			return;
		}

		mob.firstItem = false;
		GLog.i(Messages.get(this, "stolen", Messages.get(target, "name"), loot.name()));
		deliver(loot, owner);
	}
}
