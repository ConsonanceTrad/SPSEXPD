/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Poison;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.AdamantArmor;
import pd.items.Gold;
import pd.items.quest.AdventureJournal;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CrabKingSprite;
import pd.ui.BossHealthBar;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
public class CrabKing extends Mob {
	private static final int JUMP_DELAY = 5;
	private int timeToJump = JUMP_DELAY;
	{ spriteClass = CrabKingSprite.class; baseSpeed = 2f; HP = HT = 1300; EXP = 20; defenseSkill = 30; properties.add(Property.FISHER); properties.add(Property.BOSS); resistances.add(ToxicGas.class); resistances.add(Poison.class); }
	@Override public int damageRoll() { return Random.NormalIntRange(20, 50); }
	@Override public int attackSkill(Char target) { return 35; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
	@Override protected boolean act() { boolean result = super.act(); if (HP < HT) { HP = Math.min(HT, HP + 10); if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1); GLog.n(Messages.get(this, "heal")); } return result; }
	@Override protected boolean getCloser(int target) { if (fieldOfView != null && target >= 0 && target < fieldOfView.length && fieldOfView[target] && jump()) return true; return super.getCloser(target); }
	@Override protected boolean canAttack(Char enemy) { return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos; }
	@Override protected boolean doAttack(Char enemy) { timeToJump--; if (timeToJump <= 0 && Dungeon.level.adjacent(pos, enemy.pos) && jump()) return true; return super.doAttack(enemy); }
	private boolean jump() { timeToJump = JUMP_DELAY; ArrayList<Integer> cells = new ArrayList<>(); for (int cell = 0; cell < Dungeon.level.length(); cell++) if (Dungeon.level.heroFOV[cell] && Dungeon.level.passable[cell] && Actor.findChar(cell) == null && (enemy == null || !Dungeon.level.adjacent(cell, enemy.pos))) cells.add(cell); if (cells.isEmpty()) return false; int old = pos; int cell = Random.element(cells); if (sprite != null) sprite.move(old, cell); move(cell); CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 6); Sample.INSTANCE.play(Assets.Sounds.PUFF); spend(1f/speed()); return true; }
	@Override public void notice() { super.notice(); BossHealthBar.assignBoss(this); yell(Messages.get(this, "notice")); }
	@Override public void die(Object cause) { int cell = pos; super.die(cause); Dungeon.crabKingKilled = true; AdventureJournal.complete(12); Dungeon.level.unseal(); GameScene.bossSlain(); Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), cell).sprite.drop(); Dungeon.level.drop(new AdamantArmor(), cell).sprite.drop(); yell(Messages.get(this, "die")); }
	private static final String JUMP = "jump";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(JUMP, timeToJump); }
	@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); timeToJump = b.contains(JUMP) ? b.getInt(JUMP) : JUMP_DELAY; if (state != SLEEPING) BossHealthBar.assignBoss(this); }
}
