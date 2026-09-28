/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.AdamantWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Random;

public class SkeletonKing extends Mob {
	{
		spriteClass = SkeletonKingSprite.class;
		HP = HT = 2000;
		defenseSkill = 30;
		EXP = 50;
		flying = true;
		loot = PotionOfLiquidFlame.class;
		lootChance = 0.1f;
		properties.add(Property.UNDEAD);
		properties.add(Property.BOSS);
		immunities.add(Burning.class);
	}
	@Override public int damageRoll() { return Random.NormalIntRange(20, 40); }
	@Override public int attackSkill(Char target) { return 25; }
	@Override public int drRoll() { return Random.NormalIntRange(10, 25); }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 3; }
	@Override protected boolean act() {
		boolean result = super.act();
		if (state == FLEEING && buff(Terror.class) == null && enemy != null
				&& enemySeen && enemy.buff(STRDown.class) == null) state = HUNTING;
		return result;
	}
	@Override public int attackProc(Char enemy, int damage) {
		if (enemy == Dungeon.hero && Random.Int(2) == 0) {
			Buff.prolong(enemy, STRDown.class, 10f);
			state = FLEEING;
		}
		return super.attackProc(enemy, damage);
	}
	@Override public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice", Dungeon.hero == null ? "" : Dungeon.hero.name()));
	}
	@Override public void die(Object cause) {
		int deathPos = pos;
		super.die(cause);
		Dungeon.skeletonKingKilled = true;
		AdventureJournal.complete(11);
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), deathPos).sprite.drop();
		Dungeon.level.drop(new AdamantWeapon(), deathPos).sprite.drop();
		yell(Messages.get(this, "die"));
	}
}
