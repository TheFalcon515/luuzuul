import java.util.Random;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 2008.03.30
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private boolean amISitting = false;
    private String[] food  = {"apples", "bananas", "oranges", "pears", "strawberry's", "tomatoes"};

    /**
     * Create the game and initialise its internal map.
     */
    public Game(){
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms(){

        Room outside, theatre, pub, lab, office, home, bathroom, kitchen, bedroom, livingroom, diningroom;
      
        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theatre = new Room("in a lecture theatre");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        home = new Room("in your home");
        bathroom = new Room("in the bathroom");
        kitchen = new Room("in the kitchen");
        bedroom = new Room("in the bedroom");
        livingroom = new Room("in the living room");
        diningroom = new Room("in the dining room");
        
        // initialise room exits
        outside.setExit("north", home);
        outside.setExit("east", theatre);                                                                           //NORTH
        outside.setExit("south", lab);                                                                      // WEST         EAST
        outside.setExit("west", pub);                                                                              // SOUTH

        theatre.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        home.setExit("south", outside);
        home.setExit("east", bathroom);
        home.setExit("west", kitchen);
        home.setExit("north", livingroom);

        livingroom.setExit("south", home);
        livingroom.setExit("west", diningroom);
        livingroom.setExit("north", bedroom);

        bathroom.setExit("west", home);

        kitchen.setExit("east", home);

        bedroom.setExit("south", livingroom);

        diningroom.setExit("east", livingroom);


        currentRoom = outside;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play(){
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome(){
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command){
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            if (amISitting == true) {
                System.out.println("You are sitting, you can't move!");
            } else {
                goRoom(command);
            }
        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        } else if (commandWord.equals("sit")) {
            if(amISitting == false) {
                System.out.println("You are sitting now!");
                amISitting = true;
            } else {
                System.out.println("You are already sitting!");
            }
        }
        else if (commandWord.equals("stand")) {
            if(amISitting == true) {
                System.out.println("You are standing now!");
                amISitting = false;
            } else {
                System.out.println("You are already standing!");
            }
        } else if (commandWord.equals("eat")) {
                if(currentRoom.getDescription() == "in the dining room") {
                    eat();
                }else {
                    System.out.println("You can't eat here!");
                }
            }

        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Prints a message saying that the player ate food, the food is a random item from the list of food items
     */
    private void eat() {
        Random rand = new Random();
        int aux1 = rand.nextInt(food.length);
        int aux2 = (rand.nextInt(10)) +1;
        if(food[aux1] == null) {
            System.out.println("You ate everything already!");
        }else {
            System.out.println("You ate " + aux2 + " units of " + food[aux1]);
            //remove the food from the list of food items
            food[aux1] = null;
        }
    }

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     * Reminds the player of the exits available.
     */
    private void printHelp(){
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }
        String direction = command.getSecondWord();
        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
