/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon behavior restored from SPS-PD 0.9.8.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Water;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SandalsOfNature extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_SANDALS;
		levelCap = 10;
		charge = 0;
		defaultAction = AC_ROOT;
	}

	public static final String AC_FEED = "FEED";
	public static final String AC_ROOT = "ROOT";
	public static final String AC_SPROUT = "SPROUT";

	public final ArrayList<String> seeds = new ArrayList<>();

	// Retained for the hidden Shattered Spirit Form spell; normal SPS play never sets it.
	public Class curSeedEffect;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level() < levelCap && !cursed) actions.add(AC_FEED);
		if (isEquipped(hero) && charge > 0) actions.add(AC_ROOT);
		if (level() > 0 && !isEquipped(hero)) actions.add(AC_SPROUT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_FEED.equals(action)) {
			GameScene.selectItem(itemSelector);
		} else if (AC_ROOT.equals(action) && level() > 0) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge == 0) {
				GLog.i(Messages.get(this, "no_charge"));
			} else {
				root(hero);
			}
		} else if (AC_SPROUT.equals(action)) {
			sprout(hero);
		}
	}

	protected void root(Hero hero) {
		Buff.prolong(hero, Roots.class, 5f);
		Buff.affect(hero, Earthroot.MagicPlantArmor.class).level(charge);
		if (hero.sprite != null && Dungeon.level != null) {
			CellEmitter.bottom(hero.pos).start(EarthParticle.FACTORY, 0.05f, 8);
		}
		if (Camera.main != null) Camera.main.shake(1, 0.4f);
		charge = 0;
		updateQuickslot();
	}

	protected void sprout(Hero hero) {
		int length = sproutMapLength();
		int amount = 40 * level();
		for (int i = 0; i < length; i++) addSproutWater(i, amount);
		detach(hero.belongings.backpack);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		hero.spendAndNext(2f);
	}

	protected int sproutMapLength() {
		return Dungeon.level == null ? 0 : Dungeon.level.length();
	}

	protected void addSproutWater(int cell, int amount) {
		GameScene.add(Blob.seed(cell, amount, Water.class));
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Naturalism();
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc_" + (level() / 3));
		if (isEquipped(Dungeon.hero)) {
			desc += "\n\n";
			desc += cursed ? Messages.get(this, "desc_cursed") : Messages.get(this, "desc_hint");
			if (level() > 0) desc += "\n\n" + Messages.get(this, "desc_ability");
		}
		if (!seeds.isEmpty()) desc += "\n\n" + Messages.get(this, "desc_seeds", seeds.size());
		return desc;
	}

	@Override
	public Item upgrade() {
		if (level() < 3) image = ItemSpriteSheet.ARTIFACT_SANDALS;
		else if (level() < 6) image = ItemSpriteSheet.ARTIFACT_SHOES;
		else if (level() < 9) image = ItemSpriteSheet.ARTIFACT_BOOTS;
		else image = ItemSpriteSheet.ARTIFACT_GREAVES;
		return super.upgrade();
	}

	private void restoreImage() {
		if (level() <= 3) image = ItemSpriteSheet.ARTIFACT_SANDALS;
		else if (level() <= 6) image = ItemSpriteSheet.ARTIFACT_SHOES;
		else if (level() <= 9) image = ItemSpriteSheet.ARTIFACT_BOOTS;
		else image = ItemSpriteSheet.ARTIFACT_GREAVES;
	}

	public boolean canUseSeed(Item item) {
		return item instanceof Plant.Seed;
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.reset();
		curSeedEffect = null;
	}

	private static final String SEEDS = "seeds";
	private static final String CUR_SEED_EFFECT = "cur_seed_effect";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SEEDS, seeds.toArray(new String[0]));
		if (curSeedEffect != null) bundle.put(CUR_SEED_EFFECT, curSeedEffect);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		int savedCharge = bundle.getInt("charge");
		super.restoreFromBundle(bundle);
		if (level() > levelCap) level(levelCap);
		charge = Math.max(0, savedCharge);
		seeds.clear();
		if (bundle.contains(SEEDS)) {
			String[] savedSeeds = bundle.getStringArray(SEEDS);
			if (savedSeeds != null) {
				for (String seed : savedSeeds) {
					if (seed != null) seeds.add(migrateSeedName(seed));
				}
			}
		}
		curSeedEffect = bundle.contains(CUR_SEED_EFFECT) ? bundle.getClass(CUR_SEED_EFFECT) : null;
		restoreImage();
	}

	private String migrateSeedName(String saved) {
		String className = saved.replace("class ", "");
		if (!className.startsWith("com.")) return saved;
		Class<?> seedClass = Reflection.forName(className);
		if (seedClass != null && Plant.Seed.class.isAssignableFrom(seedClass)) {
			Object seed = Reflection.newInstance(seedClass);
			if (seed instanceof Item) return ((Item) seed).name();
		}
		return saved;
	}

	public class Naturalism extends ArtifactBuff {
		@Override
		public boolean isCursed() {
			return cursed;
		}

		public void charge() {
			if (charge < target.HT) {
				charge += Math.round((target.HT - charge) * (0.01f + level() * 0.01f));
				updateQuickslot();
			}
		}
	}

	protected final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(SandalsOfNature.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return VelvetPouch.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return canUseSeed(item);
		}

		@Override
		public void onSelect(Item item) {
			feedSeed(item, Dungeon.hero);
		}
	};

	protected void feedSeed(Item item, Hero hero) {
		if (!(item instanceof Plant.Seed) || hero == null) return;
		String seedName = item.name();
		if (seeds.contains(seedName)) {
			GLog.w(Messages.get(SandalsOfNature.class, "already_fed"));
			return;
		}

		seeds.add(seedName);
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.PLANT);
		hero.busy();
		hero.spend(2f);
		if (seeds.size() > level()) {
			seeds.clear();
			upgrade();
			if (level() >= 1 && level() <= levelCap) {
				GLog.p(Messages.get(SandalsOfNature.class, "levelup"));
			}
		} else {
			GLog.i(Messages.get(SandalsOfNature.class, "absorb_seed"));
		}
		item.detach(hero.belongings.backpack);
	}

	// Modern seed data is retained only for the hidden Shattered Spirit Form spell.
	private static final HashMap<Class<? extends Plant.Seed>, Integer> seedColors = new HashMap<>();
	private static final HashMap<Class<? extends Plant.Seed>, Integer> seedChargeReqs = new HashMap<>();
	static {
		seedColors.put(Rotberry.Seed.class, 0xCC0022);       seedChargeReqs.put(Rotberry.Seed.class, 8);
		seedColors.put(Firebloom.Seed.class, 0xFF7F00);     seedChargeReqs.put(Firebloom.Seed.class, 20);
		seedColors.put(Swiftthistle.Seed.class, 0xCCBB00);  seedChargeReqs.put(Swiftthistle.Seed.class, 20);
		seedColors.put(Sungrass.Seed.class, 0x2EE62E);      seedChargeReqs.put(Sungrass.Seed.class, 80);
		seedColors.put(Icecap.Seed.class, 0x66B3FF);        seedChargeReqs.put(Icecap.Seed.class, 20);
		seedColors.put(Stormvine.Seed.class, 0x195D80);     seedChargeReqs.put(Stormvine.Seed.class, 20);
		seedColors.put(Sorrowmoss.Seed.class, 0xA15CE5);    seedChargeReqs.put(Sorrowmoss.Seed.class, 20);
		seedColors.put(Mageroyal.Seed.class, 0xFF4CD2);     seedChargeReqs.put(Mageroyal.Seed.class, 12);
		seedColors.put(Earthroot.Seed.class, 0x67583D);     seedChargeReqs.put(Earthroot.Seed.class, 40);
		seedColors.put(Starflower.Seed.class, 0x404040);    seedChargeReqs.put(Starflower.Seed.class, 40);
		seedColors.put(Fadeleaf.Seed.class, 0x919999);      seedChargeReqs.put(Fadeleaf.Seed.class, 12);
		seedColors.put(Blindweed.Seed.class, 0xD9D9D9);     seedChargeReqs.put(Blindweed.Seed.class, 12);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return curSeedEffect != null && seedColors.containsKey(curSeedEffect)
				? new ItemSprite.Glowing(seedColors.get(curSeedEffect)) : null;
	}

	public final CellSelector.Listener cellSelector = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null || Dungeon.level == null || curSeedEffect == null
					|| !seedChargeReqs.containsKey(curSeedEffect) || cell < 0 || cell >= Dungeon.level.length()) return;
			if (!Dungeon.level.heroFOV[cell] || Dungeon.level.distance(curUser.pos, cell) > 3) {
				GLog.w(Messages.get(SandalsOfNature.class, "out_of_range"));
				return;
			}
			Ballistica aim = new Ballistica(curUser.pos, cell, Ballistica.STOP_TARGET);
			for (int c : aim.subPath(0, aim.dist)) CellEmitter.get(c).burst(LeafParticle.GENERAL, 6);
			Splash.at(DungeonTilemap.tileCenterToWorld(cell), -PointF.PI / 2, PointF.PI / 2,
					seedColors.get(curSeedEffect), 6);
			Invisibility.dispel(curUser);
			Plant plant = ((Plant.Seed) Reflection.newInstance(curSeedEffect)).couch(cell, null);
			plant.activate(Actor.findChar(cell));
			if (Gdx.audio != null) {
				Sample.INSTANCE.play(Assets.Sounds.PLANT);
				Sample.INSTANCE.playDelayed(Assets.Sounds.TRAMPLE, 0.25f, 1, Random.Float(0.96f, 1.05f));
			}
			if (Actor.findChar(cell) != null) {
				artifactProc(Actor.findChar(cell), visiblyUpgraded(), seedChargeReqs.get(curSeedEffect));
			}
			charge -= seedChargeReqs.get(curSeedEffect);
			Talent.onArtifactUsed(Dungeon.hero);
			updateQuickslot();
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(SandalsOfNature.class, "prompt_target");
		}
	};
}
