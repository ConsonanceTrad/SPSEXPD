/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.SpsAutoFire;
import pd.items.misc.GauntletShield;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 扩音筒 —— 演员开局的两件专属物品之一（特殊副武器）。
 *
 * <p>装备在副手栏后，点击 {@link #MAX_RANGE} 格内的敌人即可隔空喊出一击：
 * 距离越远伤害衰减越多（1 格 120% → 6 格 20%），代价是这一嗓子会_唤醒攻击范围内的所有敌人_。</p>
 */
public class Megaphone extends GauntletShield implements SpsAutoFire {



	/** 最大攻击距离（格）。 */
	public static final int MAX_RANGE = 6;
	/** 1 格时的伤害倍率。 */
	public static final float NEAR_MULT = 1.2f;
	/** 每远 1 格递减的倍率。 */
	public static final float STEP_MULT = 0.2f;
	/** 伤害倍率下限（正好落在 6 格）。 */
	public static final float MIN_MULT = 0.2f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Megaphone.class)
			.t("name", "扩音筒")
			.t("desc", "一只蒙了锈的铁皮喇叭，嗓门大得能把嗓子喊哑。\n\n装备在_副手栏_后，点击 %1$d 格内的敌人就会隔空朝它喊上一嗓子——_距离越远威力越小_（1 格 120%% → %1$d 格 20%%）。\n\n代价是这一嗓子会_惊醒范围内的所有敌人_。")
			.t("prompt", "选择要大喊的目标")
			.t("shout", "你朝%s大喊了一声！");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MAX_RANGE);
	}

	/** SPSEXPD: 占位，不显示护手盾的格挡数值。
	 *  注意必须返回空串而不是 null —— MeleeWeapon.info() 会对 statsInfo() 直接调 .equals("")。 */
	@Override public String statsInfo() { return ""; }

	/** SPSEXPD: 攻击距离 6。 */
	@Override public int reachFactor(Char owner) { return MAX_RANGE; }

	/** SPSEXPD: 距离倍率——1 格 120%，每远 1 格 -20%，到 6 格正好 20%。 */
	public static float distanceMultiplier(int distance) {
		return Math.max(MIN_MULT, NEAR_MULT - STEP_MULT * (distance - 1));
	}

	@Override
	public boolean autoFire(Hero hero, Char enemy) {
		if (hero == null || enemy == null || !isEquipped(hero)) return false;
		if (Dungeon.level == null) return false;

		int distance = Dungeon.level.distance(hero.pos, enemy.pos);
		if (distance <= 0 || distance > MAX_RANGE) return false;

		//SPSEXPD: 被墙挡住的喊不到（与其它远程武器一致）
		Ballistica bolt = new Ballistica(hero.pos, enemy.pos, Ballistica.PROJECTILE);
		if (bolt.collisionPos != enemy.pos) return false;

		curUser = hero;
		int dmg = Math.max(1, Math.round(damageRoll(hero) * distanceMultiplier(distance)));

		if (hero.sprite != null) hero.sprite.zap(enemy.pos);
		Sample.INSTANCE.play(Assets.Sounds.RAY);
		enemy.damage(dmg, this);
		GLog.i(Messages.get(Megaphone.class, "shout", enemy.name()));
		awakenAround(hero);

		hero.spendAndNext(1f);
		return true;
	}

	/** SPSEXPD: 唤醒攻击范围内（{@link #MAX_RANGE} 格内）的所有敌人。 */
	public static void awakenAround(Hero hero) {
		if (hero == null || Dungeon.level == null) return;
		for (Mob mob : Dungeon.level.mobs().snapshot()) {
			if (mob.alignment != Char.Alignment.ENEMY) continue;
			if (Dungeon.level.distance(hero.pos, mob.pos) > MAX_RANGE) continue;
			mob.beckon(hero.pos);
		}
	}

}
