//Uncomment this file if you have a recent version of firefox installed
// Only tested on Windows

package controller;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import util.TestInputGenerator;

public class SeleniumTest {
	private final static Logger log = LogManager.getLogger(SeleniumTest.class);
	static private WebDriver driver;
	static private File pageAcueil;

	private static final String username = TestInputGenerator.getRandomAlphaWord(20);
	private static final String password = TestInputGenerator.getRandomPwd(20);

	@BeforeClass
	static public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		
		String workingDir = System.getProperty("user.dir");
		String sep = File.separator;
		log.debug("dir : " + workingDir);

		String page = workingDir + sep + "src" + sep + "main" + sep + "js" + sep + "html" + sep + "index.html";
		pageAcueil = new File(page);

		log.debug("pageAcueil getAbsolutePath : " + pageAcueil.getAbsolutePath());
	}

	@AfterClass
	static public void tearDown() throws Exception {
		driver.close();
		driver.quit();
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testLoginPage() {
		driver.get(pageAcueil.toURI().toString());
		
		WebElement NameInput=null;
		WebElement PasswordInput =null;
		try{
			NameInput = driver.findElement(By.id("login"));
			PasswordInput =driver.findElement(By.id("password"));
		} catch(Exception e){
			fail("login password not found");
		}
		NameInput.sendKeys(username);
		PasswordInput.sendKeys(password);
		WebElement button = null;
		try{
			button = driver.findElement(By.cssSelector("div.form-group button.btn-default"));
		}catch(Exception e){
			fail("button not found");
		}
		WebElement buttonsubscribe = null;
		try{
			buttonsubscribe = driver.findElement(By.cssSelector("div.ng-scope h1.ng-scope"));
		}catch(Exception e){
			e.printStackTrace();fail("buttonsubscribe not found");
		}
		WebElement Home = null;
		try{
			Home = driver.findElement(By.id("sidebar-wrapper" ));
		}catch(Exception e){fail("Home not found");}
	}

	@SuppressWarnings("unused")
	@Test //2
	public void testAddItemPage() {
		String pathToHome = pageAcueil.toURI()+"#myitems/add";
		driver.get(pathToHome);
		
		WebElement titreInput = null;
		WebElement descrInput = null;

		try {    
			titreInput = driver.findElement(By.id("title"));
			descrInput = driver.findElement(By.id("description"));
		} catch(Exception e) {fail("GUI Element not found");}

		titreInput.sendKeys("DELL");
		descrInput.sendKeys("pc portable");
		WebElement buttonsubmit = null;
		try{
			driver.findElement(By.cssSelector("div.form-group button.btn-default"));}
		catch(Exception e) { fail(" Buttonsubmit not found");}
		WebElement buttoncancel = null;
		try{
			buttoncancel = driver.findElement(By.cssSelector("div.form-group button.btn-warning"));
		}catch(Exception e){fail("buttoncancel not found");}
		WebElement Items = null;
		try{
			Items = driver.findElement(By.id("sidebar-wrapper"));
		}catch(Exception e){fail("Items not found");}}

	@SuppressWarnings("unused")
	@Test //3
	public void testSearchPage() {
		String pathToHome = pageAcueil.toURI()+"#search";
		driver.get(pathToHome);
		
		WebElement searchInput = null;
		try{
			searchInput = driver.findElement(By.cssSelector("div.input-group  input.form-control "));
		}catch(Exception e){fail("searchInput not found");}
		searchInput.sendKeys("ordi");
		WebElement button = null;
		try{
			button = driver.findElement(By.cssSelector("div.ng-scope button.btn-default"));
		}catch(Exception e){fail("buttonsearch not found");}
		WebElement Search = null;
		try{
			Search = driver.findElement(By.cssSelector("div.nav span.btn-lg"));
		}catch(Exception e){fail("Search  not found");}
	}


	@SuppressWarnings("unused")
	@Test //4
	public void testMessagesPage() {
		String pathToHome = pageAcueil.toURI()+"#messages";
		driver.get(pathToHome);
		
		WebElement msgInput= null;
		try{
			msgInput = driver.findElement(By.cssSelector("div.well input.form-control"));
		}catch(Exception e){fail("msgtext not found");}
		msgInput.sendKeys("Carrots have finished my friend");
		WebElement button = null;
		try{
			button = driver.findElement(By.cssSelector("div.input-group button.btn-default"));
		}catch(Exception e){fail("buttonmsg not found");}
		WebElement Messages = null;
		try{
			Messages = driver.findElement(By.cssSelector("div.nav span.btn-lg "));
		}catch(Exception e){fail("Messages not found");}
	}


	@SuppressWarnings("unused")
	@Test //5
	public void testSettingsPage() {
		String pathToHome = pageAcueil.toURI()+"#settings";
		driver.get(pathToHome);
		
		WebElement msgInput = null;
		try{
			msgInput = driver.findElement(By.cssSelector("div.ng-scope input.ng-valid"));
		}catch(Exception e){fail("button not found");}
		WebElement Settings = null;
		try{
			Settings = driver.findElement(By.id("sidebar-wrapper"));
		}catch(Exception e){fail("Settings not found");}
	}


	@SuppressWarnings("unused")
	@Test //6
	public void testAccountPage() {
		String pathToHome = pageAcueil.toURI()+"#account";
		driver.get(pathToHome);
		
		WebElement keyInput =null;
		try{
			keyInput = driver.findElement(By.id("comment"));
		}catch(Exception e){fail("button not found");}
		keyInput.sendKeys("7880000");
		WebElement Account =null;
		try{
			Account = driver.findElement(By.id("sidebar-wrapper"));
		}catch(Exception e){fail("Account not found");}
	}


	@SuppressWarnings("unused")
	@Test //7
	public void testLogoutPage() {
		String pathToHome = pageAcueil.toURI()+"#logout";
		driver.get(pathToHome);
		
		WebElement Logout =null;
		try{
			Logout = driver.findElement(By.cssSelector("div.ng-scope p.ng-scope "));
		}catch(Exception e){fail("Logout not found");}
	}


	@SuppressWarnings("unused")
	@Test //8
	public void ContractPage() {
		String pathToHome = pageAcueil.toURI()+"#contract";
		driver.get(pathToHome);
		
		WebElement Contract =null;
		try{
			Contract = driver.findElement(By.id("sidebar-wrapper"));
		}catch(Exception e){fail("Contract not found");}
	}


	@SuppressWarnings("unused")
	@Test //9
	public void AboutPage() {
		String pathToHome = pageAcueil.toURI()+"#about";
		driver.get(pathToHome);
		
		WebElement Logout =null;
		try{
			Logout = driver.findElement(By.id("sidebar-wrapper"));
		}catch(Exception e){fail("About not found");}}
}
