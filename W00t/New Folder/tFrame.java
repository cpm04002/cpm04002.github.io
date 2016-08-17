/* tFrame.java
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
 * extends Frame to create Frame subclass that responds to TAB,
 * SHIFT-TAB, and ESC keys. (Useful for applets.)
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

/** An extended class of Frame that provides response to TAB and
  * SHIFT-TAB to change component focus. Also hides the frame in
  * response to ESC.
  */

public class tFrame extends Frame
{
    static final int ESCAPE = 27;

    componentQueue controlList;

	/** The no-argument constructor, creates the circular queue
	  * for tab stop cmponents.
	  */
    tFrame()
    {
        super();
        controlList = new componentQueue();
    }

	/** Constructor creates a tFrame object with the supplied
	  * title, creates the circular queue
	  * for tab stop cmponents.
	  */
    tFrame(String title)
    {
        super(title);
        controlList = new componentQueue();
    }

	/** Add a component to the Frame; if component is a text
	  * component, checkbox, button, choice or list, it will
	  * automatically be added to the queue of tab stops. To
	  * prevent adding such a component to the queue, use <tt>
	  * add(Component,boolean)</tt>
	  */
    public Component add(Component component)
    {
        super.add(component);
        controlList.addElement(component);
        return component;
    }

	/** Overloaded version of add, used primarily to prevent a
	  * component that would normally be a tabstop from being
	  * placed in the queue, by suppling false as the tabstop
	  * parameter.
	  */
    public Component add(Component component, boolean tabStop)
    {
        if(tabStop)
            this.add(component);
        else
            super.add(component);
        return component;
    }

	/** Removes a component from the Frame and queue.
	  */
    public synchronized void remove(Component component)
    {
        controlList.removeElement(component);
        super.remove(component);
    }

	/** Show the Frame window.
	  */
    public void show()
    {
        super.show();
        controlList.reset().requestFocus();
    }

	/** The Event handler, handles TAB, SHIFT-TAB, ESC, and
	  * ACTION_EVENT as they effect component focus. Hides
	  * the frame on close, or WINDOW_DESTROY.
	  */
    public boolean handleEvent(Event e)
    {
        switch(e.id){

            case Event.ACTION_EVENT:
                if(controlList.isElement(e.target))
                    controlList.setCurrent(e.target);
                break;

            case Event.KEY_PRESS:
                switch(e.key){
                    case '\t':
                        Component component=null;
                        for (int i=0; i<controlList.size(); i++){
                            if((e.modifiers & Event.SHIFT_MASK) ==1)
                                component = controlList.prev();
                            else
                                component = controlList.next();
                            if(component.isVisible() && component.isEnabled()){
                                component.requestFocus();
                                break;
                            }
                        }
                        return true;

                    case ESCAPE:
                        hide();
                        return true;

                    // The following will fake selection detection...
                    // After picking the TextComponent, entering
                    // some keystrokes will cause the focus queue
                    // to update itself.
                    // Not very elegant, but practical.
                    default:
                        if(e.target instanceof TextComponent
                                && e.target != controlList.getCurrent())
                            controlList.setCurrent(e.target);
                        break;
                 }
                 break;

            case Event.WINDOW_DESTROY:
                hide();
                return true;
        }
        return super.handleEvent(e);
    }
}

