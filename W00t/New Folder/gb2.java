/* gb2.java
 *
 * Version 2.0
 * Copyright(C) 1996 by Bill Giel
 *
 * E-mail: rvdi@usa.nai.net
 * WWW: http://www.nai.net/~rvdi/home.htm
 *
 ***************************************************************************
 * Abstract
 * --------
 * A simple SMTP mail sender with a guest book interface.
 *
 * Version 2.0a adds preferredSize and minimumSize Methods to logoPanel class
 *
 ***************************************************************************
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 ***************************************************************************/

/* Documentation addendum from Version 1
 *
 * NOTE: TO RECEIVE GUEST BOOK ENTRIES, YOU MUST SPECIFY YOUR E-MAIL
 *       ADDRESS AS THE 'RECEIVER' PARAMETER IN THE APPLETS HTML TAG.
 *
 *       YOUR LOCAL HOST MUST SUPPORT SMTP MESSAGES ON PORT 25
 *
 *
 * ADDENDUM TO DOCUMENTATION (7 Feb 1996) - Some of you have been
 * unable to get your guestbook applets to work on your own home
 * pages, while others have been successful.
 *
 * If you have a chance to study the code of the mailMessage method
 * of  the 'send' class, you'll see that guestbook first establishes
 * a socket with the 'mailhost' at port 25, in accordance with
 * TCP/SMTP specs (see RFC 821).
 *
 * Then, guestbook waits for the 220 'service ready' message transmission
 * over the socket. After successfully receiving 220, it then obtains the
 * 'helohost' domain name from the server and transmits the HELO <domain>
 * command over the socket. If accepted, the receiver-SMTP returns a 250
 * (OK) reply.
 *
 * Following that , guestbook sends MAIL FROM: <sender>, then  RECPT TO:
 * <receiver>, expecting 250 replies after each. Note that in guestbook,
 * the sender and receiver addresses are the same.
 *
 * It then sends the DATA command, and expects 354 intermediate reply from
 * the SMTP server. All that follows DATA is the standard text message in
 * the format defined in RFC 822, with a standard 'Subject:' field, followed
 * by a blank line. What follows the blank line is the actual text (your guest's
 * optional name, optional email address and comments or suggestions.)
 *
 * After the message is transmitted, guestbook transmits a single '\r\n . \r\n',
 * signifying end of message, waits for a 250 (OK) reply, then transmits a final
 * QUIT command, and anticipates receipt of 221, indicating the server is closing
 * the SMTP channel.
 *
 * The mailMessage method then closes the socket before returning true, only if
 * all of the above were successful. mailMessage returns false on any errors, or
 * if the receiver parameter is null.
 *
 * Guestbook should work just fine on your homepage, so long as your www host
 * supports the above SMTP protocol. You may otherwise have to experiment
 * to get guestbook to work with custom settings. That could actually be
 * fun... but since guestbook works as-is on my www server, I have no need
 * to mess with it :-(
 *
 * Please let me know if you manage to get guestbook working by revising it
 * for conditions other than those it is designed for.
 *
 * I hope this additional information is helpful.
 * ----------------------------------------------------------------------------
 * smtpSend class built up from code demonstated in sendmail.java
 * by Godmar Back, University of Utah, Computer Systems Lab, 1996
 * (a simple applet that sends you mail when your page is accessed)
 * ----------------------------------------------------------------------------
 */

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

class smtpSend
{
    static final short PORT = 25;

    String 		lastline;
    DataInputStream in;
    PrintStream p;

    String mailhost, receiver,  sender;
    TextArea scroller = null;
    Socket socket = null;

    smtpSend( String mailhost, String receiver)
    {
   		this.mailhost = mailhost;
   		this.receiver = receiver;
   		this.sender   = receiver;
    }

    public void setScroller(TextArea scroller)
    {
        this.scroller = scroller;
    }

	void expect(String expected, String errorMsg)
	    throws Exception
    {
		lastline = in.readLine();

        if (!lastline.startsWith(expected))
            throw new Exception("Error: " + errorMsg + " (Expected " + expected + ")");

		while (lastline.startsWith(expected + "-"))
		    lastline = in.readLine();
    }

    private void scrollOK()
    {
        scroller.appendText("OK");
    }


    private void openSocket()
        throws Exception
    {

        display("Connecting to " + mailhost + "...");

        try{
             socket = new Socket(mailhost, PORT);
        } catch (Exception e){
            throw new Exception("Socket Error: Can't connect!");
        }
        scrollOK();
    }

    private void openInputStream()
        throws Exception
    {
        display("Opening input stream" + "...");
        try{
            in = new DataInputStream(socket.getInputStream());
        }catch (Exception e){
            throw new Exception("Connection Error: Cannot open for input.");
        }
        scrollOK();
    }

