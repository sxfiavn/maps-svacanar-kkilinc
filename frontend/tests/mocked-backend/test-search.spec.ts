import { test, expect } from "@playwright/test";
import {
  exampleCSVWithoutHeaders,
} from "../../src/mockedJson";

test.beforeEach(async ({ page }) => {
  await page.goto(" http://localhost:5173/");
});

{/********************* MOCK UNIT TESTING FOR SEARCH FUNCTION **********************/}

test("after I search without loading, i get a message", async ({page,}) => {
  // testing image: search-edge-1
  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "failure",
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response7 = await page.waitForSelector(
    'text="There is no file loaded"'
  );
  const isVisible7 = await response7.isVisible();
  expect(isVisible7).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-edge-1.png",
    fullPage: true,
  });
});

test("search after loading no headers", async ({ page }) => {
  // testing image: search-basic-1

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

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.waitForSelector("table");
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-basic-1.png",
    fullPage: true,
  });
});

test("search after loading w/ headers", async ({ page }) => {
  // testing image: search-basic-2

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

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.waitForSelector("table");
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-basic-2.png",
    fullPage: true,
  });
});

test("search with column number", async ({ page }) => {
  // testing image: search-basic-3

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

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=1&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response5 = await page.waitForSelector("table");
  const isVisible2 = await response5.isVisible();
  //expect(isVisible2).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-basic-3.png",
    fullPage: true,
  });
});

test("search with column name", async ({ page }) => {
  // testing image: search-basic-4

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

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=city&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response5 = await page.waitForSelector("table");
  const isVisible3 = await response5.isVisible();
  //expect(isVisible3).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-basic-4.png",
    fullPage: true,
  });
});

{/********************* MOCK INTEGRATION TESTING FOR SEARCH FUNCTION **********************/}
// testing the loading of a file -> view -> search
test("testing the loading of a file -> view -> search", async ({ page }) => {
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
    path: "tests/mocked-backend/testing-images/search-integration-1.png",
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
    path: "tests/mocked-backend/testing-images/search-integration-1-1.png",
    fullPage: true,
  });

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response5 = await page.waitForSelector("table");
  const isVisible3 = await response5.isVisible();
  //expect(isVisible3).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-integration-1-2.png",
    fullPage: true,
  });
});


// testing the the mode works with search
test("testing mode switch for search", async ({ page }) => {
  // testing-image : load-integration-2
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
    path: "tests/mocked-backend/testing-images/search-integration-2.png",
    fullPage: true,
  });

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response4 = await page.waitForSelector("table");
  await response4.isVisible();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-integration-2-1.png",
    fullPage: true,
  });


  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();

  await page.route(
    "http://localhost:8080/searchcsv?target=providence&identifier=undefined&identifier=undefined&header=undefined",
    (route) => {
      route.fulfill({
        body: JSON.stringify({
          result: "success",
          data: [["providence", 500, 3, 7]],
        }),
      });
    }
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence");
  await page.getByRole("button", { name: "Submit" }).click();

  const response5 = await page.waitForSelector("table");
  const isVisible2 = await response5.isVisible();
  //expect(isVisible2).toBe(true);
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/search-integration-2-2.png",
    fullPage: true,
  });


});
