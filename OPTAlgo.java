// OPT algorithm
// Cameron Nicholson
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.HashMap;
public class OPTAlgo{

    private File f;

    // statistics vars for "summary.txt"
    private int frames; // user dependant
    private int accesses = 0; // num of memory accesses
    private int faults = 0; // num of page faults
    private int writes = 0; // num of evictions
    private int VPages = (int)Math.pow(2,21); // GIVEN

    public OPTAlgo(File f,int frames){
        this.f = f;
        this.frames = frames;
    }

    // Static char Hex to String Binary lookup
    // needed because offset % 4 != 0, which means
    // pagenum binary value ends somewhere between a single hex value.
    private static String[] staticLookup = new String[]
    {"0000","0001","0010","0011","0100","0101","0110","0111",
     "1000","1001","1010","1011","1100","1101","1110","1111"};

    public static String HexToBinary(char Hex) {
        return staticLookup[Integer.parseInt(Character.toString(Hex), 16)];
    }

    // Converts valid tracefile line into pagenum
    public static int VAtoPageNum(String line){
        StringBuilder currVA = new StringBuilder();
        for(int i=3;i<9;i++){
            if(i==8){
                currVA.append(HexToBinary(line.charAt(i)).substring(0,1));
                break;
            }
            currVA.append(HexToBinary(line.charAt(i)));
        }
        //System.out.println("Page Num binary from stringbuilder: "+currVA.toString());
        //System.out.println("Page Num: "+ Integer.parseInt(currVA.toString(),2));

        return Integer.parseInt(currVA.toString(),2);
    }

    // creates single-level table to store PTEs
    public PTE[] createPageTable(){
        int VPages = (int)Math.pow(2,21);
        PTE[] pageTable = new PTE[VPages]; // 2^21 pages

        for(int i=0; i<VPages; i++){
            PTE temp = new PTE();
            pageTable[i] = temp;
        }

        return pageTable;
    }
    
    // creates 'physical memory' to store page frames
    public int[] createRAMTable(){
        int[] ram = new int[frames]; // user specified

        // -1 indicates nothing loaded into frame
        for(int i =0; i<ram.length;i++){
            ram[i] = -1;
        }

        return ram;
    }