    private PrintStream openOutputStream()
        throws Exception
    {

        display("Opening output stream" + "...");
        try{
            p = new PrintStream(socket.getOutputStream());
        }catch (Exception e){
            throw new Exception("Connection Error: Cannot open for output.");
        }
        scrollOK();
        return p;
    }

    private String getHeloHost()
        throws Exception
    {
        String helohost;

        display("Getting Local Host Name" + "...");
        try{
            helohost = InetAddress.getLocalHost().toString();
        }catch (Exception e){
            throw new Exception("Network Error: Unknown Local Host.");
        }
        scrollOK();
        display("Local Host: " + helohost + "\r\n");
        return helohost;
    }

    private void display(String string)
    {
        if (null != scroller)
            scroller.appendText("\r\n" + string);

        System.out.println(string);
    }

    private void sendData(String subject, String message)
        throws Exception
    {
        try{
	    	String helohost = getHeloHost();

	    	p.print("HELO " + helohost + "\r\n");
	    	expect("250", "HELO");

	   	    int pos;
	   	    String hello = "Hello ";
	   	    if ((pos = lastline.indexOf(hello)) != -1) {
	   		    helohost = lastline.substring(pos + hello.length());
	   		    helohost = helohost.substring(0, helohost.indexOf(' '));
		    }

		    p.print("MAIL FROM: " + sender + "\r\n");
		    expect("250", "MAIL FROM:");

	    	p.print("RCPT TO: " + receiver + "\r\n");
		    expect("250", "RCPT TO:");

		    p.print("DATA\r\n");
	    	expect("354", "DATA");

		    p.print("Subject: " + subject);
			p.print(" (" + helohost + ")");

	    	p.print("\r\n\r\n");

	    	DataInputStream is = new DataInputStream(new StringBufferInputStream(message));
		    while (is.available() > 0) {
				String ln = is.readLine();
				if (ln.equals("."))
			    ln = "..";
		    	p.println(ln);
		    }

		    p.print(new Date().toGMTString());

            p.print("\r\n.\r\n");
            expect("250", "end of data");

	    	p.print("QUIT\r\n");
		    expect("221", "QUIT");

		}catch(Exception e){
		    throw e;
		}
	}

    public void mailMessage(String subject,String message)
        throws Exception
    {

	    if(null==receiver)throw new Exception("Parameter Error: No RECEIVER");

        try{
         	openSocket();
	    	openOutputStream();
	    	openInputStream();

	    	expect("220", "No greeting");

	    	display("Sending message via SMTP...");
	    	sendData(subject,message);

		} catch(Exception e){
		    throw e;
		}finally{
		    try {
	    		    if(socket != null)socket.close();
	    	} catch(Exception e){}
    	}

    	scrollOK();
    	display("Message Sent - Thank You!");
    	display("Press 'Quit' to close this window.");
    }

}

class logoPanel extends Canvas
{
    Image image   = null;
    boolean threeD;

    int iWidth, iHeight;

    logoPanel(Image image, boolean threeD)
    {
        this.image = image;
        this.threeD = threeD;
        if(null != image){
            iWidth=image.getWidth(this);
            iHeight=image.getHeight(this);
        }
        else{
            iWidth=400;
            iHeight=100;
        }

        if(threeD)
            resize(iWidth+4,iHeight+4);
        else
            resize(iWidth,iHeight);
    }

    public void paint(Graphics g)
    {
        int x,y;
        g.setColor(Color.lightGray);
        if(threeD){
            x=y=2;
            g.fill3DRect(0,0,size().width,size().height,false);
        }
        else{
            x=y=0;
            g.fillRect(0,0,size().width,size().height);
        }

        if(null != image)
            g.drawImage(image,x,y,this);
    }

    public Dimension minimumSize()
    {
        if(threeD)
            return(new Dimension(iWidth+4,iHeight+4));
        else
            return(new Dimension(iWidth,iHeight));
    }

    public Dimension preferredSize()
    {
        return this.minimumSize();
    }


}


class gbFrame extends tFrame
{
    static final String COPYRIGHT = "Guestbook II Copyright (C) 1996 by Bill Giel";

    TextField tf1,tf2;
    TextArea ta1, ta2;
    tButton sendButton;
    smtpSend smtp;

