/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.levels.FieldOfView;
import pd.scenes.GameScene;
import pd.sprites.ScarecrowSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.utils.data.Callback;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 蒲团的「哨兵植物」单位。
 *
 * <p>蒲团放置时会生成一个 {@link Kind#MARKER}（待命蒲团）；英雄点击它并选择后，它会变成
 * 三种固定哨兵植物之一：{@link Kind#PEASHOOTER 豌豆射手}、{@link Kind#POTATO_MINE 土豆雷}、
 * {@link Kind#WALLNUT 坚果墙}。三者都是不移动的友方单位。</p>
 */
public class FutonSentinel extends Mob {

	public enum Kind {
		MARKER, PEASHOOTER, POTATO_MINE, WALLNUT
	}

	public static final int PEASHOOTER_RANGE = 5;
	public static final int PEASHOOTER_INTERVAL = 2;
	public static final int POTATO_MINE_DAMAGE = 25;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(FutonSentinel.class)
			.t("name_marker", "待命蒲团")
			.t("name_peashooter", "豌豆射手")
			.t("name_potatomine", "土豆雷")
			.t("name_wallnut", "坚果墙")
			.t("choose_title", "选择要长出的哨兵植物")
			.t("choose_peashooter", "豌豆射手")
			.t("choose_potatomine", "土豆雷")
			.t("choose_wallnut", "坚果墙")
			.t("grown", "蒲团舒展开来，长成了一株%s。")
			.t("mine_armed", "土豆雷已经埋好了。")
			.t("desc_marker", "一团还软着的蒲团，等待着被唤醒成哨兵植物。")
			.t("desc_peashooter", "一株会朝远处敌人喷射豌豆的哨兵植物。")
			.t("desc_potatomine", "一颗埋在地里的土豆，敌人靠近时就会炸开。")
			.t("desc_wallnut", "一堵厚实耐打的坚果墙，只能挨打，不会还手。");
	}

	private static final String KIND = "kind";
	private static final String ARMED = "armed";

	private Kind kind = Kind.MARKER;
	private int shotCooldown = 0;
	private boolean armed = false;

	{
		spriteClass = ScarecrowSprite.class;
		HP = HT = 30;
		EXP = 0;
		maxLvl = -2;
		state = PASSIVE;
		alignment = Alignment.ALLY;
		properties.add(Property.PLANT);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.STATIC);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
	}

	@Override
	public String name() {
		switch (kind) {
			case PEASHOOTER: return Messages.get(FutonSentinel.class, "name_peashooter");
			case POTATO_MINE: return Messages.get(FutonSentinel.class, "name_potatomine");
			case WALLNUT: return Messages.get(FutonSentinel.class, "name_wallnut");
			default: return Messages.get(FutonSentinel.class, "name_marker");
		}
	}

	@Override
	public String description() {
		switch (kind) {
			case PEASHOOTER: return Messages.get(FutonSentinel.class, "desc_peashooter");
			case POTATO_MINE: return Messages.get(FutonSentinel.class, "desc_potatomine");
			case WALLNUT: return Messages.get(FutonSentinel.class, "desc_wallnut");
			default: return Messages.get(FutonSentinel.class, "desc_marker");
		}
	}

	public Kind kind() {
		return kind;
	}

	@Override
	public void beckon(int cell) {
		//哨兵植物不会被呼唤
	}

	/**
	 * SPSEXPD: 与待命蒲团交互 → 三选一，长出对应的哨兵植物。
	 * 已经长成植物的单位不再响应交互。
	 */
	@Override
	public boolean interact(Char c) {
		if (!(c instanceof Hero) || kind != Kind.MARKER) return false;
		final Hero hero = (Hero) c;
		final FutonSentinel self = this;
		//SPSEXPD: interact 跑在 actor 线程（Hero.act → Actor.process），而 WndOptions 构造会测量文字，
		//必须切到渲染线程，否则抛 "Text measured from the actor thread!" 直接闪退。
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(new WndOptions(
						new pd.sprites.ItemSprite(pd.atlas.items.SpecificPlaceHolderDict.SOMETHING_0),
						Messages.get(FutonSentinel.class, "choose_title"),
						"",
						Messages.get(FutonSentinel.class, "choose_peashooter"),
						Messages.get(FutonSentinel.class, "choose_potatomine"),
						Messages.get(FutonSentinel.class, "choose_wallnut")) {
					@Override
					protected void onSelect(int index) {
						Kind chosen = index == 0 ? Kind.PEASHOOTER : index == 1 ? Kind.POTATO_MINE : Kind.WALLNUT;
						self.grow(chosen);
						GLog.p(Messages.get(FutonSentinel.class, "grown", self.name()));
						hero.spendAndNext(1f);
					}
				});
			}
		});
		return true;
	}

	/** SPSEXPD: 长成指定的哨兵植物。 */
	public void grow(Kind chosen) {
		this.kind = chosen;
		switch (chosen) {
			case PEASHOOTER: HP = HT = 30; break;
			case POTATO_MINE: HP = HT = 15; break;
			case WALLNUT: HP = HT = 120; break;
			default: break;
		}
		HP = HT;
		if (sprite != null && Dungeon.level != null && Dungeon.level.heroFOV != null
				&& pos >= 0 && pos < Dungeon.level.length() && Dungeon.level.heroFOV[pos]) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.HEALING), 4);
		}
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

		switch (kind) {
			case PEASHOOTER:
				peashoot();
				break;
			case POTATO_MINE:
				mineTick();
				break;
			default:
				break;
		}

		spend(TICK);
		return true;
	}

	private void peashoot() {
		if (shotCooldown > 0) {
			shotCooldown--;
			return;
		}
		Char target = nearestEnemy(PEASHOOTER_RANGE);
		if (target == null) return;

		shotCooldown = PEASHOOTER_INTERVAL;
		int dmg = peashooterDamage();
		if (sprite != null) sprite.zap(target.pos);
		target.damage(dmg, this);
	}

	private void mineTick() {
		if (!armed) {
			armed = true;
			if (Dungeon.hero != null && Dungeon.level.heroFOV[pos]) {
				GLog.i(Messages.get(FutonSentinel.class, "mine_armed"));
			}
			return;
		}
		for (Mob m : Dungeon.level.mobs().snapshot()) {
			if (m == this || m.alignment != Alignment.ENEMY || !m.isAlive()) continue;
			if (Dungeon.level.distance(pos, m.pos) > 1) continue;
			explode();
			return;
		}
	}

	private void explode() {
		if (Dungeon.level != null) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.SMOKE), 8);
		}
		for (Mob m : Dungeon.level.mobs().snapshot()) {
			if (m == this || m.alignment != Alignment.ENEMY || !m.isAlive()) continue;
			if (Dungeon.level.distance(pos, m.pos) > 1) continue;
			m.damage(POTATO_MINE_DAMAGE + (Dungeon.hero == null ? 0 : Dungeon.hero.lvl), this);
		}
		die(this);
	}

	/** 视野内、指定距离内最近的敌人（不受隐身保护影响）。 */
	private Char nearestEnemy(int range) {
		Mob best = null;
		int bestDist = Integer.MAX_VALUE;
		for (Mob m : Dungeon.level.mobs().snapshot()) {
			if (m == this || m.alignment != Alignment.ENEMY || !m.isAlive()) continue;
			int d = Dungeon.level.distance(pos, m.pos);
			if (d > range || d >= bestDist) continue;
			if (!fieldOfView[m.pos]) continue;
			best = m;
			bestDist = d;
		}
		return best;
	}

	private int peashooterDamage() {
		int lvl = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		return 6 + render.utils.math.Random.Int(lvl / 3, lvl / 2 + 3);
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return 2;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(KIND, kind.name());
		bundle.put(ARMED, armed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		String k = bundle.getString(KIND);
		try {
			if (k != null && !k.isEmpty()) kind = Kind.valueOf(k);
		} catch (IllegalArgumentException ignored) {
			kind = Kind.MARKER;
		}
		armed = bundle.getBoolean(ARMED);
	}
}