    // creates hashtable for OPT to get O(1) lookup for future memory accesses to specific pages using the trace file
    public HashMap<Integer,Queue> createOPTHashmap(){
        // create a hashmap for O(1) lookup on next occurance of a page in tracefile
        HashMap<Integer, Queue> optHashMap = new HashMap<Integer, Queue>();
        for(int i=0; i<VPages; i++){
            Queue q = new Queue();
            optHashMap.put(i,q);
        }

        // try opening scanner to read from file
        Scanner reader = null;
        try {
            reader = new Scanner(f);
        } catch (Exception e) {
            System.out.println("File Read Exception");
            System.exit(1);
        }

        int traceLineNumber = 0;
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            if(line.matches("((I\\s{2})|(\\s(S|L|M)\\s))[0-9a-fA-F]{8}\\,[0-9]")){
                traceLineNumber++;
                int pagenum = VAtoPageNum(line);

                optHashMap.get(pagenum).enqueue(traceLineNumber);
            }
        }
        reader.close();
        return optHashMap;
    }

    // Opt - Simulate what the optimal page replacement algorithm would choose if it had perfect knowledge
    //
    // Hashmap for record of all future occurances of a page in trace file
    // Hashmap<Integer,Queue) optHashMap
    //      - Integer -- page num of address in trace
    //      - Queue -- each location of page num is enqueued to this queue
    //
    //               - if address at page 3 is seen at location 7 in tracefile,
    //                 7 is enqueued to page3's queue in the hashmap..
    //
    //               - Doing this repeatedly for every address in the trace yields
    //                 a mapping of every occurance of every page in the tracefile, and
    //                 because the occurances are enqueued as they are initially seen in the trace,
    //                 all queues containing occurances are also ordered from next occurrance (head in queue)
    //                 to last occurance (tail in queue) correctly.
    //
    //               - this gives O(1) lookup for the next occurance since the reference to the head node in
    //                 each ordered queue gives us the next soonest occurance of a page in O(1) time.
    //
    //               - this implementation is only possible as a page replacement algorithm because
    //                 having the trace file here allows us to see future memory accesses before they happen so we
    //                 can plan accordingly. This is not possible in a real world scenario.
    //                
    public void opt(){
        System.out.println("\nOPT begins...\n");

        // Initialize Virtual and Physical Memory
        PTE[] pageTable = createPageTable();
        int[] ram = createRAMTable();
        HashMap<Integer,Queue> optHashMap = createOPTHashmap();

        // try opening scanner to read from file
        Scanner reader = null;
        try {
            reader = new Scanner(f);
        } catch (Exception e) {
            System.out.println("File Read Exception");
            System.exit(1);
        }
        
        // main OPT loop
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            if(line.matches("((I\\s{2})|(\\s(S|L|M)\\s))[0-9a-fA-F]{8}\\,[0-9]")){ // valid line found in trace file
                String lineType;
                switch (line.substring(0,3)) {
                    case "I  ":
                      lineType = "I";
                      break;
                    case " L ":
                      lineType = "L";
                      break;
                    case " S ":
                      lineType = "S";
                      break;
                    case " M ":
                      lineType = "M";
                      break;
                    default:
                    lineType = "";
                }

                accesses++;
                if(lineType.equals("M")){ // 'M' indicates a 'Load' followed by a 'Store', meaning 'M' must be counted as 2 total mem accesses
                    accesses++;
                }

                int pagenum = VAtoPageNum(line);
                optHashMap.get(pagenum).dequeue(); // this page has been seen at current tracefileline. dequeue it's tracefile occurance from the hashmap                 

                // check to see if RAM is full
                // if not full put requested page into RAM
                // if full, eviction needed to create space for current page
                boolean evictionNeeded = false;
                for(int i = 0;i<ram.length;i++){
                    if(ram[i]==-1){ // found empty frame. placing current page in empty RAM frame
                        
                        if(lineType.equals("S")||lineType.equals("M")){
                            pageTable[pagenum].setDirty(true);
                        }
                        pageTable[pagenum].setValid(true);
                        ram[i] = pagenum;
                        break;
                    }
                    if(ram[i]==pagenum){ // page HIT. Set to dirty if instruction writes data, otherwise continue
                        if(lineType.equals("S")||lineType.equals("M")){
                            pageTable[pagenum].setDirty(true);
                        }
                        break;
                    }
                    if(i==ram.length-1){ // Page FAULT. reached end of RAM table. No empty frames found, eviction is now required. 
                        evictionNeeded = true;
                        faults++;
                    }
                }

                if(evictionNeeded==true){
                    // search for page not needed until furthest in future 
                    int furthestMemAccess = 0;
                    int furthestFrame = 0;
                    int furthestPage = 0;
                    for(int j = 0; j<ram.length;j++){

                        // EVICT for NULL
                        if(optHashMap.get(ram[j]).getHead()==null){ // null edge case: current page is never seen in the future, most optimal to evict.
                            if(pageTable[ram[j]].getDirty()==true){ // page to be evicted is dirty, write to disk.
                                writes++;
                            }
                            pageTable[ram[j]].setValid(false);
                            ram[j]=pagenum;
                            pageTable[ram[j]].setValid(true);
                            break;
                        }

                        int temp = optHashMap.get(ram[j]).getHead().data;
                        if(temp>furthestMemAccess){
                            furthestMemAccess = temp;
                            furthestFrame = j;
                            furthestPage = ram[j];
                        }
                        
                        // EVICT for FURTHEST
                        if(j==ram.length-1){ // null edge case not found. evict furthest page
                            if(pageTable[furthestPage].getDirty()==true){ // page to be evicted is dirty, write to disk.
                                writes++;
                            }
                            pageTable[furthestPage].setValid(false);
                            ram[furthestFrame]=pagenum;
                            pageTable[ram[j]].setValid(true);
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("OPT COMPLETE\n");
        System.out.println("\t<-- statistics summary outputted to \"summary.txt\" -->");
        reader.close();
        summaryOutput("OPT");
    }

    // Writes statistics out to file 'summary.txt'
    private void summaryOutput(String algo){
        try {
            Writer writer = new FileWriter("summary.txt");

            writer.write("Algorithm: "+algo+"\n");
            writer.write("Number of frames:\t"+frames+"\n");
            writer.write("Total memory accesses:\t"+accesses+"\n");
            writer.write("Total page faults:\t"+faults+"\n");
            writer.write("Total writes to disk:\t"+writes+"\n");
            
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("File Exception");
            System.exit(1);
        }
    }
}