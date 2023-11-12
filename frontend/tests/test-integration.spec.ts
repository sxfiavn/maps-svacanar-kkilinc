import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:5173/");
});

// "Unit Testing" filter query
test("Test Filter", async ({ page }) => {
  // Data found
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("filter -71.410972 -71.410972 41.859843 41.859843");
  await page.waitForSelector('role=button[name="Submit"]');
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Providence")).toBeVisible();

  // No data found
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0 0 -10 -10");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("No data fits the given bounds.")).toBeVisible();

  // Not enough params
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 3 3 ");
  await page.getByRole("button", { name: "Submit" }).click();
  expect(page.getByText("Error: Too few arguments given.")).toBeVisible();

  // Too many enough params
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 3 3 3 3 3");
  await page.getByRole("button", { name: "Submit" }).click();
  expect(page.getByText("Error: Too many arguments given.")).toBeVisible();

  // No params
  await page.getByLabel("Command input").fill("filter");
  await page.getByLabel("Command input").fill("filter");
  await page.getByRole("button", { name: "Submit" }).click();
  expect(page.getByText("Error: No parameters provided.")).toBeVisible();

  // Invalid params
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter a a a a");
  await page.getByRole("button", { name: "Submit" }).click();
  expect(page.getByText("Error: Couldn't fetch the data.")).toBeVisible();
});

// "Unit Testing" search area query
test("Test Search Area", async ({ page }) => {
  // Successful call
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area Providence");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Search Completed Successfully")).toBeVisible();

  // No parameters
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Error: No parameters provided.")).toBeVisible();
});

// Random Testing Filter
test("Random Testing Filter", async ({ page }) => {
  // generate 4 random numbers
  let randomNums: number[] = [];
  for (let i = 0; i < 4; i++) {
    randomNums.push(Math.random() * 100);
  }

  // make string of random numbers
  let randomNumsString = "filter ";
  for (let i = 0; i < 4; i++) {
    randomNumsString += randomNums[i] + " ";
  }

  // Test random numbers
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("randomNumsString");
  await page.getByRole("button", { name: "Submit" }).click();

  // Check if data found (probably not found tho)
  await expect(page.getByText("No data fits the given bounds.")).toBeVisible();
});

// Integration testing with filter and search area
test("Integration Testing Filter and Search Area", async ({ page }) => {
  // Filter - Data found
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("filter -71.410972 -71.410972 41.859843 41.859843");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Providence")).toBeVisible();

  // Search area
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area Providence");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Search Completed Successfully")).toBeVisible();

  // Change mode (to verbose)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to verbose mode")
  ).toBeVisible();

  // Load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file backend/data/empty_csv.csv");

  // Change mode again (to brief)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to brief mode")
  ).toBeVisible();

  // Filter again - No data found
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0 0 0 0");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("No data fits the given bounds.")).toBeVisible();
});

// Integration testing with filter and search area
test("Filter, Search Area, Filter", async ({ page }) => {
  // Filter 1 (Providence)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0000");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Error: Too Few Parameters ")).toBeVisible();

  // Search area (Providence)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area Providence");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Search Completed Successfully")).toBeVisible();

  // Filter 2 (Providence)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0000");
  await page.getByRole("button", { name: "Submit" }).click();
});

//filter, mode, filter, search_area, mode, search_area;
test("Filter, Mode, Filter, Search Area, Mode, Search Area", async ({
  page,
}) => {
  // Filter (too many parameters)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0 0 0 0 0");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Error: Too many arguments given.")
  ).toBeVisible();

  // Mode (to verbose)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to verbose mode")
  ).toBeVisible();

  // Filter (no data)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter 0 0 0 0");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("No data fits the given bounds.")).toBeVisible();
  await expect(page.getByText("Command: filter")).toBeVisible(); // From verbose mode

  // Search Area (successful)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area Bronx");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Search Completed Successfully")).toBeVisible();
  await expect(page.getByText("Command: search_area")).toBeVisible(); // From verbose mode

  // Mode (to brief)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to brief mode")
  ).toBeVisible();

  // Search Area (no parameters)
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search_area");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Error: No parameters provided.")).toBeVisible();
});

/////////////////////

// Load, load, view -> check view is from the second loaded file
test("2 consecutive loads, then view", async ({ page }) => {
  await page.goto("http://localhost:5173/");

  // Load file 1
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file backend/data/empty_csv.csv");
  await page.getByRole("button", { name: "Submit" }).click();

  // Load file 2
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file backend/data/RI_Income.csv");

  await page.getByRole("button", { name: "Submit" }).click();

  // View
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  // Expected value
  await expect(page.getByText("Rhode Island")).toBeVisible();
});

test("after I submit a nonsense command via the input box, it gives me an error message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Please enter a registered command")
  ).toBeVisible();
});

test("after I submit a view command with no file loaded, I get an error", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("There is no file loaded")).toBeVisible();
});

test("after I submit a load_file command via the input box for a valid file, it gives me an success message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("success")).toBeVisible();
});

test("after I submit a load_file command via the input box for an invalid file, it gives me an error message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file jasfhajksf");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("This is not a valid filepath!")).toBeVisible();
});

test("after I submit a mode command via the input box, it gives me a success message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to verbose mode")
  ).toBeVisible();
});

test("after I submit a mode command twice via the input box, it gives me a second success message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Application has been set to brief mode")
  ).toBeVisible();
});

test("after I submit a load_file command via the input box for a valid file with headers, and I submit a view command, I can see the data and headers", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("City/Town")).toBeVisible();
  await expect(page.getByText("Median Household Income")).toBeVisible();
  await expect(page.getByText("Median Family Income")).toBeVisible();
  await expect(page.getByText("Per Capita Income")).toBeVisible();
  await expect(page.getByText("Rhode Island")).toBeVisible();
  await expect(page.getByText("Barrington")).toBeVisible();
});

test("after I submit a load_file command, and search with column index, I get a correct search result", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence 1 true");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("providence")).toBeVisible();
  await expect(page.getByText("55,787.00")).toBeVisible();
  await expect(page.getByText("65,461.00")).toBeVisible();
  await expect(page.getByText("31,757.00")).toBeVisible();
});

test("after I submit a load_file command, and search with column name, I get a correct search result", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("search providence City/Town true");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("providence")).toBeVisible();
  await expect(page.getByText("55,787.00")).toBeVisible();
  await expect(page.getByText("65,461.00")).toBeVisible();
  await expect(page.getByText("31,757.00")).toBeVisible();
});

test("2 consecutive loads", async ({ page }) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence 1 true");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Providence")).toBeVisible();
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file  backend/data/second_file.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();
});
