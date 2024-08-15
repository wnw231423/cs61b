package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author wnw231423
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        if (firstArg.equals("init")) {
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                Repository.init();
        } else {
            Repository.checkInit();
            switch (firstArg) {
                case "add" -> {
                    if (args.length != 2) {
                        Utils.message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.add(args[1]);
                }
                case "commit" -> {
                    if (args.length != 2) {
                        Utils.message("Please enter a commit message.");
                        System.exit(0);
                    }
                    Repository.commit(args[1]);
                }
                case "log" -> {
                    if (args.length != 1) {
                        Utils.message("Incorrect operands");
                        System.exit(0);
                    }
                    Repository.log();
                }
            }
        }
    }
}
