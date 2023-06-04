package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class PlaywrightAPIExamples {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    public void initialiseContext() {
        //Below lines of code will enable tracing
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @Test
    public void uiTest() {

        page.navigate("https://www.wikipedia.org/");
        page.locator("input[name=\"search\"]").click();
        page.locator("input[name=\"search\"]").fill("playwright");
        page.locator("input[name=\"search\"]").press("Enter");
        assertEquals("https://en.wikipedia.org/wiki/Playwright", page.url());
        page.waitForTimeout(5000);
        //Close the tracing
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));
        page.close();
        browser.close();
    }


    @Test
    public void testPOSTAPIRequest() {
        Map<String, Object> data = new HashMap();
        data.put("title", "Book Title");
        data.put("body", "John Doe");
        APIRequestContext request = context.request();
        APIResponse post = request.fetch("https://example.com/api/createBook", RequestOptions.create().setMethod("post").setData(data));
    }

    @Test
    public void testGetAPIRequest() throws JsonProcessingException {
        APIRequestContext request = context.request();
        APIResponse response = request.get("https://www.bstackdemo.com/api/products", RequestOptions.create());
        assertEquals(response.status(), 200);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.text());
        assertEquals(jsonNode.get("products").get(0).get("description").asText(), "iPhone 12");
    }
}
