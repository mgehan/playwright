import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SampleTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    BrowserContext context1;
    BrowserContext context2;
    Page page1;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(100));

    }


    @BeforeMethod
    public void init(){
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920,1080).setRecordVideoDir(Paths.get("kaprukaVideo")));
        page = context.newPage();
        context .tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));

        context1 = browser.newContext();
        page1 = context1.newPage();
        context1 .tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
    }


    @Test
    public void test1(){
        SoftAssert softAssert = new SoftAssert();
        page.navigate("https://www.kapruka.com/");
        //for navigation we need to wait till page load - use wait for
        //Text Selector
        page.locator("text=Your Account").waitFor();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("HomePage.png")));
        System.out.println(page.title());
        softAssert.assertEquals("Kapruka.com | Sri Lanka Online Shopping Site | Send Gifts to Sri Lanka",page.title(),"invalid page title");
        //click will automatically auto wait
        //Another way for text selector
        page.click("'Your Account'");
        //fill also have auto wait
        page.locator("#exampleInputEmail1").fill("mgehan@gmail.com");
        page.locator("//input[@name='password']").fill("@Jaye123");
        page.click("//input[@name='Login']");
        page.waitForLoadState(LoadState.NETWORKIDLE); //wait till the 2nd page loads
        softAssert.assertTrue(page.locator("text=Gehan Mallikarachchi").isVisible(),"Not visible the user name");
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("UserPage.png")));
        page.waitForSelector("text=Place a new order").click();
        // Another way Text selector - to capture from text from parent tag
        page.waitForSelector("h2 span:has-text('Products')");
        String pageHeading = page.textContent("h2 span:has-text('Products')");
        softAssert.assertEquals(pageHeading,"Products","Mismatch heading");
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("FeaturedProducts.png")));


        //context (same browser multiple windows - what to login with different users or different URL)
        page1.navigate("https://tst1-dashboard.cake.net/");
        page1.locator("#username").waitFor();
        //sendkeys anotherway
        page1.fill("#username","opsadmin@trycake.com");
        page1.fill("#password","Qq12345678");
        page1.click("//button[text()='Sign In']");
        page1.waitForLoadState(LoadState.NETWORKIDLE);

        page1.click("text=Select Restaurant");
        page1.locator("//input[@placeholder='Search']").fill("Tst1_gehan");
        page1.click("#opsadmin");
        page1.locator("#opsadmin").waitFor();
        softAssert.assertAll();
    }

    
// Execute only once to generate the Json file
    @Test
    public void test2 (){
        SoftAssert softAssert = new SoftAssert();
        page.navigate("https://www.kapruka.com/");
        //for navigation we need to wait till page load - use wait for
        //Text Selector
        page.locator("text=Your Account").waitFor();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("HomePage.png")));
        System.out.println(page.title());
        softAssert.assertEquals("Kapruka.com | Sri Lanka Online Shopping Site | Send Gifts to Sri Lanka",page.title(),"invalid page title");
        //click will automatically auto wait
        //Another way for text selector
        page.click("'Your Account'");
        //fill also have auto wait
        page.locator("#exampleInputEmail1").fill("mgehan@gmail.com");
        page.locator("//input[@name='password']").fill("@Jaye123");
        page.click("//input[@name='Login']");

        //---------------------generate the json file and store in the set path-----------

        context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("kapruka-Login.json")));
        // refresh the project to see the Json file
        softAssert.assertAll();
    }


    @Test
    public void test3 () {
        SoftAssert softAssert = new SoftAssert();
        context2 = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(Paths.get("kapruka-Login.json")));
        
        Page page2 = context2.newPage();
        page2.navigate("https://www.kapruka.com/");

        softAssert.assertAll();
    }


    // mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="codegen www.kapruka.com"
    @Test
    public void testRecord(){


        // Go to https://www.kapruka.com/
        page.navigate("https://www.kapruka.com/");
        // Click text=Your Account
        page.locator("text=Your Account").click();
        assertThat(page).hasURL("https://www.kapruka.com/shops/customerAccounts/accountLogin.jsp");
        // Click [placeholder="Enter email"]
        page.locator("[placeholder=\"Enter email\"]").click();
        // Fill [placeholder="Enter email"]
        page.locator("[placeholder=\"Enter email\"]").fill("mgehan@gmail.com");
        // Click [placeholder="Password"]
        page.locator("[placeholder=\"Password\"]").click();
        // Fill [placeholder="Password"]
        page.locator("[placeholder=\"Password\"]").fill("@Jaye123");
        // Click input:has-text("Login")
        page.locator("input:has-text(\"Login\")").click();
        assertThat(page).hasURL("https://www.kapruka.com/shops/customerAccounts/accountView.jsp");
        // Click text=Place a new order
        page.locator("text=Place a new order").click();
        assertThat(page).hasURL("https://www.kapruka.com/");
    }

    @AfterMethod
    public void endMethod(){
        //mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace tracing.zip"
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("tracing.zip")));
        context1.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("tracing1.zip")));

    }


    @AfterTest
    public void tearDown(){
       page.close();
       page1.close();
    }
}
