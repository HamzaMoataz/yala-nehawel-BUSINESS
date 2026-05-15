package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

import static game.engine.Board.getStationedMonsters;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	private int stealEnergyFrom(Monster target){
		int amountToSteal = Constants.SCHEMER_STEAL;
		int StolenAmount=Math.min(amountToSteal, target.getEnergy());
		int newEnergy= target.getEnergy()-StolenAmount;
		target.setEnergy(newEnergy);
		return StolenAmount;
	}


	@Override
	public void setEnergy(int energy) {
		int change = energy - getEnergy();
		super.setEnergy(getEnergy() + change + 10);
	}

	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		int totalStolen=this.stealEnergyFrom(opponentMonster);
		for (int i = 0; i< getStationedMonsters().size(); i++){
			Monster m = getStationedMonsters().get(i);
			if (m!=this && m!=opponentMonster){
				totalStolen += this.stealEnergyFrom(m);
			}
		}
		this.setEnergy(this.getEnergy() + totalStolen);
	}
}
