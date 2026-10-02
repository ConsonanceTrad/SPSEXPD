package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.Fire;
import pd.messages.InlineText;
public class FireBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FireBuff3Trap.class)
			.t("name", "火种大陷阱")
			.t("desc", "会释放大范围火焰场的陷阱。");
	}


 public FireBuff3Trap(){ super(ORANGE, STARS, Fire.class, 2, 9, false); } }
