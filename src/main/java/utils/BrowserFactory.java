package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;
import static io.github.bonigarcia.wdm.config.DriverManagerType.EDGE;
import static io.github.bonigarcia.wdm.config.DriverManagerType.FIREFOX;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.util.Arrays.asList;

@Getter
public class BrowserFactory {
    private String NAME = "";
    private static final List<DriverManagerType> SETUP_DRIVERS = new ArrayList<>();
    private final WebDriver driver;

    public BrowserFactory(String browserName) {
        this.NAME = browserName;
        this.driver = createDriver();
    }

    public static boolean debuggerAttached() {
        var arguments = getRuntimeMXBean().getInputArguments().toString();

        /*
         * this is just a best-effort check, it's possible a debugger is attached via other means,
         * but if `jwdp` (java wire debug protocol) is in the program start arguments, then there's
         * a good chance a debugger is attached
         */
        return arguments.contains("jdwp");
    }

    private WebDriver createDriver() {
        return switch (NAME.toLowerCase(Locale.ROOT)) {
            case "firefox", "ff", "mozilla" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            default -> createChromeDriver();
        };
    }

    private static WebDriver createFirefoxDriver() {
        setup(FIREFOX);
        return new FirefoxDriver();
    }

    private static WebDriver createEdgeDriver() {
        setup(EDGE);
        var options = configureChromiumOptions(new EdgeOptions());

        // this is to disable the "unknown and possibly unsafe site" message from Edge
        // given our environments are mostly ephemeral, they are rarely known to Edge
        // and by default it treats them as suspicious, unless we disable this feature
        options.addArguments("--disable-features=msSmartScreenProtection");

        var logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.DRIVER, Level.INFO);
        logPrefs.enable(LogType.CLIENT, Level.ALL);
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        logPrefs.enable(LogType.PROFILER, Level.ALL);
        logPrefs.enable(LogType.SERVER, Level.ALL);
        options.setCapability("ms:loggingPrefs", logPrefs);

        return new EdgeDriver(options);
    }

    private static WebDriver createChromeDriver() {
        setup(EDGE);
        var options = configureChromiumOptions(new ChromeOptions());
        return new ChromeDriver(options);
    }

    private static <T extends ChromiumOptions<T>> T configureChromiumOptions(T options) {
        return options
                .addArguments(asList("--no-sandbox", "--ignore-certificate-errors", "--remote-allow-origins=*"))
                .addArguments(debuggerAttached()
                        ? asList("--window-size=1920,1080", "--start-maximized", "--auto-open-devtools-for-tabs")
                        : asList("--headless", "--window-size=1920,1080"));
    }

    private static synchronized void setup(DriverManagerType type) {
        if (!SETUP_DRIVERS.contains(type)) {
            WebDriverManager.getInstance(type).setup();
            SETUP_DRIVERS.add(type);
        }
    }
}
