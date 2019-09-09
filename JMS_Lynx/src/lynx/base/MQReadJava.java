package lynx.base;


import java.io.IOException;
import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

/*
import javax.jms.JMSException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;




public class JMS {

	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1412; // Listener port for your queue manager
	private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static final String QMGR = "TEST_QMGR"; // Queue manager name
	//private static final String APP_USER = "APP"; // User name that application uses to connect to MQ
	//private static final String APP_PASSWORD = "_APP_PASSWORD_"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "local"; // Queue that the applic
	
	
	public static void main(String[] args) {
		
		
		System.out.println("=========JMS =============");
		try{
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory cf = ff.createConnectionFactory();
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
		cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
		cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
		cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
		//cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		//cf.setStringProperty(WMQConstants.USERID, APP_USER);
		//cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
		
		
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
*/

/**
 * 
 * @author SANDEEPThandu
 *
 *Purpose: Read the Messages from the QueueManager 
 *
 */


public class MQReadJava
{
    private MQQueueManager _queueManager = null;
    public int portNumber = 1414;
    public String hostName = "10.166.190.230";
    public String channelName = "TCP.CLIENTS";
    public String qMgrName = "pcsbx5";
    public String inputQName = "WEBA.INBOUND";

    public MQReadJava()
    {
        super(); 
    }

    private void init(String[] args) throws IllegalArgumentException
    {
        // Set up MQ environment

        Hashtable<String, Object> mqht = new Hashtable<String, Object>();
        mqht.put(CMQC.CHANNEL_PROPERTY, channelName);
        mqht.put(CMQC.HOST_NAME_PROPERTY, hostName);
        mqht.put(CMQC.PORT_PROPERTY, new Integer(portNumber));
       //mqht.put(CMQC.USER_ID_PROPERTY, userID);
        //mqht.put(CMQC.PASSWORD_PROPERTY, password);
        try
        {
        	_queueManager = new MQQueueManager(qMgrName, mqht);
          // System.out.println("Successfully connected to "+ qMgrName);
        }
        catch (com.ibm.mq.MQException mqex)
        {
        	System.out.println("Please make sure you are connected to client VPN first and using the appropriate QueueManager and Queue Name");
           System.out.println("MQException cc=" +mqex.completionCode + " : rc=" + mqex.reasonCode);
        }
    }

    public static void main(String[] args)throws IllegalArgumentException
    {
        MQReadJava readQ = new MQReadJava();
        try
        {
            readQ.init(args);
           // readQ.selectQMgr();
         // readQ.selectQMgr();
            readQ.read();
        }

        catch (IllegalArgumentException e)
        {
        	System.out.println("Please make sure you are connected to client VPN and using the appropriate QueueManager and Queue");
            System.exit(1);
        }
        catch (MQException e)
        {
        	
        	System.out.println("Please make sure you are connected to client VPN and using the appropriate QueueManager and Queue");
            System.out.println(e);
            System.exit(1);
        }
    }

    /*private void selectQMgr() throws MQException
    {
        _queueManager = new MQQueueManager(qMgrName);
    }*/

    private void read() throws MQException
    {
        int openOptions = CMQC.MQOO_INQUIRE + CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED;

        //int   openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_FAIL_IF_QUIESCING;

        
        
        MQQueue queue = _queueManager.accessQueue( inputQName,
        openOptions,
        null, // default q manager
        null, // no dynamic q name
        null ); // no alternate user id

        //MQQueue queue = _queueManager.accessQueue( inputQName,openOptions);
        
        System.out.println("MQRead is now connected.\n");
        int depth = queue.getCurrentDepth();
       
        /**
         * Put Messages in QUEUE
         */
        /* MQMessage mqm = new MQMessage();
        try {
			mqm.writeLong(12);
			MQPutMessageOptions pmo = new MQPutMessageOptions(); 
			queue.put(mqm,pmo);
			
		} catch (IOException e1) {
			System.out.println("Cannot Write to the Queue");
			e1.printStackTrace();
		}*/
        		
        
        System.out.println("Current depth: " + depth + "\n");

        if (depth == 0)
        {
            return;
        }

        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        getOptions.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING + 
        CMQC.MQGMO_CONVERT;

        while(true)
        {
            MQMessage message = new MQMessage();
            try
            {
            	//message.writeUTF("HI");
            	//queue.put(message);
            	queue.get(message, getOptions);
                byte[] b = new byte[message.getMessageLength()];
                message.readFully(b);
                System.out.println(new String(b));
                message.clearMessage();
                
            	}

            catch (IOException e)
            {
                System.out.println("IOException during GET: " + e.getMessage());
                break;
            }
            catch (MQException e)
            {
                if (e.completionCode == 2 && e.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) {
                    if (depth > 0)
                    {
                        System.out.println("All messages read.");
                    }
                }
                else
                {
                    System.out.println("GET Exception: " + e);
                }
                break;
            }
        }
        queue.close();
        _queueManager.disconnect();
    }
}