package world;


public class Creature {

	private int x,y;
	private int vertShift;
	private species type;
	private int direction;
	private int count;
	
	public enum species {
		ghost, sheep, cow, pig;
		
		@Override
		public String toString() {
			if(this == ghost) {
				return "ghost";
			} else if (this == sheep) {
				return "sheep";
			} else if(this == cow) {
				return "cow";
			} else if(this == pig) {
				return "pig";
			} else {
				return "";
			}
		}
		
		public static species parseSpecies(String s) {
			switch(s) {
				case "cow":
					return cow;
				case "sheep":
					return sheep;
				case "pig":
					return pig;
				case "ghost":
					return ghost;
				default:
					return null;
			}
		}
		
		public String drop() {
			if (this == sheep) {
				return "chop";
			} else if(this == cow) {
				return "steak";
			} else if(this == pig) {
				return "pork";
			} else {
				return "";
			}
		}
	}
	
	public Creature(species type) {
		this.type = type;
		x = (int)(Math.random() * 1720) + 80;
		y = (int)(Math.random() * 1610) + 80;
		vertShift = y;
		direction = 1;
		count = 0;
	}
	
	public Creature(int x, int y, species type) {
		this.x = x;
		this.y = y;
		vertShift = y;
		this.type = type;
		direction = 1;
		count = 0;
	}
	
	public void move() {
		switch(type) {
			case ghost:
				x += 10 * direction;
				y = (int)(10 * direction * Math.sin(Math.toRadians(x))) + vertShift;
				if(this.x >= 1800) {
					direction = -1;
				} else if (this.x <= 60) {
					direction = 1;
				}
			case cow:
				if(count / 20 < 1) {
					direction = 1;
					x += 5;
				} else if(count / 20 >= 2 && count / 20 < 3) {
					direction = -1;
					x -= 5;
				} else if(count / 20 >= 4 && count / 20 < 5) {
					direction = -1;
					x -= 5;
				} else if(count / 20 >= 6 && count / 20 < 7) {
					direction = 1;
					x += 5;
				} else if(count >= 140) {
					count = 0;
				}
				count++;
			case pig:
				if(count / 20 < 1) {
					direction = 1;
					x += 7;
				} else if(count / 20 >= 2 && count / 20 < 3) {
					direction = -1;
					x -= 7;
				} else if(count / 20 >= 4 && count / 20 < 5) {
					direction = -1;
					x -= 7;
				} else if(count / 20 >= 6 && count / 20 < 7) {
					direction = 1;
					x += 7;
				} else if(count >= 140) {
					count = 0;
				}
				count++;
			case sheep:
				if(count / 20 < 1) {
					direction = 1;
					x += 12;
				} else if(count / 20 >= 2 && count / 20 < 3) {
					direction = -1;
					x -= 12;
				} else if(count / 20 >= 4 && count / 20 < 5) {
					direction = -1;
					x -= 12;
				} else if(count / 20 >= 6 && count / 20 < 7) {
					direction = 1;
					x += 12;
				} else if(count >= 140) {
					count = 0;
				}
				count++;
			default:
				return;
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
			case ghost:
				return 50;
			case cow:
				return 80;
			case pig:
				return 60;
			case sheep:
				return 62;
			default:
				return 10;
		}
	}
	
	public int getHeight() {
		switch(type) {
		case ghost:
			return 65;
		case cow:
			return 65;
		case pig:
			return 50;
		case sheep:
			return 50;
		default:
			return 10;
		}
	}
	
	public void shift(int shift) {
		vertShift = shift;
	}
	
	public void direct(int d) {
		direction = d;
	}
	
	public species getSpecies() {
		return type;
	}
	
	public String getImageAddress() {
		switch(type) {
			case ghost:
				return "animal/ghost.png";
			case cow:
				if(direction == -1) {
					return "animal/cow.png";
				} else {
					return "animal/cow2.png";
				}
			case sheep:
				if(direction == -1) {
					return "animal/sheep.png";
				} else {
					return "animal/sheep2.png";
				}
			case pig:
				if(direction == -1) {
					return "animal/pig.png";
				} else {
					return "animal/pig2.png";
				}
			default:
				return "";
		}
	}
	
	public boolean touch(int x, int y) {
		return (Math.abs(this.x-x) < 40 && Math.abs(this.y-y) < 50);
	}
	
	@Override
	public String toString() {
		return type.toString() + "." + x + "." + y + "." + vertShift + "." + direction;
	}

}