    gbFrame(String title, smtpSend smtp, Image image, boolean threeD)
    {

        super(title);

        this.smtp=smtp;

        setFont(new Font("System",Font.PLAIN,14));

        GridBagLayout gridbag=new GridBagLayout();
       	GridBagConstraints c=new GridBagConstraints();
       	setLayout(gridbag);

        if(null != image){
            c.insets=new Insets(5,5,8,5);
            c.weightx=1;c.weighty=1;
            c.fill=GridBagConstraints.NONE;
            c.gridwidth=GridBagConstraints.REMAINDER;
            logoPanel lp=new logoPanel(image, threeD);
       	    gridbag.setConstraints(lp,c);
       	    add(lp);

       	}

        c.weightx=1;c.weighty=1;
        c.insets=new Insets(3,5,0,0);
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        Label label = new Label(" Your Name (optional): ");
        gridbag.setConstraints(label,c);
       	add(label);

        c.insets=new Insets(3,0,0,5);
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.WEST;
        c.gridwidth=GridBagConstraints.REMAINDER;
        tf1=new TextField("",32);
       	gridbag.setConstraints(tf1,c);
       	add(tf1);

        c.insets=new Insets(3,5,0,0);
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        label = new Label(" Your Email (optional): ");
        gridbag.setConstraints(label,c);
       	add(label);

        c.insets=new Insets(3,0,0,5);
        c.gridwidth=GridBagConstraints.REMAINDER;
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.WEST;
        tf2=new TextField("",32);
       	gridbag.setConstraints(tf2,c);
       	add(tf2);



        c.insets=new Insets(8,5,0,5);
        c.gridwidth=GridBagConstraints.REMAINDER;
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.CENTER;
        label = new Label(" Any comments or suggestions are welcome! ");
        gridbag.setConstraints(label,c);
       	add(label);

       	c.insets=new Insets(3,5,3,5);
        c.gridwidth=GridBagConstraints.REMAINDER;;
        c.fill=GridBagConstraints.BOTH;
        ta1=new TextArea("",6,66);
       	gridbag.setConstraints(ta1,c);
       	add(ta1);

        c.insets=new Insets(3,5,3,0);
        c.gridwidth=2;
        c.fill=GridBagConstraints.HORIZONTAL;
        sendButton =new tButton("Send");
       	gridbag.setConstraints(sendButton,c);
       	add(sendButton);

        c.insets=new Insets(3,0,3,0);
        tButton b=new tButton("Clear");
       	gridbag.setConstraints(b,c);
       	add(b);

        c.insets=new Insets(3,0,3,5);
       	c.gridwidth=GridBagConstraints.REMAINDER;
        b=new tButton("Quit");
       	gridbag.setConstraints(b,c);
       	add(b);

      	c.insets=new Insets(3,5,3,5);
        c.gridwidth=GridBagConstraints.REMAINDER;;
        c.fill=GridBagConstraints.BOTH;
        ta2=new TextArea("Ready.",3,66);
       	gridbag.setConstraints(ta2,c);
       	add(ta2,false);
       	ta2.setEditable(false);

       	validate();

       	smtp.setScroller(ta2);

       	setBackground(Color.lightGray);

        System.out.println(COPYRIGHT);
    }

    private void clearText()
    {
        tf1.setText("");
        tf2.setText("");
        ta1.setText("");
        ta2.setText("Ready.");
    }




    public void show()
    {
        pack();
        //resize(preferredSize());
        sendButton.enable();
        clearText();
        Dimension screenSize = getToolkit().getScreenSize();
        move((screenSize.width - size().width)/2,
                       (screenSize.height - size().height)/2);
        super.show();
    }


    public boolean action(Event e, Object arg)
    {
        String NOT_SENT = "Message NOT sent.";
        String NO_DATA  = "Nothing to send.";

		if(arg.equals("Quit")){
			hide();
	  		return true;
        }
        else if(arg.equals("Clear")){
            clearText();
            return true;
        }
        else if (arg.equals("Send")){
            if(tf1.getText().length() + tf2.getText().length() + ta1.getText().length() == 0){
        	    ta2.appendText("\r\n" + NO_DATA);
        	    System.out.println(NO_DATA);
        	}
        	else{
        	    sendButton.disable();
	        	String message="Guest: " + tf1.getText() + "\n" +
        			           "Address: " + tf2.getText() + "\n\n" +
        	    		       ta1.getText() ;
                try{
        	        smtp.mailMessage("Guestbook Entry!",message);
                }catch (Exception exception){
                    String errMsg=exception.toString();
                    ta2.appendText("\r\n" + errMsg.substring(errMsg.indexOf(":")+2, errMsg.length()));
                    System.out.println(errMsg.substring(errMsg.indexOf(":")+2, errMsg.length()));
                    ta2.appendText("\r\n" + NOT_SENT);
                    System.out.println(NOT_SENT);
                    sendButton.enable();
                }
           	}
        }
        return false;
    }
}


public class gb2 extends Applet
{
    static final String COPYRIGHT  = "(C) 1996 by W.Giel";
    static final String IMAGE_LOAD = "Loading Images...wait.";
    static final String VERSION    = "Guest Book II v2.0a (4 July 1996)";
    static final String TITLE      = "Guest Book II";
	int width,height;

