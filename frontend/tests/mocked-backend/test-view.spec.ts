import { test, expect } from "@playwright/test";
import {
  exampleCSVWithoutHeaders,
  exampleCSVWithHeaders,
  emptyCSV,
  oneRow,
  oneCol,
  exampleCSVWithHeadersWithHole
} from "../../src/mockedJson";

test.beforeEach(async ({ page }) => {
  await page.goto(" http://localhost:5173/");
});

async function sendCommand(page, command) {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill(command);
  await page.getByRole("button", { name: "Submit" }).click();
}

{/********************* MOCK UNIT TESTING FOR VIEW FUNCTION **********************/}

// basic testing of viewing a file without headers
test("correct rendering of a file without headers", async ({ page }) => {

  // testing-image : view-basic-1
  await page.route(
    "http://localhost:8080/loadcsv?filepath=withoutHeaders.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file withoutHeaders.csv");
  await page.getByRole("button", { name: "Submit" }).click();

  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithoutHeaders,
      }),
    });
  });
  
  sendCommand(page, "view");
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-basic-1.png",
    fullPage: true,
  });

  await page.locator("table").filter({ hasText: "jay21blake20" }).click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-basic-1.png",
    fullPage: true,
  });
});


// basic testing of viewing a file with headers
test("correct rendering of a file with headers", async ({ page }) => {
  // testing-image : view-basic-2
  await page.route(
    "http://localhost:8080/loadcsv?filepath=withHeaders.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file withHeaders.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  // testing-image : view-basic-2
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithHeaders,
      }),
    });
  });

  sendCommand(page, "view");

  await page
    .locator("table")
    .filter({ hasText: "nameagejay21blake20" })
    .click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-basic-2.png",
    fullPage: true,
  });
});

// edge testing of viewing a file that is empty
test("correct rendering of a file that is empty", async ({ page }) => {
  // testing-image : view-edge-1
  await page.route(
    "http://localhost:8080/loadcsv?filepath=empty.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file empty.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  // testing-image : view-basic-2
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: emptyCSV,
      }),
    });
  });

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("File is empty!")).toBeVisible();
  
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-edge-1.png",
    fullPage: true,
  });
});

// edge testing of viewing a file with one row
test("correct rendering of a file with one row", async ({ page }) => {
  // testing-image : view-edge-2
  await page.route(
    "http://localhost:8080/loadcsv?filepath=oneRow.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file oneRow.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: oneRow,
      }),
    });
  });

  sendCommand(page, "view");

  await page
    .locator("table")
    .filter({ hasText: "jay21" })
    .click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-edge-2.png",
    fullPage: true,
  });
});


// edge testing of viewing a file with one col
test("correct rendering of a file with one col", async ({ page }) => {
  // testing-image : view-edge-3
  await page.route(
    "http://localhost:8080/loadcsv?filepath=oneCol.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file oneCol.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: oneCol,
      }),
    });
  });

  sendCommand(page, "view");

  await page
    .locator("table")
    .filter({ hasText: "jayblake" })
    .click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-edge-3.png",
    fullPage: true,
  });
});

// edge testing of viewing a file with holes
test("correct rendering of a file with holes", async ({ page }) => {
  // testing-image : view-edge-4
  await page.route(
    "http://localhost:8080/loadcsv?filepath=holes.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file holes.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithHeadersWithHole,
      }),
    });
  });

  sendCommand(page, "view");

  await page
    .locator("table")
    .filter({ hasText: "nameagejay21blakejay57" })
    .click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-edge-4.png",
    fullPage: true,
  });
});

{/********************* MOCK INTEGRATION TESTING FOR VIEW FUNCTION **********************/}

// integration testing of viewing a file after mode switch
test("correct rendering of a file in verbose and brief mode", async ({ page }) => {
  // testing-image : view-integration-1
  await page.route(
    "http://localhost:8080/loadcsv?filepath=reg.csv",
    (route) => {
      route.fulfill({
        body: JSON.stringify({ result: "success" }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file reg.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.route("http://localhost:8080/viewcsv", (route) => {
    route.fulfill({
      body: JSON.stringify({
        result: "success",
        data: exampleCSVWithHeaders,
      }),
    });
  });

  sendCommand(page, "view");

  await page
    .locator("table")
    .filter({ hasText: "nameagejay21blake20" })
    .click();
  const response4 = await page.waitForSelector("table");
  const isVisible4 = await response4.isVisible();
  expect(isVisible4).toBe(true);
  

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  const response2 = await page.waitForSelector(
    'text="Application has been set to verbose mode"'
  );
  const isVisible2 = await response2.isVisible();
  expect(isVisible2).toBe(true);

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  const response3 = await page.waitForSelector('text="Output:"');
  const isVisible3 = await response3.isVisible();
  expect(isVisible3).toBe(true);

  const response7 = await page.waitForSelector('text="Command: view"');
  const isVisible7 = await response7.isVisible();
  expect(isVisible7).toBe(true);

  await page.screenshot({
    path: "tests/mocked-backend/testing-images/view-integration-4.png",
    fullPage: true,
  });


});