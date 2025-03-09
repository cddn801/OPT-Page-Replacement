// Driver program for OPTAlgo
// Cameron Nicholson
import java.io.File;
public class vmsim {
  public static void main(String[] args) {

    // Checks for invalid args
    // arg.length == 5
    // arg[0] == -n
    // arg[2] == -a
    // arg[3] == opt
    if((args.length!=5) || !args[0].equals("-n") || !args[2].equals("-a") || (!args[3].equals("opt"))){
        System.out.println("USAGE: vmsim -n <numframes> -a opt <tracefile>");
        System.exit(1);
    }
    
    int frames = framesCheck(args); // check for valid args[1]
    
    File f = tracefileCheck(args); // check for valid args[4]

    OPTAlgo optAlgo = new OPTAlgo(f, frames);
    System.out.println("Starting OPT");
    optAlgo.opt();
  }


  // Checks for valid 'frames' integer in args[1]
  // valid frame number is stored to int frames
  private static int framesCheck(String[] args){
    int frames = 0;
    try {
        frames = Integer.parseInt(args[1]);
        if(frames<2){
            throw new Exception("Invalid Frame Number");
        }
        
    } catch (Exception e) {
        System.out.println("USAGE: vmsim -n <numframes> -a opt <tracefile>\nFrame Number must be a positive integer >= 2");
        System.exit(1);
    }
    return frames;
  }

  // Checks for valid filename in args[4]
  private static File tracefileCheck(String[] args){
    File f = null;
    try {
        f = new File(args[4]);
        if(!f.exists() || f.isDirectory()) { 
            throw new Exception("Incorrect Filename");
        }
    } catch (Exception e) {
        System.out.println("USAGE: vmsim -n <numframes> -a opt <tracefile>\nInvalid filename for tracefile");
        System.exit(1);
    }
    return f;
  }
}
