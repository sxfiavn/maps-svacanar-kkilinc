import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:5173/");
});


// basic testing for a basic filter query
test("correct response for a basic filter query", async ({ page }) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("filter -71.410972 -71.410972 41.859843 41.859843");
  await page.waitForSelector('role=button[name="Submit"]');
  await page.getByRole("button", { name: "Submit" }).click();
  await page.waitForSelector("text=9512");
  const text1 = await page.locator("text=9512");
  await expect(text1).toBeVisible();
  await page.waitForSelector("text=9511");
  const text4 = await page.locator("text=9511");
  await expect(text4).toBeVisible();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/integration-basic-1.png",
    fullPage: true,
  });
});

test("correct response for an edge filter query where data is not found", async ({ page }) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("filter -40 -40 41.859843 41.859843");
  await page.waitForSelector('role=button[name="Submit"]');
  await page.getByRole("button", { name: "Submit" }).click();
  await page.waitForSelector("text=No data fits the given bounds.");
  const text1 = await page.locator("text=No data fits the given bounds.");
  await expect(text1).toBeVisible();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/integration-basic-2.png",
    fullPage: true,
  });
});

test("correct response for an edge filter query where not enough parameters are given", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("filter -40 41.859843");
  await page.waitForSelector('role=button[name="Submit"]');
  await page.getByRole("button", { name: "Submit" }).click();
  await page.waitForSelector("text=Please input a complete bounding box.");
  const text1 = await page.locator("text=Please input a complete bounding box.");
  await expect(text1).toBeVisible();
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/integration-basic-3.png",
    fullPage: true,
  });
});

/////////////////////

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
  await page.screenshot({
    path: "tests/mocked-backend/testing-images/huh.png",
    fullPage: true,
  });
  await expect(page.getByText("There is no file loaded")).toBeVisible();
    

});

test("after I submit a load_file command via the input box for a valid file, it gives me an success message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/RI_Income.csv");
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
  await page.getByLabel("Command input").fill("load_file data/RI_Income.csv");
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
  await page.getByLabel("Command input").fill("load_file data/RI_Income.csv");
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
  await page.getByLabel("Command input").fill("load_file data/RI_Income.csv");
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

test("after I submit a load_file command and then view the file, I can submit a different load_file command and view that file", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/RI_Income.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search providence 1 true");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Providence")).toBeVisible();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("load_file data/second_file.csv");
  await page.getByRole("button", { name: "Submit" }).click();
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();
});

test("after I submit a valid broadband request, I get a success respnse", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("broadband Massachusetts Plymouth");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("88.4")).toBeVisible();
});

test("after I submit a valid broadband request with a two word parameter, I get a success respnse", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband [New York] Albany");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("84.7")).toBeVisible();
});

test("after I submit an invalid broadband request, I get an error message", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband Massachusetts q");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("No data returned from API")).toBeVisible();
});