	tFrame window=null;

	smtpSend smtp = null;

	String szButton;
	String szReceiver;
	String szTitle;

	String szAppletImage;
	String szLogoImage;
	int imageCount = 0;
	imageLoader il=null;
	Image images[];
	boolean threeD;

	Color bgColor;
	Color fgColor;


	Button button;


	public void init()
	{
	    String szColor;

        szReceiver = getParameter("receiver");

		if(null==(szTitle=getParameter("title")))
			szTitle=TITLE;

		if(null==(szButton=getParameter("button")))
			szButton=TITLE;

		if(null==(szColor=getParameter("bgcolor")))
			bgColor=Color.lightGray;
	    else bgColor = parseColorString(szColor);


		if(null==(szColor=getParameter("fgcolor")))
			fgColor=Color.black;
	    else fgColor = parseColorString(szColor);

	    String param=getParameter("3d_logo");
	    if(null != param && Integer.valueOf(param).intValue() > 0)threeD=true;
	    else threeD=false;


	    add (button=new Button(szButton));
		width=size().width; height=size().height;
        button.move((width-button.size().width)/2,
						(width-button.size().width)/2);



		if(null != (szAppletImage = getParameter("applet_image")))imageCount++;
		if(null != (szLogoImage = getParameter("logo_image")))imageCount++;

		if(imageCount > 0){
		    int index=0;
    		String szImage[] = new String[imageCount];
    		if(null != szAppletImage)szImage[index++]=szAppletImage;
    		if(null != szLogoImage)szImage[index]=szLogoImage;
    		il=new imageLoader(this, szImage, imageCount, null);
    		il.start();
    		images=new Image[imageCount];
    	}



 	}


    private Color parseColorString(String colorString)
    {
        if(colorString.length()==6){
            int R = Integer.valueOf(colorString.substring(0,2),16).intValue();
            int G = Integer.valueOf(colorString.substring(2,4),16).intValue();
            int B = Integer.valueOf(colorString.substring(4,6),16).intValue();
            return new Color(R,G,B);
        }
        else return Color.lightGray;
    }


	public void paint(Graphics g)
	{
		Color color=g.getColor();

		FontMetrics fm=g.getFontMetrics();

	    if(il != null){
	        g.drawString(IMAGE_LOAD,(size().width-fm.stringWidth(IMAGE_LOAD))/2,size().height-fm.getMaxDescent()-3);
	        while(null == (images = il.retrieveImages()));
	        il.stop();
	        il=null;
	    }

	    g.setColor(bgColor);
		g.fill3DRect(0,0,size().width,size().height,true);
		g.setColor(fgColor);


	    if(null != szAppletImage && null != images)
			    g.drawImage(images[0], (size().width-images[0].getWidth(this))/2, button.size().height
				    +(size().height-images[0].getHeight(this)-button.size().height)/3,this);



        g.drawString(COPYRIGHT,(size().width-fm.stringWidth(COPYRIGHT))/2,size().height-fm.getMaxDescent()-3);
        g.setColor(color);


	}

    public boolean action(Event evt, Object arg)
    {
		if(arg.equals(szButton)){

		    if (null == smtp)
		        smtp=new smtpSend(getCodeBase().getHost(),szReceiver);
            if(null == window)
                window=new gbFrame(szTitle, smtp, (null != szLogoImage && null != images)? images[imageCount-1] : null, threeD);
            window.show();
			return true;
        }
		else return false;
    }


	///////////////////////////////////////////////////
	//Applet parameters - pretty much self-explanatory
	///////////////////////////////////////////////////
    public String[][] getParameterInfo()
    {
		String[][] info = {
            {"width","int","width of the applet, in pixels"},
            {"height","int","height of the applet, in pixels"},
            {"receiver","string","SMTP 'RCPT TO:' parameter <null>"},
            {"applet_image","string","GIF file to display on applet panel <null>"},
            {"logo_image","string","GIF file to display on message dialog <null>"},
            {"3d_logo","int","Non-zero produces an inletted logo image <0>"},
            {"bgcolor","String","RGB hex triplet for applet panel background <lightGray>"},
            {"fgcolor","String","RGB hex triplet for applet panel foreground <black>"},
            {"title","string","title for popup window <Guest Book II>"},
            {"button","string","Label to appear in applet's button <Guest Book II>"}
        };
        return info;
    }

	/////////////////////////////////////
    //Applet name, author and info lines
    /////////////////////////////////////
    public String getAppletInfo()
    {
        return (	VERSION + " - simulates a guest log\n" +
        			"by E-mailing guest data to page owner, by Bill Giel\n" +
        			"http://www.nai.net/~rvdi/home.htm  or  rvdi@usa.nai.net\n" +
        			"Copyright 1996 by William Giel.");
    }


}

