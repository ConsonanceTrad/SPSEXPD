/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.levels.FieldOfView;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ScarecrowSprite;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 哨箭 —— 星兵族的召唤单位。
 *
 * <p>行为：沿当前方向_直线_飞行，每回合移动 {@link #YAKA_SPEED}（注射哨箭为
 * {@link #INJECTION_SPEED}）格；_可穿过生物_，但撞到墙壁等实心地形就停下；
 * 途经每一个格子都会对那里的敌人造成物理伤害。可以在抵达目标前_提前变向_。</p>
 */
public class SpsArrow extends Mob {

	public enum Kind { YAKA, INJECTION }

	/** 亚卡箭每回合移动的格数。 */
	public static final int YAKA_SPEED = 4;
	/** 注射哨箭每回合移动的格数（更快）。 */
	public static final int INJECTION_SPEED = 6;
	/** 下令时惊醒敌人的范围。 */
	public static final int AWAKEN_RANGE = 6;
	/** 途经伤害的基础值（再加上英雄等级）。 */
	public static final int BASE_DAMAGE = 8;
	/** 注入药水后附带的命中次数。 */
	public static final int INJECTION_HITS = 3;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SpsArrow.class)
			.t("name_yaka", "亚卡箭")
			.t("name_injection", "注射哨箭")
			.t("desc_yaka", "一支被声音驱使的哨箭，会笔直地穿过一切生物。")
			.t("desc_injection", "一支更快的哨箭，可以预先注入药水，让它在命中时附带那份效果。");
	}

	private static final String KIND = "kind";
	private static final String DX = "dx";
	private static final String DY = "dy";
	private static final String TRAVELLING = "travelling";
	private static final String TARGET = "target";
	private static final String INJECTED = "injected";
	private static final String HITS = "hits";

	private Kind kind = Kind.YAKA;
	private int dx = 0, dy = 0;
	private boolean travelling = false;
	private int targetCell = -1;

	/** SPSEXPD: 注入的药水与其剩余命中次数。 */
	private pd.items.consum.potions.Potion injected;
	private int injectionHits = 0;

	{
		spriteClass = ScarecrowSprite.class;   //SPSEXPD: 暂用占位贴图
		HP = HT = 500;
		flying = true;
		EXP = 0;
		maxLvl = -2;
		state = PASSIVE;
		alignment = Alignment.ALLY;
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Terror.class);
	}

	public Kind kind() { return kind; }

	public void setKind(Kind k) {
		this.kind = k;
	}

	@Override
	public String name() {
		return Messages.get(SpsArrow.class, kind == Kind.INJECTION ? "name_injection" : "name_yaka");
	}

	@Override
	public String description() {
		return Messages.get(SpsArrow.class, kind == Kind.INJECTION ? "desc_injection" : "desc_yaka");
	}

	/** 每回合可移动的格数。 */
	public int moveSpeed() {
		return kind == Kind.INJECTION ? INJECTION_SPEED : YAKA_SPEED;
	}

	public boolean isTravelling() {
		return travelling;
	}

	public int injectedHits() {
		return injectionHits;
	}

	/** SPSEXPD: 注入一瓶药水，接下来的若干次命中会附带它的效果。 */
	public void inject(pd.items.consum.potions.Potion potion) {
		this.injected = potion;
		this.injectionHits = INJECTION_HITS;
	}

	/** SPSEXPD: 下达/变更方向——朝目标格直线飞行（可在抵达前再次改变）。 */
	public void commandTo(int cell) {
		if (Dungeon.level == null || cell < 0) return;
		int w = Dungeon.level.width();
		int ddx = (cell % w) - (pos % w);
		int ddy = (cell / w) - (pos / w);
		dx = Integer.signum(ddx);
		dy = Integer.signum(ddy);
		if (dx == 0 && dy == 0) return;
		targetCell = cell;
		travelling = true;
		//SPSEXPD: 哨箭靠声音指挥——下令会惊醒周围一定范围的敌人
		awakenAround();
	}

	/** SPSEXPD: 惊醒周围 {@link #AWAKEN_RANGE} 格内的所有敌人。 */
	public void awakenAround() {
		if (Dungeon.level == null) return;
		for (Mob m : Dungeon.level.mobs().snapshot()) {
			if (m == this || m.alignment != Alignment.ENEMY) continue;
			if (Dungeon.level.distance(pos, m.pos) > AWAKEN_RANGE) continue;
			m.beckon(pos);
		}
	}

	@Override
	public void beckon(int cell) {
		//哨箭不受呼唤
	}

	@Override
	protected boolean act() {
		if (Dungeon.level == null) return true;

		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		FieldOfView.update(Dungeon.level, this, fieldOfView);

		throwItems();

		if (sprite != null) {
			sprite.hideAlert();
			sprite.hideLost();
		}

		if (travelling) step();

		spend(TICK);
		return true;
	}

	/** SPSEXPD: 沿当前方向走 moveSpeed() 格；撞墙或到达目标即停。 */
	private void step() {
		int w = Dungeon.level.width();
		int h = Dungeon.level.height();

		for (int i = 0; i < moveSpeed(); i++) {
			int nx = (pos % w) + dx;
			int ny = (pos / w) + dy;
			if (nx < 0 || ny < 0 || nx >= w || ny >= h) { stop(); break; }
			int next = ny * w + nx;
			//SPSEXPD: 不可穿透墙壁——实心地形直接停下（生物不算障碍，直接穿过）
			if (Dungeon.level.solid[next]) { stop(); break; }

			pos = next;
			Dungeon.level.occupyCell(this);
			if (sprite != null) sprite.place(pos);
			hitPassersby();

			if (targetCell >= 0 && pos == targetCell) { stop(); break; }
		}

		GameScene.updateFog();
	}

	private void stop() {
		travelling = false;
		dx = 0;
		dy = 0;
		targetCell = -1;
	}

	/** SPSEXPD: 对当前格上的敌人造成途经伤害（并施加注入的药水效果）。 */
	private void hitPassersby() {
		Char ch = Actor.findChar(pos);
		if (ch == null || ch == this || ch == Dungeon.hero) return;
		if (ch.alignment == alignment) return;

		int dmg = BASE_DAMAGE + (Dungeon.hero == null ? 0 : Dungeon.hero.lvl);
		ch.damage(dmg, this);
		if (ch.sprite != null) ch.sprite.flash();

		if (injected != null && injectionHits > 0 && ch.isAlive()) {
			try {
				//SPSEXPD: 把注入的药水在目标格溅开（等价于让目标"喝下/被泼到"这瓶药水）
				injected.shatter(ch.pos);
			} catch (Exception ignored) {
				//药水效果不适用于该目标时忽略
			}
			injectionHits--;
			if (injectionHits <= 0) injected = null;
		}
	}

	@Override
	public int damageRoll() {
		return BASE_DAMAGE + (Dungeon.hero == null ? 0 : Dungeon.hero.lvl);
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(KIND, kind.name());
		bundle.put(DX, dx);
		bundle.put(DY, dy);
		bundle.put(TRAVELLING, travelling);
		bundle.put(TARGET, targetCell);
		bundle.put(INJECTED, injected);
		bundle.put(HITS, injectionHits);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		kind = Kind.valueOf(bundle.getString(KIND));
		dx = bundle.getInt(DX);
		dy = bundle.getInt(DY);
		travelling = bundle.getBoolean(TRAVELLING);
		targetCell = bundle.getInt(TARGET);
		injected = (pd.items.consum.potions.Potion) bundle.get(INJECTED);
		injectionHits = bundle.getInt(HITS);
	}
}
