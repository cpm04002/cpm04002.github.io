/* tButton.java
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
 * Overrides Button to provide action if ENTER is pressed when the
 * tButton object has focus.
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

/** Overrides Button to provide action if ENTER is pressed when the
  * tButton object has focus.
  */

public class tButton extends Button
{
    static final int ENTER = 10;

	/** Construct a tButton object with the supplied label.
	  */
    tButton(String label)
    {
        super(label);
    }

	/* Provides action by posting an ACTION_EVENT to the parent when
	 * ENTER is pressed and the tButton object has focus.
	 */
    public boolean handleEvent(Event e)
    {
        switch(e.id){
             case Event.KEY_PRESS:
                switch(e.key){
                    case ENTER:
                        postEvent(new Event(getParent(),Event.ACTION_EVENT,getLabel()));
                        return true;
                }
        }
        return super.handleEvent(e);
    }
}
