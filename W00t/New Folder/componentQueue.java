/* componentQueue.java
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
 * A wrapper for component objects, being placed in or pulled out of
 * the circular queue.
 *
 ***************************************************************************
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 ***************************************************************************/

import java.awt.*;

/** A Wrapper class to manage class conversion between the Dialog or Frame
  * components and the circular queue's generic objects.
  */
public class componentQueue extends circQueue
{
	/** Adds components to the queue if they are TextComponent,
	  * Checkbox, Button, Choice or List instances.
	  */
    public synchronized void addElement(Component component)
    {
         if(component instanceof TextComponent
                || component instanceof Checkbox
                || component instanceof Button
                || component instanceof Choice
                || component instanceof List)
            super.addElement(component);
    }

	/** Resets the queue to the beginning, and returns the first
	  * Component element in the queue.
	  */
    public Component reset()
    {
        return (Component)resetQueue();
    }

	/** Get the next Component in the queue.
	  */
    public Component next()
    {
        return (Component)getNext();
    }

	/** Get the previous Component in the queue.
	  */
    public Component prev()
    {
        return (Component)super.getPrev();
    }
}


