package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.MobSpawner;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * SPSEXPD: 由转换笼果实酿造。
 * 饮用后获得一个随机限时增益；投掷则使命中的普通怪物嬗变为本层允许的另一种怪物。
 */
public class PotionOfTransmute extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfTransmute.class)
			.t("name", "嬗化药剂")
			.t("desc", "以转换笼果实酿成的不稳定药剂。饮用后会获得一项随机的临时强化；砸向普通怪物时，则会将其嬗变为本层允许的另一种怪物。");
	}

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		switch (Random.Int(5)) {
			case 0:
				Buff.affect(hero, AttackUp.class, 120f).level(20);
				break;
			case 1:
				Buff.affect(hero, DefenceUp.class, 120f).level(20);
				break;
			case 2:
				Buff.prolong(hero, HasteBuff.class, 60f);
				break;
			case 3:
				Buff.prolong(hero, Bless.class, 60f);
				break;
			case 4:
				Buff.prolong(hero, Recharging.class, 60f);
				break;
			default:
				Buff.affect(hero, Barkskin.class).set(6 + hero.lvl / 2, 240);
				break;
		}
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}

	@Override public void shatter(int cell) {
		Char ch = Actor.findChar(cell);
		if (ch != null) transmute(ch);
		super.shatter(cell);
	}

	/** SPSEXPD: 把普通怪物替换为本层允许的另一种怪物，保留当前生命（嬗化药剂与转换笼果共用）。 */
	public static boolean transmute(Char target) {
		if (!(target instanceof Mob) || Dungeon.level == null || !target.isAlive()
				|| !Dungeon.level.insideMap(target.pos)
				|| Char.hasProp(target, Char.Property.BOSS)
				|| Char.hasProp(target, Char.Property.MINIBOSS)) return false;

		ArrayList<Class<? extends Mob>> rotation = new ArrayList<>(MobSpawner.getMobRotation(Dungeon.depth));
		rotation.removeIf(type -> type == target.getClass());
		if (rotation.isEmpty()) return false;

		Class<? extends Mob> type = Random.element(rotation);
		Mob transformed = Reflection.newInstance(type);
		if (transformed == null) return false;

		int pos = target.pos;
		transformed.HT = transformed.HP = Math.max(1, target.HP);
		transformed.pos = pos;
		Actor.remove(target);
		Dungeon.level.mobs().remove(target);
		if (target.sprite != null) target.sprite.killAndErase();
		GameScene.add(transformed);
		Dungeon.level.occupyCell(transformed);
		CellEmitter.get(pos).burst(Speck.factory(Speck.CHANGE), 6);
		return true;
	}

	@Override public int value() { return 60 * quantity; }
}
