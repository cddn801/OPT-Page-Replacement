// LinkedList node class for Queue
// Cameron Nicholson
class QNode {
    Integer data;
    QNode next;
 
    public QNode(Integer data)
    {
        this.data = data;
        this.next = null;
    }

    public QNode(Integer data, Integer time){
        this.data = data;
        this.next = null;
    }

    public void setNext(QNode next){
        this.next = next;
    }

    public void setData(Integer data){
        this.data = data;
    }
}
 
// A LinkedList with a head and tail node. 
// Enqueued nodes are enqueued to the tail node reference
// Dequeued nodes are dequeued from the head node reference
class Queue {
    QNode head, tail;
 
    public Queue() { 
        this.head = null;
        this.tail = null; 
    }

    public QNode getHead(){
        return this.head;
    }

    public QNode getTail(){
        return this.tail;
    }
 
    // Enqueues a new node given int data to the tail of queue
    public void enqueue(Integer data)
    {
        // create new qnode
        QNode temp = new QNode(data);
 
        // if queue is empty, first enqueued node is the head and tail node
        if (this.head == null) {
            this.head = this.tail = temp;
            return;
        }
 
        // the qnode is enqueued to tail.next
        this.tail.next = temp;
        this.tail = temp;
    }
 
    // Dequeues a node from the head of the queue
    public void dequeue()
    {
        // Queue is already empty, return
        if (this.head == null){
            return;
        }
        
        // Move reference of head to the next node in the queue
        QNode temp = this.head.next;
        this.head = temp;
 
        // If removing head node reveals end of queue, set tail node reference to null to reset queue to empty
        if (this.head == null)
            this.tail = null;
    }
}