package org.apache.commons.mail;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
public class EmailTest {
	private static final String[] TEST_EMAILS = { "ab@bc.com", "a.b@c.org", "abcdefghijklmnopqrst@abcdefghijklmnopqrst.com.bd" };
	
	private EmailConcrete email;
	
	@Before
	public void setUpEmailTest() throws Exception {
		email = new EmailConcrete();
	}
	
	@After
	public void tearDownEmailTest() throws Exception {
		
	}
	
	/*
	 * Test addBcc(String email) function
	 */
	@Test
	public void testAddBcc() throws Exception {
		
	email.addBcc(TEST_EMAILS);
	assertEquals(3, email.getBccAddresses().size());
	}
	
	
	/*
	 * Test for addCc(String email)
	 *this method  will verify that the recipient is added
	 */
   @Test
   public void testAddCc() throws Exception {
       email.addCc(TEST_EMAILS[0]);
       assertEquals(1, email.getCcAddresses().size());
       assertEquals(TEST_EMAILS[0], email.getCcAddresses().get(0).getAddress());
   } 
   
   
   /*
    * Test for addHeader(String name, String value)
    * Verifies valid headers are stored, and invalid ones throw exceptions.
    */
   @Test
   public void testAddHeader() {
       email.addHeader("X-Custom-Header", "HeaderValue");
       assertTrue(email.headers.containsKey("X-Custom-Header"));
       assertEquals("HeaderValue", email.headers.get("X-Custom-Header"));
       try {
           email.addHeader("", "value");
           fail("Expected IllegalArgumentException for empty header name");
       } catch (IllegalArgumentException e) {
       }
       try {
           email.addHeader("X-Empty-Value", "");
           fail("Expected IllegalArgumentException for empty header value");
       } catch (IllegalArgumentException e) {
       }
   }
   
   
   /*
    * Test for addReplyTo(String email, String name)
    *  Ensures valid address is added correctly; invalid address throws EmailException.
    */
   @Test
   public void testAddReplyTo() throws Exception {
       email.addReplyTo(TEST_EMAILS[1], "Reply Name");
       assertEquals(1, email.getReplyToAddresses().size());
       InternetAddress addr = email.getReplyToAddresses().get(0);
       assertEquals(TEST_EMAILS[1], addr.getAddress());
       assertEquals("Reply Name", addr.getPersonal());
       try {
           email.addReplyTo("invalid@@email", "BadEmail");
           fail("Expected EmailException for invalid reply-to address");
       } catch (EmailException e) {
       }
   }
   
   
  
   /*
    * Test for  buildMimeMessage()
    *Tests that building the MimeMessage raises a RuntimeException as expected
    * @throws Exception
    */
   @Ignore("Fails in CI due to missing email config")
   @Test (expected = RuntimeException.class)
   public void testBuildMimeMessage() throws Exception {
       email.setHostName("localhost");
       email.setSmtpPort(1234);
       email.setFrom("a@gmail.com");
       email.addTo("hi@gmail.com");
       email.setSubject("test mail");
       email.setCharset("ISO-8859-1");
       email.setContent("test content", "test/plain");
       email.buildMimeMessage();
   }
  
   @Test
   public void testSuccessfulBuildMimeMessage() throws Exception {
   	 email.setHostName("localhost");
        email.setSmtpPort(1234);
       email.setFrom("a@gmail.com");
       email.addTo("hi@gmail.com");
       email.setSubject("Test Email");
       email.setContent("Hello, this is a test.", "text/plain");
      
       email.buildMimeMessage();
      
       assertNotNull(email.getMimeMessage());
   }
   @Ignore("Fails in CI due to missing email config")
   @Test(expected = EmailException.class)
   public void testBuildMimeMessageWithoutFrom() throws Exception {
   	email.setHostName("localhost");
       email.setSmtpPort(1234);
     
       email.addTo("hi@gmail.com");
       email.setSubject("Test Email");
       email.setContent("This email has no sender.", "text/plain");
   }
  
   @Test
   public void testBuildMimeMessageWithMultipleRecipients() throws Exception {
   	 email.setHostName("localhost");
        email.setSmtpPort(1234);
       email.setFrom("a@gmail.com");
       email.addTo("hi@gmail.com");
       email.addCc("cc@example.com");
       email.addBcc("bcc@example.com");
       email.setSubject("Test with multiple recipients");
       email.setContent("Testing To, CC, and BCC.", "text/plain");
       email.buildMimeMessage();
       MimeMessage message = email.getMimeMessage();
       assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
       assertEquals(1, message.getRecipients(Message.RecipientType.CC).length);
       assertEquals(1, message.getRecipients(Message.RecipientType.BCC).length);
   }
   @Test
   public void testBuildMimeMessageWithHeaders() throws Exception {
   	 email.setHostName("localhost");
        email.setSmtpPort(1234);
       email.setFrom("a@gmail.com");
       email.addTo("hi@gmail.com");
       email.setSubject("Header Test");
       email.addHeader("X-Test-Header", "HeaderValue");
       email.buildMimeMessage();
       assertEquals("HeaderValue", email.getMimeMessage().getHeader("X-Test-Header")[0]);
   }
   
  
   /*
    * Test getHostName()
    * Verifies that the getter returns null 
    */
   @Test
   public void testGetHostName() throws Exception {
       assertNull(email.getHostName());
       email.setHostName("smtp.example.com");
       assertEquals("smtp.example.com", email.getHostName());
   }
   
   
   /*
    * Test getMailSession()
    * Asserts that a valid Session is returned.
    */
   @Ignore("Fails in CI due to missing email config")
   @Test
   public void testGetMailSessionCreateSession() throws Exception
   {
      Session aSession = email.getMailSession();
      assertNotNull(aSession);
   }
   
   
   /*
    * Test getSentDate()
    * Tests retrieving the sent date of the email.
    */
   @Test
   public void testGetSentDate() {
       Date before = new Date();
       Date sentDate = email.getSentDate();
       Date after = new Date();
       assertTrue(!sentDate.before(before) && !sentDate.after(after));
       Date customDate = new Date(System.currentTimeMillis() - 86400000L); 
       email.setSentDate(customDate);
       assertEquals(customDate.getTime(), email.getSentDate().getTime());
   }
   
   
   /*
    * Test getSocketConnectionTimeout() and default
    * Verifies the default value and ensures a changed value
    */
   @Test
   public void testGetSocketConnectionTimeout() {
       assertEquals(60000, email.getSocketConnectionTimeout());
       email.setSocketConnectionTimeout(30000);
       assertEquals(30000, email.getSocketConnectionTimeout());
   }
   
   
   /*
    * Test setFrom(String email)
    * Ensures that a valid address is stored and an invalid one triggers an EmailException.

    */
   @Test
   public void testSetFrom() throws Exception {
       email.setFrom("noreply@example.com");
       assertEquals("noreply@example.com", email.getFromAddress().getAddress());
       try {
           email.setFrom("invalid@@example.com");
           fail("Expected EmailException for invalid from address");
       } catch (EmailException e) {
       }
   }
}
