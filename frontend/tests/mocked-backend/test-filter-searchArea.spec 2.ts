import { test, expect } from "@playwright/test";

test.describe("Mocked backend testing for searchArea function", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
  });

  // Test case for successful search with non-empty geoJSON
  test("searchArea - successful search with valid geoJSON", async ({
    page,
  }) => {
    await page.route(
      "http://localhost:8080/searchArea?param1=validSearch",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({
            result: "success",
            geoJson:
              '{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[102.0, 0.5]},"properties":{"prop0":"value0"}}]}',
          }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area validSearch");
    await page.getByRole("button", { name: "Submit" }).click();

    const response = await page.waitForSelector('text="Search Succeded"');
    expect(await response.isVisible()).toBe(true);
  });

  // Test case for successful search but with empty geoJSON
  test("searchArea - successful search but empty geoJSON", async ({ page }) => {
    await page.route(
      "http://localhost:8080/searchArea?param1=emptyGeoJson",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({
            result: "success",
            geoJson: '{"type":"FeatureCollection","features":[]}', // Empty features array
          }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area emptyGeoJson");
    await page.getByRole("button", { name: "Submit" }).click();

    const response = await page.waitForSelector(
      'text="Error: Search successful but no non-empty geoJSON found"'
    );
    expect(await response.isVisible()).toBe(true);
  });

  // Test case for search with no parameters provided
  test("searchArea - no parameters provided", async ({ page }) => {
    await page.getByLabel("Command input").fill("search_area");
    await page.getByRole("button", { name: "Submit" }).click();

    const response = await page.waitForSelector(
      'text="Error: No search parameters provided."'
    );
    expect(await response.isVisible()).toBe(true);
  });

  // Test case for server error response
  test("searchArea - server error response", async ({ page }) => {
    await page.route(
      "http://localhost:8080/searchArea?param1=serverError",
      (route) => {
        route.fulfill({
          status: 500,
          statusText: "Internal Server Error",
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area serverError");
    await page.getByRole("button", { name: "Submit" }).click();

    const response = await page.waitForSelector(
      'text="Error: An error occurred during the search"'
    );
    expect(await response.isVisible()).toBe(true);
  });

  // Test case for failed search due to other reasons
  test("searchArea - failed search", async ({ page }) => {
    await page.route(
      "http://localhost:8080/searchArea?param1=failedSearch",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({
            result: "failure",
            details: "Some failure reason",
          }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area failedSearch");
    await page.getByRole("button", { name: "Submit" }).click();

    const response = await page.waitForSelector('text="Error: Search failed"');
    expect(await response.isVisible()).toBe(true);
  });

  {
    /********************* MOCK INTEGRATION TESTING FOR SEARCH_AREA FUNCTION **********************/
  }
  test("search and searchArea integration", async ({ page }) => {
    // Simulate failure for searchArea
    await page.route(
      "http://localhost:8080/searchArea?param1=failSearch",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({ result: "failure", details: "Failed search" }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area failSearch");
    await page.getByRole("button", { name: "Submit" }).click();

    const failureResponse = await page.waitForSelector(
      'text="Error: Search failed"'
    );
    expect(await failureResponse.isVisible()).toBe(true);

    // Simulate a successful CSV search
    await page.route(
      "http://localhost:8080/searchcsv?target=someTarget",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({
            result: "success",
            data: [["someTarget", "data"]],
          }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search someTarget");
    await page.getByRole("button", { name: "Submit" }).click();

    const searchResponse = await page.waitForSelector('text="Search Result"'); // Adjust the selector as needed
    expect(await searchResponse.isVisible()).toBe(true);

    // Simulate a successful searchArea with valid geoJSON
    await page.route(
      "http://localhost:8080/searchArea?param1=validSearch",
      (route) => {
        route.fulfill({
          status: 200,
          body: JSON.stringify({
            result: "success",
            geoJson:
              '{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[102.0, 0.5]},"properties":{"prop0":"value0"}}]}',
          }),
        });
      }
    );

    await page.getByLabel("Command input").fill("search_area validSearch");
    await page.getByRole("button", { name: "Submit" }).click();

    const successResponse = await page.waitForSelector(
      'text="Search Succeded"'
    );
    expect(await successResponse.isVisible()).toBe(true);

    // Add screenshots if necessary
    // await page.screenshot({ path: "path/to/screenshot.png", fullPage: true });
  });
});
