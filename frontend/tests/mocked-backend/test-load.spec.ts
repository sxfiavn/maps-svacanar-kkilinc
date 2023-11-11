import { test, expect } from "@playwright/test";
import {
  exampleCSVWithoutHeaders,
  exampleCSVWithHeaders,
} from "../../src/mockedJson";


test.beforeEach(async ({ page }) => {
  await page.goto(" http://localhost:5173/");
});

{/********************* MOCK UNIT TESTING FOR LOAD FUNCTION **********************/}

// basic testing of loading a file that exists
test("correct response for a file that exists", async ({ page }) => {
  // testing-image : load-basic-1
  await page.route("http://localhost:8080/loadcsv?filepath=data/ri_income.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({result: "success"})
      });
    });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/ri_income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  const response = await page.waitForSelector('text="success"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);

  await page.screenshot({path: "tests/mocked-backend/testing-images/load-basic-1.png", fullPage: true,});
});

// basic testing of loading a file that does not exist
test("correct response for a file that does not exist", async ({ page }) => {
  // testing-image : load-basic-2
  await page.route(
    "http://localhost:8080/loadcsv?filepath=blah.blah.blah",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "This is not a valid file path!" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file blah.blah.blah");
  await page.getByRole("button", { name: "Submit" }).click();
  const response = await page.waitForSelector('text="This is not a valid file path!"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);

  await page.screenshot({path: "tests/mocked-backend/testing-images/load-basic-2.png",fullPage: true,});
});


// edge testing of calling the command with no specified file path
test("correct response for load_file command without a specified path", async ({ page }) => {
  // testing-image : load-edge-1
  await page.route("http://localhost:8080/loadcsv?filepath=", (route) => {
    route.fulfill({
      body: JSON.stringify({ result: "error_bad_request" }),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.screenshot({path: "tests/mocked-backend/testing-images/load-edge-1.png",fullPage: true,});

  const response = await page.waitForSelector('text="Please enter a filepath!"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);
});

// edge testing of loading in an empty file
test("correct response for loading an empty file", async ({ page }) => {
  // testing-image : load-edge-2
  await page.route("http://localhost:8080/loadcsv?filepath=", (route) => {
    route.fulfill({
      body: JSON.stringify({ result: "success" }),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.screenshot({path: "tests/mocked-backend/testing-images/load-edge-2.png",fullPage: true,});

  const response = await page.waitForSelector('text="success"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);
});


{/********************* MOCK INTEGRATION TESTING FOR LOAD FUNCTION **********************/}

// testing the loading of a file -> view -> load new -> view again
test("file path updates after second load", async ({ page }) => {
  // testing-image : load-integration-1

  /////LOAD FILE 1
  await page.route(
    "http://localhost:8080/loadcsv?filepath=noHeadersCSV",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file noHeadersCSV");
  await page.getByRole("button", { name: "Submit" }).click();
  const response = await page.waitForSelector('text="success"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-1.png",
    fullPage: true,
  });

  /////VIEW FILE 1
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithoutHeaders,
      }),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  await page.locator("table").filter({ hasText: "jay21blake20" }).click();
  const response2 = await page.waitForSelector("table");
  const isVisible2 = await response2.isVisible();
  expect(isVisible2).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-1-1.png",
    fullPage: true,
  });

  /////LOAD FILE 2
  await page.route(
    "http://localhost:8080/loadcsv?filepath=emptyCSV",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file emptyCSV");
  await page.getByRole("button", { name: "Submit" }).click();
  const response3 = await page.waitForSelector('text="success"');
  const isVisible3 = await response.isVisible();
  expect(isVisible3).toBe(false);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-1-2.png",
    fullPage: true,
  });

  /////VIEW FILE 2
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithHeaders,
      }),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  await page.locator("table").filter({ hasText: "nameagejay21blake20" }).click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-1-3.png",
    fullPage: true,
  });
});

// integration testing with mode
test("mode changes works correctly with load", async ({ page }) => {
  // testing-image : load-edge-2
  // LOAD FILE USING BRIEF MODE
  await page.route(
    "http://localhost:8080/loadcsv?filepath=goodpath",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file goodpath");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-2.png",
    fullPage: true,
  });

  const response = await page.waitForSelector('text="success"');
  const isVisible = await response.isVisible();
  expect(isVisible).toBe(true);

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-2-1.png",
    fullPage: true,
  });
  const response2 = await page.waitForSelector(
    'text="Application has been set to verbose mode"'
  );
  const isVisible2 = await response2.isVisible();
  expect(isVisible2).toBe(true);

  // LOAD FILE USING VERBOSE MODE
  await page.route(
    "http://localhost:8080/loadcsv?filepath=goodpath2",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file goodpath2");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/load-integration-2-2.png",
    fullPage: true,
  });
  const response3 = await page.waitForSelector('text="Output:"');
  const isVisible3 = await response3.isVisible();
  expect(isVisible3).toBe(true);
});