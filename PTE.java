// Page Table Entry class for OPTAlgo
// Cameron Nicholson
public class PTE{
    private boolean dirty; // indicates if a page has been written to
    private boolean referenced; // indicates if a page has been seen
    private boolean valid; // indicates if a page is currently loaded into RAM
    private int frame; // indicates frame num where 

    public PTE(){
        this.dirty = false;
        this.referenced = false;
        this.valid = false;
        this.frame = -1;
    }


    public boolean getDirty()
    {
        return this.dirty;
    }
    public boolean getValid()
    {
        return this.valid;
    }
    public boolean getReferenced(){
        return this.referenced;
    }
    public int getFrame(){
        return this.frame;
    }


    public void setDirty(boolean bool)
    {
        this.dirty = bool;
    }
    public void setReferenced(boolean bool)
    {
        this.referenced = bool;
    }
    public void setValid(boolean bool)
    {
        this.valid = bool;
    }
    public void setFrame(int frame)
    {
        this.frame = frame;
    }
}