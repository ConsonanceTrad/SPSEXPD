/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.effects.particles.SparkParticle;
import pd.items.quest.AdventureJournal;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.FlyingProtectorSprite;
import pd.utils.GLog;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class FlyingProtector extends Mob implements Callback {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FlyingProtector.class)
			.t("name", "智慧守卫")
			.t("desc", "智慧试炼的守护者。")
			.t("zap_kill", "智慧守卫的闪电击杀了你……");
	}


	private static final float TIME_TO_ZAP = 2f;
	private static final String LEGACY_DEPTH = "legacy_depth";
	private int legacyDepth = AdventureJournal.destinationForBranch(Dungeon.branch) == 22
			? 85 : legacyDungeonDepth();

	{
		spriteClass = FlyingProtectorSprite.class;
		EXP = 5;
		maxLvl = 100;
		state = HUNTING;
		flying = true;
		HP = HT = 50 + legacyDepth * 4;
		defenseSkill = 4 + legacyDepth;
		properties.add(Property.ELECTRIC);
		properties.add(Property.ELEMENT);
		resistances.add(DM100.LightningBolt.class);
	}

	public void adjustStats(int depth) {
		legacyDepth = Math.max(1, depth);
		HP = HT = 50 + legacyDepth * 4;
		defenseSkill = 4 + legacyDepth;
	}

	@Override public int damageRoll() { return Random.NormalIntRange(20, 30); }
	@Override public int attackSkill(Char target) { return 9 + legacyDepth; }
	@Override public int drRoll() { return Random.NormalIntRange(0, legacyDepth); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_DEPTH, legacyDepth);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(LEGACY_DEPTH)) legacyDepth = bundle.getInt(LEGACY_DEPTH);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);
		spend(TIME_TO_ZAP);
		if (hit(this, enemy, true)) {
			int damage = Random.Int(25, 40);
			if (Dungeon.level.water[enemy.pos] && !enemy.flying) damage = Math.round(damage * 1.5f);
			enemy.damage(damage, this);
			if (enemy.sprite != null && enemy.sprite.visible) {
				enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				enemy.sprite.flash();
			}
			if (enemy == Dungeon.hero) {
				PixelScene.shake(2, 0.3f);
				if (!enemy.isAlive()) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail(this);
					GLog.n(Messages.get(this, "zap_kill"));
				}
			}
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
		}

		if (sprite != null && (sprite.visible || (enemy.sprite != null && enemy.sprite.visible))) {
			sprite.zap(enemy.pos);
			return false;
		}
		return true;
	}

	@Override public void call() { next(); }
}
