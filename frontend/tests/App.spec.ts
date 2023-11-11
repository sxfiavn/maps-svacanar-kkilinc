import { test, expect } from "@playwright/test";

// /**
//   The general shapes of tests in Playwright Test are:
//     1. Navigate to a URL
//     2. Interact with the page
//     3. Assert something about the page against your expectations
//   Look for this pattern in the tests below!
//  */

// // If you needed to do something before every test case...
// test.beforeEach(() => {
//   // ... you'd put it here.
//   // TODO: Is there something we need to do before every test case to avoid repeating code?
// });

// /**
//  * Don't worry about the "async" yet. We'll cover it in more detail
//  * for the next sprint. For now, just think about "await" as something
//  * you put before parts of your test that might take time to run,
//  * like any interaction with the page.
//  */
// test("on page load, i see an input bar", async ({ page }) => {
//   // Notice: http, not https! Our front-end is not set up for HTTPs.
//   await page.goto("http://localhost:8000/");
//   await expect(page.getByLabel("Command input")).toBeVisible();
// });

// test("after I type into the input box, its text changes", async ({ page }) => {
//   // Step 1: Navigate to a URL
//   await page.goto("http://localhost:8000/");

//   // Step 2: Interact with the page
//   // Locate the element you are looking for
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("Awesome command");

//   // Step 3: Assert something about the page
//   // Assertions are done by using the expect() function
//   const mock_input = `Awesome command`;
//   await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
// });

// test("on page load, i see a button", async ({ page }) => {
//   await page.goto("http://localhost:8000/");
//   await expect(page.getByRole("button")).toBeVisible();
// });

// test("after I click the button, its label increments", async ({ page }) => {
//   await page.goto("http://localhost:8000/");
//   await expect(page.getByRole("button", { name: "Submit" })).toBeVisible();
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByRole("button", { name: "Submit" })).toBeVisible();
// });

// test("after I submit a nonsense command via the input box, it gives me an error message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("Awesome command");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Command not recognized")).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file with headers, it gives me an success message about the file and headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("successfully loaded file with headers")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file without headers, it gives me an success message about the file and no headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers false");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("successfully loaded file without headers")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file with unspecified headers, it gives me an success message about the file and no headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("load_file test_with_headers");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("successfully loaded file without headers")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for an invalid file without headers, it gives me an error message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("load_file jasfhajksf false");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("failed to load file: no such filename")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for an invalid file with headers, it gives me an error message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("load_file jasfhajksf true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("failed to load file: no such filename")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for an invalid file with unspecified headers, it gives me an error message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("load_file jasfhajksf");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("failed to load file: no such filename")
//   ).toBeVisible();
// });

// test("after I submit a mode command via the input box, it gives me a success message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("mode");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("Application has been set to verbose mode")
//   ).toBeVisible();
// });

// test("after I submit a mode command twice via the input box, it gives me a second success message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("mode");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("mode");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("Application has been set to brief mode")
//   ).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file with headers, and I submit a view command, I can see the data and headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Jay")).toBeVisible();
//   await expect(page.getByText("Blake")).toBeVisible();
//   await expect(page.getByText("20")).toBeVisible();
//   await expect(page.getByText("21")).toBeVisible();
//   await expect(page.getByText("age")).toBeVisible();
//   await expect(page.getByText("name")).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file without headers arg false, and I submit a view command, I can see the data and headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers false");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Jay")).toBeVisible();
//   await expect(page.getByText("Blake")).toBeVisible();
//   await expect(page.getByText("20")).toBeVisible();
//   await expect(page.getByText("21")).toBeVisible();
//   await expect(page.getByText("age")).toBeVisible();
//   await expect(page.getByText("name")).toBeVisible();
// });

// test("after I submit a load_file command via the input box for a valid file without headers arg, and I submit a view command, I can see the data and headers", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("load_file test_with_headers");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Jay")).toBeVisible();
//   await expect(page.getByText("Blake")).toBeVisible();
//   await expect(page.getByText("20")).toBeVisible();
//   await expect(page.getByText("21")).toBeVisible();
//   await expect(page.getByText("age")).toBeVisible();
//   await expect(page.getByText("name")).toBeVisible();
// });

// test("after I submit a view command with no file loaded, I get an error", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("There is no file loaded")).toBeVisible();
// });

// test("after I submit a load_file command, and search with valid parameters, I get a correct search result", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 0 Jay");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Jay")).toBeVisible();
//   await expect(page.getByText("21")).toBeVisible();
// });

// test("after I submit a load_file command, and search with another set of valid parameters, I get a correct search result", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 0 Blake");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Blake")).toBeVisible();
//   await expect(page.getByText("20")).toBeVisible();
// });

// test("after I submit a load_file command, and search with yet another set of valid parameters, I get a correct search result", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 1 21");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Jay")).toBeVisible();
//   await expect(page.getByText("21")).toBeVisible();
// });

// test("after I submit a load_file command, and search with yet yet another set of valid parameters, I get a correct search result", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 1 20");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("Blake")).toBeVisible();
//   await expect(page.getByText("20")).toBeVisible();
// });

// test("after I submit a load_file command, and view that file, I can then submit a new load_file command, and view that file", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("successfully loaded file with headers")
//   ).toBeVisible();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("57")).not.toBeVisible();
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers_with_hole true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByRole('cell', { name: 'successfully loaded file with headers' }).nth(1)).toBeVisible();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("view");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(page.getByText("57")).toBeVisible();
// });

// test("after I submit a load_file command, and search for something that isn't in the file, I get a failure message", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 1 40");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("No result found for given search parameters")
//   ).toBeVisible();
// });


// test("after I submit a load_file command, and search for something that returns two rows, it returns two rows", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers_with_hole true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 0 Jay");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("21")
//   ).toBeVisible();
//   await expect(
//     page.getByText("57")
//   ).toBeVisible();
// });


// test("after I submit a load_file command, and search for a row with a hole, it should not return data in the hole", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers_with_hole true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 0 Blake");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("Blake")
//   ).toBeVisible();
//   await expect(
//     page.getByText("20")
//   ).not.toBeVisible();
// });

// test("after I submit a load_file command, and search for something that isn't in the file, I get a failure message part 2", async ({
//   page,
// }) => {
//   await page.goto("http://localhost:8000/");
//   await page.getByLabel("Command input").click();
//   await page
//     .getByLabel("Command input")
//     .fill("load_file test_with_headers true");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await page.getByLabel("Command input").click();
//   await page.getByLabel("Command input").fill("search 0 21");
//   await page.getByRole("button", { name: "Submit" }).click();
//   await expect(
//     page.getByText("No result found for given search parameters")
//   ).toBeVisible();
// });