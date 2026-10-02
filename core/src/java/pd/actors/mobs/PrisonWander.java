/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Vertigo;
import pd.effects.CellEmitter;
import pd.effects.Chains;
import pd.effects.Effects;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.wands.WandOfLight;
import pd.items.equipment.weapon.enchantments.EnchantmentLight;
import pd.levels.GroundItems;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.plants.Blindweed;
import pd.plants.Firebloom;
import pd.plants.Icecap;
import pd.plants.Plant;
import pd.plants.Sorrowmoss;
import pd.plants.Starflower;
import pd.plants.Stormvine;
import pd.scenes.GameScene;
import pd.sprites.PrisonWanderSprite;
import pd.sprites.SeekingBombSprite;
import pd.ui.BossHealthBar;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SPS-PD's prison warden boss, including health breaks, chains, plants and seeking bombs. */
public class PrisonWander extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PrisonWander.class)
			.t("name", "典狱长")
			.t("desc", "监狱的最高管理者，结实而强大。")
			.t("notice", "我看到你了！")
			.t("die", "我还会回来的！")
			.t("scorpion", "没人能从我手上逃离！！！")
			.t("seekbombp.name", "追猎炸弹")
			.t("seekbombp.desc", "会追踪目标，并在倒计时结束后爆炸的炸弹。");
	}


	private boolean chainsUsed;
	private int breaks;

	{
		spriteClass = PrisonWanderSprite.class;
		HP = HT = 800;
		EXP = 40;
		defenseSkill = 20;
		viewDistance = 7;
		properties.add(Property.HUMAN);
		properties.add(Property.BOSS);

		immunities.add(Burning.class);
		immunities.add(Poison.class);
		immunities.add(Chill.class);
		immunities.add(Blindness.class);
		immunities.add(Vertigo.class);
		resistances.add(ToxicGas.class);
		weaknesses.add(WandOfLight.class);
		weaknesses.add(EnchantmentLight.class);
		immunities.add(Icecap.class);
		immunities.add(Firebloom.class);
		immunities.add(Blindweed.class);
		immunities.add(Stormvine.class);
		immunities.add(Sorrowmoss.class);

		HUNTING = new Hunting();
	}

	@Override public int damageRoll() { return Random.NormalIntRange(12, 21); }
	@Override public int attackSkill(Char target) { return 35; }
	@Override public int drRoll() { return Random.NormalIntRange(7, 12); }
	@Override public float attackDelay() { return 0.75f; }

	@Override
	protected boolean act() {
		if (HP > 0 && 7 - breaks > 8 * HP / HT) {
			breaks++;
			if (breaks < 4) {
				Buff.affect(this, GlassShield.class).turns(1);
				spawnBomb();
				chainsUsed = false;
			}
			return true;
		}

		if (Random.Int(10) == 0 && breaks < 4 && Dungeon.level != null) {
			Plant.Seed seed = randomBossSeed();
			if (Dungeon.level.passable[pos]) {
				GroundItems.plant( Dungeon.level, seed, pos);
			}
			spend(TICK);
			return true;
		}
		return super.act();
	}

	static Plant.Seed randomBossSeed() {
		switch (Random.chances(new float[]{8, 4, 6, 4, 3, 1})) {
			case 0: return new Firebloom.Seed();
			case 1: return new Icecap.Seed();
			case 2: return new Sorrowmoss.Seed();
			case 3: return new Blindweed.Seed();
			case 4: return new Stormvine.Seed();
			default: return new Starflower.Seed();
		}
	}

	@Override
	public void damage(int damage, Object source) {
		GlassShield shield = buff(GlassShield.class);
		if (shield != null) damage = shield.capDamage(damage);
		super.damage(damage, source);
	}

	private void spawnBomb() {
		if (Dungeon.level == null || Dungeon.hero == null) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (fieldOfView != null && !fieldOfView[cell]) continue;
			if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			if (Dungeon.level.adjacent(cell, Dungeon.hero.pos)) continue;
			candidates.add(cell);
		}
		if (candidates.isEmpty()) return;
		SeekBombP bomb = new SeekBombP();
		bomb.pos = Random.element(candidates);
		bomb.state = bomb.HUNTING;
		GameScene.add(bomb);
	}

	private boolean chain(int target) {
		if (enemy == null || Char.hasProp(enemy, Property.IMMOVABLE)) return false;
		Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);
		if (chain.collisionPos != enemy.pos || chain.path.size() < 2
				|| Dungeon.level.pit[chain.path.get(1)]) return false;

		int newPos = -1;
		for (int cell : chain.subPath(1, chain.dist)) {
			if (!Dungeon.level.solid[cell] && Actor.findChar(cell) == null
					&& (Dungeon.level.openSpace[cell] || !Char.hasProp(enemy, Property.LARGE))) {
				newPos = cell;
				break;
			}
		}
		if (newPos < 0) return false;

		final Char chainedEnemy = enemy;
		final int destination = newPos;
		this.target = destination;
		chainsUsed = true;
		if (sprite != null && chainedEnemy.sprite != null
				&& (sprite.visible || chainedEnemy.sprite.visible) && sprite.parent != null) {
			yell(Messages.get(this, "scorpion"));
			new Item().throwSound();
			Sample.INSTANCE.play(Assets.Sounds.CHAINS);
			sprite.parent.add(new Chains(sprite.center(), chainedEnemy.sprite.destinationCenter(),
					Effects.Type.CHAIN, new Callback() {
						@Override public void call() {
							Actor.add(new SourceTimedPushing(chainedEnemy, chainedEnemy.pos, destination,
									new Callback() {
										@Override public void call() { pullEnemy(chainedEnemy, destination); }
									}));
							next();
						}
					}));
		} else {
			pullEnemy(chainedEnemy, destination);
		}
		return true;
	}

	private void pullEnemy(Char target, int destination) {
		target.pos = destination;
		if (target.sprite != null) target.sprite.place(destination);
		Dungeon.level.occupyCell(target);
		Cripple.prolong(target, Cripple.class, 4f);
		Buff.prolong(target, Vertigo.class, 8f);
		if (target == Dungeon.hero) {
			Dungeon.hero.interrupt();
			Dungeon.observe();
			GameScene.updateFog();
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) < 2) {
			int opposite = enemy.pos + enemy.pos - pos;
			Ballistica trajectory = new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(enemy, trajectory, 1, false, false, this);
		} else if (Random.Int(10) < 2) {
			teleportHero();
		}
		return super.attackProc(enemy, damage);
	}

	private void teleportHero() {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (fieldOfView != null && !fieldOfView[cell]) continue;
			if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			if (Dungeon.level.adjacent(cell, Dungeon.hero.pos)) continue;
			candidates.add(cell);
		}
		if (candidates.isEmpty()) return;
		int oldPos = Dungeon.hero.pos;
		int newPos = Random.element(candidates);
		Buff.affect(Dungeon.hero, STRDown.class, 5f);
		if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.move(oldPos, newPos);
		Dungeon.hero.move(newPos, false);
		Dungeon.hero.interrupt();
		Dungeon.observe();
		GameScene.updateFog();
		if (Dungeon.hero.sprite != null && Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.Sounds.PUFF);
		}
		spend(1f);
	}

	private static final class SourceTimedPushing extends Pushing {
		SourceTimedPushing(Char ch, int from, int to, Callback callback) {
			super(ch, from, to, callback);
			spend(-1f);
		}
	}

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		pd.items.equipment.weapon.rockcode.RockCode.dropForPerformer(
				new pd.items.equipment.weapon.rockcode.Ichain());
		SpsPrisonBossRewards.grant(pos, rareLoot(), commonLoot());
		yell(Messages.get(this, "die"));
	}

	static Item rareLoot() { return new EtherealChains().identify(); }
	static Item commonLoot() { return new DungeonBomb(); }

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;
			if (!chainsUsed && enemyInFOV && !isCharmedBy(enemy) && !canAttack(enemy)
					&& Dungeon.level.distance(pos, enemy.pos) < 5 && Random.Int(3) == 0
					&& chain(enemy.pos)) {
				return !(sprite != null && enemy.sprite != null
						&& (sprite.visible || enemy.sprite.visible));
			}
			return super.act(enemyInFOV, justAlerted);
		}
	}

	private static final String CHAINS_USED = "chains_used";
	private static final String BREAKS = "breaks";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAINS_USED, chainsUsed);
		bundle.put(BREAKS, breaks);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chainsUsed = bundle.getBoolean(CHAINS_USED);
		breaks = bundle.getInt(BREAKS);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class SeekBombP extends Mob {
		private static final int BOMB_DELAY = 8;
		private static final String TIMER = "timer";
		private int timeToBomb = BOMB_DELAY;
		private boolean exploding;

		{
			spriteClass = SeekingBombSprite.class;
			HP = HT = 1;
			EXP = 0;
			defenseSkill = 0;
			baseSpeed = 1f;
			state = HUNTING;
			properties.add(Property.MECH);
			properties.add(Property.MINIBOSS);
			properties.add(Property.INORGANIC);
			properties.add(Property.BOSS_MINION);
		}

		@Override
		protected boolean act() {
			if (sprite != null && sprite.visible) yell(Integer.toString(Math.max(0, timeToBomb)) + "!");
			if (timeToBomb <= 0) {
				explode(true);
				return true;
			}
			return super.act();
		}

		@Override
		public void move(int step, boolean travelling) {
			super.move(step, travelling);
			timeToBomb--;
		}

		@Override public int attackSkill(Char target) { return 10; }
		@Override public int damageRoll() { return Random.NormalIntRange(0, 1); }
		@Override public int drRoll() { return 0; }

		@Override
		public int attackProc(Char enemy, int damage) {
			int result = super.attackProc(enemy, damage);
			explode(true);
			return result;
		}

		private void explode(boolean announce) {
			if (exploding) return;
			exploding = true;
			new DungeonBomb().explode(pos);
			if (announce && sprite != null) yell("KA-BOOM!!!");
			destroy();
			if (sprite != null) sprite.die();
		}

		@Override
		public void die(Object cause) {
			if (!exploding) new DungeonBomb().explode(pos);
			super.die(cause);
		}

		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIMER, timeToBomb);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			timeToBomb = bundle.contains(TIMER) ? bundle.getInt(TIMER) : BOMB_DELAY;
		}
	}
}
