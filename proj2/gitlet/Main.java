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
                        Utils.message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.log();
                }
                case "checkout" -> {
                    switch (args.length) {
                        case 2 -> {
                            //TODO: checkout [branch]
                        }
                        case 3 -> {
                            if (!args[1].equals("--")) {
                                Utils.message("Incorrect operands.");
                                System.exit(0);
                            }
                            Repository.checkoutWithoutBranch(args[2]);
                        }
                        case 4 -> {
                            if (!args[2].equals("--")) {
                                Utils.message("Incorrect operands.");
                                System.exit(0);
                            }
                            Repository.checkoutWithoutBranch(args[3], args[1]);
                        }
                        default -> {
                            Utils.message("incorrect operands.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }
}
