package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.messages.InlineText;
public class ShockBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ShockBuff2Trap.class)
			.t("name", "雷种中陷阱")
			.t("desc", "会释放中等范围雷电场的陷阱。");
	}


 public ShockBuff2Trap(){ super(YELLOW, WAVES, ElectriShock.class, 1, 6, false); } }
