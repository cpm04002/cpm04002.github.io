/* circQueue.java
 *
 * Tabbed Focus Classes
 * Copyright(C) 1996 by Bill Giel
 *
 * E-mail: rvdi@usa.nai.net
 * WWW: http://www.nai.net/~rvdi/home.htm
 *
 ***************************************************************************
 * Abstract
 * --------
 * Classes to create a circular queue doubly linked list
 *
 ***************************************************************************
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 ***************************************************************************/



/** An element of the circular queue, similar to a doubly linked list
  * element, with next and previous references, except the head links
  * forward to the tail, and the tail links back to the head, creating
  * a continuous loop of linked elements
  */

class qElement
{
    Object element;
    qElement next;
    qElement prev;

    qElement(Object element,qElement tail,qElement head)
    {
        this.element=element;
        if(null != head && null != tail){
            prev=head;
            head.next=this;
            next=tail;
        }
        else{
            next=this;
            prev=this;
        }
    }
}

/** The actual circular queue, creates and manages a doubly linked
  * list of qElement objects.
  */

public class circQueue
{
    qElement head=null;
    qElement tail=null;
    qElement current=null;
    int count=0;

    /** Adds an object to the queue.
      */

    public synchronized void addElement(Object object)
    {
        current = new qElement(object,tail,head);
        if(null == tail) tail=current;
        head=current;
        tail.prev=current;
        count++;
    }

    /** Removes objects from the queue.
	  */
    public synchronized boolean removeElement(Object obj)
    {
        for(int i=0; i< count; i++){
            if(obj.equals(current.element)){
                if(count > 1){
                    current.prev.next=current.next;
                    current.next.prev=current.prev;
                    if(tail==current)
                        tail=current.next;
                    if(head==current)
                        head=current.prev;
                    count--;
                }
                else{
                    current=head=tail=null;
                    count=0;
                }
                resetQueue();
                return true;
            }
            getNext();
        }
        return false;
    }

    /** Returns the number of elements in the queue.
      */
    public int size()
    {
        return count;
    }

	/** Resets the current queue item to the beginning (tail) and
	  * returns the first element.
	  */
    public  Object resetQueue()
    {
        current=tail;
        if(null == current) return null;
        else return current.element;
    }

    /** Returns the currently referenced element
      */
    public  Object getCurrent()
    {
        return current.element;
    }

	/** Steps through the queue, seeking the requested object
	  * and makes it the current object, if found.
	  */
    public  void setCurrent(Object obj)
    {
        for(int i=0; i< count; i++){
            if(obj.equals(current.element))break;
            getNext();
        }
    }

    /** Tests for the existence of an object within the queue, and
      * if found, returns true .
      * Otherwise, returns false.
      */

    public  boolean isElement(Object obj)
    {
        qElement cur = current;
        boolean retval = false;
        
        for(int i=0; i< count; i++){
            if(obj.equals(current.element)){
            	retval = true;
            	break;
            }
            getNext();
        }
        current=cur;
        return retval;
    }

	/** Steps forward through the list, returning the next object. Will
	  * step through the head and into the tail.
	  */
    public Object getNext()
    {
        current=current.next;
        return current.element;
    }

	/** Steps back through the list, returning the previous object. Will
	  * step back through the tail and into the head.
	  */
    public Object getPrev()
    {
        current=current.prev;
        return current.element;
    }
}
