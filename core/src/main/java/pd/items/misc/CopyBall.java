/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.SpeedUp;
import pd.actors.buffs.WatchOut;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.bombs.DungeonBomb;
import pd.items.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.GooSprite;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CopyBall extends Item {
	public static final String AC_USE = "USE";
	public static final int FULL_CHARGE = 50;
	public static final int USE_COST = 10;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.SLIME_BALL;
		defaultAction = AC_USE;
		unique = true;
		usesTargeting = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= USE_COST) actions.add(AC_USE);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (charge < USE_COST) GLog.i(Messages.get(this, "break"));
			else { curUser = hero; GameScene.selectCell(shooter); }
		} else super.execute(hero, action);
	}

	public boolean castAt(Hero hero, int target) {
		if (hero == null || charge < USE_COST || Dungeon.level == null || !Dungeon.level.insideMap(target)) return false;
		charge -= USE_COST;
		new CopyBallAmmo().cast(hero, target);
		updateQuickslot();
		return true;
	}

	public SlimeS spawnClone(int position) {
		if (Dungeon.hero == null || Dungeon.level == null || !Dungeon.level.insideMap(position)) return null;
		if (Dungeon.level.heroFOV[position]) {
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			Splash.at(position, 0xFFD500, 5);
		}
		int newPos = position;
		if (Actor.findChar(position) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = position + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) candidates.add(cell);
			}
			newPos = candidates.isEmpty() ? -1 : Random.element(candidates);
		}
		if (newPos == -1) {
			new DungeonBomb().explode(position);
			return null;
		}
		SlimeS slime = new SlimeS();
		slime.spawnFrom(Dungeon.hero);
		slime.pos = newPos;
		GameScene.add(slime);
		Sample.INSTANCE.play(Assets.Sounds.BEE);
		return slime;
	}

	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }
	@Override public String status() { return Integer.toString(charge / USE_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }

	public class CopyBallAmmo extends MissileWeapon {
		{
			image = ItemSpriteSheet.SLIME_BALL;
			tier = 1;
			ACC = 1000f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return 0; }
		@Override public int max(int lvl) { return 0; }
		@Override public int STRReq(int lvl) { return 0; }
		@Override public int damageRoll(Char owner) { return 0; }
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) {
				Splash.at(cell, 0xCC99FFFF, 1);
				new DungeonBomb().explode(cell);
			} else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			defender.damage(attacker.damageRoll() + 10, Hunger.class);
			spawnClone(defender.pos);
			return super.proc(attacker, defender, damage);
		}
	}

	public static class SlimeS extends DirectableAlly {
		{
			spriteClass = GooSprite.class;
			viewDistance = 6;
			flying = true;
		}

		public void spawnFrom(Hero hero) {
			HP = HT = Math.max(1, hero.HT / 3);
			defenseSkill = hero.defenseSkill(null);
		}

		@Override public int attackSkill(Char target) {
			return Dungeon.hero == null ? 0 : Dungeon.hero.attackSkill(target);
		}
		@Override public int damageRoll() { return 0; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Dungeon.hero != null) enemy.damage(Math.max(0, Dungeon.hero.damageRoll() / 3), Hunger.class);
			return super.attackProc(enemy, damage);
		}
		@Override protected boolean canAttack(Char enemy) {
			return Dungeon.level != null && Dungeon.level.distance(pos, enemy.pos) <= 2;
		}
		@Override public synchronized boolean add(Buff buff) {
			if (buff instanceof AttackUp || buff instanceof DefenceUp || buff instanceof ShieldArmor
					|| buff instanceof MagicArmor || buff instanceof HasteBuff
					|| buff instanceof SpeedUp
					|| buff instanceof HiddenShadow || buff instanceof WatchOut) return super.add(buff);
			return false;
		}
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) castAt(curUser, target); }
		@Override public String prompt() { return Messages.get(CopyBall.class, "prompt"); }
	};
}
