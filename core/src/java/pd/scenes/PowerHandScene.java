/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.scenes;

import pd.Assets;
import pd.Badges;
import pd.Chrome;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.effects.Flare;
import pd.items.PowerHand;
import pd.messages.Messages;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.StyledButton;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.utils.geom.RectF;
import pd.messages.InlineText;

public class PowerHandScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(PowerHandScene.class)
			.t("exit", "结束游戏")
			.t("stay", "向下探索")
			.t("text", "你启动了这个手套，瞬间来到一个由1和0构成的世界。看起来你成功逃离了那个地牢，但前方已经没有路了。好好休息吧。");
	}


	private static final int WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private StyledButton exitButton;
	private StyledButton stayButton;

	{
		inGameScene = true;
	}

	@Override
	public void create() {
		super.create();
		RenderedTextBlock text = renderTextBlock(Messages.get(this, "text"), 8);
		text.maxWidth(PixelScene.landscape() ? 2 * WIDTH - 4 : WIDTH);
		add(text);

		Image pudding = new Image(Assets.Sprites.PUDDING_CUP);
		add(pudding);

		exitButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "exit")) {
			@Override protected void onClick() {
				exitButton.enable(false);
				stayButton.enable(false);
				Dungeon.win(PowerHand.class);
				Dungeon.deleteGame(GamesInProgress.curSlot, true);
				Badges.saveGlobal();
				Game.switchScene(RankingsScene.class);
			}
		};
		exitButton.icon(Icons.CLOSE.get());
		exitButton.setSize(WIDTH, BUTTON_HEIGHT);
		add(exitButton);

		stayButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "stay")) {
			@Override protected void onClick() {
				exitButton.enable(false);
				stayButton.enable(false);
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = Dungeon.depth;
				InterlevelScene.returnBranch = PowerHand.CHAOS_BRANCH;
				InterlevelScene.returnPos = -1;
				Game.switchScene(InterlevelScene.class);
			}
		};
		stayButton.setSize(WIDTH, BUTTON_HEIGHT);
		add(stayButton);

		RectF insets = getCommonInsets();
		int width = (int) (Camera.main.width - insets.left - insets.right);
		int height = (int) (Camera.main.height - insets.top - insets.bottom);
		float contentHeight = pudding.height + 8 + text.height() + 8
				+ exitButton.height() + 2 + stayButton.height();
		pudding.x = insets.left + (width - pudding.width) / 2;
		pudding.y = insets.top + (height - contentHeight) / 2;
		align(pudding);
		text.setPos(insets.left + (width - text.width()) / 2, pudding.y + pudding.height + 8);
		align(text);
		exitButton.setPos(insets.left + (width - exitButton.width()) / 2, text.bottom() + 8);
		stayButton.setPos(exitButton.left(), exitButton.bottom() + 2);
		new Flare(8, 48).color(0xFFDDBB, true).show(pudding, 0).angularSpeed = 30;
		fadeIn();
	}

	@Override
	protected void onBackPressed() {
		// Choosing an ending is intentional; do not dismiss into an invalid save state.
	}
}
