import { test, expect } from "@playwright/test";

// basic testing for a basic query
test("correct response for a basic filter query", async ({ page }) => {
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: "10-25 9:00PM",
      featuresInBox: [
        ["Pawtucket & Central Falls", "RI"],
        ["Providence", "RI"],
      ],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72 40 42");
  await page.getByRole("button", { name: "Submit" }).click();
  });

  const timestampText = await page.locator("text=10-25 9:00PM");
  await expect(timestampText).toBeVisible();
  const cityText1 = await page.locator("text=Pawtucket & Central Falls");
  await expect(cityText1).toBeVisible();
  const cityText2 = await page.locator("text=Providence");
  await expect(cityText2).toBeVisible();
});

// edge testing for a query missing bounds
test("correct response for an edge testing for a query missing bounds", async ({
  page,
}) => {
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: [],
      retrieval_time: [],
      featuresInBox: [],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72");
  await page.getByRole("button", { name: "Submit" }).click();

  const errorText = await page.locator(
    "text=Please input a complete bounding box."
  );
  await expect(errorText).toBeVisible();
});

// edge testing for a query unable to be fetched
test("correct response for an eedge testing for a query unable to be fetched", async ({
  page,
}) => {
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "failure",
      retrieval_time: [],
      featuresInBox: [],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72 40 42");
  await page.getByRole("button", { name: "Submit" }).click();

  const errorText = await page.locator(
    "text=The server was unable to obtain a response."
  );
  await expect(errorText).toBeVisible();
});

// edge testing for a query with no features
test("correct response for query with no features", async ({ page }) => {
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: [],
      featuresInBox: [],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72 40 42");
  await page.getByRole("button", { name: "Submit" }).click();


  const errorText = await page.locator("text=No data fits the given bounds.");
  await expect(errorText).toBeVisible();
});

// edge testing for a query with out of order bounds
test("correct response a query with out of order bounds", async ({ page }) => {
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: [],
      featuresInBox: [],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 72 70 42 40");
  await page.getByRole("button", { name: "Submit" }).click();


  const errorText = await page.locator("text=No data fits the given bounds.");
  await expect(errorText).toBeVisible();
});

{
  /********************* MOCK INTEGRATION TESTING FOR FILTER FUNCTION **********************/
}

test("correct response for broadband query then filter query", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  ////////////////////BROADBAND
  await page.route(
    "http://localhost:8080/broadband?state=florida&county=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: {},
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband florida");
  await page.getByRole("button", { name: "Submit" }).click();

  const response = await page.waitForSelector(
    'text="Please input a state and a county."'
  );
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);

  /////////////////////FILTER

  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: "10-25 9:00PM",
      featuresInBox: [
        ["Pawtucket & Central Falls", "RI"],
        ["Providence", "RI"],
      ],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72 40 42");
  await page.getByRole("button", { name: "Submit" }).click();

  const timestampText = await page.locator("text=10-25 9:00PM");
  await expect(timestampText).toBeVisible();
  const cityText1 = await page.locator("text=Pawtucket & Central Falls");
  await expect(cityText1).toBeVisible();
  const cityText2 = await page.locator("text=Providence");
  await expect(cityText2).toBeVisible();
});

// two filter queries
test("correct response for 2 filter queries in a row", async ({ page }) => {
  await page.goto("http://localhost:5173/");
  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: [],
      featuresInBox: [],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.goto("http://localhost:5173/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 72 70 42 40");
  await page.getByRole("button", { name: "Submit" }).click();

  const errorText = await page.locator("text=No data fits the given bounds.");
  await expect(errorText).toBeVisible();
  /////////////////////FILTER

  await page.route("**/filter*", (route) => {
    const mockResponse = {
      result: "success",
      retrieval_time: "10-25 9:00PM",
      featuresInBox: [
        ["Pawtucket & Central Falls", "RI"],
        ["Providence", "RI"],
      ],
    };
    route.fulfill({
      status: 200,
      body: JSON.stringify(mockResponse),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 70 72 40 42");
  await page.getByRole("button", { name: "Submit" }).click();

  const timestampText = await page.locator("text=10-25 9:00PM");
  await expect(timestampText).toBeVisible();
  const cityText1 = await page.locator("text=Pawtucket & Central Falls");
  await expect(cityText1).toBeVisible();
  const cityText2 = await page.locator("text=Providence");
  await expect(cityText2).toBeVisible();
});
