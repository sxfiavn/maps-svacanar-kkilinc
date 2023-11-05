import { test, expect, Page } from "@playwright/test";

test.describe("REPL Application E2E Tests", () => {
  let page: Page;

  test.beforeEach(async ({ browser }) => {
    page = await browser.newPage();
    await page.goto("http://localhost:8000");
  });

  test.afterEach(async () => {
    await page.close();
  });

  test("should display the welcome message initially", async () => {
    const welcomeMessage = await page.textContent(".repl-history");
    expect(welcomeMessage).toContain("Welcome:");
  });

  test("should allow input commands", async () => {
    await page.fill(".repl-command-box", "help");
    await page.click("button"); // Click the submit button
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("OnStartUp"); // Assuming OnStartUp displays something related to 'OnStartUp'
  });

  test("should warn when loading a non-existing file", async () => {
    await page.fill(".repl-command-box", "load_file non_existing_file.csv");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("No data loaded");
  });

  test("should display data when a valid file is loaded", async () => {
    // Assuming that 'existing_file.csv' is available in your mocked data
    await page.fill(".repl-command-box", "load_file path/to/dataset1.csv");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Load CSV data from file: path/to/dataset1.csv"
    );
  });

  test("should switch mode to verbose", async () => {
    await page.fill(".repl-command-box", "mode verbose");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(
      page.locator("table").filter({ hasText: "Changed mode to 'verbose'" })
    ).toBeVisible();
  });

  test("should switch mode to brief", async () => {
    await page.fill(".repl-command-box", "mode brief");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'brief'");
  });

  test("should show error on invalid mode", async () => {
    await page.fill(".repl-command-box", "mode invalidMode");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("is not a valid mode");
  });

  test("should perform basic search correctly", async () => {
    // Assuming 'existing_file.csv' and search command have been loaded
    await page.fill(".repl-command-box", "search column value");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Displayed search results"); // Update as per your output
  });

  test("should show no match on search", async () => {
    await page.fill(".repl-command-box", "search column noMatch");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("No matching results found.");
  });

  test("should warn for search without data", async () => {
    await page.fill(".repl-command-box", "search column value");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("No data loaded");
  });

  test("should handle multiple commands", async () => {
    await page.fill(".repl-command-box", "mode verbose");
    await page.click("button");
    await page.fill(".repl-command-box", "path/to/dataset1.csv");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Loaded CSV data");
  });

  test("should clear input after command submit", async () => {
    await page.fill(".repl-command-box", "mode verbose");
    await page.click("button");
    const inputBoxValue = await page.inputValue(".repl-command-box");
    expect(inputBoxValue).toBe("");
  });

  test("should display unknown command error", async () => {
    await page.fill(".repl-command-box", "randomCommand");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Unknown command");
  });

  test("should handle empty command", async () => {
    await page.fill(".repl-command-box", "");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Unknown command"); // Or whatever your app displays in this case
  });

  test("should handle leading and trailing whitespaces in command", async () => {
    await page.fill(".repl-command-box", "  mode verbose  ");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'");
  });

  test("should handle command case sensitivity", async () => {
    await page.fill(".repl-command-box", "MoDe verbose");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'"); // If your app is case insensitive
  });

  test("should handle extra spaces between command parts", async () => {
    await page.fill(".repl-command-box", "mode  verbose");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'");
  });

  test("should display error for missing file path in load_file", async () => {
    await page.fill(".repl-command-box", "load_file");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("File path is missing");
  });

  test("should display error for missing search parameters", async () => {
    await page.fill(".repl-command-box", "search");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Missing search parameters");
  });

  test("should display help text on command help", async () => {
    await page.fill(".repl-command-box", "help");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Welcome Message:"); // Or whatever your help message is
  });

  test("should display error for invalid file format in load_file", async () => {
    await page.fill(".repl-command-box", "load_file invalid.txt");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Invalid file format");
  });

  test("should allow input commands", async () => {
    await page.fill(".repl-command-box", "help");
    await page.click("button"); // Click the submit button
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("OnStartUp"); // Assuming OnStartUp displays something related to 'OnStartUp'
  });

  test("should not allow viewing CSV without file load", async () => {
    await page.fill(".repl-command-box", "view");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "No data loaded. Please use load_file first."
    );
  });

  test("should not allow searching CSV without file load", async () => {
    await page.fill(".repl-command-box", "search column value");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "No data loaded. Please use load_file before searching."
    );
  });

  test("should handle quotes around file path in load_file", async () => {
    await page.fill(".repl-command-box", 'load_file "path/to/dataset1.csv"');
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Load CSV data from file: path/to/dataset1.csv"
    );
  });

  test("should handle single quotes around file path in load_file", async () => {
    await page.fill(".repl-command-box", "load_file 'path/to/dataset1.csv'");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Load CSV data from file: path/to/dataset1.csv"
    );
  });

  test("should handle extra multiple spaces between command and arguments", async () => {
    await page.fill(".repl-command-box", "mode    verbose");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'");
  });

  test("should handle multiple spaces between command parts", async () => {
    await page.fill(".repl-command-box", "load_file   path/to/dataset1.csv");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Load CSV data from file: path/to/dataset1.csv"
    );
  });

  test("should handle command with both quotes and extra spaces", async () => {
    await page.fill(
      ".repl-command-box",
      '  load_file   "path/to/dataset1.csv"  '
    );
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Load CSV data from file: path/to/dataset1.csv"
    );
  });

  test("Load multiple CSV files consecutively", async () => {
    await page.fill(".repl-command-box", "load_file path/to/dataset1.csv");
    await page.click("button");
    let commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Loaded CSV data from file: path/to/dataset1.csv"
    );

    await page.fill(".repl-command-box", "load_file path/to/dataset2.csv");
    await page.click("button");
    commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Loaded CSV data from file: path/to/dataset2.csv"
    );
  });

  test("Perform search operation after changing modes", async () => {
    await page.fill(".repl-command-box", "load_file path/to/dataset1.csv");
    await page.click("button");
    await page.fill(".repl-command-box", "mode verbose");
    await page.click("button");
    await page.fill(".repl-command-box", "search age 28");
    await page.click("button");
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Alice"); // Replace with your expectation
  });

  test("Change mode and load file", async () => {
    await page.fill(".repl-command-box", "mode verbose");
    await page.click("button");
    let commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'");

    await page.fill(".repl-command-box", "load_file path/to/dataset1.csv");
    await page.click("button");

    
    await page.fill(".repl-command-box", "load_file path/to/dataset2.csv");
    await page.click("button");
    
    commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Loaded CSV data from file: path/to/dataset1.csv in verbose mode"
    ); // Expected output when in verbose mode
  });

  test("Change mode and then perform search", async () => {
    await page.fill(".repl-command-box", "mode brief");
    await page.click("button");
    let commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'brief'");

    await page.fill(".repl-command-box", "search column value");
    await page.click("button");
    commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Brief search result"); // Expected output when in brief mode
  });

  test("load_file with a valid file path", async () => {
    await page.fill(
      'input[aria-label="Command input"]',
      "load_file path/to/dataset1.csv"
    );
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "Loaded CSV data from file: path/to/dataset1.csv"
    );
  });

  test("load_file with an invalid file path", async () => {
    await page.fill(
      'input[aria-label="Command input"]',
      "load_file invalid/path"
    );
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Not a valid file path.");
  });

  test("view command without data", async () => {
    await page.fill('input[aria-label="Command input"]', "view");
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain(
      "No data loaded. Please use load_file first."
    );
  });

  test("view command with data", async () => {
    // Load file first
    await page.fill(
      'input[aria-label="Command input"]',
      "load_file path/to/dataset1.csv"
    );
    await page.click('button:text("Submit")');
    await page.fill('input[aria-label="Command input"]', "view");
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("selector-for-displayed-data"); // Replace with actual expected text
  });


  

  test("search command with invalid parameters", async () => {
    // Assuming some loaded data
    await page.fill(
      'input[aria-label="Command input"]',
      "load_file path/to/dataset1.csv"
    );
    await page.click('button:text("Submit")');
    await page.fill(
      'input[aria-label="Command input"]',
      "search wrong-column value"
    );
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("No matching results found.");
  });

  test("mode change command", async () => {
    await page.fill('input[aria-label="Command input"]', "mode verbose");
    await page.click('button:text("Submit")');
    const commandOutput = await page.textContent(".repl-history");
    expect(commandOutput).toContain("Changed mode to 'verbose'");
  });
});
