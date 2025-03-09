# OPT Page Replacement Simulator

Implements a **single-level page table** for a **32-bit address space**, where all **pages will be 2 KB** in size. The number
of frames is a parameter to the execution of the program and chosen by the user.

USAGE: vmsim -n \<numframes\> -a opt \<tracefile\>

This program will run through the memory references of a trace file and display the action taken for each
address (hit, page fault – no eviction, page fault – evict clean, page fault – evict dirty).

## Trace Files

Trace files produced come from a valgrind tool called “lackey” that logs program
memory accesses. It prints memory data access traces that look like the following:

I 0023C790,2 # instruction fetch at 0x0023C790 of size 2</br>
I 0023C792,</br>
S BE80199C,4 # data store at 0xBE80199C of size 4</br>
I 0025242B,</br>
L BE801950,4 # data load at 0xBE801950 of size 4</br>
I 0023D476,</br>
M 0025747C,1 # data modify at 0x0025747C of size 1</br>
I 0023DC20,</br>
L 00254962,</br>
L BE801FB3,</br>
I 00252305,</br>
L 00254AEB,</br>
S 00257998,</br>

Every instruction executed has an "I" (instruction) event representing its fetch from RAM (a memory
access). When executed, some instructions do additional memory accesses and are followed by one or
more "L” (load), "S” (store) or "M” (modify – a load followed by a store at the same address) events
indented by one space. Some instructions do more than one load or store, as in the last two examples in
the above trace.

Modify is for an instruction like INC (increment) that both loads and stores in the same step. These 
instructions are counted as both a load and a store of the specified address.

Some lines are not in this format as they are non-important other output from the valgrind program/tool. 
These lines are ignored for the purposes of this algorithm.

For simplicity, memory accesses are treated as falling within a single page, essentially ignoring the
size and assuming every access is at the specified address and of size 1.


### Instructor's Note

Implementing OPT in a naïve fashion will lead to unacceptable performance. It should not take more
than 5 minutes to run your program.
