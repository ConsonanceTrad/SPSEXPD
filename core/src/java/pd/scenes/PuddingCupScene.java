package pd.scenes;

import pd.Assets;
import pd.Chrome;
import pd.Dungeon;
import pd.effects.Flare;
import pd.messages.Messages;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.StyledButton;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.utils.geom.RectF;
import pd.messages.InlineText;

public class PuddingCupScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(PuddingCupScene.class)
			.t("exit", "好的")
			.t("stay", "这不是新手教程，给我回来")
			.t("text", "恭喜你完成了新手教程，之后的冒险要由你自己掌控了。");
	}




	private static final int WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private static final float SMALL_GAP = 2;
	private static final float LARGE_GAP = 8;

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
			@Override
			protected void onClick() {
				onBackPressed();
			}
		};
		exitButton.icon(Icons.CLOSE.get());
		exitButton.setSize(WIDTH, BUTTON_HEIGHT);
		add(exitButton);

		stayButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "stay")) {
			@Override
			protected void onClick() {
				onBackPressed();
			}
		};
		stayButton.setSize(WIDTH, BUTTON_HEIGHT);
		add(stayButton);

		RectF insets = getCommonInsets();
		int width = (int) (Camera.main.width - insets.left + insets.right);
		int height = (int) (Camera.main.height - insets.top + insets.bottom);
		float contentHeight = pudding.height + LARGE_GAP + text.height() + LARGE_GAP
				+ exitButton.height() + SMALL_GAP + stayButton.height();

		pudding.x = insets.left + (width - pudding.width) / 2;
		pudding.y = insets.top + (height - contentHeight) / 2;
		align(pudding);
		text.setPos(insets.left + (width - text.width()) / 2,
				pudding.y + pudding.height + LARGE_GAP);
		align(text);
		exitButton.setPos(insets.left + (width - exitButton.width()) / 2,
				text.top() + text.height() + LARGE_GAP);
		stayButton.setPos(exitButton.left(), exitButton.bottom() + SMALL_GAP);

		new Flare(8, 48).color(0xFFDDBB, true).show(pudding, 0).angularSpeed = 30;
		fadeIn();
	}

	@Override
	protected void onBackPressed() {
		if (exitButton.isActive()) {
			exitButton.enable(false);
			stayButton.enable(false);
			if (Dungeon.isTutorial()) {
				Game.switchScene(TitleScene.class);
			} else {
				InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
				Game.switchScene(InterlevelScene.class);
			}
		}
	}
}
