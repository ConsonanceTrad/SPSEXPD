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
import watabou.noosa.Game;
import watabou.noosa.audio.Music;
import watabou.noosa.audio.Sample;
import watabou.utils.DeviceCompat;
import watabou.utils.PlatformSupport;

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
	
	public ShatteredPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );

		//pre-v3.3.0
		watabou.utils.Bundle.addAlias(
				pd.items.keys.WornKey.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.fusion.EyeOfSkadi.class,
				"com.hmdzl.spspd.items.artifacts.EyeOfSkadi" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.fusion.EyeOfSkadi.EyeRecharge.class,
				"com.hmdzl.spspd.items.artifacts.EyeOfSkadi$eyeRecharge" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.fusion.NoomlinCrown.class,
				"com.hmdzl.spspd.items.artifacts.NoomlinCrown" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.fusion.NoomlinCrown.Crown.class,
				"com.hmdzl.spspd.items.artifacts.NoomlinCrown$crown" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.AlienBag.class,
				"com.hmdzl.spspd.items.artifacts.AlienBag" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.AlienBag.BagRecharge.class,
				"com.hmdzl.spspd.items.artifacts.AlienBag$bagRecharge" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.Pylon.class,
				"com.hmdzl.spspd.items.artifacts.Pylon" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.Pylon.BeaconRecharge.class,
				"com.hmdzl.spspd.items.artifacts.Pylon$beaconRecharge" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.TimeOclock.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.TimeOclock.OclockRecharge.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$oclockRecharge" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.TimeOclock.TimeStasis.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$timeStasis2" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.TimeOclock.Clock.class,
				"com.hmdzl.spspd.items.artifacts.TimeOclock$clock" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.RobotDMT.class,
				"com.hmdzl.spspd.items.artifacts.RobotDMT" );
		watabou.utils.Bundle.addAlias(
				pd.items.artifacts.RobotDMT.DmtRecharge.class,
				"com.hmdzl.spspd.items.artifacts.RobotDMT$dmtRecharge" );
		watabou.utils.Bundle.addAlias(
				pd.items.medicine.TimePill.class,
				"com.hmdzl.spspd.items.medicine.Timepill" );
		watabou.utils.Bundle.addAlias(
				pd.items.medicine.Timepill2.class,
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
