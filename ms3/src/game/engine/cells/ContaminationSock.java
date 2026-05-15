package game.engine.cells;

import game.engine.Constants;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier {

	public ContaminationSock(String name, int effect) {

		super(name, effect);
		setNegativeEffect(effect);
	}

	@Override
	public void transport(Monster monster) {
		monster.setPosition(monster.getPosition() + getNegativeEffect());
		modifyCanisterEnergy(monster, -Constants.SLIP_PENALTY);
	}

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		monster.alterEnergy(canisterValue);
	}

	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster); // calls transport(), which handles energy
	}

	@Override
	public int getEffect() {
		return getNegativeEffect();
	}

}

