
 /**
  * This template file was generated by dynaTrace client.
  * The dynaTrace community portal can be found here: http://community.dynatrace.com/
  * For information how to publish a plugin please visit http://community.dynatrace.com/plugins/contribute/
  **/ 

package com.dynatrace.sshClientPlugin;

import com.dynatrace.diagnostics.pdk.*;
import java.util.logging.Logger;
import com.jcraft.jsch.*;
import java.io.*;
import java.util.Collection;

public class SSHClientPlugin implements Action {

	/**
	 * This plugin uses SSH to log into a defined remote machine and executes the command specified.  
	 * A common  use case for this plugin is to run a script on a remote machine when an incident fires.
	 *
	 **/

	private static final Logger log = Logger.getLogger(SSHClientPlugin.class.getName());
	private static final String CONFIG_USERNAME = "username";
	private static final String CONFIG_PASSWORD = "password";
	private static final String CONFIG_HOST     = "host";
	private static final String CONFIG_COMMAND  = "command";

	private String username;
	private String password;
	private String host;
	private String command;

	public Status setup(ActionEnvironment env) throws Exception {
		return new Status(Status.StatusCode.Success);
	}

	public Status execute(ActionEnvironment env) throws Exception {
		getArgs(env);
		sendCommand();
		return new Status(Status.StatusCode.Success);
	}

	/**
	 */	@Override
	public void teardown(ActionEnvironment env) throws Exception {
		// Nothing to do at this point. 
	}


	private void sendCommand() {
   	   try{
      	      	JSch jsch=new JSch();
      		Session session=jsch.getSession(username, host, 22);
      		session.setConfig("StrictHostKeyChecking", "no");
      		session.setPassword(password);
      		session.connect(10000);
      
      		Channel channel=session.openChannel("exec");
      		((ChannelExec)channel).setCommand(command);

      		channel.setInputStream(null);

      		((ChannelExec)channel).setErrStream(System.err);

      		InputStream in=channel.getInputStream();
      		channel.connect();

      		byte[] tmp=new byte[1024];
      		while(true){
       	 		while(in.available()>0){
          			int i=in.read(tmp, 0, 1024);
          			if(i<0)break;
          			log.info(new String(tmp, 0, i));
        		}
        		if(channel.isClosed()){
          		log.info("exit-status: "+channel.getExitStatus());
          		break;
        	}
        	try{Thread.sleep(1000);}catch(Exception ee){}
      	}
      	channel.disconnect();
      	session.disconnect();
    	} catch (Exception e){
      		log.info(e.toString());
    	}
  }

  private void getArgs(ActionEnvironment env) {
	  username = env.getConfigString(CONFIG_USERNAME);
	  password = env.getConfigPassword(CONFIG_PASSWORD);
	  host = env.getConfigString(CONFIG_HOST);
	  command = env.getConfigString(CONFIG_COMMAND);

  }
	
}

