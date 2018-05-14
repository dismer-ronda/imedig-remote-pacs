package es.pryades.imedig.wiewer.test;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;

import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;


/**
 * Unit test for simple App.
 */
public class Hl7SRTest extends TestCase
{
	public void setUp() 
	{
	}

	public void tearDown() 
	{
	}
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Hl7SRTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     * @throws IOException 
     */
    public static Test suite() throws IOException
    {
        return new TestSuite( Hl7SRTest.class );
    }
    
    static String createMSH( String app, String date )
    {
    	return "MSH|^~\\&|" + app + "||||" + date + "||ORU^R01|P||2.3.1\r"; 
    }
    
    static String createPID( String patientName, String patientId, String patientBirthdate )
    {
    	return "PID|||" + patientId + "||" + patientName + "^^^^||" + patientBirthdate + "|M\r"; 
    }
    
    static String createOBR( String index, String date )
    {
    	return "OBR|" + index + "||||||" + date + "|||||\r";
    }
    	
    static String createOBX( String index, String observation )
    {
    	return "OBX|" + index + "|TX|^SR Text||" + observation + "||||||F\r"; 
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    @SuppressWarnings("rawtypes")
	public void testApp() throws Exception
    {
        int port = 2575; // The port to listen on

        /*
         * Now, create a connection to that server, and send a message
         */

       // Create a message to send
       String msg2 = 
	   	   createMSH( "IMEDIG", "20130703121900" ) + 
		   createPID( "ANOM1", "2403", "19880902" ) +
		   createOBR( "1", "200602201410" ) +
		   createOBX( "1", "Una prueba de observaciones" ) +
		   createOBX( "2", "Otra prueba de observaciones" );
    	   
    	   /*"MSH|^~\\&|Karisma|PHDC|Sectra|PHDC|20060504092725+1000||ORU^R01^ORU_R01|1874|P|2.4|||AL|NE|61|ASCII|ENG\r"+
    	   "PID|1||96787^^^Karisma^PI^PHDC|96787^^^Karisma^PI|KESTRAL^TESTING^Irene^^^^L||19800101+1000|M|||17 Burgundy Street^^HEIDELBERG^VIC^3084^Australia|||||||||||||||||||N\r"+
    	   "PV1||O|OP^^^PHDC^^^^^Pennant Hills Diagnostic Clinic|||||000000Y^Kestral^Testing^^^^^AUSHICPR~zKest^Kestral^Testing^^^^^LOC001||||||||||||A\r"+
    	   "ORC|RE||2006A0000304-1^Karisma||CM|||||Root^Root user||000000Y^Kestral^Testing^^^^^AUSHICPR~zKest^Kestral^Testing^^^^^LOC001|Main||2006006181132+1000||PHDC||Root^Root user\r"+
    	   "OBR|1||2006A0000304-1^Karisma|XCHEST^CHEST^L||20060406+1000|20060504092724+1000|||||||||000000Y^Kestral^Testing^^^^^AUSHICPR~zKest^Kestral^Testing^^^^^LOC001||Main|X|2006A00003 04||20060504092724+1000||X|F||^^^20060504092724+1000^^R|||||HK&Kestral&Harry\r"+
    	   "OBX|1|TX|12245-7^SR Text^LN|1|The lungs are clear.||||||F|||20060504092724+1000\r"+
    	   "OBX|2|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|3|TX|12245-7^SR Text^LN|1|The heart and mediastinum are normal in size and shape.||||||F|||20060504092724+1000\r"+
    	   "OBX|4|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|5|TX|12245-7^SR Text^LN|1|There is spondylotic change involving the thoracic spine.||||||F|||20060504092724+1000\r"+
    	   "OBX|6|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|7|TX|12245-7^SR Text^LN|1|CONCLUSION: Lungs, heart and mediastinum radiologically normal.||||||F|||20060504092724+1000\r"+
    	   "OBX|8|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|9|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|10|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|11|TX|12245-7^SR Text^LN|1|||||||F|||20060504092724+1000\r"+
    	   "OBX|12|TX|12245-7^REPORT^LN|1|||||||F|||20060504092724+1000\r";*/

    	   //PID|||PATID1234^5^M11||JONES^WILLIAM^A^III||2/09/198819610615|M-||C|1200 N ELM - See more at: http://www.corepointhealth.com/resource-center/hl7-resources/hl7-pid-segment#sthash.cPWJ3DT4.dpuf
    	   //"OBR|1|1234|1234|20120163245|||20121118200101|||||||||||||||||F\r" + 

    	   //"OBX|1|HD|^Study Instance UID||20120163245||||||F\r" +
    	   //"OBX|1|TX|^SR Text||Prueba de informe.||||||F\r" +
		
       Parser p = new GenericParser();
       
       Message adt = p.parse(msg2);
       
       /*// The connection hub connects to listening servers
       ConnectionHub connectionHub = ConnectionHub..getInstance(ctx);

       // A connection object represents a socket attached to an HL7 server
       Connection connection = connectionHub.attach("localhost", port, new PipeParser(), MinLowerLayerProtocol.class);

       // The initiator is used to transmit unsolicited messages
       Initiator initiator = connection.getInitiator();
       Message response = initiator.sendAndReceive( adt );

       String responseString = parser.encode(response);
       System.out.println("Received response:\n" + responseString);

       connectionHub.discard(connection);
       ConnectionHub.shutdown();*/
       
       boolean useTls = false; // Should we use TLS/SSL?       
       // Remember, we created our HAPI Context above like so:
       HapiContext context = new DefaultHapiContext();

       // A connection object represents a socket attached to an HL7 server
       Connection connection = context.newClient("imedig", port, useTls);

       // The initiator is used to transmit unsolicited messages
       Initiator initiator = connection.getInitiator();
       Message response = initiator.sendAndReceive(adt);

       String responseString = p.encode(response);
       System.out.println("Received response:\n" + responseString);

       /*
       * If you want to send another message to the same destination, it's fine
       * to ask the context again for a client to attach to the same host/port.
       * The context will be smart about it and return the same (already
       * connected) client Connection instance, assuming it hasn't been closed.

       connection = context.newClient("localhost", port, useTls);
       initiator = connection.getInitiator();
       response = initiator.sendAndReceive(adt);
       
       * Close the connection when you are done with it. If you are designing a
       * system which will continuously send out messages, you may want to
       * consider not closing the connection until you have no more messages to
       * send out. This is more efficient, as most (if not all) HL7 receiving
       * applications are capable of receiving lots of messages in a row over
       * the same connection, even with a long delay between messages.
       * 
       * See
       * http://hl7api.sourceforge.net/xref/ca/uhn/hl7v2/examples/SendLotsOfMessages.html 
       * for an example of this.
       */
       connection.close();
    }
}
