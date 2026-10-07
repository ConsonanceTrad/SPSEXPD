/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Daze;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfElements;
import pd.items.equipment.rings.RingOfEvasion;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfFuror;
import pd.items.equipment.rings.RingOfHaste;
import pd.items.equipment.rings.RingOfMight;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.rings.RingOfWealth;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import pd.messages.InlineText;

/**
 * SPSEXPD: 移植自 Darkest PD 0.7.2 的神器「古老者之手」（HandOfTheElder）。
 *
 * <p>把戒指镶进骨手即可让它成长（戒指等级决定成长量，被诅咒的戒指也会诅咒骨手）；
 * 镶嵌过的戒指种类决定了「指向」敌人时额外附加的状态。指向会按目标最大生命造成暗影伤害、
 * 定身（无法逃脱）并施加各枚戒指对应的负面状态，消耗 1 点充能，充能在背包中缓慢恢复。</p>
 */
public class HandOfTheElder extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HandOfTheElder.class)
			.t("name", "古老者之手")
			.t("ac_point", "指向")
			.t("ac_wear", "镶嵌戒指")
			.t("point_prompt", "选择要指向的目标")
			.t("wear_prompt", "选择要镶嵌的戒指")
			.t("unknown_ring", "这只手认不出那枚戒指。")
			.t("duplicate_ring", "这只手已经镶过同类戒指了。")
			.t("no_charge", "古老者之手尚未蓄积足够的力量。")
			.t("cannot_wear", "被诅咒的骨手紧紧攥着，你无法为它戴上戒指。")
			.t("levelup", "你把%1$s按进骨手，腐朽的指节重新聚拢，古老者之手变强了！")
			.t("cursed_levelup", "你把%1$s按进骨手，戒指的诅咒也顺着骨指爬上来了！")
			.t("point_done", "骨手指向了%1$s，它再也逃不掉了。")
			.t("desc", "腐朽的骨手看起来年代久远，但其中依然隐藏着这位先知的能力。把戒指嵌进骨指，它的力量就会增长；被手指指到的敌人将被钉在原地。")
			.t("desc_equipped", "骨手在你的背包中缓慢地蓄积着力量。")
			.t("desc_rings", "已镶嵌的戒指：");
	}

	public static final String AC_POINT = "POINT";
	public static final String AC_WEAR = "WEAR";
	public static final int MAX_RINGS = 5;
	private static final String RINGS = "rings";

	private final ArrayList<Class<? extends Ring>> rings = new ArrayList<>();

	/** 已镶嵌的戒指种类 → 指向时附加的负面状态。 */
	private static final HashMap<Class<?>, Class<? extends FlavourBuff>> RING_EFFECTS = new HashMap<>();
	static {
		RING_EFFECTS.put(RingOfAccuracy.class, ArmorBreak.class);
		RING_EFFECTS.put(RingOfElements.class, Chill.class);
		RING_EFFECTS.put(RingOfEvasion.class, Daze.class);
		RING_EFFECTS.put(RingOfForce.class, Weakness.class);
		RING_EFFECTS.put(RingOfFuror.class, Slow.class);
		RING_EFFECTS.put(RingOfHaste.class, Slow.class);
		RING_EFFECTS.put(RingOfMight.class, Weakness.class);
		RING_EFFECTS.put(RingOfSharpshooting.class, Blindness.class);
		RING_EFFECTS.put(RingOfWealth.class, Vertigo.class);
	}

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 10;
		charge = 2;
		partialCharge = 0;
		chargeCap = 2;
		defaultAction = AC_POINT;
		usesTargeting = true;
	}

	public int charge() {
		return charge;
	}

	public int chargeCap() {
		return chargeCap;
	}

	public ArrayList<Class<? extends Ring>> rings() {
		return new ArrayList<>(rings);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) {
			if (rings.size() < MAX_RINGS) actions.add(AC_WEAR);
			if (charge > 0) actions.add(AC_POINT);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_POINT.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				usesTargeting = false;
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
				usesTargeting = false;
			} else {
				usesTargeting = true;
				GameScene.selectCell(charSelector);
			}
		} else if (AC_WEAR.equals(action)) {
			curUser = hero;
			if (cursed) {
				GLog.w(Messages.get(this, "cannot_wear"));
			} else if (rings.size() < MAX_RINGS) {
				GameScene.selectItem(ringSelector);
			}
		}
	}

	/** 把一枚戒指镶进骨手：按戒指等级成长，诅咒随戒指传播。 */
	public boolean wearRing(Ring ring) {
		if (ring == null || rings.size() >= MAX_RINGS) return false;
		if (rings.contains(ring.getClass())) return false;

		int gain = Math.max(1, Math.min(levelCap - level(), ring.level() + 1));
		if (gain > 0) level(level() + gain);

		if (ring.cursed) {
			cursed = true;
			cursedKnown = true;
			if (Dungeon.hero != null && Dungeon.hero.sprite != null) {
				Sample.INSTANCE.play(Assets.Sounds.CURSED);
			}
			GLog.p(Messages.get(this, "cursed_levelup", ring.name()));
		} else {
			if (Dungeon.hero != null && Dungeon.hero.sprite != null) {
				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			}
			GLog.p(Messages.get(this, "levelup", ring.name()));
		}

		rings.add(ring.getClass());
		updateQuickslot();
		return true;
	}

	/** 指向一个目标：暗影伤害 + 定身 + 各戒指对应的负面状态。 */
	public void pointAt(Char target) {
		Char source = curUser != null ? curUser : Dungeon.hero;
		if (target == null || source == null) return;
		charge = Math.max(0, charge - 1);

		float factor = cursed ? 1.25f : 1.0f;
		float duration = (level() / 2f + 2f) * factor;

		int max = Math.max(1, target.HT / 5);
		int min = Math.min(max, Math.max(1, target.HT / 10));
		int damage = Random.Int(min, max);
		target.damage(damage, this);

		if (target.isAlive()) {
			//无法逃脱：定身
			Buff.prolong(target, Roots.class, duration);
			for (Class<? extends Ring> ring : rings) {
				Class<? extends FlavourBuff> effect = RING_EFFECTS.get(ring);
				if (effect == null) effect = Cripple.class;
				Buff.prolong(target, effect, duration);
			}
			GLog.p(Messages.get(this, "point_done", target.name()));
		}

		updateQuickslot();
		source.next();
	}

	private final CellSelector.Listener charSelector = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null || Dungeon.level == null) return;
			if (!Dungeon.level.insideMap(cell)) return;
			Char target = Actor.findChar(cell);
			if (target == null || target == curUser) return;
			pointAt(target);
		}

		@Override
		public String prompt() {
			return Messages.get(HandOfTheElder.class, "point_prompt");
		}
	};

	private final pd.windows.WndBag.ItemSelector ringSelector = new pd.windows.WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(HandOfTheElder.class, "wear_prompt");
		}

		@Override
		public Class<? extends pd.items.equipment.bags.Bag> preferredBag() {
			return pd.actors.hero.Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Ring;
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Ring) || curUser == null) return;
			Ring ring = (Ring) item;
			if (!ring.isIdentified()) {
				GLog.w(Messages.get(HandOfTheElder.class, "unknown_ring"));
				return;
			}
			if (rings.contains(ring.getClass())) {
				GLog.w(Messages.get(HandOfTheElder.class, "duplicate_ring"));
				return;
			}
			if (ring.isEquipped(curUser) && !ring.doUnequip(curUser, false)) {
				GLog.w(Messages.get(HandOfTheElder.class, "cannot_wear"));
				return;
			}
			if (!wearRing(ring)) return;

			if (curUser.sprite != null) {
				curUser.sprite.operate(curUser.pos);
				curUser.sprite.emitter().burst(pd.effects.particles.ElmoParticle.FACTORY, 12);
			}
			curUser.busy();
			curUser.spend(2f);
			ring.detachAll(curUser.belongings.backpack);
		}
	};

	@Override
	public String desc() {
		String desc = super.desc();
		if (Dungeon.hero != null && isEquipped(Dungeon.hero)) {
			desc += "\n\n" + Messages.get(this, "desc_equipped");
			if (!rings.isEmpty()) {
				desc += "\n\n" + Messages.get(this, "desc_rings");
				for (Class<? extends Ring> ring : rings) {
					desc += "\n" + Messages.get(ring, "name");
				}
			}
		}
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Recharge();
	}

	/** 背包中缓慢充能：每回合累积 1.05^等级 × 0.025。 */
	public class Recharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				partialCharge += (float) (Math.pow(1.05, level()) * 0.025);
				if (partialCharge >= 1f) {
					charge++;
					partialCharge -= 1f;
					if (charge >= chargeCap) partialCharge = 0f;
					updateQuickslot();
				}
			} else {
				partialCharge = 0f;
			}
			spend(TICK);
			return true;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		ArrayList<String> names = new ArrayList<>();
		for (Class<? extends Ring> ring : rings) names.add(ring.getName());
		bundle.put(RINGS, names.toArray(new String[0]));
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		rings.clear();
		String[] names = bundle.getStringArray(RINGS);
		if (names == null) return;
		for (String name : names) {
			Class<?> type = render.utils.serialize.Reflection.forName(name);
			if (type != null && Ring.class.isAssignableFrom(type)) {
				@SuppressWarnings("unchecked")
				Class<? extends Ring> ring = (Class<? extends Ring>) type;
				if (!rings.contains(ring)) rings.add(ring);
			}
		}
	}
}
