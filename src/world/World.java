package world;

import java.awt.Color;
//import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import world.Creature.species;
import world.Item.type;

public class World extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Random r;
	private Scanner in;
	private FileReader reader;
	private boolean start;
	private int x, y;
	private String[][] world;

	private final String[] cali = { "pixel/cali2.png", "pixel/cali1.png",
			"pixel/cali3.png" };
	private final String[] kilo = { "pixel/kilo2.png", "pixel/kilo1.png",
			"pixel/kilo3.png" };
	private final String[] lina = { "pixel/lina2.png", "pixel/lina1.png",
			"pixel/lina3.png" };
	private final String[] mauri = { "pixel/mauri2.png", "pixel/mauri1.png",
			"pixel/mauri3.png" };

	private final String[][] character = { cali, kilo, lina, mauri };

	private final String[] healthicon = { "pixel/healthicon0.png",
			"pixel/healthicon1.png", "pixel/healthicon2.png",
			"pixel/healthicon3.png", "pixel/healthicon2.png",
			"pixel/healthicon1.png" };
	private final String[] hungericon = { "pixel/hungericon0.png",
			"pixel/hungericon1.png", "pixel/hungericon2.png",
			"pixel/hungericon3.png", "pixel/hungericon4.png",
			"pixel/hungericon3.png", "pixel/hungericon2.png",
			"pixel/hungericon1.png" };

	private Item[] inventory;
	private Item[] drawer;

	private ArrayList<Item> items;
	private ArrayList<Plant> plants;
	private ArrayList<Creature> ghosts;
	private ArrayList<Creature> animals;

	private Item hand;

	private int n, t, button, version, rotation1, rotation2, v, selected, time,
			delay, rebound, hunger, health;
	private boolean swing, hit, pause, behind, outside, sleep, toggleDrawer,
			hold, toggleInventory, unlocked, dead;

	// real value is smaller...for some reason
	private final int DIMENSION = 480;

	public World() {
		setFocusable(true);
		timer = new Timer(1, this);
		r = new Random();
		addKeyListener(new TAdapter());

		start = pause = behind = sleep = toggleDrawer = toggleInventory = unlocked = dead = false;
		outside = true;

		x = 770;
		y = 850;

		t = n = button = version = rotation1 = rotation2 = v = selected = time = delay = rebound = 0;

		hunger = 16;
		health = 24;

		swing = hit = false;

		world = new String[4][4];
		items = new ArrayList<Item>();
		plants = new ArrayList<Plant>(5);
		ghosts = new ArrayList<Creature>();
		animals = new ArrayList<Creature>();

		inventory = new Item[10];
		drawer = new Item[10];

		hand = null;
		hold = false;

		try {
			reader = new FileReader("save.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		in = new Scanner(reader);

		title();
	}

	public void title() {
		timer.start();
		repaint();
	}

	//parses data, creates appropriate Items and adds them to the destination
	public void add(String[] data, Item[] destination) {
		for (int i = 0; i < data.length; i++) {
			String name = "";
			if (!data[i].equals("null")) {
				int first = data[i].indexOf(".");
				name = data[i].substring(0, first);
			}
			type pl = Item.type.parseType(name);
			if (pl != null) {
				destination[i] = new Item(pl, 0, 0);
				if (destination[i].getType() == type.key) {
					destination[i].unsubmerge();
				}
			} else {
				destination[i] = null;
			}
			if (name.equals("canFull")) {
				destination[i].change();
			}
		}
	}

	//parses data, creates appropriate Animals and adds them to the destination
	public void add(String[] data, ArrayList<Creature> destination) {
		for (int i = 0; i < data.length; i++) {
			String name = "";
			int oldX, oldY, shift, direction;
			oldX = oldY = shift = direction = 0;
			if (!data[i].equals("null") && data[i].length() != 0) {
				int first = data[i].indexOf(".");
				int second = data[i].indexOf(".", first + 1);
				int third = data[i].indexOf(".", second + 1);
				int fourth = data[i].indexOf(".", third + 1);
				name = data[i].substring(0, first);
				oldX = Integer.parseInt(data[i].substring(first + 1, second));
				oldY = Integer.parseInt(data[i].substring(second + 1, third));
				shift = Integer.parseInt(data[i].substring(third + 1, fourth));
				direction = Integer.parseInt(data[i].substring(fourth + 1));

			}
			species sp = Creature.species.parseSpecies(name);
			Creature an = new Creature(oldX, oldY, sp);
			an.shift(shift);
			an.direct(direction);
			destination.add(an);
		}
	}

	//reads game data from save file and starts game
	public void resume() {
		try {
			reader = new FileReader("save.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		in = new Scanner(reader);

		reset();
		if (!in.hasNextInt()) {
			start();
			return;
		}
		x = Integer.parseInt(in.nextLine());
		y = Integer.parseInt(in.nextLine());
		time = Integer.parseInt(in.nextLine());
		hunger = Integer.parseInt(in.nextLine());
		health = Integer.parseInt(in.nextLine());

		String[] inv = in.nextLine().split(",");
		add(inv, inventory);

		String[] dr = in.nextLine().split(",");
		add(dr, drawer);

		String ite = in.nextLine();
		if (ite.equals("null")) {
			items = new ArrayList<Item>();
		} else {
			String[] it = ite.split(",");
			for (int i = 0; i < it.length; i++) {
				String name = "";
				String pos = "";
				int oldX, oldY;
				oldX = oldY = 0;
				if (!it[i].equals("null") && it[i].length() != 0) {
					int first = it[i].indexOf(".");
					int second = it[i].indexOf(".", first + 1);
					int third = it[i].indexOf(".", second + 1);
					name = it[i].substring(0, first);
					oldX = Integer.parseInt(it[i].substring(first + 1, second));
					oldY = Integer.parseInt(it[i].substring(second + 1, third));
					pos = it[i].substring(third + 1);

				}
				type pl = Item.type.parseType(name);
				if (!(pl == null)) {
					items.add(new Item(pl, oldX, oldY));
					if (items.get(items.size() - 1).getType() == type.key) {
						if (oldY <= 1310) {
							items.get(items.size() - 1).unsubmerge();
						}
					}
				} else {
					items.add(null);
				}
				if (name.equals("canFull")) {
					items.get(i).change();
				}
				if (pos.equals("+")) {
					items.get(i).putInside();
				}
			}
		}

		String[] gar = in.nextLine().split(",");
		for (int i = 0; i < gar.length; i++) {
			String name = "";
			int oldX, oldY;
			oldX = oldY = 0;
			String isWat = "";
			String isGro = "";
			if (!gar[i].equals("null")) {
				int first = gar[i].indexOf(".");
				int second = gar[i].indexOf(".", first + 1);
				int third = gar[i].indexOf(".", second + 1);
				name = gar[i].substring(0, first);
				oldX = Integer.parseInt(gar[i].substring(first + 1, second));
				oldY = Integer.parseInt(gar[i].substring(second + 1, third));
				isWat = gar[i].substring(third + 1, third + 2);
				isGro = gar[i].substring(third + 2);
			}
			type pl = Item.type.parseType(name);
			if (!(pl == null)) {
				plants.add(new Plant(oldX, oldY));
				plants.get(plants.size() - 1).setPlant(pl);
			}
			if (isWat.equals("+")) {
				plants.get(plants.size() - 1).water();
			}
			if (isGro.equals("+")) {
				plants.get(plants.size() - 1).grow();
			}
		}

		String anima = in.nextLine();
		if (anima.equals("null")) {
			animals = new ArrayList<Creature>();
		} else {
			String[] ani = anima.split(",");
			add(ani, animals);
		}

		String ghos = in.nextLine();
		if (ghos.equals("null")) {
			ghosts = new ArrayList<Creature>();
		} else {
			String[] gho = ghos.split(",");
			add(gho, ghosts);
		}

		outside = in.nextBoolean();
		sleep = in.nextBoolean();
		toggleDrawer = in.nextBoolean();
		hold = in.nextBoolean();
		toggleInventory = in.nextBoolean();
		unlocked = in.nextBoolean();
		version = in.nextInt();
		v = in.nextInt();
		if (hold) {
			in.nextLine();
			String handItem = in.nextLine();
			int first = handItem.indexOf(".");
			int second = handItem.indexOf(".", first + 1);
			String name = handItem.substring(0, first);
			int oldX = Integer.parseInt(handItem.substring(first + 1, second));
			int oldY = Integer.parseInt(handItem.substring(second + 1));
			type pl = Item.type.parseType(name);
			if (!pl.equals(null)) {
				hand = new Item(pl, oldX, oldY);
				if (hand.getType() == type.key) {
					hand.unsubmerge();
				}
			} else {
				hand = null;
			}
			if (name.equals("canFull")) {
				hand.change();
			}
		}
		int k = 1;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				world[i][j] = "pixel/floor" + k + ".png";
				k++;
			}
		}
		timer.start();
		start = true;
		repaint();
	}

	//writes game data to save file
	public void save() {
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter("save.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pWriter = new PrintWriter(fWriter);
		pWriter.println(x);
		pWriter.println(y);
		pWriter.println(time);
		pWriter.println(hunger);
		pWriter.println(health);
		for (int i = 0; i < inventory.length; i++) {
			if (i == inventory.length - 1) {
				if (inventory[i] == null) {
					pWriter.print("null");
				} else {
					pWriter.print(inventory[i]);
				}
			} else {
				if (inventory[i] == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(inventory[i] + ",");
				}
			}
		}
		pWriter.println();
		for (int i = 0; i < drawer.length; i++) {
			if (i == drawer.length - 1) {
				if (drawer[i] == null) {
					pWriter.print("null");
				} else {
					pWriter.print(drawer[i]);
				}
			} else {
				if (drawer[i] == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(drawer[i] + ",");
				}
			}
		}
		pWriter.println();
		for (int i = 0; i < items.size(); i++) {
			if (i == items.size() - 1) {
				if (items.get(i) == null) {
					pWriter.print("null");
				} else {
					pWriter.print(items.get(i));
				}
			} else {
				if (items.get(i) == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(items.get(i) + ",");
				}
			}
		}
		if (items.size() == 0) {
			pWriter.print("null");
		}
		pWriter.println();
		for (int i = 0; i < plants.size(); i++) {
			if (i == plants.size() - 1) {
				if (plants.get(i) == null) {
					pWriter.print("null");
				} else {
					pWriter.print(plants.get(i));
				}
			} else {
				if (plants.get(i) == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(plants.get(i) + ",");
				}
			}
		}
		if (plants.size() == 0) {
			pWriter.print("null");
		}
		pWriter.println();

		for (int i = 0; i < animals.size(); i++) {
			if (i == animals.size() - 1) {
				if (animals.get(i) == null) {
					pWriter.print("null");
				} else {
					pWriter.print(animals.get(i));
				}
			} else {
				if (animals.get(i) == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(animals.get(i) + ",");
				}
			}
		}
		if (animals.size() == 0) {
			pWriter.print("null");
		}
		pWriter.println();

		for (int i = 0; i < ghosts.size(); i++) {
			if (i == ghosts.size() - 1) {
				if (ghosts.get(i) == null) {
					pWriter.print("null");
				} else {
					pWriter.print(ghosts.get(i));
				}
			} else {
				if (ghosts.get(i) == null) {
					pWriter.print("null,");
				} else {
					pWriter.print(ghosts.get(i) + ",");
				}
			}
		}
		if (ghosts.size() == 0) {
			pWriter.print("null");
		}
		pWriter.println();

		pWriter.println(outside);
		pWriter.println(sleep);
		pWriter.println(toggleDrawer);
		pWriter.println(hold);
		pWriter.println(toggleInventory);
		pWriter.println(unlocked);
		pWriter.println(version);
		pWriter.println(v);
		if (hold) {
			pWriter.println(hand);
		}
		pWriter.close();
	}

	//sets to default game configuration
	public void reset() {
		pause = behind = sleep = toggleDrawer = toggleInventory = unlocked = dead = false;
		outside = true;

		x = 770;
		y = 850;

		t = n = button = version = rotation1 = rotation2 = selected = time = delay = rebound = 0;

		hunger = 16;
		health = 24;

		swing = false;
		hit = false;

		world = new String[4][4];
		items = new ArrayList<Item>();
		plants = new ArrayList<Plant>(5);
		ghosts = new ArrayList<Creature>();
		animals = new ArrayList<Creature>();

		inventory = new Item[10];
		drawer = new Item[10];

		hand = null;
		hold = false;
	}

	//puts starting items in the appropriate location, adds other game elements randomly, starts game
	public void start() {
		reset();

		drawer[0] = new Item(type.sword, 0, 0);

		int k = 1;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				world[i][j] = "pixel/floor" + k + ".png";
				k++;
			}
		}

		int xItem, yItem;
		for (int i = 0; i < 10; i++) {
			xItem = 10 * r.nextInt(172) + 80;
			yItem = 10 * r.nextInt(121) + 80;
			while (xItem <= 990 && xItem >= 570) {
				xItem = 10 * r.nextInt(172) + 80;
			}
			while (yItem <= 920 && yItem >= 530) {
				yItem = 10 * r.nextInt(121) + 80;
			}
			if (i == 0) {
				items.add(new Item(type.can, xItem, yItem));
			} else if (i < 6) {
				items.add(new Item(type.seeds, xItem, yItem));
			} else if (i == 6) {
				while (yItem <= 1340 && yItem >= 1670) {
					yItem = 10 * r.nextInt(33) + 1340;
				}
				items.add(new Item(type.key, xItem, 1520));
			} else if (i == 7) {
				animals.add(new Creature(xItem, yItem, species.cow));
			} else if (i == 8) {

				animals.add(new Creature(xItem, yItem, species.pig));

			} else if (i == 9) {
				animals.add(new Creature(xItem, yItem, species.sheep));
			}

		}

		timer.start();
		start = true;
		repaint();

	}

	//draws game based on location (start screen, outside, inside, paused, etc)
	public void paint(Graphics g) {
		super.paint(g);
		if (!start) {
			drawTitle(g);
		} else if (outside) {
			drawOutside(g);
			if (pause) {
				drawPause(g);
			}
		} else {
			drawInside(g);
			if (pause) {
				drawPause(g);
			}
		}
	}

	//draw the image with specified dimensions and position
	public void drawImage(Graphics g, int row, int col, int width, int height,
			String address) {
		File file = new File(address);
		Image i;
		try {
			i = ImageIO.read(file);
			g.drawImage(i, row, col, width, height, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//returns the player's current x coordinate in pixels
	public int getXCoord() {
		if (outside) {
			if (x <= /* 2 * */DIMENSION) {
				return Math.max(-50, x - 120);
			} else if (x >= 3 * DIMENSION) {
				return x - (3 * DIMENSION) + 360;
			} else {
				return 360;
			}
		} else {
			return x;
		}
	}

	//returns the player's current y coordinate in pixels
	public int getYCoord() {
		if (outside) {
			if (y <= /* 2 * */DIMENSION) {
				return Math.max(-50, y - 120);
			} else if (y >= 3 * DIMENSION) {
				return y - (3 * DIMENSION) + 360;
			} else {
				return 360;
			}
		} else {
			return y;
		}
	}

	//gets the player's current row coordinate (which row of tiles the player is on)
	public int getRow(int index) {
		if (x <= DIMENSION) {
			return index;
		} else if (x >= (3 * DIMENSION)) {
			return 2 + index;
		} else {
			return x / DIMENSION - 1 + index;
		}
	}

	//gets the player's current column coordinate (which column of tiles the player is on)
	public int getCol(int index) {
		if (y <= DIMENSION) {
			return index;
		} else if (y >= (3 * DIMENSION)) {
			return 2 + index;
		} else {
			return y / DIMENSION - 1 + index;
		}
	}

	//returns letter's vertical offset -- for title screen
	public int getShift(int n) {
		return Math.max(DIMENSION, Math.min((3 * DIMENSION), n));
	}

	
	public boolean moveRight() {
		if (!start) {
			button++;
			button %= 4;
		} else if (pause) {
			button++;
			button %= 3;
		} else {
			if (toggleInventory || toggleDrawer) {
				selected++;
				selected %= 10;
				return true;
			}
			if (!outside) {
				if (sleep) {
					return false;
				}
				if (x < 520) {
					x += 10;
					return true;
				}
			} else if (x < 1800) {
				if (toggleInventory) {
					return false;
				}
				x += 10;
				return true;
			}
		}
		return false;
	}

	public boolean moveLeft() {
		if (!start) {
			button--;
			if (button < 0) {
				button = 3;
			}
		} else if (pause) {
			button--;
			if (button < 0) {
				button = 2;
			}
		} else {
			if (toggleInventory || toggleDrawer) {
				selected--;
				if (selected < 0) {
					selected = 9;
				}
				return true;
			}
			if (!outside) {
				if (sleep) {
					return false;
				}
				if (x > 160) {
					x -= 10;
					return true;
				}
			} else if (x > 80) {
				if (toggleInventory) {
					return false;
				}
				x -= 10;
				return true;
			}
		}
		return false;
	}

	public boolean moveUp() {
		if (!start) {
			if (button == 3) {
				button = 2;
			} else if (button == 2) {
				button = 0;
			} else {
				button = 3;
			}
		} else if (pause) {
			if (button == 0) {
				button = 2;
			} else {
				button = 0;
			}
		} else {
			if (toggleInventory || toggleDrawer) {
				selected += 5;
				selected %= 10;
				return true;
			}
			if (!outside) {
				if (sleep) {
					return false;
				}
				if (x > 430 && y == 370) {
					return false;
				}
				if (y > 260) {
					y -= 10;
					return true;
				}
			} else {
				if (toggleInventory) {
					return false;
				}
				if (x > 620 && x < 930 && y < 850 && y >= 840) {
					return false;
				}
				if (y > 80) {
					y -= 10;
					return true;
				}
			}
		}
		return false;
	}

	public boolean moveDown() {
		if (!start) {
			if (button == 3) {
				button = 0;
			} else if (button == 2) {
				button = 3;
			} else {
				button = 2;
			}
		} else if (pause) {
			if (button == 2) {
				button = 1;
			} else {
				button = 2;
			}
		} else {
			if (toggleInventory || toggleDrawer) {
				selected -= 5;
				if (selected < 0) {
					selected += 10;
				}
				return true;
			}
			if (!outside) {
				if (sleep) {
					return false;
				}
				if (x > 430 && y == 350) {
					return false;
				}
				if (y < 400) {
					y += 10;
					return true;
				}
			} else {
				if (toggleInventory) {
					return false;
				}
				if (x > 620 && x < 930 && y == 830) {
					return false;
				}
				if (y < 1690) {
					y += 10;
					return true;
				}
			}
		}
		return false;
	}

	public boolean inventory() {
		toggleInventory = !toggleInventory;
		selected = 0;
		return true;
	}

	public boolean pause() {
		pause = !pause;
		button = 0;
		return true;
	}

	public boolean storageEdit() {
		if (toggleInventory) {
			if (hold) {
				if (inventory[selected] == null) {
					hand.drop(x, y, outside);
					inventory[selected] = hand;
					hand = null;
					hold = false;
				} else {
					hand.drop(x, y, outside);
					Item temp = hand;
					hand = inventory[selected];
					hand.grab();
					inventory[selected] = temp;
					return true;
				}
			} else {
				if (inventory[selected] != null) {
					hold = true;
					hand = inventory[selected];
					hand.grab();
					inventory[selected] = null;
					return true;
				}
			}
		} else if (toggleDrawer) {
			if (hold) {
				if (drawer[selected] == null) {
					hand.drop(x, y, outside);
					drawer[selected] = hand;
					hand = null;
					hold = false;
				} else {
					hand.drop(x, y, outside);
					Item temp = hand;
					hand = drawer[selected];
					hand.grab();
					drawer[selected] = temp;
					return true;
				}
			} else {
				if (drawer[selected] != null) {
					hold = true;
					hand = drawer[selected];
					hand.grab();
					drawer[selected] = null;
					return true;
				}
			}
		}
		return false;
	}

	public boolean enter() {
		if (!start) {
			if (button == 0) {
				v--;
				if (v < 0) {
					v = 3;
				}
				return true;
			} else if (button == 1) {
				v++;
				v %= 4;
				return true;
			} else if (button == 2) {
				timer.stop();
				start();
				return true;
			} else {
				timer.stop();
				resume();
				return true;
			}
		} else if (pause) {
			if (button == 0) {
				save();
				pause = false;
				return true;
			} else if (button == 1) {
				FileWriter fWriter = null;
				try {
					fWriter = new FileWriter("save.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
				PrintWriter pWriter = new PrintWriter(fWriter);
				pWriter.println(" ");
				pWriter.close();
				start = false;
				return true;
			} else {
				save();
				start = false;
				return true;
			}
		}

		if (outside && x < 830 && x > 720 && y < 910 && y >= 840 && t > 5) {
			if (unlocked) {
				outside = false;
				x = 340;
				y = 260;
				return true;
			} else {
				if (hold && hand.getType() == type.key) {
					unlocked = true;
					outside = false;
					x = 340;
					y = 260;
					hand = null;
					hold = false;
					return true;
				} else {
					return false;
				}
			}
		} else if (outside && y > 1310 && hold) {
			if (hand.getType() == type.can) {
				hand.change();
				return true;
			} else {
				hand.submerge();
			}
		} else if (outside && hold && hand.getType() == type.sword) {
			for (int i = 0; i < animals.size(); i++) {
				Creature animal = animals.get(i);
				if (animal.touch(x, y)) {
					items.add(new Item(Item.type.parseType(animals.get(i)
							.getSpecies().drop()), animals.get(i).getX(),
							animals.get(i).getY()));
					animals.remove(i);
					hand.change();
					swing = true;
					return true;
				}
			}
		} else if (outside
				&& ((x >= 560 && x <= 1170 && y >= 360 && y <= 510)
						|| (x >= 700 && x <= 1010 && y >= 200 && y <= 350) || (x >= 860
						&& x <= 1340 && y >= 520 && y <= 670))) {
			int pX = (int) (160 * Math.round(x / 160.0));
			int pY = (int) (160 * Math.round((y + 40) / 160.0));
			boolean full = false;
			Plant plant = null;
			for (Plant p : plants) {
				if (p.getX() == pX && p.getY() == pY) {
					full = true;
					plant = p;
				}
			}
			if (full) {
				if (plant.isGrown()) {
					plant.harvest();
					items.add(new Item(plant.getPlant(), pX + r.nextInt(41)
							- 20, pY + r.nextInt(41) - 20));
					return true;
				} else if (hold && hand.getType() == type.can && hand.isAlt()) {
					plant.water();
					hand.change();
					return true;
				}
			} else {
				if (hold && hand.getType() == type.seeds) {
					plants.add(new Plant(pX, pY));
					hand = null;
					hold = false;
					return true;
				}
			}
		} else if (!outside && x < 380 && x > 280 && y < 300 && y >= 260) {
			outside = true;
			x = 770;
			y = 850;
			return true;
		} else if (!outside && x > 430 && y <= 380 && y >= 340) {
			sleep = !sleep;
			if (sleep == false) {
				time = 0;
				hunger -= 4;
				hunger = Math.max(0, hunger);
				ghosts.clear();
			}
			return true;
		} else if (!outside && x >= 180 && x <= 240 && y <= 270) {
			toggleDrawer = !toggleDrawer;
			return true;
		}

		if (hold && delay == 0) {
			for (int i = 0; i < items.size(); i++) {
				if (Math.abs(items.get(i).getX() - x) <= 20
						&& Math.abs(items.get(i).getY() - y) <= 20) {
					items.add(hand);
					hand.drop(x, y, outside);
					hand = items.get(i);
					hand.grab();
					items.remove(i);
					return true;
				}
			}

			hand.drop(x, y, outside);
			items.add(hand);
			hand = null;
			hold = false;

		} else {
			for (int i = 0; i < items.size(); i++) {
				if (Math.abs(items.get(i).getX() - x) <= 20
						&& Math.abs(items.get(i).getY() - y) <= 20) {
					hold = true;
					hand = items.remove(i);
					hand.grab();
					return true;
				}
			}
		}

		return false;
	}

	public boolean eat() {
		if (hold && hand.getType().isFood()) {
			if (hunger < 16) {
				hunger += hand.getType().calories();
				hunger = Math.min(16, hunger);
			}
			hand = null;
			hold = false;
			return true;
		}
		return false;
	}

	public void drawInside(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 740);
		drawImage(g, 200, 120, 400, 400, "pixel/room.png");

		for (Item i : items) {
			if (Math.abs(x - i.getX()) <= 400 && Math.abs(y - i.getY()) <= 400
					&& !i.isOutside()) {
				drawImage(g, i.getX() + i.getHorizAlign() - 360, i.getY() + 80,
						i.getWidth(), i.getHeight(), i.getAddress());
			}
		}

		version %= 3;
		if (sleep) {
			if (v == 0) {
				drawImage(g, 475, 335, 160, 160, "pixel/caliSleep.png");
			} else if (v == 1) {
				drawImage(g, 475, 335, 160, 160, "pixel/kiloSleep.png");
			} else if (v == 2) {
				drawImage(g, 475, 335, 160, 160, "pixel/linaSleep.png");
			} else {
				drawImage(g, 475, 335, 160, 160, "pixel/mauriSleep.png");
			}
			g.setColor(new Color(0, 0, 0, 120));
			g.fillRect(0, 0, 800, 740);
			g.setColor(new Color(0, 25, 0, 35));
			g.fillRect(458, 230, 19, 19);
			g.fillRect(483, 230, 19, 19);
			g.fillRect(483, 255, 19, 19);
			g.fillRect(458, 255, 19, 19);
		} else if (toggleDrawer || toggleInventory) {
			Color brown = new Color(40, 30, 20, 40);
			drawImage(g, getXCoord(), getYCoord(), 160, 160, character[v][0]);
			g.setColor(brown);
			g.fillRect(0, 0, 800, 740);
		} else {
			drawImage(g, getXCoord(), getYCoord(), 160, 160,
					character[v][version]);
		}

		if (behind && !sleep) {
			drawImage(g, 508, 434, 92, 43, "pixel/bed.png");
		}

		if (time < 2400) {
			g.setColor(new Color(0, 10, 20, Math.max(
					Math.min((5 * time / 100), 255), 0)));
		} else {
			g.setColor(new Color(0, 10, 20, 120 - Math.max(
					Math.min((5 * (time - 2400) / 100), 255), 0)));
		}
		g.fillRect(0, 0, 800, 800);

		if (toggleDrawer) {
			Color brown = new Color(40, 30, 20, 40);
			g.setColor(brown);
			drawImage(g, getXCoord(), getYCoord(), 160, 160, character[v][0]);
			g.fillRect(0, 0, 800, 740);
			drawImage(g, 233, 170, 400, 400, "pixel/drawer.png");
			int shiftDown, shiftRight;
			shiftDown = shiftRight = 0;
			for (int i = 0; i < drawer.length; i++) {
				if (drawer[i] != null) {
					if (drawer[i].getType() == type.tomato) {
						shiftDown = 10;
						shiftRight = 10;
					} else if (drawer[i].getType() == type.can) {
						shiftDown = 10;
						shiftRight = -5;
					} else if (drawer[i].getType() == type.seeds) {
						shiftDown = 5;
						shiftRight = 5;
					} else {
						shiftDown = shiftRight = 0;
					}
					drawImage(g, 280 + 50 * (i % 5) + shiftRight, 310 + 45
							* (i / 5) + shiftDown,
							drawer[i].getWidth() * 3 / 4,
							drawer[i].getHeight() * 3 / 4,
							drawer[i].getAddress());
				}
			}
			g.setColor(new Color(242, 205, 80, 30));
			g.fillRect(280 + 50 * (selected % 5), 310 + 45 * (selected / 5),
					40, 40);
		}

		g.setColor(Color.white);

		if (hold) {
			drawImage(g, getXCoord() + hand.getHorizAlign() - 396, getYCoord()
					+ hand.getVertAlign() - 355, hand.getWidth(),
					hand.getHeight(), hand.getAddress());
		}

		drawImage(g, 535, 527, 50, 50, "pixel/clock.png");
		g.setColor(new Color(229, 193, 45));
		g.fillRect(
				(int) (558 - 19 * Math.cos(Math.toRadians(90 + time * 3 / 40))),
				(int) (550 - 19 * Math.sin(Math.toRadians(90 + time * 3 / 40))),
				4, 4);

		g.setColor(new Color(242, 140, 213));
		g.fillRect(210, 558, 93 - 6 * (16 - hunger), 10);
		g.setColor(new Color(204, 120, 180, 240));
		g.fillRect(210, 558, 93 - 6 * (16 - hunger), 4);
		drawImage(g, 210, 557, 96, 12, "pixel/hungerbar.png");
		drawImage(g, 314, 557, 12, 13, hungericon[rotation2]);

		g.setColor(new Color(104, 244, 4));
		g.fillRect(210, 536, 93 - 4 * (24 - health), 10);
		g.setColor(new Color(95, 170, 41, 120));
		g.fillRect(210, 536, 93 - 4 * (24 - health), 4);
		drawImage(g, 210, 535, 96, 12, "pixel/healthbar.png");
		drawImage(g, 314, 535, 12, 13, healthicon[rotation1]);

		if (toggleInventory) {
			drawImage(g, 233, 170, 400, 400, "pixel/inventory.png");
			int shiftDown, shiftRight;
			shiftDown = shiftRight = 0;
			for (int i = 0; i < inventory.length; i++) {
				if (inventory[i] != null) {
					if (inventory[i].getType() == type.tomato) {
						shiftDown = 10;
						shiftRight = 10;
					} else if (inventory[i].getType() == type.can) {
						shiftDown = 10;
						shiftRight = -5;
					} else if (inventory[i].getType() == type.seeds) {
						shiftDown = 5;
						shiftRight = 5;
					} else {
						shiftDown = shiftRight = 0;
					}
					drawImage(g, 280 + 50 * (i % 5) + shiftRight, 310 + 45
							* (i / 5) + shiftDown,
							inventory[i].getWidth() * 3 / 4,
							inventory[i].getHeight() * 3 / 4,
							inventory[i].getAddress());
				}
			}
			g.setColor(new Color(242, 205, 80, 30));
			g.fillRect(280 + 50 * (selected % 5), 310 + 45 * (selected / 5),
					40, 40);
		}

	}

	public void drawOutside(Graphics g) {
		if (x > DIMENSION && x < (3 * DIMENSION) && y > DIMENSION
				&& y < (3 * DIMENSION)) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int row = Math.min(Math.max(x / DIMENSION - 1 + j, 0), 3);
					int col = Math.min(Math.max(y / DIMENSION - 1 + i, 0), 3);
					drawImage(g, DIMENSION * j - x % DIMENSION, DIMENSION * i
							- y % DIMENSION, DIMENSION, DIMENSION,
							world[col][row]);
				}
			}
		} else if ((x <= DIMENSION || x >= (3 * DIMENSION))
				&& (y <= DIMENSION || y >= (3 * DIMENSION))) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					int row = getRow(j);
					int col = getCol(i);
					drawImage(g, DIMENSION * j, DIMENSION * i, DIMENSION,
							DIMENSION, world[col][row]);
				}
			}
		} else if (x <= DIMENSION || x >= (3 * DIMENSION)) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 2; j++) {
					int row = getRow(j);
					int col = getCol(i);
					drawImage(g, DIMENSION * j, DIMENSION * i - y % DIMENSION,
							DIMENSION, DIMENSION, world[col][row]);
				}
			}
		} else {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 3; j++) {
					int row = getRow(j);
					int col = getCol(i);
					drawImage(g, DIMENSION * j - x % DIMENSION, DIMENSION * i,
							DIMENSION, DIMENSION, world[col][row]);
				}
			}
		}

		for (Plant p : plants) {
			if (Math.abs(x - p.getX()) <= 800 && Math.abs(y - p.getY()) <= 800) {
				drawImage(g, p.getX() - getShift(x) + 360, p.getY()
						- getShift(y) + 360, 80, p.getHeight(), p.getAddress());
			}
		}

		String s = "house/house";

		if (!behind) {

			if (x <= 1360 && y <= 1320) {
				for (int i = 0; i < 20; i++) {
					drawImage(g, 80 * (i % 5) + 1002 - Math.max(x, DIMENSION),
							80 * (i / 5) + 1000 - Math.max(y, DIMENSION), 80,
							80, s + (char) (65 + i) + ".png");
				}
			}
		}

		for (Item i : items) {
			if (Math.abs(x - i.getX()) <= 800 && Math.abs(y - i.getY()) <= 800
					&& i.isOutside()) {
				drawImage(g, i.getHorizAlign() + i.getX() - getShift(x),
						i.getVertAlign() + i.getY() - getShift(y),
						i.getWidth(), i.getHeight(), i.getAddress());
			}
		}

		if (y > 1310) {
			if (v == 0) {
				drawImage(g, getXCoord(), getYCoord() + version % 4 * 2, 160,
						160, "pixel/caliWet.png");
			} else if (v == 1) {
				drawImage(g, getXCoord(), getYCoord() + version % 4 * 2, 160,
						160, "pixel/kiloWet.png");
			} else if (v == 2) {
				drawImage(g, getXCoord(), getYCoord() + version % 4 * 2, 160,
						160, "pixel/linaWet.png");
			} else {
				drawImage(g, getXCoord(), getYCoord() + version % 4 * 2, 160,
						160, "pixel/mauriWet.png");
			}
		} else if (hit) {
			if (v == 0) {
				drawImage(g, getXCoord(), getYCoord(), 160, 160,
						"pixel/caliHurt.png");
			} else if (v == 1) {
				drawImage(g, getXCoord(), getYCoord(), 160, 160,
						"pixel/kiloHurt.png");
			} else if (v == 2) {
				drawImage(g, getXCoord(), getYCoord(), 160, 160,
						"pixel/linaHurt.png");
			} else {
				drawImage(g, getXCoord(), getYCoord(), 160, 160,
						"pixel/mauriHurt.png");
			}
		} else {
			version %= 3;
			drawImage(g, getXCoord(), getYCoord(), 160, 160,
					character[v][version]);
		}

		if (hold) {
			drawImage(g, getXCoord() + hand.getHorizAlign() - 396, getYCoord()
					+ hand.getVertAlign() - 355, hand.getWidth(),
					hand.getHeight(), hand.getAddress());
		}

		if (behind) {

			if (Math.abs(x - 802) < 580 && Math.abs(y - 800) < 520) {
				for (int i = 0; i < 20; i++) {
					drawImage(g, 80 * (i % 5) + 1002 - x, 80 * (i / 5) + 1000
							- y, 80, 80, s + (char) (65 + i) + ".png");
				}
			}
		}

		for (Creature a : animals) {
			if (a != null && Math.abs(x - a.getX()) <= 800
					&& Math.abs(y - a.getY()) <= 800) {
				drawImage(g, a.getX() - getShift(x) + 370, a.getY()
						- getShift(y) + 410, a.getWidth(), a.getHeight(),
						a.getImageAddress());
				g.setColor(Color.red);
			}
		}

		if (time < 2400) {
			g.setColor(new Color(0, 10, 20, Math.max(
					Math.min((10 * time / 100), 255), 0)));
		} else {
			g.setColor(new Color(0, 10, 20, 240 - Math.max(
					Math.min((10 * (time - 2400) / 100), 255), 0)));
		}
		g.fillRect(0, 0, 800, 800);

		for (Creature gh : ghosts) {
			if (Math.abs(x - gh.getX()) <= 800
					&& Math.abs(y - gh.getY()) <= 800) {
				drawImage(g, gh.getX() - getShift(x) + 370, gh.getY()
						- getShift(y) + 400, gh.getWidth(), gh.getHeight(),
						gh.getImageAddress());
				g.setColor(Color.red);
				if (gh.touch(x, y)) {
					if (!hit) {
						health -= 8;
						hit = true;
					}
					if (health <= 0) {
						dead = true;
					}
				}
			}
		}

		drawImage(g, 730, 650, 50, 50, "pixel/clock.png");
		g.setColor(new Color(229, 193, 45));
		g.fillRect(
				(int) (753 - 19 * Math.cos(Math.toRadians(90 + time * 3 / 40))),
				(int) (673 - 19 * Math.sin(Math.toRadians(90 + time * 3 / 40))),
				4, 4);

		g.fillRect(660, 650, 50, 50);
		drawImage(g, 662, 652, 46, 46, "pixel/map.png");
		g.setColor(new Color(109, 0, 145, 195));
		g.fillRect(661 + (x - 80) / 39, 652 + (y - 80) / 39, 3, 3);

		g.setColor(new Color(242, 140, 213));
		g.fillRect(20, 691, 93 - 6 * (16 - hunger), 10);
		g.setColor(new Color(204, 120, 180, 240));
		g.fillRect(20, 691, 93 - 6 * (16 - hunger), 4);
		drawImage(g, 20, 690, 96, 12, "pixel/hungerbar.png");
		drawImage(g, 124, 690, 12, 13, hungericon[rotation2]);

		g.setColor(new Color(104, 244, 4));
		g.fillRect(20, 661, 93 - 4 * (24 - health), 10);
		g.setColor(new Color(95, 170, 41, 120));
		g.fillRect(20, 661, 93 - 4 * (24 - health), 4);
		drawImage(g, 20, 660, 96, 12, "pixel/healthbar.png");
		drawImage(g, 124, 660, 12, 13, healthicon[rotation1]);

		if (toggleInventory) {
			Color brown = new Color(40, 30, 20, 40);
			g.setColor(brown);
			g.fillRect(0, 0, 800, 740);
			drawImage(g, 233, 170, 400, 400, "pixel/inventory.png");
			int shiftDown, shiftRight;
			shiftDown = shiftRight = 0;
			for (int i = 0; i < inventory.length; i++) {
				if (inventory[i] != null) {
					if (inventory[i].getType() == type.tomato) {
						shiftDown = 10;
						shiftRight = 10;
					} else if (inventory[i].getType() == type.can) {
						shiftDown = 10;
						shiftRight = -5;

					} else if (inventory[i].getType() == type.seeds) {
						shiftDown = 5;
						shiftRight = 5;
					} else {
						shiftDown = shiftRight = 0;
					}
					drawImage(g, 280 + 50 * (i % 5) + shiftRight, 310 + 45
							* (i / 5) + shiftDown,
							inventory[i].getWidth() * 3 / 4,
							inventory[i].getHeight() * 3 / 4,
							inventory[i].getAddress());
				}
			}
			g.setColor(new Color(242, 205, 80, 30));
			g.fillRect(280 + 50 * (selected % 5), 310 + 45 * (selected / 5),
					40, 40);
		}
	}

	public void drawTitle(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 740);
		drawImage(g, 200, 120, 420, 368, "pixel/title.png");
		drawImage(g, 370, 430, 80, 30, "pixel/button3.png");
		drawImage(g, 484, 191, 94, 30, "pixel/button4.png");
		g.setColor(new Color(255, 255, 255, 100));
		if (button == 0) {
			g.fillRect(230, 310, 20, 30);
		} else if (button == 1) {
			g.fillRect(570, 310, 20, 30);
		} else if (button == 2) {
			g.fillRect(370, 430, 80, 30);
		} else {
			g.fillRect(484, 191, 94, 30);
		}
		drawImage(g, 230, 310, 20, 30, "pixel/button1.png");
		drawImage(g, 570, 310, 20, 30, "pixel/button2.png");

		if (v == 0) {
			drawImage(g, 350, 250, 160, 160, character[v][0]);
		} else if (v == 1) {
			drawImage(g, 368, 250, 160, 160, character[v][0]);
		} else {
			drawImage(g, 356, 250, 160, 160, character[v][0]);
		}

		drawImage(g, 20, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50, 75,
				"pixel/s.png");
		drawImage(g, 80, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50, 75,
				"pixel/l.png");
		drawImage(g, 140, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/e.png");
		drawImage(g, 200, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/e.png");
		drawImage(g, 260, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/p.png");
		drawImage(g, 320, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/y.png");

		drawImage(g, 420, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/v.png");
		drawImage(g, 480, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/a.png");
		drawImage(g, 540, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/l.png");
		drawImage(g, 600, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/l.png");
		drawImage(g, 660, (int) (530 + 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/e.png");
		drawImage(g, 720, (int) (530 - 3 * Math.sin(Math.toRadians(n))), 50,
				75, "pixel/y.png");
	}

	public void drawPause(Graphics g) {
		drawImage(g, 190, 162, 420, 368, "pixel/title.png");
		drawImage(g, 290, 280, 101, 45, "pixel/button5.png");
		drawImage(g, 412, 280, 101, 45, "pixel/button6.png");
		drawImage(g, 290, 350, 227, 45, "pixel/button7.png");
		g.setColor(new Color(255, 255, 255, 100));
		if (button == 0) {
			g.fillRect(290, 280, 101, 45);
		} else if (button == 1) {
			g.fillRect(412, 280, 101, 45);
		} else if (button == 2) {
			g.fillRect(290, 350, 227, 45);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (start) {

			if (dead) {
				FileWriter fWriter = null;
				try {
					fWriter = new FileWriter("save.txt");
				} catch (IOException f) {
					f.printStackTrace();
				}
				PrintWriter pWriter = new PrintWriter(fWriter);
				pWriter.println(" ");
				pWriter.close();
				start = false;
			}
			if (t < 1000) {
				t++;
			}

			if (time != 0 && time % 300 == 0 && hunger > 0) {
				hunger--;
			}

			if (health < 24 && time % 200 == 0 && hunger >= 8) {
				health++;
			}

			if (hunger <= 4 && health > 0 && time % 250 == 0) {
				health--;
			}

			time++;
			time %= 4800;

			if (swing) {
				delay++;
			}
			if (delay == 3) {
				swing = false;
				delay = 0;
				if (hold && hand.getType().isSword()) {
					hand.change();
				}
			}
			if (hit) {
				rebound++;
			}
			if (rebound == 6) {
				hit = false;
				rebound = 0;
			}

			if (time == 1200) {
				ghosts.add(new Creature(species.ghost));
				ghosts.add(new Creature(species.ghost));
				ghosts.add(new Creature(species.ghost));
				ghosts.add(new Creature(species.ghost));
				ghosts.add(new Creature(species.ghost));
			} else if (time == 3600) {
				ghosts.clear();
			}

			if (outside || (!outside && time % 2 == 0)) {
				rotation1++;
				rotation2++;
				rotation1 %= 6;
				rotation2 %= 8;
			}

			repaint();
			for (Plant p : plants) {
				p.addTime();
			}
			for (Creature g : ghosts) {
				g.move();
			}

			for (Creature a : animals) {
				if (a != null) {
					a.move();
				}
			}

			behind = false;
			if (outside) {
				if (x > 610 && x < 940 && y < 840 && y > 520) {
					behind = true;
				}
			} else {
				if (x > 430 && y >= 330 && y <= 350) {
					behind = true;
				}
			}
		} else {
			n += 15;
			n %= 360;
			repaint();
		}
	}

	// calls appropriate method based on user keypress
	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (!start) {
				int keycode = e.getKeyCode();
				switch (keycode) {
				case KeyEvent.VK_LEFT:
					moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					moveRight();
					break;
				case KeyEvent.VK_DOWN:
					moveDown();
					break;
				case KeyEvent.VK_UP:
					moveUp();
					break;
				case KeyEvent.VK_SPACE:
					enter();
					break;
				}
			} else {
				int keycode = e.getKeyCode();
				switch (keycode) {
				case KeyEvent.VK_LEFT:
					version++;
					moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					version++;
					moveRight();
					break;
				case KeyEvent.VK_DOWN:
					version++;
					moveDown();
					break;
				case KeyEvent.VK_UP:
					version++;
					moveUp();
					break;
				case KeyEvent.VK_SPACE:
					enter();
					break;
				case KeyEvent.VK_E:
					if (!sleep) {
						inventory();
					}
					break;
				case KeyEvent.VK_Q:
					storageEdit();
					break;
				case KeyEvent.VK_ESCAPE:
					pause();
					break;
				case KeyEvent.VK_SHIFT:
					eat();
				}
			}
		}
	}
}