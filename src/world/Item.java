package world;

public class Item {

	private int x;
	private int y;
	private boolean altered;
	private boolean submerged;
	private type type;
	private boolean outside;
	
	public enum type {
		carrot, tomato, corn, seeds, can, key, sword, steak, chop, pork;
		
		@Override
		public String toString() {
			if(this == carrot) {
				return "carrot";
			} else if (this == tomato) {
				return "tomato";
			} else if(this == corn) {
				return "corn";
			} else if(this == seeds) {
				return "seeds";
			} else if(this == can) {
				return "can";
			} else if(this == key) {
				return "key";
			} else if(this == sword) {
				return "sword";
			} else if(this == steak) {
				return "steak";
			} else if(this == chop) {
				return "chop";
			} else if(this == pork) {
				return "pork";
			} else {
				return "";
			}
		}
		
		public static type parseType(String s) {
			switch(s) {
				case "seeds":
					return seeds;
				case "can":
					return can;
				case "canFull":
					return can;
				case "tomato":
					return tomato;
				case "corn":
					return corn;
				case "carrot":
					return carrot;
				case "key":
					return key;
				case "sword":
					return sword;
				case "steak":
					return steak;
				case "chop":
					return chop;
				case "pork":
					return pork;
				default:
					return null;
			}
		}
		
		public boolean isCan() {
			return this == can;
		}
		
		public boolean isKey() {
			return this == key;
		}
		
		public boolean isSword() {
			return this == sword;
		}
		
		public boolean isFood() {
			return this == corn || this == carrot || this == tomato || this == steak || this == chop || this == pork;
		}
		
		public int calories() {
			switch(this) {
				case corn:
					return 1;
				case carrot:
					return 1;
				case tomato:
					return 1;
				case steak:
					return 3;
				case chop:
					return 3;
				case pork:
					return 3;
				default:
					return 0;
			}
		}
		
	}
	
	public Item(type type, int x, int y) {
		altered = false;
		submerged = false;
		if(type.isKey()) {
			submerged = true;
		}
		this.type = type;
		this.x = x;
		this.y = y;
		outside = true;
	}
	
	public void change() {
		altered = !altered;
	}
	
	public void submerge() {
		submerged = true;
	}
	
	public void unsubmerge() {
		submerged = false;
	}
	
	public boolean isOutside() {
		return outside;
	}
	
	public String getAddress() {
		switch(type) {
			case carrot:
				if(!altered) {
					if(submerged) {
						return "items/carrotWet.png";
					} else {
						return "items/carrot.png";
					}
				} else {
					return "items/carrotAlt.png";
				}
			case tomato:
				if(submerged) {
					return "items/tomatoWet.png";
				} else {
					return "items/tomato.png";
				}
			case corn:
				if(!altered) {
					if(submerged) {
						return "items/cornWet.png";
					} else {
						return "items/corn.png";
					}
				} else {
					return "items/cornAlt.png";
				}
			case seeds:
				if(submerged) {
					return "items/seedsWet.png";
				} else {
					return "items/seeds.png";
				}
			case can:
				if(!altered) {
					return "items/can.png";
				} else {
					return "items/canFull.png";
				}
			case key: 
				if(submerged) {
					return "items/keyWet.png";
				} else {
					return "items/key.png";
				}
			case sword:
				if(submerged) {
					return "items/swordWet.png";
				} else if(altered) {
					return "items/swordAlt.png";
				} else {
					return "items/sword.png";
				}
			case steak: 
				if(submerged) {
					return "items/steakWet.png";
				} else {
					return "items/steak.png";
				}
			case chop: 
				if(submerged) {
					return "items/chopWet.png";
				} else {
					return "items/chop.png";
				}
			case pork: 
				if(submerged) {
					return "items/porkWet.png";
				} else {
					return "items/pork.png";
				}
			default:
				return "";
		}
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getWidth() {
		switch(type) {
			case carrot:
				return 40;
			case tomato:
				return 30;
			case corn:
				return 40;
			case seeds:
				return 40;
			case can:
				return 63;
			case key:
				return 15;
			case sword:
				if(altered) {
					return 70;
				} else {
					return 30;
				}
			case steak:
				return 35;
			case chop:
				return 35;
			case pork:
				return 35;
			default:
				return 0;
		}
	}
	
	public int getHeight() {
		switch(type) {
			case carrot:
				return 40;
			case tomato:
				return 30;
			case corn:
				return 40;
			case seeds:
				return 40;
			case can:
				return 28;
			case key:
				return 30;
			case sword:
				if(altered) {
					return 30;
				} else {
					return 70;
				}
			case steak:
				return 35;
			case chop:
				return 35;
			case pork:
				return 33;
			default:
				return 0;
		}
	}
	
	public int getHorizAlign() {
		switch(type) {
			case carrot:
				return 400;
			case tomato:
				return 400;
			case corn:
				return 400;
			case seeds:
				return 400;
			case can:
				return 380;
			case key:
				return 413;
			case sword:
				return 410;
			case steak:
				return 405;
			case chop:
				return 405;
			case pork:
				return 405;
			default:
				return 0;
		}
	}
	
	public int getVertAlign() {
		switch(type) {
			case carrot:
				return 430;
			case tomato:
				return 430;
			case corn:
				return 430;
			case seeds:
				return 430;
			case can:
				return 440;
			case key:
				return 445;
			case sword:
				return 400;
			case steak:
				return 425;
			case chop:
				return 425;
			case pork:
				return 425;
			default:
				return 0;
		}
	}
	
	public void grab() {
		submerged = false;
		if(type.isSword() && altered) {
			altered = false;
		}
	}
	
	public void drop(int x, int y, boolean outside) {
//		retrieved = false;
		this.x = x;
		this.y = y;
		this.outside = outside;
	}
	
	public void putInside() {
		outside = false;
	}
	
	public boolean isAlt() {
		return altered;
	}
	
	public type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		String s = "";
		if(type.isCan()) {
			if(altered) {
				s = "canFull" + "." + x + "." + y + ".";
			} else {
				s = "can" + "." + x + "." + y + ".";
			}
		} else {
			s = type.toString() + "." + x + "." + y + ".";
		}
		
		if(outside) {
			s += "-";
		} else {
			s += "+";
		}
		
		return s;
	}
}
