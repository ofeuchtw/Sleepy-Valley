package world;

import java.util.Random;

import world.Item.type;

public class Plant {
	
	private int x;
	private int y;
	private int time;
	private type plant;
	private boolean watered;
	private boolean grown;
	private final String sprout = "plant/sprout.png";
	private final String carrotStalk = "plant/carrotStalk.png";
	private final String carrotCrop = "plant/carrotCrop.png";
	private final String stalk = "plant/stalk.png";
	private final String cornCrop = "plant/cornCrop.png";
	private final String tomatoCrop = "plant/tomatoCrop.png";
	private Random r;
	

	public Plant(int x, int y) {
		this.x = x;
		this.y = y;
		time = 0;
		watered = false;
		grown = false;
		
		r = new Random();
		int n = r.nextInt(3);
		if(n == 0) {
			plant = type.carrot;
		} else if(n == 1) {
			 plant = type.corn;
		} else {
			plant = type.tomato;
		}
	}
	
	public Plant(String plantData) {
		String typeString = "";
		int oldX, oldY;
		oldX = oldY = 0;
		String isWatered = "";
		String isGrown = "";
		
		if (!plantData.equals("null")) {
			String data[] = plantData.split("\\.");
			typeString = data[0];
			oldX = Integer.parseInt(data[1]);
			oldY = Integer.parseInt(data[2]);
			isWatered = data[3].substring(0,1);
			isGrown = data[3].substring(1);
		}
		
		type plantType = Item.type.parseType(typeString);
		
		if(plantType == null) {
			return;
		}
		
		plant = plantType;
		watered = isWatered.equals("+");
		grown = isGrown.equals("+");
		x = oldX;
		y = oldY;
		time = 0;
	}
	
	public String getAddress() {
		if(grown) {
			switch(plant) {
				case carrot:
					return carrotCrop;
				case corn:
					return cornCrop;
				case tomato:
					return tomatoCrop;
				default:
					return "";
			}
		} else if(watered) {
			if(plant == type.carrot) {
				return carrotStalk;
			} else {
				return stalk;
			}
		} else {
			return sprout;
		}
	}
	
	public type getPlant() {
		return plant;
	}
	
	public void setPlant(type plant) {
		this.plant = plant;
	}
	
	public void water() {
		watered = true;
	}
	
	public void addTime() {
		if(watered && !grown) {
			time++;
		}
		if(time % 1000 == 0 && watered) {
			grown = true;
		}
	}
	
	public void harvest() {
		grown = false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isGrown() {
		return grown;
	}
	
	public void grow() {
		grown = true;
	}
	
	public int getHeight() {
		if(watered == false) {
			return 40;
		} else {
			if(plant == type.carrot) {
				return 60;
			} else {
				return 90;
			}
		}
	}
	
	@Override
	public String toString() {
		String signature = getPlant().toString() + "." + x  + "." + y + ".";
		if(watered) {
			signature += "+";
		} else {
			signature += "-";
		}
		
		if(grown) {
			signature += "+";
		} else {
			signature += "-";
		}
		
		return signature;
	}
}
