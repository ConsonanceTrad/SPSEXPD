/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd;

import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.scenes.TitleScene;
import pd.scenes.WelcomeScene;
import render.noosa.Game;
import render.noosa.audio.Music;
import render.noosa.audio.Sample;
import render.utils.platform.DeviceCompat;
import render.utils.platform.PlatformSupport;

public class ShatteredPixelDungeon extends Game {

	//rankings from v1.2.3 and older use a different score formula, so this reference is kept
	public static final int v1_2_3 = 628;

	//savegames from versions older than v3.1.1 are no longer supported, and data from them is ignored
	public static final int v3_1_1 = 850;
	public static final int v3_2_1 = 861; //last version for Android 4.4- and Java 8
	public static final int v3_2_5 = 877;
	public static final int v3_3_0 = 883;

	//starting here we are doing 2 version codes per public update, so use code-1 to get both
	public static final int v4_0_0 = 909;

	//SPSXPD: 特质（Perk）体系起始版本 —— 早于此版本的存档带有破碎天赋数据，不再兼容
	public static final int v_traits = 925;
	
	public ShatteredPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );

		//pre-v3.3.0
		render.utils.serialize.Bundle.addAlias(
				pd.items.specific.keys.WornKey.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.fusion.EyeOfSkadi.class,
				"com.hmdzl.spspd.items.artifacts.EyeOfSkadi" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.fusion.EyeOfSkadi.EyeRecharge.class,
				"com.hmdzl.spspd.items.artifacts.EyeOfSkadi$eyeRecharge" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.fusion.NoomlinCrown.class,
				"com.hmdzl.spspd.items.artifacts.NoomlinCrown" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.fusion.NoomlinCrown.Crown.class,
				"com.hmdzl.spspd.items.artifacts.NoomlinCrown$crown" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.AlienBag.class,
				"com.hmdzl.spspd.items.artifacts.AlienBag" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.AlienBag.BagRecharge.class,
				"com.hmdzl.spspd.items.artifacts.AlienBag$bagRecharge" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.Pylon.class,
				"com.hmdzl.spspd.items.artifacts.Pylon" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.Pylon.BeaconRecharge.class,
				"com.hmdzl.spspd.items.artifacts.Pylon$beaconRecharge" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.TimeOclock.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.TimeOclock.OclockRecharge.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$oclockRecharge" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.TimeOclock.TimeStasis.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$timeStasis2" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.TimeOclock.Clock.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$clock" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.RobotDMT.class,
				"com.hmdzl.spspd.items.artifacts.RobotDMT" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.equipment.artifacts.RobotDMT.DmtRecharge.class,
				"com.hmdzl.spspd.items.artifacts.RobotDMT$dmtRecharge" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.consum.medicine.TimePill.class,
				"com.hmdzl.spspd.items.medicine.Timepill" );
		render.utils.serialize.Bundle.addAlias(
				pd.items.consum.medicine.Timepill2.class,
				"com.hmdzl.spspd.items.medicine.Timepill2" );

	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		SPDAction.loadBindings();
		
		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()*SPDSettings.musicVol()/100f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()*SPDSettings.SFXVol()/100f );

		Sample.INSTANCE.load( Assets.Sounds.all );
		
	}

	@Override
	public void finish() {
		if (!DeviceCompat.isiOS()) {
			super.finish();
		} else {
			//can't exit on iOS (Apple guidelines), so just go to title screen
			switchScene(TitleScene.class);
		}
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (width == 0 || height == 0){
			return;
		}

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			PixelScene.noFade = true;
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}
	
	@Override
	public void destroy(){
		super.destroy();
		GameScene.endActorThread();
	}
	
	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}
